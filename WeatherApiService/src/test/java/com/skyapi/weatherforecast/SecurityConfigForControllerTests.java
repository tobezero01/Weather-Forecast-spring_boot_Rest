package com.skyapi.weatherforecast;

import jakarta.persistence.Column;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfigForControllerTests {

    @Bean
    SecurityFilterChain securityFilterChain1(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        httpSecurity.csrf(cs -> cs.disable());

        return httpSecurity.build();
    }

}
