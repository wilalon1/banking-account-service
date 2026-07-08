package com.banking.accountservice.service.impl;

import com.banking.accountservice.client.CustomerClient;
import com.banking.accountservice.client.TransactionClient;
import com.banking.accountservice.dto.CustomerDTO;
import com.banking.accountservice.dto.TransactionDTO;
import com.banking.accountservice.model.Account;
import com.banking.accountservice.repository.AccountRepository;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.TestObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


class AccountServiceImplTest {

    @Mock
    private AccountRepository repository;
    @Mock
    private CustomerClient customerClient;
    @Mock
    private TransactionClient transactionClient;
    @Mock
    private AccountServiceImpl service;


    @Mock
    private AccountRepository accountRepository;


    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

    @Test
    void testCreateAccountWithMinimumAmount() {

        Account account = Account.builder()
                .customerId("CUST001")
                .type("SAVINGS")
                .balance(0.0)
                .build();

        CustomerDTO customer = new CustomerDTO();
        customer.setCustomerType("STANDARD");

        when(repository.save(any(Account.class)))
                .thenReturn(Mono.just(account));

        when(customerClient.getCustomer(anyString()))
                .thenReturn(Single.just(customer));

        Account created = service.create(account).blockingGet();

        assertEquals("SAVINGS", created.getType());
        assertEquals(0.0, created.getBalance());

        verify(repository, times(1)).save(any(Account.class));
    }
    @Test
    void testCreateVIPAccountWithLowAverageShouldFail() {

        Account account = Account.builder()
                .id("ACC1")
                .customerId("CUSTVIP")
                .type("SAVINGS")
                .balance(500.0)
                .build();

        when(repository.save(any(Account.class)))
                .thenReturn(Mono.just(account));

        CustomerDTO customer = new CustomerDTO();
        customer.setCustomerType("VIP");

        when(customerClient.getCustomer(anyString()))
                .thenReturn(Single.just(customer));

        TransactionDTO transaction = new TransactionDTO();
        transaction.setId("TX1");
        transaction.setAccountId("ACC1");
        transaction.setBalanceAfter(450.0);
        transaction.setType("DEPOSIT");

        when(transactionClient.getTransactions(anyString()))
                .thenReturn(Single.just(List.of(transaction)));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.create(account).blockingGet()
        );

        assertEquals(
                "VIP must maintain a minimum daily average of 1000",
                ex.getMessage()
        );

        verify(repository).save(any(Account.class));
    }
}