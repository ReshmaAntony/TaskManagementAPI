package com.clicksolutions.taskmanagement.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.clicksolutions.taskmanagement.repository.UserRepository;
import com.clicksolutions.taskmanagement.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetailsService userDetailsService() {
        logger.info("Initializing UserDetailsService.");

        return new UserDetailsService() {

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                logger.info("Attempting to load user by username: {}", username);

                return userRepository.findByUsername(username)
                        .orElseThrow(() -> {
                            logger.error("User not found with username: {}", username);
                            return new UsernameNotFoundException("User not found");
                        });
            }
        };
    }
}
