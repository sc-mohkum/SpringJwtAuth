package com.kafka.springjwt.repository;

import com.kafka.springjwt.entity.RolesEntity;
import com.kafka.springjwt.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class TokenGenerationRepositoryImpl implements TokenGenerationRepository{

      @Autowired
         private MongoTemplate mongoTemplate;
    @Override
    public RolesEntity getDetailByUsername(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        RolesEntity user = mongoTemplate.findOne(query, RolesEntity.class);

        if (user != null) {

            return user;
        }

        throw new UserNotFoundException("Details Not Found");
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username).and("password").is(password));
        return mongoTemplate.exists(query, RolesEntity.class);
    }
}
