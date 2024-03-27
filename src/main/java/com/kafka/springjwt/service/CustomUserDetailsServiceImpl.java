package com.kafka.springjwt.service;

import com.kafka.springjwt.entity.RolesEntity;
import com.kafka.springjwt.exceptions.ExceptionHandlerController;
import com.kafka.springjwt.repository.TokenGenerationRepositoryImpl;
import com.kafka.springjwt.repository.UserRegisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private TokenGenerationRepositoryImpl tokenGenerationRepository;
    @Autowired
    private ExceptionHandlerController exceptionHandlerController;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        RolesEntity rolesEntity = tokenGenerationRepository.getDetailByUsername(username);

        if (rolesEntity == null) {
            exceptionHandlerController.handleRuntimeException(new UsernameNotFoundException("User not found with username: " + username), null);

        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(rolesEntity.getRole())); // Assuming role is stored in RolesEntity

        return new User(rolesEntity.getUsername(), rolesEntity.getPassword(), authorities);
    }
}
