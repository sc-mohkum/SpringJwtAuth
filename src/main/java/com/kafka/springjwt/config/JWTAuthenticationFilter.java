package com.kafka.springjwt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.springjwt.entity.RolesEntity;
import com.kafka.springjwt.exceptions.InavlidTokenAuthentication;
import com.kafka.springjwt.exceptions.InvalidCredentialsException;
import com.kafka.springjwt.repository.TokenGenerationRepository;
import com.kafka.springjwt.scheduler.KafkaMongoDBScheduler;
import com.kafka.springjwt.service.CustomUserDetailsServiceImpl;
import com.kafka.springjwt.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;

@Configuration
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    @Autowired
    CustomUserDetailsServiceImpl customUserDetailsServcie;
    @Autowired
    JwtUtils jwtUtils;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestTokenHeader=request.getHeader("Authorization");
        String userName=null;
        String jwtToken=null;

        if(requestTokenHeader!=null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);

            try {
                userName = this.jwtUtils.getUsernameFromToken(jwtToken);

            } catch (Exception e) {
                e.printStackTrace();
            }


            UserDetails userDetails = this.customUserDetailsServcie.loadUserByUsername(userName);
            if (userName != null && hasAdminRole(userName)&&SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                logger.info("Token is not validated");
            }
        }
        filterChain.doFilter(request,response);
    }
    private boolean hasAdminRole(String userName) {
        UserDetails userDetails = customUserDetailsServcie.loadUserByUsername(userName);
        return userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("Admin"));
    }
}
