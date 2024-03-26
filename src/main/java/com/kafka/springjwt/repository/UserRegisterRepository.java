package com.kafka.springjwt.repository;

import com.kafka.springjwt.entity.RolesEntity;
import org.springframework.stereotype.Repository;

public interface UserRegisterRepository {
    boolean existsByUsername(String username);

    void saveUserWithEncryptedPassword(RolesEntity rolesEntity);

    String encryptPassword(String password);


}
