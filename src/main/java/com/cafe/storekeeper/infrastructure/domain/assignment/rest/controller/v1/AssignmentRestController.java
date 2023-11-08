package com.cafe.storekeeper.infrastructure.domain.assignment.rest.controller.v1;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cafe.storekeeper.infrastructure.adapter.IAssignmentService;
import com.cafe.storekeeper.infrastructure.domain.assignment.rest.model.dto.AssignmentDTO;
import com.cafe.storekeeper.infrastructure.exception.ModelException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/assignment/v1")
@Slf4j
@RequiredArgsConstructor
public class AssignmentRestController {

    private final IAssignmentService service;

    @GetMapping(value = "/employees/{employeeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AssignmentDTO> findByEmployeeId(@PathVariable String employeeId) throws ModelException {
        log.info("|==========> (START FIND BY ID)");
        return new ResponseEntity<>(this.service.findByEmployeeId(employeeId), HttpStatus.OK);
    }

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AssignmentDTO> save(@RequestBody AssignmentDTO request) throws ModelException {
        log.info("|==========> (SAVE/UPDATE)");
        return new ResponseEntity<>(this.service.save(request), HttpStatus.OK);
    }

}