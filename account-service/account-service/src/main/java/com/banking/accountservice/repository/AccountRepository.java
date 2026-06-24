package com.banking.accountservice.repository;

import com.banking.accountservice.model.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRepository extends ReactiveMongoRepository<Account, String> {
    Flux<Account> findByCustomerId(String customerId);
}