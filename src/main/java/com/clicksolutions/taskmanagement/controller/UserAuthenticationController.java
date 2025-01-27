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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("v1/api/auth")
@RequiredArgsConstructor
public class UserAuthenticationController {
	
	@Autowired
	AuthenticationService authenticationService;
	
	@PostMapping("/signup")
	public ResponseEntity<User> signup(@RequestBody SignUpDto signUpDto){
		
		return ResponseEntity.ok(authenticationService.signup(signUpDto));
	}
	
	@PostMapping("/login")
	public ResponseEntity<JwtAuthenticationResponse> sigin(@RequestBody LoginDto loginDto){
		return ResponseEntity.ok(authenticationService.sigin(loginDto));
	}

}
