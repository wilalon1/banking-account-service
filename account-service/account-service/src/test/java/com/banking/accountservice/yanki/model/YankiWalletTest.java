package com.banking.accountservice.yanki.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class YankiWalletTest {

    @Test
    void shouldCreateYankiWalletUsingBuilder() {

        YankiWallet wallet = YankiWallet.builder()
                .id("wallet-001")
                .documentType("ID CARD")
                .documentNumber("12345678")
                .phoneNumber("999999999")
                .imei("IMEI123456")
                .email("test@gmail.com")
                .balance(BigDecimal.valueOf(100.50))
                .associatedDebitCardId("card-001")
                .build();


        assertEquals("wallet-001", wallet.getId());
        assertEquals("ID CARD", wallet.getDocumentType());
        assertEquals("12345678", wallet.getDocumentNumber());
        assertEquals("999999999", wallet.getPhoneNumber());
        assertEquals("IMEI123456", wallet.getImei());
        assertEquals("test@gmail.com", wallet.getEmail());
        assertEquals(BigDecimal.valueOf(100.50), wallet.getBalance());
        assertEquals("card-001", wallet.getAssociatedDebitCardId());
    }


    @Test
    void shouldCreateYankiWalletUsingEmptyConstructor() {

        YankiWallet wallet = new YankiWallet();

        wallet.setId("wallet-002");
        wallet.setPhoneNumber("988888888");
        wallet.setBalance(BigDecimal.TEN);


        assertEquals("wallet-002", wallet.getId());
        assertEquals("988888888", wallet.getPhoneNumber());
        assertEquals(BigDecimal.TEN, wallet.getBalance());
    }


    @Test
    void shouldCompareTwoWallets() {

        YankiWallet wallet1 = YankiWallet.builder()
                .id("wallet-001")
                .phoneNumber("999999999")
                .balance(BigDecimal.valueOf(200))
                .build();


        YankiWallet wallet2 = YankiWallet.builder()
                .id("wallet-001")
                .phoneNumber("999999999")
                .balance(BigDecimal.valueOf(200))
                .build();


        assertEquals(wallet1, wallet2);
        assertEquals(wallet1.hashCode(), wallet2.hashCode());
    }


    @Test
    void shouldGenerateToString() {

        YankiWallet wallet = YankiWallet.builder()
                .id("wallet-001")
                .email("wallet@test.com")
                .build();


        String result = wallet.toString();


        assertNotNull(result);
        assertTrue(result.contains("wallet-001"));
        assertTrue(result.contains("wallet@test.com"));
    }


    @Test
    void shouldAllowNullValues() {

        YankiWallet wallet = new YankiWallet();


        assertNull(wallet.getId());
        assertNull(wallet.getDocumentType());
        assertNull(wallet.getDocumentNumber());
        assertNull(wallet.getPhoneNumber());
        assertNull(wallet.getImei());
        assertNull(wallet.getEmail());
        assertNull(wallet.getBalance());
        assertNull(wallet.getAssociatedDebitCardId());
    }
}