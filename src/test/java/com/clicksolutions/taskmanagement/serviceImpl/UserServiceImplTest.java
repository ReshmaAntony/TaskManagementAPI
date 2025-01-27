package com.clicksolutions.taskmanagement.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.clicksolutions.taskmanagement.entity.User;
import com.clicksolutions.taskmanagement.repository.UserRepository;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
       
        String username = "testuser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPassword("testpassword");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

      
        UserDetailsService userDetailsService = userServiceImpl.userDetailsService();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

       
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsUsernameNotFoundException() {
        
        String username = "nonexistentuser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        UserDetailsService userDetailsService = userServiceImpl.userDetailsService();
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));

        verify(userRepository, times(1)).findByUsername(username);
    }
}
