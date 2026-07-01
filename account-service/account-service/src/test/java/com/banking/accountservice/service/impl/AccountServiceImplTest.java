package com.banking.accountservice.service.impl;

import com.banking.accountservice.client.CustomerClient;
import com.banking.accountservice.client.TransactionClient;
import com.banking.accountservice.dto.CustomerDTO;
import com.banking.accountservice.dto.TransactionDTO;
import com.banking.accountservice.model.Account;
import com.banking.accountservice.repository.AccountRepository;
import io.reactivex.rxjava3.observers.TestObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    private AccountRepository repository;
    private CustomerClient customerClient;
    private TransactionClient transactionClient;

    private AccountServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(AccountRepository.class);
        customerClient = mock(CustomerClient.class);
        transactionClient = mock(TransactionClient.class);

        service = new AccountServiceImpl(repository, customerClient, transactionClient);
    }

    // =========================
    // CASO 1: CUENTA NORMAL
    // =========================
    @Test
    void shouldCreateAccountSuccessfully() {

        Account account = new Account("1", "C1", "CURRENT", 100.0);

        when(repository.save(any(Account.class)))
                .thenReturn(Mono.just(account));

        when(customerClient.getCustomer(anyString()))
                .thenReturn(io.reactivex.rxjava3.core.Single.just(
                        new CustomerDTO("C1", "NORMAL")
                ));

        TestObserver<Account> test = service.create(account).test();

        test.assertComplete();
        test.assertValue(acc -> acc.getId().equals("1"));
    }

    // =========================
    // CASO 2: VIP OK
    // =========================
    @Test
    void shouldAllowVipAccountWhenAverageIsOk() {

        Account account = new Account("10", "VIP1", "SAVINGS", 100.0);

        when(repository.save(any(Account.class)))
                .thenReturn(Mono.just(account));

        when(customerClient.getCustomer(anyString()))
                .thenReturn(io.reactivex.rxjava3.core.Single.just(
                        new CustomerDTO("VIP1", "VIP")
                ));

        TransactionDTO t1 = new TransactionDTO();
        t1.setBalanceAfter(1200.0);

        TransactionDTO t2 = new TransactionDTO();
        t2.setBalanceAfter(1500.0);

        when(transactionClient.getTransactions(anyString()))
                .thenReturn(io.reactivex.rxjava3.core.Single.just(List.of(t1, t2)));

        service.create(account)
                .test()
                .assertComplete()
                .assertValue(acc -> acc.getId().equals("10"));
    }

    // =========================
    // CASO 3: VIP FAIL
    // =========================
    @Test
    void shouldFailWhenVipAverageIsLow() {

        Account account = new Account("20", "VIP2", "SAVINGS", 100.0);

        when(repository.save(any(Account.class)))
                .thenReturn(Mono.just(account));

        when(customerClient.getCustomer(anyString()))
                .thenReturn(io.reactivex.rxjava3.core.Single.just(
                        new CustomerDTO("VIP2", "VIP")
                ));

        TransactionDTO t1 = new TransactionDTO();
        t1.setBalanceAfter(200.0);

        TransactionDTO t2 = new TransactionDTO();
        t2.setBalanceAfter(300.0);

        when(transactionClient.getTransactions(anyString()))
                .thenReturn(io.reactivex.rxjava3.core.Single.just(List.of(t1, t2)));

        service.create(account)
                .test()
                .assertError(error ->
                        error.getMessage().contains("VIP must maintain")
                );
    }

    // =========================
    // FIND ALL
    // =========================
    @Test
    void shouldReturnAllAccounts() {

        Account acc = new Account("1", "C1", "CURRENT", 100.0);

        when(repository.findAll())
                .thenReturn(Flux.just(acc));

        service.findAll()
                .test()
                .assertValueCount(1)
                .assertComplete();
    }

    // =========================
    // FIND BY ID
    // =========================
    @Test
    void shouldFindById() {

        Account acc = new Account("99", "C1", "CURRENT", 100.0);

        when(repository.findById("99"))
                .thenReturn(Mono.just(acc));

        service.findById("99")
                .test()
                .assertValue(a -> a.getId().equals("99"))
                .assertComplete();
    }
}