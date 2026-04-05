package com.banking.cbs.account.account.repository;

import com.banking.cbs.account.account.entity.AccountMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountMasterRepository extends JpaRepository<AccountMaster, String> {

    List<AccountMaster> findByProductId(String productId);

    List<AccountMaster> findByEntityId(String entityId);

    List<AccountMaster> findByCustomerId(String customerId);

    List<AccountMaster> findByBranchCodeAndAccountStatus(String branchCode, String accountStatus);

    List<AccountMaster> findByAccountStatus(String accountStatus);

    long countByProductIdAndAccountStatusNot(String productId, String accountStatus);
}
