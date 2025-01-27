package com.clicksolutions.taskmanagement.util;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RateLimitingFilter implements Filter {

    private final LimitRequestRate limitRequestRate = new LimitRequestRate(10, 60_000); 
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = httpRequest.getRemoteAddr(); 
        if (limitRequestRate.isAllowed(clientIp)) {
            try {
                chain.doFilter(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            httpResponse.setStatus(HttpServletResponse.SC_REQUEST_TIMEOUT);
            httpResponse.getWriter().write("Rate limit exceeded. Please try again later.");
        }
    }
}
