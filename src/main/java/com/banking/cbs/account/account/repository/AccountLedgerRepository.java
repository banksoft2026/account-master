package com.banking.cbs.account.account.repository;

import com.banking.cbs.account.account.entity.AccountLedger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface AccountLedgerRepository extends JpaRepository<AccountLedger, String> {

    Page<AccountLedger> findByAccountIdOrderByValueDateDescPostingDateDesc(String accountId, Pageable pageable);

    Page<AccountLedger> findByAccountIdAndValueDateBetweenOrderByValueDateDescPostingDateDesc(
            String accountId, LocalDate from, LocalDate to, Pageable pageable);
}
