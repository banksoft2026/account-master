package com.banking.cbs.account.product.repository;

import com.banking.cbs.account.product.entity.ProductCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductChargeRepository extends JpaRepository<ProductCharge, String> {

    List<ProductCharge> findByProductId(String productId);

    List<ProductCharge> findByProductIdAndIsActiveTrue(String productId);
}
