package com.clicksolutions.taskmanagement.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RateLimitingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

    private final LimitRequestRate limitRequestRate = new LimitRequestRate(10, 60_000); 

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = httpRequest.getRemoteAddr(); 

        logger.debug("Received request from client IP: {}", clientIp);

        if (limitRequestRate.isAllowed(clientIp)) {
            logger.info("Request allowed for client IP: {}", clientIp);
            try {
                chain.doFilter(request, response);
            } catch (Exception e) {
                logger.error("Error processing request for client IP: {}", clientIp, e);
                throw e;
            }
        } else {
            logger.warn("Rate limit exceeded for client IP: {}", clientIp);
            httpResponse.setStatus(HttpServletResponse.SC_REQUEST_TIMEOUT);
            httpResponse.getWriter().write("Rate limit exceeded. Please try again later.");
        }
    }
}
