package com.clicksolutions.taskmanagement.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public interface JWTService {

	String extractUsername(String token);
	
	String generateToken(UserDetails userDetails);
	
	public boolean isTokenValid(String token, UserDetails userDetails);
}
