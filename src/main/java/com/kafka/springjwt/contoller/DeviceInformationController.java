package com.kafka.springjwt.contoller;

import com.kafka.springjwt.entity.DeviceInformation;
import com.kafka.springjwt.service.KafkaProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/api")
@RolesAllowed({"Admin"})
public class DeviceInformationController {

    private final Logger logger = LoggerFactory.getLogger(DeviceInformationController.class);

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @PostMapping("/devices")

    public ResponseEntity<String> sendDeviceInformationToKafka(@RequestBody DeviceInformation deviceInformation) {
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
        if (!"Admin".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to access this resource");
        }

        kafkaProducerService.sendDeviceInformation(deviceInformation);
        return ResponseEntity.ok("Device information sent to Kafka topic successfully");
    }
}
