package com.cafe.storekeeper.infrastructure.domain.product_category.rest.controller.v1;

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
import com.cafe.storekeeper.infrastructure.adapter.IProductCategoryService;
import com.cafe.storekeeper.infrastructure.adapter.model.StandardPaginationRestResponse;
import com.cafe.storekeeper.infrastructure.domain.product_category.rest.model.dto.ProductCategoryDTO;
import com.cafe.storekeeper.infrastructure.exception.ModelException;
import com.cafe.storekeeper.infrastructure.exception.RequestException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/product_category/v1")
@Slf4j
@RequiredArgsConstructor
public class ProductCategoryRestController {

    private final IProductCategoryService service;

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StandardPaginationRestResponse> find(HttpServletRequest request)
            throws ModelException, RequestException {
        log.info("|==========> (START FIND)");
        List<ProductCategoryDTO> result = this.service.find(UtilResponse.buildCriteriaParams(request));
        StandardPaginationRestResponse response = UtilResponse.fillResponsePagination(result, result.size(),
                result.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductCategoryDTO> findById(@PathVariable String id) throws ModelException {
        log.info("|==========> (START FIND BY ID)");
        return new ResponseEntity<>(this.service.findById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductCategoryDTO> save(@RequestBody ProductCategoryDTO request) throws ModelException {
        log.info("|==========> (START SAVE)");
        return new ResponseEntity<>(this.service.save(request), HttpStatus.OK);
    }

}