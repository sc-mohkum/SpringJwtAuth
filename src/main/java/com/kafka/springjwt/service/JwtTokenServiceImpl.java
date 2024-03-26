package com.kafka.springjwt.service;

import com.kafka.springjwt.entity.RolesEntity;
import com.kafka.springjwt.exceptions.InvalidCredentialsException;
import com.kafka.springjwt.exceptions.UserNotFoundException;
import com.kafka.springjwt.repository.TokenGenerationRepository;
import com.kafka.springjwt.repository.UserRegisterRepository;
import com.kafka.springjwt.utils.JwtUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtTokenServiceImpl implements JwtTokenService{
    @Autowired
    private TokenGenerationRepository tokenRepository;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    CustomUserDetailsServiceImpl customUserDetailsService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRegisterRepository userRegisterRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private TokenGenerationRepository tokenGenerationRepository;

    public String authenticateAndGetToken(String username, String password) {

        RolesEntity user = tokenRepository.getDetailByUsername(username);
        String encryptPassword=userRegisterRepository.encryptPassword(password);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        if(tokenGenerationRepository.checkCredentials(username,encryptPassword)){
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            return jwtUtils.generateToken(userDetails);
        }
        return null;
    }
}
