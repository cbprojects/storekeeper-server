package com.cafe.storekeeper.infrastructure.domain.bill.rest.controller.v1;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cafe.storekeeper.helper.util.UtilResponse;
import com.cafe.storekeeper.infrastructure.adapter.IBillService;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardPaginationRestResponse;
import com.cafe.storekeeper.infrastructure.domain.bill.rest.model.dto.BillDTO;
import com.cafe.storekeeper.infrastructure.domain.bill.rest.model.request.StatusBillRequest;
import com.cafe.storekeeper.infrastructure.exception.ModelException;
import com.cafe.storekeeper.infrastructure.exception.RequestException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/bill/v1")
@Slf4j
@RequiredArgsConstructor
public class BillRestController {

    private final IBillService service;

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StandardPaginationRestResponse> find(HttpServletRequest request)
            throws ModelException, RequestException {
        log.info("|==========> (START FIND)");
        List<BillDTO> result = this.service.find(UtilResponse.buildCriteriaParams(request));
        StandardPaginationRestResponse response = UtilResponse.fillResponsePagination(result, result.size(),
                result.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BillDTO> findById(@PathVariable String id) throws ModelException {
        log.info("|==========> (START FIND BY ID)");
        return new ResponseEntity<>(this.service.findById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BillDTO> save(@RequestBody BillDTO request) throws ModelException {
        log.info("|==========> (SAVE/UPDATE)");
        return new ResponseEntity<>(this.service.save(request), HttpStatus.OK);
    }

    @PutMapping(value = "/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BillDTO> updateStatus(@RequestBody StatusBillRequest request) throws ModelException {
        log.info("|==========> (START UPDATE STATUS)");
        return new ResponseEntity<>(this.service.updateStatus(request), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> delete(@PathVariable String id) throws ModelException {
        log.info("|==========> (START DELETE)");
        return new ResponseEntity<>(this.service.delete(id), HttpStatus.OK);
    }

}