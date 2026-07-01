package com.banking.accountservice.client;

import com.banking.accountservice.dto.TransactionDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TransactionClient {

    private final WebClient webClient;

    @CircuitBreaker(
            name = "transactionClient",
            fallbackMethod = "fallbackTransactions"
    )
    public Single<List<TransactionDTO>> getTransactions(String accountId) {

        return Single.fromPublisher(
                webClient.get()
                        .uri(
                                "http://transaction-service/api/transactions/account/{id}",
                                accountId
                        )
                        .retrieve()
                        .bodyToFlux(TransactionDTO.class)
                        .collectList()
        );
    }

    public Single<List<TransactionDTO>> fallbackTransactions(
            String accountId,
            Throwable ex) {

        System.out.println(
                "Circuit breaker activo: " + ex.getMessage()
        );

        return Single.just(List.of());
    }
}