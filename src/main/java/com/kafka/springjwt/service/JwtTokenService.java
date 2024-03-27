package com.kafka.springjwt.service;

import com.kafka.springjwt.entity.JwtResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface JwtTokenService {
 ResponseEntity<Object> authenticateAndGetToken(String username, String password);

 boolean hasAdminRole(Authentication authentication);

 List<String> getRoles(Authentication token);


}
