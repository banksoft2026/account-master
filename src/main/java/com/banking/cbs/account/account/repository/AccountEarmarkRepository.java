package com.banking.cbs.account.account.repository;

import com.banking.cbs.account.account.entity.AccountEarmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountEarmarkRepository extends JpaRepository<AccountEarmark, String> {

    List<AccountEarmark> findByAccountIdAndIsActiveTrue(String accountId);
}
