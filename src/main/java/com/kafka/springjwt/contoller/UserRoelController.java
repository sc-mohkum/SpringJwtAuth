package com.kafka.springjwt.contoller;

import com.kafka.springjwt.entity.RolesEntity;
import com.kafka.springjwt.exceptions.DuplicateUsernameException;
import com.kafka.springjwt.service.UserRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRoelController {

    @Autowired
    UserRegisterService userRegisterService;

    @PostMapping("/register")
    ResponseEntity<String> registerUser(@RequestBody RolesEntity rolesEntity) throws DuplicateUsernameException {

        userRegisterService.registerUser(rolesEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }
}
