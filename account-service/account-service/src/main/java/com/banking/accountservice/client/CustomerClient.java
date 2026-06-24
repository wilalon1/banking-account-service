package com.banking.accountservice.client;

import com.banking.accountservice.dto.CustomerDTO;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class CustomerClient {

    private final WebClient webClient;

    public Single<CustomerDTO> getCustomer(String customerId) {

        return Single.fromPublisher(
                webClient.get()
                        .uri("http://CUSTOMER-SERVICE/api/customers/{id}", customerId)
                        .retrieve()
                        .bodyToMono(CustomerDTO.class)
        );
    }
}