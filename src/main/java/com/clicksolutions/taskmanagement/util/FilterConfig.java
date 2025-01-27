package com.clicksolutions.taskmanagement.util;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

	    @Bean
	    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilter() {
	        FilterRegistrationBean<RateLimitingFilter> registrationBean = new FilterRegistrationBean<>();
	        registrationBean.setFilter(new RateLimitingFilter());
	        registrationBean.addUrlPatterns("/api/tasks/*"); 
	        return registrationBean;
	    }
	}


