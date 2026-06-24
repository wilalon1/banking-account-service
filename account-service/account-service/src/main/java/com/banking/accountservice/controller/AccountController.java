package com.banking.accountservice.controller;

import com.banking.accountservice.model.Account;
import com.banking.accountservice.service.AccountService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<Account> create(@RequestBody Account account) {
        return service.create(account);
    }

    @GetMapping
    public Flux<Account> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Account> findById(@PathVariable String id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Mono<Account> update(@PathVariable String id,
                                @RequestBody Account account) {
        return service.update(id, account);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return service.delete(id);
    }
}