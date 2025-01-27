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

@Service
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
		User user = new User();
		user.setEmail(signUpDto.getEmail());
		user.setUsername(signUpDto.getName());
		user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
		user.setRole(Role.USER);

		return userRepository.save(user);
	}

	@Override
	public JwtAuthenticationResponse sigin(LoginDto loginDto) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				loginDto.getEmail(), loginDto.getPassword()));
		
		var user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(
				() -> new IllegalArgumentException("Invalid email or password"));
		var jwt = jwtService.generateToken((UserDetails) user);

		JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
		
		jwtAuthenticationResponse.setToken(jwt);

		
		return jwtAuthenticationResponse;
	}

}
