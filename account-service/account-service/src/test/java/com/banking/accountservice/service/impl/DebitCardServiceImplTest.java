package com.banking.accountservice.service.impl;

import com.banking.accountservice.model.Account;
import com.banking.accountservice.model.DebitCard;
import com.banking.accountservice.repository.AccountRepository;
import com.banking.accountservice.repository.DebitCardRepository;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DebitCardServiceImplTest {

    private DebitCardRepository debitCardRepository;
    private AccountRepository accountRepository;

    private DebitCardServiceImpl debitCardService;


    @BeforeEach
    void setUp() {

        debitCardRepository = mock(DebitCardRepository.class);
        accountRepository = mock(AccountRepository.class);

        debitCardService = new DebitCardServiceImpl(
                debitCardRepository,
                accountRepository
        );
    }


    @Test
    void shouldCreateDebitCardSuccessfully() {

        DebitCard savedCard = DebitCard.builder()
                .customerId("customer-001")
                .accountId("account-001")
                .cardNumber("1234567890123456")
                .status("ACTIVE")
                .expiryDate("12/30")
                .cvv("123")
                .build();


        when(debitCardRepository.save(any(DebitCard.class)))
                .thenReturn(Mono.just(savedCard));


        DebitCard result = debitCardService
                .createDebitCard(
                        "customer-001",
                        "account-001"
                )
                .blockingGet();


        assertNotNull(result);
        assertEquals("customer-001", result.getCustomerId());
        assertEquals("account-001", result.getAccountId());
        assertEquals("ACTIVE", result.getStatus());
        assertEquals("12/30", result.getExpiryDate());
        assertEquals("123", result.getCvv());

        assertNotNull(result.getCardNumber());
        assertEquals(16, result.getCardNumber().length());


        verify(debitCardRepository)
                .save(any(DebitCard.class));
    }


    @Test
    void shouldPayWithDebitCardSuccessfully() {

        DebitCard card = DebitCard.builder()
                .cardNumber("1111222233334444")
                .accountId("account-001")
                .status("ACTIVE")
                .build();


        Account account = Account.builder()
                .id("account-001")
                .balance(500.0)
                .build();


        Account savedAccount = Account.builder()
                .id("account-001")
                .balance(400.0)
                .build();


        when(debitCardRepository.findByCardNumber("1111222233334444"))
                .thenReturn(Mono.just(card));


        when(accountRepository.findById("account-001"))
                .thenReturn(Mono.just(account));


        when(accountRepository.save(any(Account.class)))
                .thenReturn(Mono.just(savedAccount));


        String result = debitCardService
                .payWithDebitCard(
                        "1111222233334444",
                        100.0
                )
                .blockingGet();


        assertEquals(
                "Payment made successfully.",
                result
        );


        verify(accountRepository)
                .save(any(Account.class));
    }


    @Test
    void shouldRejectPaymentWhenCardIsBlocked() {

        DebitCard card = DebitCard.builder()
                .cardNumber("1111222233334444")
                .accountId("account-001")
                .status("BLOCKED")
                .build();


        when(debitCardRepository.findByCardNumber("1111222233334444"))
                .thenReturn(Mono.just(card));


        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> debitCardService
                        .payWithDebitCard(
                                "1111222233334444",
                                100.0
                        )
                        .blockingGet()
        );


        assertEquals(
                "Card blocked",
                exception.getMessage()
        );


        verify(accountRepository, never())
                .findById(anyString());
    }


    @Test
    void shouldRejectPaymentWhenBalanceIsInsufficient() {

        DebitCard card = DebitCard.builder()
                .cardNumber("1111222233334444")
                .accountId("account-001")
                .status("ACTIVE")
                .build();


        Account account = Account.builder()
                .id("account-001")
                .balance(50.0)
                .build();


        when(debitCardRepository.findByCardNumber("1111222233334444"))
                .thenReturn(Mono.just(card));


        when(accountRepository.findById("account-001"))
                .thenReturn(Mono.just(account));


        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> debitCardService
                        .payWithDebitCard(
                                "1111222233334444",
                                100.0
                        )
                        .blockingGet()
        );


        assertEquals(
                "Insufficient balance",
                exception.getMessage()
        );


        verify(accountRepository, never())
                .save(any());
    }


    @Test
    void shouldFindCardsByCustomerSuccessfully() {

        DebitCard card1 = DebitCard.builder()
                .customerId("customer-001")
                .cardNumber("1111222233334444")
                .build();


        DebitCard card2 = DebitCard.builder()
                .customerId("customer-001")
                .cardNumber("5555666677778888")
                .build();


        when(debitCardRepository.findByCustomerId("customer-001"))
                .thenReturn(
                        Flux.fromIterable(
                                Arrays.asList(card1, card2)
                        )
                );


        var cards = debitCardService
                .findCardsByCustomer("customer-001")
                .toList()
                .blockingGet();


        assertEquals(2, cards.size());
        assertEquals(
                "1111222233334444",
                cards.get(0).getCardNumber()
        );
        assertEquals(
                "5555666677778888",
                cards.get(1).getCardNumber()
        );


        verify(debitCardRepository)
                .findByCustomerId("customer-001");
    }
}