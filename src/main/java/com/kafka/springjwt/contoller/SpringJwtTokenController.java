package com.kafka.springjwt.contoller;


import com.kafka.springjwt.entity.JwtEntity;
import com.kafka.springjwt.entity.JwtResponse;
import com.kafka.springjwt.entity.RolesEntity;
import com.kafka.springjwt.service.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringJwtTokenController {


    @Autowired
    private JwtTokenService jwtTokenService;

    @PostMapping(value = "/token")
    public ResponseEntity<Object> getToken(@RequestBody JwtEntity jwtEntity) {
        return jwtTokenService.authenticateAndGetToken(jwtEntity.getUserName(), jwtEntity.getPassword());
    }

}
