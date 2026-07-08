package com.banking.accountservice.yanki.controller;

import com.banking.accountservice.yanki.model.YankiWallet;
import com.banking.accountservice.yanki.service.YankiWalletService;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@WebFluxTest(YankiWalletController.class)
@Import(YankiWalletControllerTest.TestSecurityConfig.class)
class YankiWalletControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private YankiWalletService yankiWalletService;


    @Test
    void shouldCreateWallet() {

        YankiWallet wallet = new YankiWallet();
        wallet.setId("wallet-001");


        when(yankiWalletService.createWallet(any(YankiWallet.class)))
                .thenReturn(Single.just(wallet));


        webTestClient.post()
                .uri("/yanki/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(wallet)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(YankiWallet.class)
                .isEqualTo(wallet);


        verify(yankiWalletService)
                .createWallet(any(YankiWallet.class));
    }


    @Test
    void shouldTransferMoney() {

        when(
                yankiWalletService.sendMoney(
                        "999999999",
                        "888888888",
                        BigDecimal.valueOf(100)
                )
        ).thenReturn(Single.just("Transfer successful"));


        Map<String, Object> body = new HashMap<>();
        body.put("fromPhone", "999999999");
        body.put("toPhone", "888888888");
        body.put("amount", 100);


        webTestClient.post()
                .uri("/yanki/wallets/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("Transfer successful");


        verify(yankiWalletService)
                .sendMoney(
                        "999999999",
                        "888888888",
                        BigDecimal.valueOf(100)
                );
    }


    @Test
    void shouldAssociateDebitCard() {

        YankiWallet wallet = new YankiWallet();
        wallet.setId("wallet-001");
        wallet.setAssociatedDebitCardId("card-001");


        when(
                yankiWalletService.associateDebitCard(
                        "wallet-001",
                        "card-001"
                )
        ).thenReturn(Single.just(wallet));


        Map<String, String> body = new HashMap<>();
        body.put("debitCardId", "card-001");


        webTestClient.post()
                .uri("/yanki/wallets/wallet-001/associate-debit-card")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(YankiWallet.class)
                .isEqualTo(wallet);


        verify(yankiWalletService)
                .associateDebitCard(
                        "wallet-001",
                        "card-001"
                );
    }


    @TestConfiguration
    @EnableWebFluxSecurity
    static class TestSecurityConfig {

        @Bean
        SecurityWebFilterChain springSecurityFilterChain(
                ServerHttpSecurity http) {

            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(exchange ->
                            exchange.anyExchange().permitAll()
                    )
                    .build();
        }
    }
}