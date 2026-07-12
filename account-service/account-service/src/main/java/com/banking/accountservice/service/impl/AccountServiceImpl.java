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

/**
 * Implementation of the AccountService interface.
 *
 * This service contains the business logic for account management operations,
 * including account creation, retrieval, update, deletion, transaction
 * commission calculation, and Redis cache operations.
 *
 * It communicates with Customer Service and Transaction Service to validate
 * business rules related to account creation.
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {


    public static final Double COMMISSION_VALUE = 3.0;


    private final AccountRepository repository;
    private final CustomerClient customerClient;
    private final TransactionClient transactionClient;

    private final ReactiveRedisTemplate<String, Object> redis;

    /**
     * Creates a new account.
     *
     * For VIP customers with savings accounts, this method validates that
     * the account maintains a minimum daily average balance of 1000.
     *
     * @param account account information to be created
     * @return created account wrapped in a reactive Single response
     */
    @Override
    public Single<Account> create(Account account) {

        return customerClient.getCustomer(account.getCustomerId())
                .flatMap(customer -> {

                    // Validate VIP customer rules
                    if ("VIP".equals(customer.getCustomerType())
                            && "SAVINGS".equals(account.getType())) {


                        return transactionClient
                                .getTransactions(account.getId())
                                .flatMap(transactions -> {


                                    double averageBalance =
                                            transactions.stream()
                                                    .mapToDouble(TransactionDTO::getBalanceAfter)
                                                    .average()
                                                    .orElse(0);


                                    // VIP customers must maintain minimum balance
                                    if (averageBalance < 1000) {

                                        return Single.error(
                                                new RuntimeException(
                                                        "VIP must maintain a minimum daily average of 1000"
                                                )
                                        );
                                    }


                                    return Single.fromPublisher(
                                            repository.save(account)
                                    );
                                });

                    }


                    // Save account for non VIP customers
                    return Single.fromPublisher(
                            repository.save(account)
                    );

                });
    }

    /**
     * Retrieves all accounts registered in the system.
     *
     * @return list of accounts wrapped in a reactive Single response
     */
    @Override
    public Single<List<Account>> findAll() {

        return Single.fromPublisher(
                repository.findAll()
                        .collectList()
        );
    }

    /**
     * Retrieves an account by its identifier.
     *
     * @param id account identifier
     * @return account information wrapped in a reactive Single response
     */
    @Override
    public Single<Account> findById(String id) {

        return Single.fromPublisher(
                repository.findById(id)
        );
    }

    /**
     * Updates an existing account.
     *
     * The account information is updated using the provided identifier.
     *
     * @param id account identifier to update
     * @param account account information with updated values
     * @return updated account wrapped in a reactive Single response
     */
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

    /**
     * Deletes an account by identifier.
     *
     * @param id account identifier to delete
     * @return completion signal using reactive Completable
     */
    @Override
    public Completable delete(String id) {

        return Completable.fromPublisher(
                repository.deleteById(id)
        );
    }

    /**
     * Calculates the commission amount for account transactions.
     *
     * The calculation applies a commission only when the number of transactions
     * exceeds the allowed free transaction limit.
     *
     * @param accountId account identifier
     * @param txs list of transactions to evaluate
     * @return calculated commission value
     */
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

    /**
     * Retrieves the number of free transactions allowed for an account.
     *
     * @param accountId account identifier
     * @return number of free transactions allowed
     */
    private int getFreeTransactionsForAccount(String accountId) {

        return 3;
    }

    /**
     * Stores accounts information in Redis cache.
     *
     * @param accounts list of accounts to store
     * @return operation result wrapped in a reactive Single response
     */
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

    /**
     * Retrieves accounts information from Redis cache.
     *
     * @return cached accounts list wrapped in a reactive Single response
     */
    @Override
    public Single<List<Account>> findAllFromRedis() {

        return Single.fromPublisher(
                        redis.opsForValue()
                                .get("accounts")
                )
                .map(data -> (List<Account>) data);
    }

}