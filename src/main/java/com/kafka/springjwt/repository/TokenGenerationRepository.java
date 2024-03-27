package com.kafka.springjwt.repository;

import com.kafka.springjwt.entity.RolesEntity;

public interface TokenGenerationRepository {
    RolesEntity getDetailByUsername(String username);

    boolean checkCredentials(String username, String password);

    String getRoleByUsername(String username);
}
