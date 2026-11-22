package com.cafe.storekeeper.infrastructure.domain.assignment.db.persistence;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.cafe.storekeeper.helper.constant.ConstantsTableNames;
import com.cafe.storekeeper.infrastructure.adapter.model.AbstractEntity;
import com.cafe.storekeeper.infrastructure.domain.client.db.persistence.ClientEntity;
import com.cafe.storekeeper.infrastructure.domain.employee.db.persistence.EmployeeEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Document(collection = ConstantsTableNames.ASSIGNMENTS)
public class AssignmentEntity extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @DBRef(lazy = true)
    @Field("employee")
    private EmployeeEntity employee;

    @DBRef(lazy = true)
    @Field("clients")
    // DTO/entity elements are serializable; serialization uses the concrete list implementation.
    @SuppressWarnings("serial")
    private List<ClientEntity> clients;

}