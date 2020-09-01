package org.jbpm.contrib;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
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

@Wid(widfile = "CustomDefinitions.wid", name = "EventWorkItemHandler",
        displayName = "EventWorkItemHandler",
        defaultHandler = "mvel: new org.jbpm.contrib.EventWorkItemHandler()",
        documentation = "event-workitem/index.html",
        category = "event-workitem",
        icon = "CustomDefinitions.png",
        parameters = {
                @WidParameter(name = "requestTopic", required = true),
                @WidParameter(name = "successResponseTopic", required = false),
                @WidParameter(name = "errorResponseTopic", required = false),
                @WidParameter(name = "requestCompensationTopic", required = false)
        },
        results = {
                @WidResult(name = "Result")
        },
        mavenDepends = {
                @WidMavenDepends(group = "org.jbpm.contrib", artifact = "event-workitem", version = "7.53.0.Final")
        },
        serviceInfo = @WidService(category = "event-workitem", description = "Event work item",
                keywords = "",
                action = @WidAction(title = "EventWorkItemHandler Name")
        )
)
public class EventWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {

    Producer<String, String> producer;
    Map<Long, AtomicBoolean> consumers = new ConcurrentHashMap<>();

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

            producer = Optional.ofNullable(producer).orElseGet(() -> ProducerCreator.createProducer());
            ProducerRecord<String, String> record = new ProducerRecord<>(requestTopic,
                                                                         UUID.randomUUID().toString(),
                                                                         "Request payload");
            //publish request event
            RecordMetadata metadata = producer.send(record).get();
            System.out.println("Record sent with key to partition " + metadata.partition()
                                       + " with offset " + metadata.offset());

            if (StringUtils.isAllEmpty(successResponseTopic, errorResponseTopic)) {
                completeWorkItemAsync(workItem, new Event("", "", "success"));
                return;
            }

            //start consumer in a different thread (async flow)
            CompletableFuture.runAsync(() -> runConsumer(workItem, successResponseTopic,
                                                         errorResponseTopic))
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
        AtomicBoolean started = consumers.get(workItem.getId());
        Optional.ofNullable(started)
                .ifPresent(c -> c.set(true));
        consumers.remove(workItem.getId());
        Optional.ofNullable(manager).ifPresent(m -> m.abortWorkItem(workItem.getId()));
        System.out.println("stop consumer.....");
    }

    /**
     * Run consumer loop
     * @param workItem
     * @param successResponseTopic
     * @param errorResponseTopic
     */
    void runConsumer(WorkItem workItem, String successResponseTopic, String errorResponseTopic) {
        if (StringUtils.isAllEmpty(successResponseTopic, errorResponseTopic)) {
            completeWorkItemAsync(workItem, new Event("", "", "success"));
            return;
        }

        System.out.println("========== START kafka consumer ============");

        Consumer<String, String> consumer = ConsumerCreator.createConsumer(successResponseTopic, errorResponseTopic);
        try {
            int noMessageFound = 0;
            AtomicBoolean stop = new AtomicBoolean(false);
            consumers.put(workItem.getId(), stop);

//            getRuntimeEngine(workItem, getRuntimeManager(workItem)).getKieSession().addEventListener(new DefaultProcessEventListener() {
//                @Override
//                public void beforeProcessCompleted(ProcessCompletedEvent event) {
//                    abortWorkItem(workItem, null);
//                    super.beforeProcessCompleted(event);
//                }
//            });

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

                    completeWorkItemAsync(workItem, new Event("123", "456", type));
                    stop.set(true);
                });
                // commits the offset of record to broker.
                consumer.commitSync();
            }
            consumer.close();
            consumers.remove(workItem.getId());
            System.out.println("========== END kafka consumer EventWorkItemHandler ============");
        } catch (Exception e) {
            consumer.commitSync();
            consumer.close();
            e.printStackTrace();
            handleException(new RuntimeException("Error from kafka", e));
        }
    }

    private RuntimeManager getRuntimeManager(WorkItem workItem) {
        String deploymentId = ((WorkItemImpl) workItem).getDeploymentId();
        return RuntimeManagerRegistry.get().getManager(deploymentId);
    }

    private RuntimeEngine getRuntimeEngine(WorkItem workItem, RuntimeManager manager) {
        Long processInstanceId = workItem.getProcessInstanceId();
        if (manager != null) {
            return manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        }
        return null;
    }

    private void completeWorkItemAsync(WorkItem workItem, Event event) {
        Map<String, Object> results = new HashMap<>();
        results.put("Result", event);

        RuntimeManager manager = getRuntimeManager(workItem);
        RuntimeEngine engine = getRuntimeEngine(workItem, manager);
        //complete the work item with the received event
        if (engine != null) {
            engine.getKieSession().getWorkItemManager().completeWorkItem(workItem.getId(), results);
            manager.disposeRuntimeEngine(engine);
        }
    }
}