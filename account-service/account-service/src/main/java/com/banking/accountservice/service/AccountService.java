package com.banking.accountservice.service;

import com.banking.accountservice.model.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {

    Mono<Account> create(Account account);

    Flux<Account> findAll();

    Mono<Account> findById(String id);

    Mono<Account> update(String id, Account account);

    Mono<Void> delete(String id);
}