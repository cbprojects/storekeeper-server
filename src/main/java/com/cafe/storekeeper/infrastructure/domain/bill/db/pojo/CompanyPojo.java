package com.cafe.storekeeper.infrastructure.domain.bill.db.pojo;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Field;

import com.cafe.storekeeper.helper.enumerated.EDocumentType;

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
public class CompanyPojo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Field("business_name")
    private String businessName;

    @Field("document_number")
    private String documentNumber;

    @Field("document_type")
    private EDocumentType documentType;

}