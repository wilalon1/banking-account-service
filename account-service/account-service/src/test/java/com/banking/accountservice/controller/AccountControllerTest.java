package com.banking.accountservice.controller;

import com.banking.accountservice.client.TransactionClient;
import com.banking.accountservice.dto.TransactionDTO;
import com.banking.accountservice.model.Account;
import com.banking.accountservice.service.AccountService;
import com.banking.accountservice.service.impl.AccountServiceImpl;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static reactor.core.publisher.Mono.when;

@WebFluxTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AccountService accountService;

    private WebTestClient client;

    @Mock
    private TransactionClient transactionClient;

    @InjectMocks
    private AccountServiceImpl accountServiceImpl;

    @BeforeEach
    void setup() {
        this.client = webTestClient
                .mutateWith(mockUser().roles("ADMIN"))
                .mutateWith(csrf());
    }

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

        client.post()
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

        client.get()
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

        client.get()
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

        client.put()
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

        client.delete()
                .uri("/api/accounts/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testCreateAccountEndpoint() {

        Account account = Account.builder()
                .customerId("CUST123")
                .type("SAVINGS")
                .balance(100.0)
                .build();

        Mockito.when(accountService.create(Mockito.any(Account.class)))
                .thenReturn(Single.just(account));

        client.post()
                .uri("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(account)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.customerId").isEqualTo("CUST123")
                .jsonPath("$.balance").isEqualTo(100.0);
    }

    @Test
    void testGetAccountById() {

        Account account = Account.builder()
                .id("ACC001")
                .customerId("CUST123")
                .type("SAVINGS")
                .balance(150.0)
                .build();

        Mockito.when(accountService.findById("ACC001"))
                .thenReturn(Single.just(account));

        client.get()
                .uri("/api/accounts/ACC001")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.type").isEqualTo("SAVINGS");
    }


    @Test
    void testCommissionAppliedAfterMaxTransactions() {

        Account account = Account.builder()
                .id("A1")
                .customerId("C1")
                .type("SAVINGS")
                .balance(200.0)
                .build();


        List<TransactionDTO> txs = Arrays.asList(
                createTransaction("1", "A1", 100.0),
                createTransaction("2", "A1", 50.0),
                createTransaction("3", "A1", 50.0),
                createTransaction("4", "A1", 10.0),
                createTransaction("5", "A1", 10.0)
        );


        double commission = accountServiceImpl.calculateCommission(
                account.getId(),
                txs
        );


        double expected = 2 * AccountServiceImpl.COMMISSION_VALUE;


        assertEquals(expected, commission, 0.001);
    }

    private TransactionDTO createTransaction(
            String id,
            String accountId,
            Double amount) {

        TransactionDTO dto = new TransactionDTO();
        dto.setId(id);
        dto.setAccountId(accountId);
        dto.setType("DEPOSIT");
        dto.setAmount(amount);

        return dto;
    }


}