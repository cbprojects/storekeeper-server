package com.cafe.storekeeper.infrastructure.domain.company.db.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.cafe.storekeeper.infrastructure.domain.company.db.persistence.CompanyEntity;

public interface CompanyRepository extends MongoRepository<CompanyEntity, String> {

    Optional<CompanyEntity> findById(String id);

}
