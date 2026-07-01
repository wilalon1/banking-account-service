package com.banking.accountservice.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerDTOTest {

    @Test
    void shouldCreateUsingNoArgsConstructor() {

        CustomerDTO customer = new CustomerDTO();

        customer.setId("C1");
        customer.setCustomerType("VIP");

        assertEquals("C1", customer.getId());
        assertEquals("VIP", customer.getCustomerType());
    }

    @Test
    void shouldCreateUsingAllArgsConstructor() {

        CustomerDTO customer = new CustomerDTO("C2", "NORMAL");

        assertEquals("C2", customer.getId());
        assertEquals("NORMAL", customer.getCustomerType());
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {

        CustomerDTO customer1 = new CustomerDTO("C1", "VIP");
        CustomerDTO customer2 = new CustomerDTO("C1", "VIP");

        assertEquals(customer1, customer2);
        assertEquals(customer1.hashCode(), customer2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {

        CustomerDTO customer1 = new CustomerDTO("C1", "VIP");
        CustomerDTO customer2 = new CustomerDTO("C2", "NORMAL");

        assertNotEquals(customer1, customer2);
    }

    @Test
    void shouldGenerateToString() {

        CustomerDTO customer = new CustomerDTO("C1", "VIP");

        String result = customer.toString();

        assertNotNull(result);
        assertTrue(result.contains("C1"));
        assertTrue(result.contains("VIP"));
    }
}