package com.kafka.springjwt.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.springjwt.entity.DeviceInformation;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Component
public class KafkaMongoDBScheduler {

    private final Logger logger = LoggerFactory.getLogger(KafkaMongoDBScheduler.class);

    @Autowired
    private KafkaConsumer<String, DeviceInformation> kafkaConsumer;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String CONSUMER_GROUP_ID = "test-consumer-group";
    private static final String TOPIC_NAME = "Test-Topic";

    private boolean initialized = false;
    @PostConstruct
    public void init() {
        // Subscribe to the topic
        kafkaConsumer.subscribe(Collections.singletonList(TOPIC_NAME));
    }
    @Scheduled(fixedDelay = 60000) // 10 minutes
    public void fetchAndSaveMessages() {
        if (!initialized) {
            // Seek to the last committed offset for each partition
            kafkaConsumer.seekToEnd(kafkaConsumer.assignment());
            kafkaConsumer.poll(Duration.ZERO); // Poll without blocking to trigger assignment
            kafkaConsumer.seekToBeginning(kafkaConsumer.assignment()); // Reset to beginning for further consumption
            initialized = true;

            // Log information about seeking to the last committed offset
            for (TopicPartition partition : kafkaConsumer.assignment()) {
                long position = kafkaConsumer.position(partition);
                logger.info("Seeking to last committed offset for partition {}: {}", partition, position);
            }
        }

        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);
        logger.info("Scheduler started at {}", formattedTime);

        ConsumerRecords<String, DeviceInformation> records = kafkaConsumer.poll(Duration.ofMillis(10000));

        try {
            for (ConsumerRecord<String, DeviceInformation> record : records) {
                DeviceInformation message = record.value();

                if (!isMessageAlreadyExists(message)) {
                    saveMessageToMongoDB(message);
                    logger.info("New data added to MongoDB: {}", message);
                } else {
                    logger.info("Data found in Kafka but already exists in MongoDB: {}", message);
                }
            }

            // Commit offsets after processing the records
            kafkaConsumer.commitSync();
        } catch (Exception e) {
            logger.error("Error occurred while processing records", e);
        }

        if (records.isEmpty()) {
            logger.info("No data found in Kafka topic.");
        }

    }
    private boolean isMessageAlreadyExists(DeviceInformation message) {
        Query query = new Query();
        query.addCriteria(Criteria.where("deviceID").is(message.getDeviceID()));
        return mongoTemplate.exists(query, "Data");
    }

    private void saveMessageToMongoDB(DeviceInformation message) {
        mongoTemplate.save(message, "Data");
    }
}