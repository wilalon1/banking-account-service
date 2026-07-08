package com.banking.accountservice.yanki.repository;

import com.banking.accountservice.yanki.model.YankiWallet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface YankiWalletRepository extends ReactiveMongoRepository<YankiWallet, String> {
    Mono<YankiWallet> findByPhoneNumber(String phoneNumber);
    Mono<YankiWallet> findByDocumentNumber(String documentNumber);
}