package com.cafe.storekeeper.infrastructure.domain.assignment.rest.model.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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

    @Valid
    @NotNull(message = ConstantsMessage.ASSIGNMENT_NO_EMPLOYEE_MESSAGE)
    @JsonProperty("employee")
    private EmployeeDTO employee;

    @Valid
    @NotEmpty(message = ConstantsMessage.ASSIGNMENT_EMPTY_CLIENTS_MESSAGE)
    @JsonProperty("clients")
    private List<ClientDTO> clients;

}