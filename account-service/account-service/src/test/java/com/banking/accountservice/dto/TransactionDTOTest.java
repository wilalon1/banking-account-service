package com.banking.accountservice.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionDTOTest {

    @Test
    void shouldCreateTransactionDTOWithAllFields() {
        // Arrange
        LocalDateTime date = LocalDateTime.now();

        TransactionDTO dto = new TransactionDTO();
        dto.setId("tx-001");
        dto.setAccountId("acc-001");
        dto.setType("DEPOSIT");
        dto.setAmount(100.0);
        dto.setBalanceAfter(500.0);
        dto.setDate(date);

        // Assert
        assertEquals("tx-001", dto.getId());
        assertEquals("acc-001", dto.getAccountId());
        assertEquals("DEPOSIT", dto.getType());
        assertEquals(100.0, dto.getAmount());
        assertEquals(500.0, dto.getBalanceAfter());
        assertEquals(date, dto.getDate());
    }

    @Test
    void shouldSupportEqualsAndHashCode() {
        // Arrange
        LocalDateTime date = LocalDateTime.now();

        TransactionDTO dto1 = new TransactionDTO();
        dto1.setId("tx-001");
        dto1.setAccountId("acc-001");
        dto1.setType("WITHDRAWAL");
        dto1.setAmount(50.0);
        dto1.setBalanceAfter(450.0);
        dto1.setDate(date);

        TransactionDTO dto2 = new TransactionDTO();
        dto2.setId("tx-001");
        dto2.setAccountId("acc-001");
        dto2.setType("WITHDRAWAL");
        dto2.setAmount(50.0);
        dto2.setBalanceAfter(450.0);
        dto2.setDate(date);

        // Assert
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void shouldGenerateToString() {
        // Arrange
        TransactionDTO dto = new TransactionDTO();
        dto.setId("tx-001");
        dto.setType("DEPOSIT");

        // Act
        String result = dto.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("tx-001"));
        assertTrue(result.contains("DEPOSIT"));
    }

    @Test
    void shouldAllowNullValues() {
        // Arrange
        TransactionDTO dto = new TransactionDTO();

        // Assert
        assertNull(dto.getId());
        assertNull(dto.getAccountId());
        assertNull(dto.getType());
        assertNull(dto.getAmount());
        assertNull(dto.getBalanceAfter());
        assertNull(dto.getDate());
    }
}