package com.kafka.springjwt.service;

import com.kafka.springjwt.entity.DeviceInformation;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {
    private static final String TOPIC = "test-topic";

    KafkaConsumer<String,DeviceInformation> KafkaConsumer;
    @Autowired
    private KafkaTemplate<String, DeviceInformation> kafkaTemplate;

    @Override
    public void sendDeviceInformation(DeviceInformation deviceInformation) {
        kafkaTemplate.send(TOPIC,deviceInformation);
    }
}
