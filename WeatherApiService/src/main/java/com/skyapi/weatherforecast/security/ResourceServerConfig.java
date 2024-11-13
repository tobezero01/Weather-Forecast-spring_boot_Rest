package com.skyapi.weatherforecast.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.stream.Collectors;

@Configuration
public class ResourceServerConfig {

    private static final String LOCATION_ENDPOINT_PATTERN = "/v1/location/**";
    private static final String REALTIME_ENDPOINT_PATTERN = "/v1/realtime/**";
    private static final String HOURLY_ENDPOINT_PATTERN = "/v1/hourly/**";
    private static final String DAILY_ENDPOINT_PATTERN = "/v1/daily/**";
    private static final String FULL_ENDPOINT_PATTERN = "/v1/full/**";
    private static final String SCOPE_READER = "SCOPE_READER";
    private static final String SCOPE_SYSTEM = "SCOPE_SYSTEM";
    private static final String SCOPE_UPDATER = "SCOPE_UPDATER";

    private static String[] getScopeFor(String... scopes) {
        return scopes;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(cs -> cs.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers(HttpMethod.GET, LOCATION_ENDPOINT_PATTERN).hasAnyAuthority(getScopeFor(SCOPE_READER, SCOPE_SYSTEM))
                        .requestMatchers(HttpMethod.POST, LOCATION_ENDPOINT_PATTERN).hasAuthority(SCOPE_SYSTEM)
                        .requestMatchers(HttpMethod.PUT, LOCATION_ENDPOINT_PATTERN).hasAuthority(SCOPE_SYSTEM)
                        .requestMatchers(HttpMethod.DELETE, LOCATION_ENDPOINT_PATTERN).hasAuthority(SCOPE_SYSTEM)

                        .requestMatchers(HttpMethod.GET, HOURLY_ENDPOINT_PATTERN).hasAnyAuthority(getScopeFor(SCOPE_SYSTEM, SCOPE_UPDATER, SCOPE_READER))
                        .requestMatchers(HttpMethod.PUT, HOURLY_ENDPOINT_PATTERN).hasAnyAuthority(getScopeFor(SCOPE_SYSTEM, SCOPE_UPDATER))

                        .requestMatchers(HttpMethod.GET, DAILY_ENDPOINT_PATTERN).hasAnyAuthority(getScopeFor(SCOPE_SYSTEM, SCOPE_UPDATER, SCOPE_READER))
                        .requestMatchers(HttpMethod.PUT, DAILY_ENDPOINT_PATTERN).hasAnyAuthority(getScopeFor(SCOPE_SYSTEM, SCOPE_UPDATER))

                        .requestMatchers(HttpMethod.GET, FULL_ENDPOINT_PATTERN).hasAnyAuthority(getScopeFor(SCOPE_SYSTEM, SCOPE_UPDATER, SCOPE_READER))
                        .requestMatchers(HttpMethod.PUT, FULL_ENDPOINT_PATTERN).hasAnyAuthority(getScopeFor(SCOPE_SYSTEM, SCOPE_UPDATER))

                        .requestMatchers(HttpMethod.GET, REALTIME_ENDPOINT_PATTERN).hasAnyAuthority(getScopeFor(SCOPE_SYSTEM, SCOPE_UPDATER, SCOPE_READER))
                        .requestMatchers(HttpMethod.PUT, REALTIME_ENDPOINT_PATTERN).hasAnyAuthority(getScopeFor(SCOPE_SYSTEM, SCOPE_UPDATER))
                        .anyRequest().authenticated()
                );
        return httpSecurity.build();
    }

}
