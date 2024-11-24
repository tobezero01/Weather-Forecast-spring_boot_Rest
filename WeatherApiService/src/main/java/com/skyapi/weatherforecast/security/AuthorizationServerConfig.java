package com.skyapi.weatherforecast.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.skyapi.weatherforecast.clientApp.ClientAppRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.Instant;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class AuthorizationServerConfig {

    @Value("${app.security.jwt.issuer}")
    private String issuerName;

    @Value("${app.security.jwt.access-token.expiration}")
    private int accessTokenExpirationTime;

    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(ClientAppRepository clientAppRepository) {
        return new RegisteredClientRepository() {
            @Override
            public RegisteredClient findByClientId(String clientId) {
                return clientAppRepository.findByClientId(clientId).map(clientApp ->
                        RegisteredClient.withId(clientApp.getId().toString())
                                .clientId(clientApp.getClientId())
                                .clientSecret(clientApp.getClientSecret())
                                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                                .scope(clientApp.getRole().toString())  // Sử dụng role từ AppRole
                                .build()
                ).orElse(null);
            }

            @Override
            public void save(RegisteredClient registeredClient) {}

            @Override
            public RegisteredClient findById(String id) {
                return null;
            }
        };
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return (context) -> {
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                JwtClaimsSet.Builder claims = context.getClaims();
                claims.issuer(issuerName);
                claims.expiresAt(Instant.now().plus(Duration.ofMinutes(accessTokenExpirationTime)));

                claims.claims( (claim) -> {
                    claim.put("scope", context.getRegisteredClient().getScopes());
                    claim.put("name", context.getRegisteredClient().getClientName());
                    claim.remove("aud");
                });

            }
        };
    }


}
