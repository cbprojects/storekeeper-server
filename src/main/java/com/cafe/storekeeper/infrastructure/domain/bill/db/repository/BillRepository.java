package com.cafe.storekeeper.infrastructure.domain.bill.db.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.cafe.storekeeper.infrastructure.domain.bill.db.persistence.BillEntity;

public interface BillRepository extends MongoRepository<BillEntity, String> {

    Optional<BillEntity> findById(String id);

}
