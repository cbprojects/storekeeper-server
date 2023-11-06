package com.cafe.storekeeper.infrastructure.domain.employee.db.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.cafe.storekeeper.infrastructure.domain.employee.db.persistence.EmployeeEntity;

public interface EmployeeRepository extends MongoRepository<EmployeeEntity, String> {

    Optional<EmployeeEntity> findById(String id);

}
