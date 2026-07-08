package com.banking.accountservice.client;

import com.banking.accountservice.dto.TransactionDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionClient {

    private final WebClient webClient;

    @CircuitBreaker(name = "transactionClient", fallbackMethod = "fallbackTransactions")
    @TimeLimiter(name = "transactionClient")
    public Single<List<TransactionDTO>> getTransactions(String accountId) {
        return Single.fromPublisher(
                webClient.get()
                        .uri("http://transaction-service/api/transactions/account/{id}", accountId)
                        .retrieve()
                        .bodyToFlux(TransactionDTO.class)
                        .timeout(Duration.ofSeconds(2))
                        .collectList()
        );
    }

    public Single<List<TransactionDTO>> fallbackTransactions(
            String accountId,
            Throwable ex) {

        log.warn("Circuit breaker activo para transactionClient: {}", ex.toString(), ex);

        return Single.just(List.of());
    }
}