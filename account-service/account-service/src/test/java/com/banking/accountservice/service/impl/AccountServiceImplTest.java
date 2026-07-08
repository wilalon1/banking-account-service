package com.banking.accountservice.service.impl;

import com.banking.accountservice.client.CustomerClient;
import com.banking.accountservice.client.TransactionClient;
import com.banking.accountservice.dto.CustomerDTO;
import com.banking.accountservice.dto.TransactionDTO;
import com.banking.accountservice.model.Account;
import com.banking.accountservice.repository.AccountRepository;

import io.reactivex.rxjava3.core.Single;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class AccountServiceImplTest {


    @Mock
    private AccountRepository repository;


    @Mock
    private CustomerClient customerClient;


    @Mock
    private TransactionClient transactionClient;


    @Mock
    private ReactiveRedisTemplate<String, Object> redis;


    private ReactiveValueOperations<String, Object> valueOperations;


    private AccountServiceImpl service;


    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        valueOperations = mock(ReactiveValueOperations.class);

        when(redis.opsForValue())
                .thenReturn(valueOperations);


        service = new AccountServiceImpl(
                repository,
                customerClient,
                transactionClient,
                redis
        );
    }


    @Test
    void shouldCreateAccountSuccessfully() {

        Account account =
                new Account("1", "C1", "CURRENT", 100.0);


        when(repository.save(any(Account.class)))
                .thenReturn(Mono.just(account));


        when(customerClient.getCustomer(anyString()))
                .thenReturn(
                        Single.just(
                                new CustomerDTO("C1", "NORMAL")
                        )
                );


        service.create(account)
                .test()
                .assertComplete()
                .assertValue(acc ->
                        acc.getId().equals("1")
                );
    }


    @Test
    void shouldAllowVipAccountWhenAverageIsOk() {

        Account account =
                new Account("10", "VIP1", "SAVINGS", 100.0);


        when(repository.save(any(Account.class)))
                .thenReturn(Mono.just(account));


        when(customerClient.getCustomer(anyString()))
                .thenReturn(
                        Single.just(
                                new CustomerDTO("VIP1", "VIP")
                        )
                );


        TransactionDTO t1 = new TransactionDTO();
        t1.setBalanceAfter(1200.0);


        TransactionDTO t2 = new TransactionDTO();
        t2.setBalanceAfter(1500.0);


        when(transactionClient.getTransactions(anyString()))
                .thenReturn(
                        Single.just(
                                List.of(t1, t2)
                        )
                );


        service.create(account)
                .test()
                .assertComplete()
                .assertValue(acc ->
                        acc.getId().equals("10")
                );
    }


    @Test
    void shouldFailWhenVipAverageIsLow() {

        Account account =
                new Account("20", "VIP2", "SAVINGS", 100.0);


        when(repository.save(any(Account.class)))
                .thenReturn(Mono.just(account));


        when(customerClient.getCustomer(anyString()))
                .thenReturn(
                        Single.just(
                                new CustomerDTO("VIP2", "VIP")
                        )
                );


        TransactionDTO t1 = new TransactionDTO();
        t1.setBalanceAfter(200.0);


        TransactionDTO t2 = new TransactionDTO();
        t2.setBalanceAfter(300.0);


        when(transactionClient.getTransactions(anyString()))
                .thenReturn(
                        Single.just(
                                List.of(t1, t2)
                        )
                );


        service.create(account)
                .test()
                .assertError(error ->
                        error.getMessage()
                                .contains("VIP must maintain")
                );
    }


    @Test
    void shouldReturnAllAccounts() {

        Account account =
                new Account("1", "C1", "CURRENT", 100.0);


        when(repository.findAll())
                .thenReturn(
                        Flux.just(account)
                );


        service.findAll()
                .test()
                .assertComplete()
                .assertValue(accounts ->
                        accounts.size() == 1
                                && accounts.get(0)
                                .getId()
                                .equals("1")
                );


        verify(repository, times(1))
                .findAll();
    }


    @Test
    void shouldFindAccountById() {

        Account account =
                new Account("99", "C1", "CURRENT", 100.0);


        when(repository.findById("99"))
                .thenReturn(
                        Mono.just(account)
                );


        service.findById("99")
                .test()
                .assertComplete()
                .assertValue(acc ->
                        acc.getId().equals("99")
                );
    }


    @Test
    void shouldCreateStandardSavingsAccountWithMinimumBalance() {

        Account account =
                Account.builder()
                        .customerId("CUST001")
                        .type("SAVINGS")
                        .balance(0.0)
                        .build();


        CustomerDTO customer = new CustomerDTO();
        customer.setCustomerType("STANDARD");


        when(repository.save(any(Account.class)))
                .thenReturn(
                        Mono.just(account)
                );


        when(customerClient.getCustomer(anyString()))
                .thenReturn(
                        Single.just(customer)
                );


        Account result =
                service.create(account)
                        .blockingGet();


        assertEquals(
                "SAVINGS",
                result.getType()
        );


        assertEquals(
                0.0,
                result.getBalance()
        );


        verify(repository)
                .save(any(Account.class));
    }


    @Test
    void shouldFailWhenVipAccountAverageBalanceIsLowerThanRequired() {

        Account account =
                Account.builder()
                        .id("ACC1")
                        .customerId("CUSTVIP")
                        .type("SAVINGS")
                        .balance(500.0)
                        .build();


        when(repository.save(any(Account.class)))
                .thenReturn(
                        Mono.just(account)
                );


        CustomerDTO customer = new CustomerDTO();
        customer.setCustomerType("VIP");


        when(customerClient.getCustomer(anyString()))
                .thenReturn(
                        Single.just(customer)
                );


        TransactionDTO transaction = new TransactionDTO();

        transaction.setId("TX1");
        transaction.setAccountId("ACC1");
        transaction.setBalanceAfter(450.0);
        transaction.setType("DEPOSIT");


        when(transactionClient.getTransactions(anyString()))
                .thenReturn(
                        Single.just(
                                List.of(transaction)
                        )
                );


        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () ->
                                service.create(account)
                                        .blockingGet()
                );


        assertEquals(
                "VIP must maintain a minimum daily average of 1000",
                exception.getMessage()
        );
    }


    @Test
    void shouldSaveAndFindAllAccountsSuccessfullyFromRedis() {

        List<Account> accounts =
                List.of(
                        new Account("1", "C001", "SAVINGS", 1500.0),
                        new Account("2", "C002", "CURRENT", 3000.0)
                );


        when(valueOperations.set(
                "accounts",
                accounts
        ))
                .thenReturn(
                        Mono.just(true)
                );


        service.saveAllToRedis(accounts)
                .test()
                .assertComplete()
                .assertValue(true);


        verify(valueOperations)
                .set(
                        "accounts",
                        accounts
                );


        when(valueOperations.get("accounts"))
                .thenReturn(
                        Mono.just(accounts)
                );


        service.findAllFromRedis()
                .test()
                .assertComplete()
                .assertValue(result ->
                        result.size() == 2
                                && result.get(0)
                                .getId()
                                .equals("1")
                );


        verify(valueOperations)
                .get("accounts");
    }

}