package com.kafka.springjwt.contoller;

import com.kafka.springjwt.entity.RolesEntity;
import com.kafka.springjwt.exceptions.DuplicateUsernameException;
import com.kafka.springjwt.exceptions.ExceptionHandlerController;
import com.kafka.springjwt.exceptions.InvalidCredentialsException;
import com.kafka.springjwt.exceptions.UserNotFoundException;
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
    private UserRegisterService userRegisterService;
    @Autowired
    private ExceptionHandlerController exceptionHandlerController;

    @PostMapping("/register")
    ResponseEntity<?> registerUser(@RequestBody RolesEntity rolesEntity) throws DuplicateUsernameException {


        // Check if the username is null or empty
        if (rolesEntity.getUsername() == null || rolesEntity.getUsername().trim().isEmpty()) {
            return exceptionHandlerController.handleNullUserNamePasswordException(new IllegalArgumentException("UserName Can Not Null "),null);
        }

        // Check if the password is null or empty
        if (rolesEntity.getPassword() == null || rolesEntity.getPassword().trim().isEmpty()) {
            return exceptionHandlerController.handleNullUserNamePasswordException(new IllegalArgumentException("Password Can Not Null "),null);
        }

        userRegisterService.registerUser(rolesEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }
}
