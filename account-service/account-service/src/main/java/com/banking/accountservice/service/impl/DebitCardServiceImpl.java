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

@Service
@RequiredArgsConstructor
public class DebitCardServiceImpl implements DebitCardService {

    private final DebitCardRepository debitCardRepository;
    private final AccountRepository accountRepository;

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

    @Override
    public Flowable<DebitCard> findCardsByCustomer(String customerId) {
        return RxJava3Adapter.fluxToFlowable(debitCardRepository.findByCustomerId(customerId));
    }
}
