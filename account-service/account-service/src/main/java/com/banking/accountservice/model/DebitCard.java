package com.banking.accountservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "debit_cards")
public class DebitCard {

    @Id
    private String id;

    private String customerId;   // Reference to the owner client

    private String accountId;    // Associated account ID

    private String cardNumber;   // Unique card number (can be generated)

    private String status;       // ACTIVE, LOCKED, etc.

    private String expiryDate;   // Typical "MM/YY" format for cards

    private String cvv;


}
