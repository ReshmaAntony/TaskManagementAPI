package com.clicksolutions.taskmanagement.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.clicksolutions.taskmanagement.repository.UserRepository;
import com.clicksolutions.taskmanagement.service.UserService;


@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetailsService userDetailsService() {
		
		return new UserDetailsService() {
			
			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				
				return userRepository.findByEmail(username)
						.orElseThrow(
								() -> new UsernameNotFoundException("user not found"));	  
			}
		};
		
	}

}


