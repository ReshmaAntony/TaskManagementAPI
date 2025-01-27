package com.clicksolutions.taskmanagement.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.clicksolutions.taskmanagement.dto.JwtAuthenticationResponse;
import com.clicksolutions.taskmanagement.dto.LoginDto;
import com.clicksolutions.taskmanagement.dto.SignUpDto;
import com.clicksolutions.taskmanagement.entity.Role;
import com.clicksolutions.taskmanagement.entity.User;
import com.clicksolutions.taskmanagement.repository.UserRepository;
import com.clicksolutions.taskmanagement.service.AuthenticationService;
import com.clicksolutions.taskmanagement.service.JWTService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JWTService jwtService;

    @Override
    public User signup(SignUpDto signUpDto) {
        log.info("Starting signup process for email: {}", signUpDto.getEmail());
        User user = new User();
        user.setEmail(signUpDto.getEmail());
        user.setUsername(signUpDto.getName());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        
        String role = signUpDto.getRole();
        if (role == null || role.isEmpty()) {
            log.info("No role specified. Defaulting to USER.");
            user.setRole(Role.USER);
        }else {
            try {
                user.setRole(Role.valueOf(role.toUpperCase())); // Convert role to uppercase
                log.info("Role set to: {}", role);
            } catch (IllegalArgumentException e) {
                log.error("Invalid role: {}. Defaulting to USER.", role);
                user.setRole(Role.USER);
            }
        }
        //user.setRole(Role.USER);

        User savedUser = userRepository.save(user);
        log.info("User signed up successfully with email: {}", signUpDto.getEmail());
        return savedUser;
    }

    @Override
    public JwtAuthenticationResponse sigin(LoginDto loginDto) {
        log.info("Starting login process for email: {}", loginDto.getUsername());
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
            );
            log.info("Authentication successful for email: {}", loginDto.getUsername());
        } catch (Exception e) {
            log.error("Authentication failed for email: {}. Error: {}", loginDto.getUsername(), e.getMessage());
            throw new IllegalArgumentException("Invalid email or password");
        }

        var user = userRepository.findByUsername(loginDto.getUsername()).orElseThrow(
            () -> {
                log.error("User not found for email: {}", loginDto.getUsername());
                return new IllegalArgumentException("Invalid email or password");
            }
        );

        try {
            var jwt = jwtService.generateToken((UserDetails) user);
            log.info("JWT generated successfully for email: {}", loginDto.getUsername());

            JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
            jwtAuthenticationResponse.setToken(jwt);
            return jwtAuthenticationResponse;
        } catch (Exception e) {
            log.error("Error generating JWT for email: {}. Error: {}", loginDto.getUsername(), e.getMessage());
            throw new IllegalArgumentException("Problem generating token");
        }
    }
}
