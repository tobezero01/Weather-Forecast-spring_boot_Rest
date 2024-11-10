package com.skyapi.weatherforecast.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties
public record RsaKeyProperties(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
}
