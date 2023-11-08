package com.cafe.storekeeper.infrastructure.domain.assignment.db.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.cafe.storekeeper.infrastructure.domain.assignment.db.persistence.AssignmentEntity;

public interface AssignmentRepository extends MongoRepository<AssignmentEntity, String> {

    @Query("{ 'employee.$id' : ?0 }")
    Optional<AssignmentEntity> findByEmployeeId(ObjectId employeeId);

}