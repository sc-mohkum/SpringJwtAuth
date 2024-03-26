package com.kafka.springjwt.service;

import com.kafka.springjwt.entity.RolesEntity;
import com.kafka.springjwt.exceptions.DuplicateUsernameException;

public interface UserRegisterService {
    String registerUser(RolesEntity rolesEntity) throws DuplicateUsernameException;
}
