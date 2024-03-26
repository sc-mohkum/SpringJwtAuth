package com.kafka.springjwt.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.springjwt.entity.DeviceInformation;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Component
public class KafkaMongoDBScheduler {

    private final Logger logger = LoggerFactory.getLogger(KafkaMongoDBScheduler.class);

    @Autowired
    private KafkaConsumer<String, DeviceInformation> kafkaConsumer;

    @Autowired
    private MongoTemplate mongoTemplate;

    // Variable to store the last processed message
    private String lastProcessedMessageIdentifier;

    public KafkaMongoDBScheduler() {
        // Load the last processed message identifier from MongoDB
        lastProcessedMessageIdentifier = loadLastProcessedMessageIdentifier();
    }

    @Scheduled(fixedDelay = 60000) // 10 minutes
    public void fetchAndSaveMessages() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);
        logger.info("Scheduler started at {}", formattedTime);

        kafkaConsumer.subscribe(Collections.singletonList("test-topic"));

        // Seek to the last processed message identifier
        if (lastProcessedMessageIdentifier != null) {
            kafkaConsumer.seekToEnd(kafkaConsumer.assignment());
        }

        ConsumerRecords<String, DeviceInformation> records = kafkaConsumer.poll(Duration.ofMillis(10000));

        for (ConsumerRecord<String, DeviceInformation> record : records) {
            DeviceInformation message = record.value();
            String messageIdentifier = getMessageIdentifier(message);

            if (lastProcessedMessageIdentifier == null || messageIdentifier.compareTo(lastProcessedMessageIdentifier) > 0) {
                // Check if message already exists in MongoDB
                if (!isMessageAlreadyExists(message)) {
                    // New message found, save it to MongoDB
                    saveMessageToMongoDB(message);
                    logger.info("New data added to MongoDB: {}", message);
                    lastProcessedMessageIdentifier = messageIdentifier;
                    storeLastProcessedMessageIdentifier(lastProcessedMessageIdentifier);
                } else {
                    // Message already exists in MongoDB
                    logger.info("Data found in Kafka but already exists in MongoDB: {}", message);
                }
            }
        }

        if (records.isEmpty()) {
            logger.info("No data found in Kafka topic.");
        }
    }

    // Extract a unique identifier from the message
    private String getMessageIdentifier(DeviceInformation message) {
        return message.getDeviceID(); // You can change this based on your message structure
    }

    // Check if the message already exists in MongoDB
    private boolean isMessageAlreadyExists(DeviceInformation message) {
        Query query = new Query();
        query.addCriteria(Criteria.where("deviceID").is(message.getDeviceID()));
        return mongoTemplate.exists(query, "Data");
    }

    private void saveMessageToMongoDB(DeviceInformation message) {
        mongoTemplate.save(message, "Data");
    }

    // Load the last processed message identifier from MongoDB
    private String loadLastProcessedMessageIdentifier() {
        try {
            // Assuming you have a MongoDB collection named "metadata" with a document storing the last processed message identifier
            MongoCollection<Document> metadataCollection = mongoTemplate.getCollection("metadata");

            if (metadataCollection != null) {
                Document metadataDoc = metadataCollection.find().first();
                if (metadataDoc != null && metadataDoc.containsKey("lastProcessedMessageIdentifier")) {
                    return metadataDoc.getString("lastProcessedMessageIdentifier");
                } else {
                    logger.warn("No 'lastProcessedMessageIdentifier' field found in the metadata document");
                }
            } else {
                logger.error("Unable to get the 'metadata' collection from MongoDB");
            }
        } catch (Exception e) {
            logger.error("Error occurred while loading last processed message identifier", e);
        }
        return null;
    }

    // Store the last processed message identifier in MongoDB
    private void storeLastProcessedMessageIdentifier(String messageIdentifier) {
        // Assuming you have a MongoDB collection named "metadata" with a document storing the last processed message identifier
        MongoCollection<Document> metadataCollection = mongoTemplate.getCollection("metadata");

        Document updateDoc = new Document("$set", new Document("lastProcessedMessageIdentifier", messageIdentifier));
        metadataCollection.updateOne(new Document(), updateDoc, new UpdateOptions().upsert(true));
    }
}