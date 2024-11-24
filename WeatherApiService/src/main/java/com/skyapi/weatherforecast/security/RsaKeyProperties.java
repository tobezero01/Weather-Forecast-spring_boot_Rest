package com.skyapi.weatherforecast.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@ConfigurationProperties(prefix = "rsa")
public class RsaKeyProperties {

    private static final Logger logger = LoggerFactory.getLogger(RsaKeyProperties.class);

    private Resource privateKey;
    private Resource publicKey;

    public void setPrivateKey(Resource privateKey) {
        this.privateKey = privateKey;
    }

    public void setPublicKey(Resource publicKey) {
        this.publicKey = publicKey;
    }

    @Bean
    public RSAPublicKey publicKey() throws Exception {
        try {
            return loadPublicKey(publicKey);
        } catch (Exception e) {
            logger.error("Failed to load public key", e);
            throw e;
        }
    }

    @Bean
    public RSAPrivateKey privateKey() throws Exception {
        try {
            return loadPrivateKey(privateKey);
        } catch (Exception e) {
            logger.error("Failed to load private key", e);
            throw e;
        }
    }

    private RSAPublicKey loadPublicKey(Resource resource) throws Exception {

        String key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwbiH+tI0YlKSLImxBTwv" +
                "IoL/YPMSxVFFaT/JfAmfDCS6K/0JVSGANCsUMcZHEX4f7CMIvSLfrVLlwK19JK5H" +
                "jbdauSBZtJuuGvRfnEMIRkMvzoSvP+CTyf/AdMVxI7ouZ2PHWOV28ADqPG5ZMwM6" +
                "N+jZa2BAvtWH2AU5HS8WtIKjhw4nadrwrbVT2WPAtBobZxIicGsVAkdEvIH23KYA" +
                "85hrlQtXFktZgA2nNpw47H/tGE7ZoDj9aRFQNmPO/nedZy9BvrdOP/9EOs4Fg32K" +
                "5sPFPZueNcTi6PaVOrWa3t8qdG4lTcBjMtUY4ABm31aIOsC2z6klGEbCa9EgWzMY" +
                "GQIDAQAB";
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(spec);

    }

    private RSAPrivateKey loadPrivateKey(Resource resource) throws Exception {
        try (InputStream inputStream = resource.getInputStream()) {
            String key = new String(inputStream.readAllBytes())
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(spec);
        }
    }
}
