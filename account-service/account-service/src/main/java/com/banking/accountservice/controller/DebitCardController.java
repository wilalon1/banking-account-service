package com.banking.accountservice.controller;

import com.banking.accountservice.model.DebitCard;
import com.banking.accountservice.service.DebitCardService;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST Controller responsible for managing debit card operations.
 *
 * This controller provides endpoints for:
 * - Creating debit cards associated with customer accounts.
 * - Making payments using debit cards.
 * - Retrieving debit cards by customer.
 *
 * Reactive programming is used through RxJava Single and Flowable
 * to handle asynchronous responses.
 */
@Tag(
        name = "DebiCard",
        description = "DebiCard-related transactions"
)
@RestController
@RequestMapping("/debit-cards")
@RequiredArgsConstructor
public class DebitCardController {

    private final DebitCardService debitCardService;

    /**
     * Creates a new debit card associated with an account.
     *
     * The customer identifier is received in the request body,
     * while the account identifier is provided as a path parameter.
     *
     * Example request body:
     * {
     *     "customerId": "12345"
     * }
     *
     * @param accountId identifier of the account associated with the card
     * @param body request body containing customer information
     *
     * @return created debit card information
     */

    @PostMapping("/accounts/{accountId}")
    public Single<DebitCard> createDebitCard(
            @PathVariable String accountId,
            @RequestBody Map<String, String> body) {
        String customerId = body.get("customerId");
        return debitCardService.createDebitCard(customerId, accountId);
    }
    /**
     * Performs a payment using a debit card.
     *
     * The method validates the card status and account balance
     * before completing the payment.
     *
     * Example request body:
     * {
     *     "amount": 150.00
     * }
     *
     * @param cardNumber debit card number used for payment
     * @param body request body containing payment amount
     *
     * @return payment confirmation message
     */
    @PostMapping("/{cardNumber}/payments")
    public Single<String> payWithCard(
            @PathVariable String cardNumber,
            @RequestBody Map<String, Double> body) {
        Double amount = body.get("amount");
        return debitCardService.payWithDebitCard(cardNumber, amount);
    }

    /**
     * Retrieves all debit cards associated with a customer.
     *
     * @param customerId identifier of the customer
     *
     * @return stream of debit cards belonging to the customer
     */
    @GetMapping("/customer/{customerId}")
    public Flowable<DebitCard> findCardsByCustomer(@PathVariable String customerId) {
        return debitCardService.findCardsByCustomer(customerId);
    }
}