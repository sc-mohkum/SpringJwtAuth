package com.kafka.springjwt.service;

import com.kafka.springjwt.entity.DeviceInformation;
import org.springframework.stereotype.Service;


public interface KafkaProducerService {

    void sendDeviceInformation(DeviceInformation deviceInformation);
}
