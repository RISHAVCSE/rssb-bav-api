package com.RSSBAMB.API.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Keycloak Admin Client
 * Provides Keycloak instance for admin operations
 */
@Configuration
public class KeycloakProvider {

    @Value("${keycloak.server-url:http://localhost:14082}")
    private String serverUrl;

    @Value("${keycloak.realm:springboot-test}")
    private String realm;

    @Value("${keycloak.client-id:admin-cli}")
    private String clientId;

    @Value("${keycloak.client-secret:}")
    private String clientSecret;

    @Value("${keycloak.username:admin}")
    private String username;

    @Value("${keycloak.password:admin}")
    private String password;

    /**
     * Creates and configures the Keycloak Admin Client Bean
     * @return Keycloak admin client instance
     */
    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType("client_credentials")
                .build();
    }
}

