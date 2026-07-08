package com.banking.accountservice.yanki.service;

import com.banking.accountservice.yanki.model.YankiWallet;
import io.reactivex.rxjava3.core.Single;

import java.math.BigDecimal;

public interface YankiWalletService {
    Single<YankiWallet> createWallet(YankiWallet wallet);
    Single<String> sendMoney(String fromPhone, String toPhone, BigDecimal amount);
    Single<YankiWallet> associateDebitCard(String walletId, String debitCardId);
}