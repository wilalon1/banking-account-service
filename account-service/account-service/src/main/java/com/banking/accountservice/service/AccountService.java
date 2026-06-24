package com.banking.accountservice.service;

import com.banking.accountservice.model.Account;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface AccountService {

    Single<Account> create(Account account);

    Observable<Account> findAll();

    Single<Account> findById(String id);

    Single<Account> update(String id, Account account);

    Completable delete(String id);
}