package com.banking.accountservice.yanki.controller;

import com.banking.accountservice.yanki.model.YankiWallet;
import com.banking.accountservice.yanki.service.YankiWalletService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;


/**
 * REST Controller responsible for managing Yanki digital wallets.
 *
 * This controller exposes endpoints to:
 * - Create new digital wallets.
 * - Transfer money between wallets using mobile phone numbers.
 * - Associate debit cards with Yanki wallets.
 *
 * The controller uses reactive programming with RxJava Single
 * to handle asynchronous responses.
 */
@RestController
@RequestMapping("/yanki/wallets")
@RequiredArgsConstructor
public class YankiWalletController {

    private final YankiWalletService yankiWalletService;

    /**
     * Creates a new Yanki digital wallet.
     *
     * This endpoint receives the wallet information and delegates
     * the creation process to the YankiWalletService.
     *
     * @param wallet wallet information to be created
     * @return created Yanki wallet
     */
    @PostMapping
    public Single<YankiWallet> createWallet(@RequestBody YankiWallet wallet) {
        return yankiWalletService.createWallet(wallet);
    }

    /**
     * Transfers money between two Yanki wallets.
     *
     * The transfer is performed using the sender and receiver
     * mobile phone numbers.
     *
     * Expected request body:
     * {
     *   "fromPhone": "999999999",
     *   "toPhone": "888888888",
     *   "amount": 100.00
     * }
     *
     * @param body request data containing sender phone,
     *             receiver phone and transfer amount
     * @return confirmation message after successful transfer
     */
    @PostMapping("/transfer")
    public Single<String> transfer(@RequestBody Map<String, Object> body) {
        String fromPhone = (String) body.get("fromPhone");
        String toPhone = (String) body.get("toPhone");
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        return yankiWalletService.sendMoney(fromPhone, toPhone, amount);
    }

    /**
     * Associates a debit card with an existing Yanki wallet.
     *
     * This operation links a debit card identifier to the wallet,
     * allowing the wallet to use the associated bank account.
     *
     * Expected request body:
     * {
     *   "debitCardId": "123456"
     * }
     *
     * @param walletId identifier of the Yanki wallet
     * @param body request containing debit card information
     * @return updated Yanki wallet with associated debit card
     */
    @PostMapping("/{walletId}/associate-debit-card")
    public Single<YankiWallet> associateDebitCard(
            @PathVariable String walletId,
            @RequestBody Map<String, String> body) {
        String debitCardId = body.get("debitCardId");
        return yankiWalletService.associateDebitCard(walletId, debitCardId);
    }
}