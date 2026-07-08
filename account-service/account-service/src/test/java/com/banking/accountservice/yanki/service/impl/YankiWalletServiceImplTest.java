package com.banking.accountservice.yanki.service.impl;

import com.banking.accountservice.yanki.model.YankiWallet;
import com.banking.accountservice.yanki.repository.YankiWalletRepository;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class YankiWalletServiceImplTest {

    private YankiWalletRepository walletRepository;

    private YankiWalletServiceImpl yankiWalletService;


    @BeforeEach
    void setUp() {

        walletRepository = mock(YankiWalletRepository.class);

        yankiWalletService = new YankiWalletServiceImpl(
                walletRepository
        );
    }


    @Test
    void shouldCreateWalletWithZeroBalance() {

        YankiWallet wallet = YankiWallet.builder()
                .phoneNumber("999999999")
                .email("test@gmail.com")
                .build();


        when(walletRepository.save(any(YankiWallet.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0))
                );


        YankiWallet result = yankiWalletService
                .createWallet(wallet)
                .blockingGet();


        assertNotNull(result);
        assertEquals(
                BigDecimal.ZERO,
                result.getBalance()
        );


        verify(walletRepository)
                .save(any(YankiWallet.class));
    }


    @Test
    void shouldTransferMoneySuccessfully() {

        YankiWallet fromWallet = YankiWallet.builder()
                .phoneNumber("999999999")
                .balance(BigDecimal.valueOf(500))
                .build();


        YankiWallet toWallet = YankiWallet.builder()
                .phoneNumber("888888888")
                .balance(BigDecimal.valueOf(100))
                .build();


        when(walletRepository.findByPhoneNumber("999999999"))
                .thenReturn(Mono.just(fromWallet));


        when(walletRepository.findByPhoneNumber("888888888"))
                .thenReturn(Mono.just(toWallet));


        when(walletRepository.save(any(YankiWallet.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0))
                );


        String result = yankiWalletService
                .sendMoney(
                        "999999999",
                        "888888888",
                        BigDecimal.valueOf(200)
                )
                .blockingGet();


        assertEquals(
                "Transfer completed successfully.",
                result
        );


        assertEquals(
                BigDecimal.valueOf(300),
                fromWallet.getBalance()
        );


        assertEquals(
                BigDecimal.valueOf(300),
                toWallet.getBalance()
        );


        verify(walletRepository, times(2))
                .save(any(YankiWallet.class));
    }


    @Test
    void shouldRejectTransferWhenBalanceIsInsufficient() {

        YankiWallet fromWallet = YankiWallet.builder()
                .phoneNumber("999999999")
                .balance(BigDecimal.valueOf(50))
                .build();


        when(walletRepository.findByPhoneNumber("999999999"))
                .thenReturn(Mono.just(fromWallet));


        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> yankiWalletService
                        .sendMoney(
                                "999999999",
                                "888888888",
                                BigDecimal.valueOf(100)
                        )
                        .blockingGet()
        );


        assertEquals(
                "Insufficient balance",
                exception.getMessage()
        );


        verify(walletRepository, never())
                .save(any());
    }


    @Test
    void shouldAssociateDebitCardSuccessfully() {

        YankiWallet wallet = YankiWallet.builder()
                .id("wallet-001")
                .phoneNumber("999999999")
                .balance(BigDecimal.valueOf(100))
                .build();


        when(walletRepository.findById("wallet-001"))
                .thenReturn(Mono.just(wallet));


        when(walletRepository.save(any(YankiWallet.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0))
                );


        YankiWallet result = yankiWalletService
                .associateDebitCard(
                        "wallet-001",
                        "card-001"
                )
                .blockingGet();


        assertEquals(
                "card-001",
                result.getAssociatedDebitCardId()
        );


        verify(walletRepository)
                .save(wallet);
    }
}