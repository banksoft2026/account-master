package com.banking.cbs.account.product.repository;

import com.banking.cbs.account.product.entity.AccountProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountProductRepository extends JpaRepository<AccountProduct, String> {

    List<AccountProduct> findByLifecycleStatus(String lifecycleStatus);

    List<AccountProduct> findByAccountTypeAndLifecycleStatus(String accountType, String lifecycleStatus);

    List<AccountProduct> findByTargetSegment(String targetSegment);

    Optional<AccountProduct> findByProductCode(String productCode);

    boolean existsByProductCode(String productCode);
}
