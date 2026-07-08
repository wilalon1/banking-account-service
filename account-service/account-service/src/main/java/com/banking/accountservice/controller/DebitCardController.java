package com.banking.accountservice.controller;

import com.banking.accountservice.model.DebitCard;
import com.banking.accountservice.service.DebitCardService;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/debit-cards")
@RequiredArgsConstructor
public class DebitCardController {

    private final DebitCardService debitCardService;

    @PostMapping("/accounts/{accountId}")
    public Single<DebitCard> createDebitCard(
            @PathVariable String accountId,
            @RequestBody Map<String, String> body) {
        String customerId = body.get("customerId");
        return debitCardService.createDebitCard(customerId, accountId);
    }

    @PostMapping("/{cardNumber}/payments")
    public Single<String> payWithCard(
            @PathVariable String cardNumber,
            @RequestBody Map<String, Double> body) {
        Double amount = body.get("amount");
        return debitCardService.payWithDebitCard(cardNumber, amount);
    }

    @GetMapping("/customer/{customerId}")
    public Flowable<DebitCard> findCardsByCustomer(@PathVariable String customerId) {
        return debitCardService.findCardsByCustomer(customerId);
    }
}