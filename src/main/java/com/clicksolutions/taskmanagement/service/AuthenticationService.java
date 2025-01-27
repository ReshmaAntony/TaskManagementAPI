package com.clicksolutions.taskmanagement.service;

import org.springframework.stereotype.Service;

import com.clicksolutions.taskmanagement.dto.JwtAuthenticationResponse;
import com.clicksolutions.taskmanagement.dto.LoginDto;
import com.clicksolutions.taskmanagement.dto.SignUpDto;
import com.clicksolutions.taskmanagement.entity.User;


@Service
public interface AuthenticationService {
	
	User signup(SignUpDto signUpDto);
	JwtAuthenticationResponse sigin(LoginDto loginDto);
}
