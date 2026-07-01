package com.banking.accountservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class WebClientConfigTest {

    private final WebClientConfig config = new WebClientConfig();

    @Test
    void shouldCreateWebClientBuilder() {

        WebClient.Builder builder = config.webClientBuilder();

        assertNotNull(builder);
    }

    @Test
    void shouldCreateWebClient() {

        WebClient.Builder builder = config.webClientBuilder();

        WebClient webClient = config.webClient(builder);

        assertNotNull(webClient);
    }
}