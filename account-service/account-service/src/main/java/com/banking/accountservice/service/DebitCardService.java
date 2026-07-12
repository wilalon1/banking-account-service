package com.banking.accountservice.service;

import com.banking.accountservice.model.DebitCard;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.Flowable;
/**
 * Service interface that defines debit card management operations.
 *
 * This interface provides reactive methods for creating debit cards,
 * processing payments using debit cards, and retrieving debit cards
 * associated with a customer.
 *
 * RxJava 3 reactive types are used to support asynchronous and
 * non-blocking communication.
 */
public interface DebitCardService {
    /**
     * Creates a new debit card associated with a customer account.
     *
     * @param customerId customer identifier
     * @param accountId account identifier associated with the debit card
     * @return created debit card wrapped in a reactive Single response
     */
    Single<DebitCard> createDebitCard(String customerId, String accountId);
    /**
     * Processes a payment using a debit card.
     *
     * The operation validates that the card is active and that the
     * associated account has enough balance before completing the payment.
     *
     * @param cardNumber debit card number used for payment
     * @param amount payment amount to be processed
     * @return payment result message wrapped in a reactive Single response
     */
    Single<String> payWithDebitCard(String cardNumber, Double amount);
    /**
     * Retrieves all debit cards associated with a customer.
     *
     * @param customerId customer identifier
     * @return stream of debit cards using a reactive Flowable
     */
    Flowable<DebitCard> findCardsByCustomer(String customerId);
}
