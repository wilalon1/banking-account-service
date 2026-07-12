package com.banking.accountservice.controller;

import com.banking.accountservice.model.Account;
import com.banking.accountservice.service.AccountService;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller responsible for handling account management operations.
 *
 * This controller exposes endpoints to create, retrieve, update,
 * and delete customer accounts.
 *
 * The communication uses reactive programming with RxJava 3 types:
 * Single and Completable.
 */

@Tag(
        name = "Customer",
        description = "Customer-related transactions"
)
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;

    /**
     * Constructor injection of the AccountService dependency.
     *
     * @param service service layer responsible for account operations
     */
    public AccountController(AccountService service) {
        this.service = service;
    }

    /**
     * Creates a new bank account.
     *
     * @param account account information received in the request body
     * @return created account as a reactive Single response
     */
    @PostMapping
    public Single<Account> create(@RequestBody Account account) {
        return service.create(account);
    }

    /**
     * Retrieves all registered accounts.
     *
     * @return list of accounts wrapped in a reactive Single response
     */
    @GetMapping
    public Single<List<Account>> findAll() {
        return service.findAll();
    }

    /**
     * Retrieves an account by its identifier.
     *
     * @param id account identifier
     * @return account information as a reactive Single response
     */
    @GetMapping("/{id}")
    public Single<Account> findById(@PathVariable String id) {

        return service.findById(id);
    }
    /**
     * Updates an existing account.
     *
     * @param id account identifier to update
     * @param account account information with updated values
     * @return updated account as a reactive Single response
     */

    @PutMapping("/{id}")
    public Single<Account> update(@PathVariable String id,
                                  @RequestBody Account account) {
        return service.update(id, account);
    }
    /**
     * Deletes an account by its identifier.
     *
     * @param id account identifier to delete
     * @return completion signal using reactive Completable
     */
    @DeleteMapping("/{id}")
    public Completable delete(@PathVariable String id) {
        return service.delete(id);
    }
}