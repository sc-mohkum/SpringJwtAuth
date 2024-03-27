package com.kafka.springjwt.service;

import com.kafka.springjwt.entity.JwtResponse;
import com.kafka.springjwt.entity.RolesEntity;
import com.kafka.springjwt.exceptions.ExceptionHandlerController;
import com.kafka.springjwt.exceptions.InvalidCredentialsException;
import com.kafka.springjwt.exceptions.UserNotFoundException;
import com.kafka.springjwt.repository.TokenGenerationRepository;
import com.kafka.springjwt.repository.UserRegisterRepository;
import com.kafka.springjwt.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private ExceptionHandlerController exceptionHandlerController;
    @Autowired
    private JwtResponse jwtResponse;

    public ResponseEntity<Object> authenticateAndGetToken(String username, String password) {

        RolesEntity user = tokenRepository.getDetailByUsername(username);
        String encryptPassword=userRegisterRepository.encryptPassword(password);
        if (user == null ) {

            return exceptionHandlerController.handleUserNotFoundException(new UserNotFoundException("User not found"), null);
        }
        if(!tokenGenerationRepository.checkCredentials(username,encryptPassword)){
            return exceptionHandlerController.handleInvalidCredentialsException(new InvalidCredentialsException("Invalid username or password"), null);
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        String token = jwtUtils.generateToken(userDetails);
        jwtResponse.setToken(token);
        return ResponseEntity.ok(jwtResponse);
    }
    private static final String ROLES_CLAIM_KEY = "roles";

    public boolean hasAdminRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("Admin"::equals);
    }

    public List<String> getRoles(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

}
