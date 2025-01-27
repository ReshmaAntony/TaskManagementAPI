package com.clicksolutions.taskmanagement.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    private static final Logger logger = LoggerFactory.getLogger(FilterConfig.class);

    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilter() {
        logger.info("Registering RateLimitingFilter.");

        FilterRegistrationBean<RateLimitingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RateLimitingFilter());
        registrationBean.addUrlPatterns("/api/tasks/*"); 

        logger.info("RateLimitingFilter registered for URL patterns: /api/tasks/*");

        return registrationBean;
    }
}
