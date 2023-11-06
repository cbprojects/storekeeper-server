package com.cafe.storekeeper.infrastructure.domain.assignment.db.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cafe.storekeeper.infrastructure.domain.assignment.db.persistence.AssignmentEntity;

public interface AssignmentRepository extends MongoRepository<AssignmentEntity, String> {

    @Query("SELECT a FROM AssignmentEntity a WHERE a.employee.id = :employeeId")
    Optional<AssignmentEntity> findByEmployeeId(@Param("employeeId") String employeeId);

}