package com.clicksolutions.taskmanagement.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.clicksolutions.taskmanagement.service.JWTService;
import com.clicksolutions.taskmanagement.service.UserService;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        logger.debug("Authorization header: {}", authHeader);

        final String jwt;
        final String userEmail;

        if (StringUtils.isEmpty(authHeader) || !org.apache.commons.lang3.StringUtils.startsWith(authHeader, "Bearer ")) {
            logger.info("Authorization header is missing or does not start with 'Bearer'. Skipping authentication.");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        logger.debug("Extracted JWT: {}", jwt);

        userEmail = jwtService.extractUsername(jwt);
        logger.debug("Extracted username from JWT: {}", userEmail);

        if (StringUtils.isNotEmpty(userEmail) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail);
            logger.debug("Loaded user details for email: {}", userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                logger.info("JWT is valid for user: {}", userEmail);

                SecurityContext securityContext = SecurityContextHolder.getContext();

                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                securityContext.setAuthentication(token);
                logger.info("User {} successfully authenticated and SecurityContext updated.", userEmail);
            } else {
                logger.warn("Invalid JWT for user: {}", userEmail);
            }
        }

        filterChain.doFilter(request, response);
        logger.debug("Completed filtering for the request.");
    }
}
