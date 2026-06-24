package com.banking.accountservice.controller;

import com.banking.accountservice.model.Account;
import com.banking.accountservice.service.AccountService;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping
    public Single<Account> create(@RequestBody Account account) {
        return service.create(account);
    }

    @GetMapping
    public Observable<Account> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Single<Account> findById(@PathVariable String id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Single<Account> update(@PathVariable String id,
                                  @RequestBody Account account) {
        return service.update(id, account);
    }

    @DeleteMapping("/{id}")
    public Completable delete(@PathVariable String id) {
        return service.delete(id);
    }
}