package com.banking.accountservice.service;

import com.banking.accountservice.model.DebitCard;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.Flowable;

public interface DebitCardService {

    Single<DebitCard> createDebitCard(String customerId, String accountId);

    Single<String> payWithDebitCard(String cardNumber, Double amount);

    Flowable<DebitCard> findCardsByCustomer(String customerId);
}
