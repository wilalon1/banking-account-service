package com.banking.accountservice.client;

import com.banking.accountservice.dto.TransactionDTO;
import io.reactivex.rxjava3.observers.TestObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class TransactionClientTest {

    private TransactionClient transactionClient;

    @BeforeEach
    void setUp() {
        WebClient webClient = mock(WebClient.class);
        transactionClient = new TransactionClient(webClient);
    }

    @Test
    void shouldReturnEmptyListWhenFallbackIsCalled() {

        TestObserver<List<TransactionDTO>> observer =
                transactionClient
                        .fallbackTransactions(
                                "ACC-001",
                                new RuntimeException("Transaction Service Down")
                        )
                        .test();

        observer.assertComplete();
        observer.assertNoErrors();

        List<TransactionDTO> result = observer.values().get(0);

        assertTrue(result.isEmpty());
    }

}