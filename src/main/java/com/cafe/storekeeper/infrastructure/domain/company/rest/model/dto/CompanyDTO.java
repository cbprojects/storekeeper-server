package com.cafe.storekeeper.infrastructure.domain.company.rest.model.dto;

import com.cafe.storekeeper.helper.enumerated.EDocumentType;
import com.cafe.storekeeper.infrastructure.adapter.model.AbstractDTO;
import com.cafe.storekeeper.infrastructure.domain.company.db.pojo.ContactCompanyPojo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyDTO extends AbstractDTO {

    @JsonProperty("business_name")
    private String businessName;

    @JsonProperty("info")
    private ContactCompanyPojo info;

    @JsonProperty("document_number")
    private String documentNumber;

    @JsonProperty("document_type")
    private EDocumentType documentType;

    @JsonProperty("image")
    private String image;

}
