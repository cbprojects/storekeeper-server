package com.cafe.storekeeper.infrastructure.domain.product.db.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.cafe.storekeeper.infrastructure.domain.product.db.persistence.ProductEntity;

public interface ProductRepository extends MongoRepository<ProductEntity, String> {

    Optional<ProductEntity> findById(String id);

}
