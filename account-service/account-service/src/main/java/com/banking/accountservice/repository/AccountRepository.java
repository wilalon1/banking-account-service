package com.banking.accountservice.repository;

import com.banking.accountservice.model.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;


public interface AccountRepository extends ReactiveMongoRepository<Account, String> {

}