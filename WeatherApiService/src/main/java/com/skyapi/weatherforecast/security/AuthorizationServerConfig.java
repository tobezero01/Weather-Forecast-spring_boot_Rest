package com.skyapi.weatherforecast.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.skyapi.weatherforecast.clientApp.ClientAppRepository;
import com.skyapi.weatherforecast.common.ClientApp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Optional;

@Configuration
public class AuthorizationServerConfig {
    private final RsaKeyProperties rsaKeyProperties;

    @Value("${app.security.jwt.issuer}")
    private String issuerName;
    @Value("${app.security.jwt.access-token.expiration}")
    private int accessTokenExpirationTime;

    public AuthorizationServerConfig(RsaKeyProperties rsaKeyProperties) {
        this.rsaKeyProperties = rsaKeyProperties;
    }

    @Bean
    public JwtDecoder jwtDecoder() throws Exception {
        return NimbusJwtDecoder.withPublicKey(rsaKeyProperties.publicKey()).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() throws Exception {
        JWK jwk = new RSAKey.Builder(rsaKeyProperties.publicKey())
                .privateKey(rsaKeyProperties.privateKey())
                .build();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public RegisteredClientRepository registeredClientRepository(ClientAppRepository clientAppRepository) {
        return new RegisteredClientRepository() {
            @Override
            public void save(RegisteredClient registeredClient) {
                // Save logic if needed
            }

            @Override
            public RegisteredClient findById(String id) {
                return null;
            }

            @Override
            public RegisteredClient findByClientId(String clientId) {
                Optional<ClientApp> result = clientAppRepository.findByClientId(clientId);
                if (result.isEmpty()) return null;
                ClientApp clientApp = result.get();
                return RegisteredClient.withId(clientApp.getId().toString())
                        .clientName(clientApp.getName())
                        .clientId(clientApp.getClientId())
                        .clientSecret(clientApp.getClientSecret()) // mã hóa secret
                        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                        .build();
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(httpSecurity);
        httpSecurity.csrf(cs -> cs.disable());
        return httpSecurity.build();
    }

    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return (context -> {
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                RegisteredClient client = context.getRegisteredClient();
                JwtClaimsSet.Builder builder = context.getClaims();

                builder.issuer(issuerName);
                builder.claims((claims) -> {
                   claims.put("scope", client.getScopes());
                   claims.put("name", client.getClientName());
                   claims.remove("aud");
                });
            }
        });
    }


}
