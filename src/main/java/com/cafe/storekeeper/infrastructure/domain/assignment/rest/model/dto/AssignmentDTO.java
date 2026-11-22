package com.cafe.storekeeper.infrastructure.domain.assignment.rest.model.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import com.cafe.storekeeper.helper.constant.ConstantsMessage;
import com.cafe.storekeeper.infrastructure.adapter.model.AbstractDTO;
import com.cafe.storekeeper.infrastructure.domain.client.rest.model.dto.ClientDTO;
import com.cafe.storekeeper.infrastructure.domain.employee.rest.model.dto.EmployeeDTO;
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
public class AssignmentDTO extends AbstractDTO {
    private static final long serialVersionUID = 1L;


    @Valid
    @NotNull(message = ConstantsMessage.ASSIGNMENT_NO_EMPLOYEE_MESSAGE)
    @JsonProperty("employee")
    private EmployeeDTO employee;

    @Valid
    @NotEmpty(message = ConstantsMessage.ASSIGNMENT_EMPTY_CLIENTS_MESSAGE)
    @JsonProperty("clients")
    // DTO/entity elements are serializable; serialization uses the concrete list implementation.
    @SuppressWarnings("serial")
    private List<ClientDTO> clients;

}