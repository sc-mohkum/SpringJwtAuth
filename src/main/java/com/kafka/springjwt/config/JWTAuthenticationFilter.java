package com.kafka.springjwt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.springjwt.entity.RolesEntity;
import com.kafka.springjwt.exceptions.ExceptionHandlerController;
import com.kafka.springjwt.exceptions.InavlidTokenAuthentication;
import com.kafka.springjwt.exceptions.InvalidCredentialsException;
import com.kafka.springjwt.repository.TokenGenerationRepository;
import com.kafka.springjwt.scheduler.KafkaMongoDBScheduler;
import com.kafka.springjwt.service.CustomUserDetailsServiceImpl;
import com.kafka.springjwt.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
import org.springframework.security.core.GrantedAuthority;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
    private static final String secret = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";

    @Autowired
    private CustomUserDetailsServiceImpl customUserDetailsServcie;
    @Autowired
    private ExceptionHandlerController exceptionHandlerController;
    @Autowired
    private TokenGenerationRepository tokenGenerationRepository;
    @Autowired
    JwtUtils jwtUtils;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestTokenHeader = request.getHeader("Authorization");
        String userName = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);

            try {
                userName = jwtUtils.getUsernameFromToken(jwtToken);
            } catch (ExpiredJwtException e) {
                logger.error("JWT Token has expired", e);
                exceptionHandlerController.handleExpiredJwtException(e, response);
                return;
            } catch (Exception e) {
                logger.error("Invalid JWT Token", e);
                exceptionHandlerController.handleInvalidTokenException(new InavlidTokenAuthentication("Invalid Token"), response);
                return;
            }

            UserDetails userDetails = customUserDetailsServcie.loadUserByUsername(userName);
            List<String> roles = jwtUtils.getRolesFromToken(jwtToken);
            if (userName != null && !roles.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
                Collection<? extends GrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
    }


