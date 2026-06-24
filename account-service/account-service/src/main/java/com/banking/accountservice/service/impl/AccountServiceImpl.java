package com.banking.accountservice.service.impl;

import com.banking.accountservice.model.Account;
import com.banking.accountservice.repository.AccountRepository;
import com.banking.accountservice.service.AccountService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;

    public AccountServiceImpl(AccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Account> create(Account account) {
        return repository.save(account);
    }

    @Override
    public Flux<Account> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Account> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Account> update(String id, Account account) {
        return repository.findById(id)
                .flatMap(existing -> {
                    existing.setCustomerId(account.getCustomerId());
                    existing.setType(account.getType());
                    existing.setBalance(account.getBalance());
                    return repository.save(existing);
                });
    }

    @Override
    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }
}