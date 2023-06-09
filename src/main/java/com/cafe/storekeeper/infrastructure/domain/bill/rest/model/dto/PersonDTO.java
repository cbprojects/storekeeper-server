package com.cafe.storekeeper.infrastructure.domain.bill.rest.model.dto;

import java.io.Serializable;

import com.cafe.storekeeper.helper.enumerated.EDocumentType;
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
public class PersonDTO implements Serializable {
    private static final long serialVersionUID = 1430828715777440238L;

    @JsonProperty("name")
    private String name;

    @JsonProperty("document_number")
    private String documentNumber;

    @JsonProperty("document_type")
    private EDocumentType documentType;

}