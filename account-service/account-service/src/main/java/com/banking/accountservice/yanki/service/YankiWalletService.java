package com.banking.accountservice.yanki.service;

import com.banking.accountservice.yanki.model.YankiWallet;
import io.reactivex.rxjava3.core.Single;

import java.math.BigDecimal;
/**
 * Service interface that defines the operations available for managing
 * Yanki digital wallets.
 *
 * This service provides functionalities for creating wallets,
 * transferring money between users, and associating debit cards.
 *
 * The methods use RxJava Single to handle asynchronous operations.
 */
public interface YankiWalletService {
    /**
     * Creates a new Yanki digital wallet.
     *
     * @param wallet wallet information to be created
     * @return a Single containing the created wallet
     */
    Single<YankiWallet> createWallet(YankiWallet wallet);
    /**
     * Transfers money from one Yanki wallet to another.
     *
     * The transfer is performed using the phone numbers associated
     * with the sender and receiver wallets.
     *
     * @param fromPhone sender wallet phone number
     * @param toPhone receiver wallet phone number
     * @param amount amount of money to transfer
     *
     * @return a Single containing the transfer result message
     */
    Single<String> sendMoney(String fromPhone, String toPhone, BigDecimal amount);
    /**
     * Associates a debit card with an existing Yanki wallet.
     *
     * @param walletId identifier of the Yanki wallet
     * @param debitCardId identifier of the debit card to associate
     *
     * @return a Single containing the updated wallet information
     */
    Single<YankiWallet> associateDebitCard(String walletId, String debitCardId);
}