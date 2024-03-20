package com.kafka.springjwt.contoller;

import com.kafka.springjwt.entity.DeviceInformation;
import com.kafka.springjwt.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SpringJwtController {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @PostMapping("/devices")
    public ResponseEntity<String> sendDeviceInformationToKafka(@RequestBody DeviceInformation deviceInformation) {
        kafkaProducerService.sendDeviceInformation(deviceInformation);
        return ResponseEntity.ok("Device information sent to Kafka topic successfully");
    }
}
