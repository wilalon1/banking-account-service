package com.banking.accountservice.yanki.service.impl;

import java.math.BigDecimal;
import com.banking.accountservice.yanki.model.YankiWallet;
import com.banking.accountservice.yanki.repository.YankiWalletRepository;
import com.banking.accountservice.yanki.service.YankiWalletService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.adapter.rxjava.RxJava3Adapter;

/**
 * Implementation of the YankiWalletService interface.
 *
 * This service contains the business logic for managing Yanki digital wallets,
 * including wallet creation, money transfers between users, and debit card association.
 *
 * Reactive programming is implemented using RxJava Single and Reactor adapters
 * to handle asynchronous database operations.
 */
@Service
@RequiredArgsConstructor
public class YankiWalletServiceImpl implements YankiWalletService {

    private final YankiWalletRepository walletRepository;
    /**
     * Creates a new Yanki wallet.
     *
     * When a wallet is created, its initial balance is automatically
     * initialized with zero before saving it into the database.
     *
     * @param wallet wallet information to be created
     * @return created Yanki wallet
     */
    @Override
    public Single<YankiWallet> createWallet(YankiWallet wallet) {
        wallet.setBalance(BigDecimal.ZERO);
        return RxJava3Adapter.monoToSingle(walletRepository.save(wallet));
    }
    /**
     * Transfers money from one Yanki wallet to another.
     *
     * The method validates that the sender has enough balance,
     * subtracts the amount from the sender wallet, adds the amount
     * to the receiver wallet, and saves both updated wallets.
     *
     * @param fromPhone sender wallet phone number
     * @param toPhone receiver wallet phone number
     * @param amount amount of money to transfer
     *
     * @return confirmation message when the transfer is completed
     *
     * @throws RuntimeException if the sender does not have enough balance
     */
    @Override
    public Single<String> sendMoney(String fromPhone, String toPhone, BigDecimal amount) {
        return RxJava3Adapter.monoToSingle(walletRepository.findByPhoneNumber(fromPhone))
                .flatMap(from -> {
                    if (from.getBalance().compareTo(amount) < 0) {
                        return Single.error(new RuntimeException("Insufficient balance"));
                    }

                    from.setBalance(from.getBalance().subtract(amount));

                    return RxJava3Adapter.monoToSingle(walletRepository.findByPhoneNumber(toPhone))
                            .flatMap(to -> {
                                to.setBalance(to.getBalance().add(amount));

                                return RxJava3Adapter.monoToSingle(walletRepository.save(from))
                                        .flatMap(f -> RxJava3Adapter.monoToSingle(walletRepository.save(to)))
                                        .map(x -> "Transfer completed successfully.");
                            });
                });
    }
    /**
     * Associates a debit card with an existing Yanki wallet.
     *
     * This method updates the wallet information by storing
     * the identifier of the associated debit card.
     *
     * @param walletId identifier of the Yanki wallet
     * @param debitCardId identifier of the debit card to associate
     *
     * @return updated Yanki wallet
     */
    @Override
    public Single<YankiWallet> associateDebitCard(String walletId, String debitCardId) {
        return RxJava3Adapter.monoToSingle(walletRepository.findById(walletId))
                .flatMap(wallet -> {
                    wallet.setAssociatedDebitCardId(debitCardId);
                    return RxJava3Adapter.monoToSingle(walletRepository.save(wallet));
                });
    }
}