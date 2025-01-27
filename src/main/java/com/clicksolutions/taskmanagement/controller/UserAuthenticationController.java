package com.clicksolutions.taskmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clicksolutions.taskmanagement.dto.JwtAuthenticationResponse;
import com.clicksolutions.taskmanagement.dto.LoginDto;
import com.clicksolutions.taskmanagement.dto.SignUpDto;
import com.clicksolutions.taskmanagement.entity.User;
import com.clicksolutions.taskmanagement.service.AuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("v1/api/auth")
@RequiredArgsConstructor
@Slf4j
public class UserAuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

    @Operation(
        summary = "User Signup",
        description = "Registers a new user with the provided details.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "User successfully signed up.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid input data.",
                content = @Content(mediaType = "application/json")
            )
        }
    )
    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody SignUpDto signUpDto) {
        log.info("Signup request received for email: {}", signUpDto.getEmail());
        User user = authenticationService.signup(signUpDto);
        log.info("User successfully signed up with email: {}", signUpDto.getEmail());
        return ResponseEntity.ok(user);
    }

    @Operation(
        summary = "User Login",
        description = "Authenticates a user with the provided credentials and returns a JWT token.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Login successful.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtAuthenticationResponse.class))
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Unauthorized. Invalid username or password.",
                content = @Content(mediaType = "application/json")
            )
        }
    )
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> sigin(@RequestBody LoginDto loginDto) {
        log.info("Login request received for user: {}", loginDto.getUsername());
        JwtAuthenticationResponse response = authenticationService.sigin(loginDto);
        log.info("User successfully logged in with user: {}", loginDto.getUsername());
        return ResponseEntity.ok(response);
    }
}
