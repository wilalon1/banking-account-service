package com.banking.accountservice.service.impl;

import com.banking.accountservice.client.CustomerClient;
import com.banking.accountservice.client.TransactionClient;
import com.banking.accountservice.dto.TransactionDTO;
import com.banking.accountservice.model.Account;
import com.banking.accountservice.repository.AccountRepository;
import com.banking.accountservice.service.AccountService;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {


    public static final Double COMMISSION_VALUE = 3.0;


    private final AccountRepository repository;
    private final CustomerClient customerClient;
    private final TransactionClient transactionClient;

    private final ReactiveRedisTemplate<String, Object> redis;


    @Override
    public Single<Account> create(Account account) {

        return Single.fromPublisher(repository.save(account))
                .flatMap(savedAccount ->

                        customerClient.getCustomer(savedAccount.getCustomerId())
                                .flatMap(customer -> {

                                    if ("VIP".equals(customer.getCustomerType())
                                            && "SAVINGS".equals(savedAccount.getType())) {


                                        return transactionClient
                                                .getTransactions(savedAccount.getId())
                                                .flatMap(transactions -> {


                                                    double promedio =
                                                            transactions.stream()
                                                                    .mapToDouble(TransactionDTO::getBalanceAfter)
                                                                    .average()
                                                                    .orElse(0);


                                                    if (promedio < 1000) {

                                                        return Single.error(
                                                                new RuntimeException(
                                                                        "VIP must maintain a minimum daily average of 1000"
                                                                )
                                                        );
                                                    }


                                                    return Single.just(savedAccount);
                                                });
                                    }


                                    return Single.just(savedAccount);

                                })
                );
    }


    @Override
    public Single<List<Account>> findAll() {

        return Single.fromPublisher(
                repository.findAll()
                        .collectList()
        );
    }


    @Override
    public Single<Account> findById(String id) {

        return Single.fromPublisher(
                repository.findById(id)
        );
    }


    @Override
    public Single<Account> update(String id, Account account) {

        return Single.fromPublisher(
                        repository.findById(id)
                )
                .flatMap(existing -> {


                    existing.setCustomerId(
                            account.getCustomerId()
                    );

                    existing.setType(
                            account.getType()
                    );

                    existing.setBalance(
                            account.getBalance()
                    );


                    return Single.fromPublisher(
                            repository.save(existing)
                    );

                });
    }


    @Override
    public Completable delete(String id) {

        return Completable.fromPublisher(
                repository.deleteById(id)
        );
    }


    @Override
    public double calculateCommission(
            String accountId,
            List<TransactionDTO> txs) {


        int free =
                getFreeTransactionsForAccount(accountId);


        int count =
                txs.size();


        int extra =
                count > free ? count - free : 0;


        return extra * COMMISSION_VALUE;
    }


    private int getFreeTransactionsForAccount(String accountId) {

        return 3;
    }


    @Override
    public Single<Boolean> saveAllToRedis(List<Account> accounts) {

        return Single.fromPublisher(
                redis.opsForValue()
                        .set(
                                "accounts",
                                accounts
                        )
        );
    }


    @Override
    public Single<List<Account>> findAllFromRedis() {

        return Single.fromPublisher(
                        redis.opsForValue()
                                .get("accounts")
                )
                .map(data -> (List<Account>) data);
    }

}