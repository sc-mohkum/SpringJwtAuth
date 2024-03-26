package com.kafka.springjwt.service;

public interface JwtTokenService {
 String authenticateAndGetToken(String username, String password);
}
