package com.banking.accountservice.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DebitCardTest {


    @Test
    void shouldCreateDebitCardUsingBuilder() {

        DebitCard card = DebitCard.builder()
                .id("card-001")
                .customerId("customer-001")
                .accountId("account-001")
                .cardNumber("1111222233334444")
                .status("ACTIVE")
                .expiryDate("12/30")
                .cvv("123")
                .build();


        assertEquals("card-001", card.getId());
        assertEquals("customer-001", card.getCustomerId());
        assertEquals("account-001", card.getAccountId());
        assertEquals("1111222233334444", card.getCardNumber());
        assertEquals("ACTIVE", card.getStatus());
        assertEquals("12/30", card.getExpiryDate());
        assertEquals("123", card.getCvv());
    }


    @Test
    void shouldCreateDebitCardUsingNoArgsConstructor() {

        DebitCard card = new DebitCard();

        card.setId("card-002");
        card.setCustomerId("customer-002");
        card.setAccountId("account-002");
        card.setCardNumber("5555666677778888");
        card.setStatus("LOCKED");
        card.setExpiryDate("10/29");
        card.setCvv("456");


        assertEquals("card-002", card.getId());
        assertEquals("customer-002", card.getCustomerId());
        assertEquals("account-002", card.getAccountId());
        assertEquals("5555666677778888", card.getCardNumber());
        assertEquals("LOCKED", card.getStatus());
        assertEquals("10/29", card.getExpiryDate());
        assertEquals("456", card.getCvv());
    }


    @Test
    void shouldCreateDebitCardUsingAllArgsConstructor() {

        DebitCard card = new DebitCard(
                "card-003",
                "customer-003",
                "account-003",
                "9999000011112222",
                "ACTIVE",
                "05/31",
                "789"
        );


        assertEquals("card-003", card.getId());
        assertEquals("customer-003", card.getCustomerId());
        assertEquals("account-003", card.getAccountId());
        assertEquals("9999000011112222", card.getCardNumber());
        assertEquals("ACTIVE", card.getStatus());
        assertEquals("05/31", card.getExpiryDate());
        assertEquals("789", card.getCvv());
    }


    @Test
    void shouldCompareTwoDebitCards() {

        DebitCard card1 = DebitCard.builder()
                .id("card-001")
                .customerId("customer-001")
                .accountId("account-001")
                .cardNumber("1111222233334444")
                .status("ACTIVE")
                .expiryDate("12/30")
                .cvv("123")
                .build();


        DebitCard card2 = DebitCard.builder()
                .id("card-001")
                .customerId("customer-001")
                .accountId("account-001")
                .cardNumber("1111222233334444")
                .status("ACTIVE")
                .expiryDate("12/30")
                .cvv("123")
                .build();


        assertEquals(card1, card2);
        assertEquals(card1.hashCode(), card2.hashCode());
    }


    @Test
    void shouldGenerateToString() {

        DebitCard card = DebitCard.builder()
                .id("card-001")
                .cardNumber("1111222233334444")
                .status("ACTIVE")
                .build();


        String result = card.toString();


        assertNotNull(result);
        assertTrue(result.contains("card-001"));
        assertTrue(result.contains("1111222233334444"));
        assertTrue(result.contains("ACTIVE"));
    }


    @Test
    void shouldAllowNullValues() {

        DebitCard card = new DebitCard();


        assertNull(card.getId());
        assertNull(card.getCustomerId());
        assertNull(card.getAccountId());
        assertNull(card.getCardNumber());
        assertNull(card.getStatus());
        assertNull(card.getExpiryDate());
        assertNull(card.getCvv());
    }
}