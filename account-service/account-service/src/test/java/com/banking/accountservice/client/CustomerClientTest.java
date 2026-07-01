package com.banking.accountservice.client;

import com.banking.accountservice.dto.CustomerDTO;
import io.reactivex.rxjava3.observers.TestObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
class CustomerClientTest {

    private WebClient webClient;

    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;
    private WebClient.ResponseSpec responseSpec;

    private CustomerClient customerClient;

    @BeforeEach
    void setUp() {

        webClient = mock(WebClient.class);

        requestHeadersUriSpec =
                mock(WebClient.RequestHeadersUriSpec.class);

        requestHeadersSpec =
                mock(WebClient.RequestHeadersSpec.class);

        responseSpec =
                mock(WebClient.ResponseSpec.class);

        customerClient = new CustomerClient(webClient);
    }


    @Test
    void shouldReturnErrorWhenFallbackIsCalled() {

        TestObserver<CustomerDTO> observer =
                customerClient
                        .fallbackCustomer(
                                "C1",
                                new RuntimeException("Service Down"))
                        .test();

        observer.assertError(RuntimeException.class);

        observer.assertError(error ->
                "Customer Service no disponible"
                        .equals(error.getMessage()));
    }
}