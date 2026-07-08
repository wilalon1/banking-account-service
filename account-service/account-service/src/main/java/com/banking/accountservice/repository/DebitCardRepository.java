package com.banking.accountservice.repository;

import com.banking.accountservice.model.DebitCard;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DebitCardRepository extends ReactiveMongoRepository<DebitCard, String> {
    Flux<DebitCard> findByCustomerId(String customerId);
    Flux<DebitCard> findByAccountId(String accountId);
    Mono<DebitCard> findByCardNumber(String cardNumber);
}