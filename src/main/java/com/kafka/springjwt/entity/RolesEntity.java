package com.kafka.springjwt.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "Roles")
@Data
public class RolesEntity {

         private String username;
         private String password;
         private String role;
    }



