package com.cafe.storekeeper.infrastructure.domain.product_category.db.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.cafe.storekeeper.infrastructure.domain.product_category.db.persistence.ProductCategoryEntity;

public interface ProductCategoryRepository extends MongoRepository<ProductCategoryEntity, String> {

    Optional<ProductCategoryEntity> findById(String id);

}
