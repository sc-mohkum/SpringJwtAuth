package com.kafka.springjwt.repository;

import com.kafka.springjwt.entity.RolesEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Repository
public class UserRegisterRepositoryImpl implements UserRegisterRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String AES = "AES";
    private static final String SECRET_KEY = "My-SecretKey1234";
    private static final String ROLES_COLLECTION = "Roles";

    @Override
    public boolean existsByUsername(String username) {
        return mongoTemplate.exists(Query.query(Criteria.where("username").is(username)), RolesEntity.class);
    }

    @Override
    public void saveUserWithEncryptedPassword(RolesEntity rolesEntity) {
        String encryptedPassword = encryptPassword(rolesEntity.getPassword());
        rolesEntity.setPassword(encryptedPassword);
        mongoTemplate.save(rolesEntity,ROLES_COLLECTION);
    }


    @Override
    public String encryptPassword(String password) {
        try {
            Key secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), AES);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(password.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
