package com.banking.cbs.account.product.repository;

import com.banking.cbs.account.product.entity.ProductParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductParameterRepository extends JpaRepository<ProductParameter, String> {

    List<ProductParameter> findByProductId(String productId);

    Optional<ProductParameter> findByProductIdAndParamKey(String productId, String paramKey);

    boolean existsByProductIdAndParamKey(String productId, String paramKey);

    void deleteByProductIdAndParamKey(String productId, String paramKey);
}
