package com.kafka.springjwt.contoller;


import com.kafka.springjwt.entity.JwtEntity;
import com.kafka.springjwt.entity.JwtResponse;
import com.kafka.springjwt.service.CustomUserDetailsServcie;
import com.kafka.springjwt.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringJwtTokenController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailsServcie customUserDetailsServcie;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private JwtResponse jwtResponse;

    @PostMapping(value = "/token")
    public ResponseEntity<?> generateToken(@RequestBody JwtEntity jwtEntity) throws Exception {
        System.out.println(jwtEntity);

        try {

            this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtEntity.getUserName(), jwtEntity.getPassword()));
        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
            throw new Exception("Bad Credentials");
        }

        UserDetails userDetails = this.customUserDetailsServcie.loadUserByUsername(jwtEntity.getUserName());
        String token = this.jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));

    }
}
