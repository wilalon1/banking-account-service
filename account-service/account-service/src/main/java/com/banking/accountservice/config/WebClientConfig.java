package com.banking.accountservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @org.springframework.cloud.client.loadbalancer.LoadBalanced
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}