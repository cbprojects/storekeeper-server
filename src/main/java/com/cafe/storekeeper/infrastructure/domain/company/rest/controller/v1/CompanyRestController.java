package com.cafe.storekeeper.infrastructure.domain.company.rest.controller.v1;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cafe.storekeeper.helper.util.UtilResponse;
import com.cafe.storekeeper.infrastructure.adapter.ICompanyService;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardPaginationRestResponse;
import com.cafe.storekeeper.infrastructure.domain.company.rest.model.dto.CompanyDTO;
import com.cafe.storekeeper.infrastructure.exception.ModelException;
import com.cafe.storekeeper.infrastructure.exception.RequestException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/company/v1")
@Slf4j
@RequiredArgsConstructor
public class CompanyRestController {

    private final ICompanyService service;

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StandardPaginationRestResponse> find(HttpServletRequest request)
            throws ModelException, RequestException {
        log.info("|==========> (START FIND)");
        List<CompanyDTO> result = this.service.find(UtilResponse.buildCriteriaParams(request));
        StandardPaginationRestResponse response = UtilResponse.fillResponsePagination(result, result.size(),
                result.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyDTO> findById(@PathVariable String id) throws ModelException {
        log.info("|==========> (START FIND BY ID)");
        return new ResponseEntity<>(this.service.findById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyDTO> save(@RequestBody CompanyDTO request) throws ModelException {
        log.info("|==========> (START SAVE)");
        return new ResponseEntity<>(this.service.save(request), HttpStatus.OK);
    }

}