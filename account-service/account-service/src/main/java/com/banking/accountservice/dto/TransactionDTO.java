package com.banking.accountservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionDTO {

    private String id;
    private String accountId;
    private String type;
    private Double amount;
    private Double balanceAfter;
    private LocalDateTime date;


}