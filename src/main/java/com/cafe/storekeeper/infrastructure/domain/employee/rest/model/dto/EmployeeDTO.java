package com.cafe.storekeeper.infrastructure.domain.employee.rest.model.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.cafe.storekeeper.helper.enums.EDocumentType;
import com.cafe.storekeeper.infrastructure.adapter.model.AbstractDTO;
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
public class EmployeeDTO extends AbstractDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("document_number")
    private String documentNumber;

    @JsonProperty("document_type")
    private EDocumentType documentType;

    @JsonProperty("image")
    private String image;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonProperty("hire_date")
    private LocalDateTime hireDate;

}