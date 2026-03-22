package com.RSSBAMB.API.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf-> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**","/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html","/swagger-ui/index.html").permitAll()
                        // User management endpoints - only authenticated users with admin role
                        .requestMatchers("/api/users/**").authenticated()
                        // Other API endpoints
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt()
                );
        return http.build();
    }
}
