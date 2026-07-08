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

@RestController
@RequestMapping("/yanki/wallets")
@RequiredArgsConstructor
public class YankiWalletController {

    private final YankiWalletService yankiWalletService;

    // Create wallet
    @PostMapping
    public Single<YankiWallet> createWallet(@RequestBody YankiWallet wallet) {
        return yankiWalletService.createWallet(wallet);
    }

    // Transfer money to another cell phone number
    @PostMapping("/transfer")
    public Single<String> transfer(@RequestBody Map<String, Object> body) {
        String fromPhone = (String) body.get("fromPhone");
        String toPhone = (String) body.get("toPhone");
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        return yankiWalletService.sendMoney(fromPhone, toPhone, amount);
    }

    // Link bank debit card
    @PostMapping("/{walletId}/associate-debit-card")
    public Single<YankiWallet> associateDebitCard(
            @PathVariable String walletId,
            @RequestBody Map<String, String> body) {
        String debitCardId = body.get("debitCardId");
        return yankiWalletService.associateDebitCard(walletId, debitCardId);
    }
}