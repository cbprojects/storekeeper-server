package com.cafe.storekeeper.infrastructure.domain.provider.db.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.cafe.storekeeper.infrastructure.domain.provider.db.persistence.ProviderEntity;

public interface ProviderRepository extends MongoRepository<ProviderEntity, String> {

    Optional<ProviderEntity> findById(String id);

    Optional<ProviderEntity> findByDocumentNumber(String documentNumber);

}