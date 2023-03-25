package com.cafe.storekeeper.infrastructure.domain.client.db.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.cafe.storekeeper.infrastructure.domain.client.db.persistence.ClientEntity;

public interface ClientRepository extends MongoRepository<ClientEntity, String> {

    Optional<ClientEntity> findById(String id);

}
