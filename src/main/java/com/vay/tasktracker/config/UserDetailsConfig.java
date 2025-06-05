package com.vay.tasktracker.config;

import com.vay.tasktracker.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class UserDetailsConfig {

    @Bean
    @Primary
    public UserDetailsService userDetailsService(UserService userService) {
        return userService;
    }
} 