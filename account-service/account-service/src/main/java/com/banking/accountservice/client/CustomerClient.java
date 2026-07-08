package com.banking.accountservice.client;

import com.banking.accountservice.dto.CustomerDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CustomerClient {

    private final WebClient webClient;

    @CircuitBreaker(name = "customerService", fallbackMethod = "fallbackCustomer")
    public Single<CustomerDTO> getCustomer(String id) {

        return Single.fromPublisher(
                webClient.get()
                        .uri("http://customer-service/api/customers/{id}", id)
                        .retrieve()
                        .bodyToMono(CustomerDTO.class)
                        .timeout(Duration.ofSeconds(2))
        );
    }

    public Single<CustomerDTO> fallbackCustomer(String id, Throwable ex) {

        System.out.println("Circuit Breaker activado: " + ex.getMessage());

        return Single.error(
                new RuntimeException("Customer Service no disponible")
        );
    }
}