package com.banking.cbs.account.account.repository;

import com.banking.cbs.account.account.entity.AccountDailyPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountDailyPositionRepository extends JpaRepository<AccountDailyPosition, String> {

    Optional<AccountDailyPosition> findByAccountIdAndPositionDateAndCurrencyCode(
            String accountId, LocalDate positionDate, String currencyCode);

    List<AccountDailyPosition> findByAccountIdAndPositionDateBetweenOrderByPositionDateDesc(
            String accountId, LocalDate from, LocalDate to);

    List<AccountDailyPosition> findByPositionDateAndEodConfirmedFalse(LocalDate date);
}
