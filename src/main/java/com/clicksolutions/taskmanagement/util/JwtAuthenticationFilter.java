package com.clicksolutions.taskmanagement.util;

import java.io.IOException;

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
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	@Autowired
	 JWTService jwtService;
	@Autowired
	 UserService userService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		final String authHeader = request.getHeader("Authorization");
		System.out.println(authHeader);
		final String jwt;
		final String userEmail;
		
		if(StringUtils.isEmpty(authHeader) || !org.apache.commons.lang3.StringUtils.startsWith(authHeader, "Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		jwt = authHeader.substring(7);
		userEmail= jwtService.extractUsername(jwt);
		
		if(StringUtils.isNotEmpty(userEmail) && SecurityContextHolder.getContext().getAuthentication() ==null) {
			UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail);
			if(jwtService.isTokenValid(jwt, userDetails)) {
				
				SecurityContext securityContext = SecurityContextHolder.getContext();
				
				UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				
				token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				securityContext.setAuthentication(token);
				
			}
		}
		
		filterChain.doFilter(request, response);
		
	}

}

