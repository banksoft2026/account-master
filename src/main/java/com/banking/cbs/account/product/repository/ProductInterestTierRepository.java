package com.banking.cbs.account.product.repository;

import com.banking.cbs.account.product.entity.ProductInterestTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductInterestTierRepository extends JpaRepository<ProductInterestTier, String> {

    List<ProductInterestTier> findByProductIdOrderByTierSequenceAsc(String productId);

    boolean existsByProductIdAndTierSequence(String productId, Short tierSequence);
}
