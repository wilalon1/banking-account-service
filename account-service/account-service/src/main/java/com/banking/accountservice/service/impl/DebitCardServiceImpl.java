package com.banking.accountservice.service.impl;

import com.banking.accountservice.model.DebitCard;
import com.banking.accountservice.model.Account;
import com.banking.accountservice.repository.DebitCardRepository;
import com.banking.accountservice.repository.AccountRepository;
import com.banking.accountservice.service.DebitCardService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.Flowable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.adapter.rxjava.RxJava3Adapter;

import java.util.UUID;
/**
 * Implementation of the DebitCardService interface.
 *
 * This service manages debit card operations including card creation,
 * payment processing, and customer card retrieval.
 *
 * It also validates card status and available account balance before
 * processing debit card payments.
 */
@Service
@RequiredArgsConstructor
public class DebitCardServiceImpl implements DebitCardService {

    private final DebitCardRepository debitCardRepository;
    private final AccountRepository accountRepository;

    /**
     * Creates a new debit card associated with an account.
     *
     * A unique card number is generated automatically and the card
     * is created with ACTIVE status.
     *
     * @param customerId customer identifier
     * @param accountId linked account identifier
     * @return created debit card wrapped in a reactive Single response
     */
    @Override
    public Single<DebitCard> createDebitCard(String customerId, String accountId) {
        String cardNumber = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);

        DebitCard card = DebitCard.builder()
                .customerId(customerId)
                .accountId(accountId)
                .cardNumber(cardNumber)
                .status("ACTIVE")
                .expiryDate("12/30")
                .cvv("123")
                .build();

        return RxJava3Adapter.monoToSingle(debitCardRepository.save(card));
    }

    /**
     * Processes a payment using a debit card.
     *
     * The method validates that:
     * - The card is active.
     * - The linked account has sufficient balance.
     *
     * If both validations succeed, the payment amount is deducted
     * from the account balance.
     *
     * @param cardNumber debit card number
     * @param amount payment amount
     * @return success message wrapped in a reactive Single response
     */
    @Override
    public Single<String> payWithDebitCard(String cardNumber, Double amount) {
        return RxJava3Adapter.monoToSingle(debitCardRepository.findByCardNumber(cardNumber))
                .flatMap(card -> {
                    if (!"ACTIVE".equals(card.getStatus())) {
                        return Single.error(new RuntimeException("Card blocked"));
                    }
                    return RxJava3Adapter.monoToSingle(accountRepository.findById(card.getAccountId()))
                            .flatMap(account -> {
                                if (account.getBalance() < amount) {
                                    return Single.error(new RuntimeException("Insufficient balance"));
                                }
                                account.setBalance(account.getBalance() - amount);
                                return RxJava3Adapter.monoToSingle(accountRepository.save(account))
                                        .map(saved -> "Payment made successfully.");
                            });
                });
    }
    /**
     * Retrieves all debit cards associated with a customer.
     *
     * @param customerId customer identifier
     * @return stream of debit cards using a reactive Flowable
     */
    @Override
    public Flowable<DebitCard> findCardsByCustomer(String customerId) {
        return RxJava3Adapter.fluxToFlowable(debitCardRepository.findByCustomerId(customerId));
    }
}
