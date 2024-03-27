package com.kafka.springjwt.contoller;

import com.kafka.springjwt.entity.DeviceInformation;
import com.kafka.springjwt.exceptions.ExceptionHandlerController;
import com.kafka.springjwt.exceptions.InvalidCredentialsException;
import com.kafka.springjwt.service.JwtTokenService;
import com.kafka.springjwt.service.KafkaProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.GrantedAuthority;
import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RolesAllowed({"Admin"})
public class DeviceInformationController {

    private final Logger logger = LoggerFactory.getLogger(DeviceInformationController.class);

    @Autowired
    private KafkaProducerService kafkaProducerService;
    @Autowired
    private JwtTokenService jwtTokenService;
    @Autowired
    private ExceptionHandlerController exceptionHandlerController;

    @PostMapping("/devices")

    public ResponseEntity<?> sendDeviceInformationToKafka(@RequestBody DeviceInformation deviceInformation) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !jwtTokenService.hasAdminRole(authentication)) {
            return exceptionHandlerController.handleInvalidCredentialsException(new InvalidCredentialsException("Do not have permission to access. Roles: " + jwtTokenService.getRoles(authentication)), null);
        }

        kafkaProducerService.sendDeviceInformation(deviceInformation);
        return ResponseEntity.ok("Device information sent to Kafka topic successfully");
    }


}


