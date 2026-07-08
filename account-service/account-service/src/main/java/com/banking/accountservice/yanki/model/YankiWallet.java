package com.banking.accountservice.yanki.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "yanki_wallets")
public class YankiWallet {
    @Id
    private String id;
    private String documentType;      // ID CARD, CEX, PASSPORT
    private String documentNumber;
    private String phoneNumber;       // Only
    private String imei;
    private String email;
    private BigDecimal balance;
    private String associatedDebitCardId; // null if it is not associated
}