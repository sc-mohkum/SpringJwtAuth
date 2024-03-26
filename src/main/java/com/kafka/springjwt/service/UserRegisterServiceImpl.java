package com.kafka.springjwt.service;

import com.kafka.springjwt.entity.RolesEntity;
import com.kafka.springjwt.exceptions.DuplicateUsernameException;
import com.kafka.springjwt.repository.UserRegisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRegisterServiceImpl implements UserRegisterService {

  @Autowired
  private UserRegisterRepository userRegisterRepository;
    @Override
    public String registerUser(RolesEntity rolesEntity) throws DuplicateUsernameException {
        if (userRegisterRepository.existsByUsername(rolesEntity.getUsername())) {
            throw new DuplicateUsernameException("Username already exists");
        } else {
            userRegisterRepository.saveUserWithEncryptedPassword(rolesEntity);
            return "Details Added";
        }
    }
}
