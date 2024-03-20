package com.kafka.springjwt.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
public class JwtResponse {
    String token;

    public JwtResponse() {

    }
    public JwtResponse(String token) {
        this.token = token;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }



}
