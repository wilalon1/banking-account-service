package com.banking.accountservice.client;

import com.banking.accountservice.dto.TransactionDTO;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TransactionClient {

    private final WebClient webClient;

    public Single<List<TransactionDTO>> getTransactions(String accountId) {

        return Single.fromPublisher(
                webClient.get()
                        .uri("http://TRANSACTION-SERVICE/api/transactions/account/{id}", accountId)
                        .retrieve()
                        .bodyToFlux(TransactionDTO.class)
                        .collectList()
        );
    }
}