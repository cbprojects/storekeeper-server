package com.cafe.storekeeper.infrastructure.domain.employee.db.persistence;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import com.cafe.storekeeper.helper.constant.ConstantsTableNames;
import com.cafe.storekeeper.helper.enums.EDocumentType;
import com.cafe.storekeeper.infrastructure.adapter.model.AbstractEntity;

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
@Document(collection = ConstantsTableNames.EMPLOYEES)
public class EmployeeEntity extends AbstractEntity {

    @Field("name")
    private String name;

    @Field("document_number")
    @Indexed(unique = true)
    private String documentNumber;

    @Field("document_type")
    private EDocumentType documentType;

    @Field("image")
    private String image;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Field("hire_date")
    private LocalDateTime hireDate;

}