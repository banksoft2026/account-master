package com.banking.cbs.account.account.repository;

import com.banking.cbs.account.account.entity.AccountParameters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountParametersRepository extends JpaRepository<AccountParameters, String> {

    Optional<AccountParameters> findByAccountId(String accountId);
}
