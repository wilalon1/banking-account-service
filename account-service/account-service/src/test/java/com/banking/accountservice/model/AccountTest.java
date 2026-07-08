package com.banking.accountservice.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {


    @Test
    void shouldCreateAccountUsingBuilder() {

        Account account = Account.builder()
                .id("account-001")
                .customerId("customer-001")
                .type("SAVINGS")
                .balance(1000.0)
                .build();


        assertEquals("account-001", account.getId());
        assertEquals("customer-001", account.getCustomerId());
        assertEquals("SAVINGS", account.getType());
        assertEquals(1000.0, account.getBalance());
    }


    @Test
    void shouldCreateAccountUsingNoArgsConstructor() {

        Account account = new Account();

        account.setId("account-002");
        account.setCustomerId("customer-002");
        account.setType("CURRENT");
        account.setBalance(500.0);


        assertEquals("account-002", account.getId());
        assertEquals("customer-002", account.getCustomerId());
        assertEquals("CURRENT", account.getType());
        assertEquals(500.0, account.getBalance());
    }


    @Test
    void shouldCreateAccountUsingAllArgsConstructor() {

        Account account = new Account(
                "account-003",
                "customer-003",
                "FIXED",
                3000.0
        );


        assertEquals("account-003", account.getId());
        assertEquals("customer-003", account.getCustomerId());
        assertEquals("FIXED", account.getType());
        assertEquals(3000.0, account.getBalance());
    }


    @Test
    void shouldCompareTwoAccounts() {

        Account account1 = Account.builder()
                .id("account-001")
                .customerId("customer-001")
                .type("SAVINGS")
                .balance(1000.0)
                .build();


        Account account2 = Account.builder()
                .id("account-001")
                .customerId("customer-001")
                .type("SAVINGS")
                .balance(1000.0)
                .build();


        assertEquals(account1, account2);
        assertEquals(account1.hashCode(), account2.hashCode());
    }


    @Test
    void shouldGenerateToString() {

        Account account = Account.builder()
                .id("account-001")
                .customerId("customer-001")
                .type("SAVINGS")
                .balance(1000.0)
                .build();


        String result = account.toString();


        assertNotNull(result);
        assertTrue(result.contains("account-001"));
        assertTrue(result.contains("customer-001"));
        assertTrue(result.contains("SAVINGS"));
    }


    @Test
    void shouldAllowNullValues() {

        Account account = new Account();


        assertNull(account.getId());
        assertNull(account.getCustomerId());
        assertNull(account.getType());
        assertNull(account.getBalance());
    }
}