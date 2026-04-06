package com.banking.cbs.account.account.repository;

import com.banking.cbs.account.account.entity.AccountUncollected;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountUncollectedRepository extends JpaRepository<AccountUncollected, String> {

    List<AccountUncollected> findByAccountIdAndCollectionStatusIn(String accountId, List<String> statuses);

    List<AccountUncollected> findByAccountIdOrderByPresentedDateDesc(String accountId);

    Optional<AccountUncollected> findByAccountIdAndInstrumentRef(String accountId, String ref);
}
