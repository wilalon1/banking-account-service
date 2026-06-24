package com.banking.accountservice.dto;

import lombok.Data;

@Data
public class CustomerDTO {
    private String id;
    private String customerType; // VIP, NORMAL
}