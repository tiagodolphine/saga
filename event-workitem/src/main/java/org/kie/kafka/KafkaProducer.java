package org.kie.kafka;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class KafkaProducer {

    public static void main(String[] args) {
        Producer<String, String> producer = ProducerCreator.createProducer();
        ProducerRecord<String, String> record = new ProducerRecord<>(IKafkaConstants.TOPIC_SUBSCRIBE_NAME, "This is record");
        try {
            RecordMetadata metadata = producer.send(record).get();
            System.out.println("Record sent with key to partition " + metadata.partition()
                                       + " with offset " + metadata.offset());
        } catch (Exception e) {
            System.out.println("Error in sending record");
            System.out.println(e);
        }
    }
}
