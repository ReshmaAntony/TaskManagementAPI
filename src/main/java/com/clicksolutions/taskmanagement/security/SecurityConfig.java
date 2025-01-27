package com.clicksolutions.taskmanagement.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.clicksolutions.taskmanagement.entity.Role;
import com.clicksolutions.taskmanagement.service.UserService;
import com.clicksolutions.taskmanagement.util.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring security filter chain...");
        
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(request -> {
                log.info("Setting up authorization rules...");
                
                request.requestMatchers("/v1/api/auth/**").permitAll();
                request.requestMatchers("/v1/api/tasks/**").permitAll();
                request.requestMatchers("/api/user").hasAnyAuthority(Role.USER.name());
                request.anyRequest().authenticated();
            })
            .sessionManagement(manager -> {
                log.info("Configuring stateless session management...");
                manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            })
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        log.info("Security filter chain configured successfully.");
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("Configuring web security to ignore specific paths...");
        return (web) -> web.ignoring()
            .requestMatchers(
                "/resources/**",
                "/static/**",
                "/public/**",
                "/webui/**",
                "/h2-console/**",
                "/configuration/**",
                "/swagger-ui/**",
                "/api-docs/**"
            );
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        log.info("Creating DaoAuthenticationProvider...");
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService.userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        log.info("DaoAuthenticationProvider configured successfully.");
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("Configuring BCryptPasswordEncoder...");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        log.info("Retrieving AuthenticationManager from configuration...");
        return config.getAuthenticationManager();
    }
}
