package com.banking.cbs.account.account.repository;

import com.banking.cbs.account.account.entity.AccountParameterOverride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountParameterOverrideRepository extends JpaRepository<AccountParameterOverride, String> {

    List<AccountParameterOverride> findByAccountIdAndIsActiveTrue(String accountId);

    Optional<AccountParameterOverride> findByAccountIdAndParamKeyAndIsActiveTrue(String accountId, String paramKey);
}
