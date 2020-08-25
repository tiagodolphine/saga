package org.jbpm.contrib;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.jbpm.process.workitem.core.util.RequiredParameterValidator;
import org.jbpm.process.workitem.core.util.Wid;
import org.jbpm.process.workitem.core.util.WidMavenDepends;
import org.jbpm.process.workitem.core.util.WidParameter;
import org.jbpm.process.workitem.core.util.WidResult;
import org.jbpm.process.workitem.core.util.service.WidAction;
import org.jbpm.process.workitem.core.util.service.WidService;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.kafka.ConsumerCreator;
import org.kie.kafka.Event;
import org.kie.kafka.IKafkaConstants;
import org.kie.kafka.ProducerCreator;

@Wid(widfile = "CustomDefinitions.wid", name = "CustomDefinitions",
        displayName = "CustomDefinitions",
        defaultHandler = "mvel: new org.jbpm.contrib.EventWorkItemHandler()",
        documentation = "event-workitem/index.html",
        category = "event-workitem",
        icon = "CustomDefinitions.png",
        parameters = {
                @WidParameter(name = "requestTopic", required = true),
                @WidParameter(name = "successResponseTopic", required = true),
                @WidParameter(name = "errorResponseTopic", required = true),
                @WidParameter(name = "requestCompensationTopic", required = true)
        },
        results = {
                @WidResult(name = "Result")
        },
        mavenDepends = {
                @WidMavenDepends(group = "org.jbpm.contrib", artifact = "event-workitem", version = "7.40.0.Final")
        },
        serviceInfo = @WidService(category = "event-workitem", description = "Event work item",
                keywords = "",
                action = @WidAction(title = "EventWorkItemHandler Name")
        )
)
public class EventWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {

    private final AtomicBoolean stop = new AtomicBoolean(false);

    public void executeWorkItem(WorkItem workItem,
                                WorkItemManager manager) {
        try {
            RequiredParameterValidator.validate(this.getClass(),
                                                workItem);
            //debugging parameters
            String requestTopic = (String) workItem.getParameter("requestTopic");
            String successResponseTopic = (String) workItem.getParameter("successResponseTopic");
            String errorResponseTopic = (String) workItem.getParameter("errorResponseTopic");
            String requestCompensationTopic = (String) workItem.getParameter("requestCompensationTopic");

            System.out.println("==========SAGA Kafka WorkItemHandler ============");

            Producer<String, String> producer = ProducerCreator.createProducer();
            ProducerRecord<String, String> record = new ProducerRecord<>(requestTopic,
                                                                         UUID.randomUUID().toString(),
                                                                         "Request payload");
            //publish request event
            RecordMetadata metadata = producer.send(record).get();
            System.out.println("Record sent with key to partition " + metadata.partition()
                                       + " with offset " + metadata.offset());

            //start consumer in a different thread (async flow)
            CompletableFuture.runAsync(() -> runErrorConsumer(workItem, successResponseTopic, errorResponseTopic))
                    .exceptionally(t -> {
                        handleException(t);
                        return null;
                    });
        } catch (Throwable cause) {
            handleException(cause);
        }
    }

    @Override
    public void abortWorkItem(WorkItem workItem,
                              WorkItemManager manager) {
        stop.set(true);
    }

    /**
     * Run consumer loop
     * @param workItem
     * @param successResponseTopic
     * @param errorResponseTopic
     */
    void runErrorConsumer(WorkItem workItem, String successResponseTopic, String errorResponseTopic) {
        System.out.println("========== START kafka consumer ============");

        Consumer<String, String> consumer = ConsumerCreator.createConsumer(successResponseTopic, errorResponseTopic);
        try {
            int noMessageFound = 0;
            AtomicBoolean stop = new AtomicBoolean(false);
            while (!stop.get()) {
                System.out.println("========== Kafka Consumer Loop ============");

                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(2000));

                // 1000 is the time in milliseconds consumer will wait if no record is found at broker.
                if (consumerRecords.count() == 0) {
                    noMessageFound++;
                    if (noMessageFound > IKafkaConstants.MAX_NO_MESSAGE_FOUND_COUNT)
                    // If no message found count is reached to threshold exit loop.
                    {
                        break;
                    } else {
                        continue;
                    }
                }
                //print each record.
                consumerRecords.forEach(record -> {
                    System.out.println("Record Key " + record.key());
                    System.out.println("Record value " + record.value());
                    System.out.println("Record partition " + record.partition());
                    System.out.println("Record offset " + record.offset());

                    String type = Optional.ofNullable(record)
                            .map(ConsumerRecord::topic)
                            .filter(successResponseTopic::equals)
                            .map(t -> "success")
                            .orElse("error");

                    Map<String, Object> results = new HashMap<String, Object>();
                    results.put("Result", new Event("123", "456", type));

                    final String deploymentId = ((WorkItemImpl) workItem).getDeploymentId();
                    RuntimeManager manager = RuntimeManagerRegistry.get().getManager(deploymentId);
                    final long processInstanceId = workItem.getProcessInstanceId();
                    final long workItemId = workItem.getId();

                    //complete the work item with the received event
                    if (manager != null) {
                        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
                        engine.getKieSession().getWorkItemManager().completeWorkItem(workItemId,
                                                                                     results);
                        manager.disposeRuntimeEngine(engine);
                    }
                    stop.set(true);
                });
                // commits the offset of record to broker.
                consumer.commitSync();
            }
            consumer.close();
            System.out.println("========== END kafka consumer EventWorkItemHandler ============");
        } catch (Exception e) {
            consumer.commitSync();
            consumer.close();
            e.printStackTrace();
            handleException(new RuntimeException("Error from kafka", e));
        }
    }
}