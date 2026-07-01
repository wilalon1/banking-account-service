package com.banking.accountservice.controller;

import com.banking.accountservice.model.Account;
import com.banking.accountservice.service.AccountService;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AccountService accountService;

    @Test
    void shouldCreateAccount() {

        Account account = Account.builder()
                .id("1")
                .customerId("C1")
                .type("CURRENT")
                .balance(500.0)
                .build();

        Mockito.when(accountService.create(Mockito.any(Account.class)))
                .thenReturn(Single.just(account));

        webTestClient.post()
                .uri("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(account)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.customerId").isEqualTo("C1")
                .jsonPath("$.type").isEqualTo("CURRENT");
    }

    @Test
    void shouldFindById() {

        Account account = Account.builder()
                .id("1")
                .customerId("C1")
                .type("CURRENT")
                .balance(500.0)
                .build();

        Mockito.when(accountService.findById("1"))
                .thenReturn(Single.just(account));

        webTestClient.get()
                .uri("/api/accounts/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1");
    }

    @Test
    void shouldFindAllAccounts() {

        Account account = Account.builder()
                .id("1")
                .customerId("C1")
                .type("CURRENT")
                .balance(500.0)
                .build();

        Mockito.when(accountService.findAll())
                .thenReturn(Observable.just(account));

        webTestClient.get()
                .uri("/api/accounts")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("1");
    }

    @Test
    void shouldUpdateAccount() {

        Account account = Account.builder()
                .id("1")
                .customerId("C1")
                .type("SAVINGS")
                .balance(1000.0)
                .build();

        Mockito.when(accountService.update(Mockito.eq("1"), Mockito.any(Account.class)))
                .thenReturn(Single.just(account));

        webTestClient.put()
                .uri("/api/accounts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(account)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.type").isEqualTo("SAVINGS");
    }

    @Test
    void shouldDeleteAccount() {

        Mockito.when(accountService.delete("1"))
                .thenReturn(Completable.complete());

        webTestClient.delete()
                .uri("/api/accounts/1")
                .exchange()
                .expectStatus().isOk();
    }

}