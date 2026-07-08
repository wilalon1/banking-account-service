package com.banking.accountservice.yanki.service.impl;

import java.math.BigDecimal;
import com.banking.accountservice.yanki.model.YankiWallet;
import com.banking.accountservice.yanki.repository.YankiWalletRepository;
import com.banking.accountservice.yanki.service.YankiWalletService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.adapter.rxjava.RxJava3Adapter;
@Service
@RequiredArgsConstructor
public class YankiWalletServiceImpl implements YankiWalletService {

    private final YankiWalletRepository walletRepository;

    @Override
    public Single<YankiWallet> createWallet(YankiWallet wallet) {
        wallet.setBalance(BigDecimal.ZERO);
        return RxJava3Adapter.monoToSingle(walletRepository.save(wallet));
    }

    @Override
    public Single<String> sendMoney(String fromPhone, String toPhone, BigDecimal amount) {
        return RxJava3Adapter.monoToSingle(walletRepository.findByPhoneNumber(fromPhone))
                .flatMap(from -> {
                    if (from.getBalance().compareTo(amount) < 0) {
                        return Single.error(new RuntimeException("Saldo insuficiente"));
                    }

                    from.setBalance(from.getBalance().subtract(amount));

                    return RxJava3Adapter.monoToSingle(walletRepository.findByPhoneNumber(toPhone))
                            .flatMap(to -> {
                                to.setBalance(to.getBalance().add(amount));

                                return RxJava3Adapter.monoToSingle(walletRepository.save(from))
                                        .flatMap(f -> RxJava3Adapter.monoToSingle(walletRepository.save(to)))
                                        .map(x -> "Transferencia realizada correctamente.");
                            });
                });
    }

    @Override
    public Single<YankiWallet> associateDebitCard(String walletId, String debitCardId) {
        return RxJava3Adapter.monoToSingle(walletRepository.findById(walletId))
                .flatMap(wallet -> {
                    wallet.setAssociatedDebitCardId(debitCardId);
                    return RxJava3Adapter.monoToSingle(walletRepository.save(wallet));
                });
    }
}