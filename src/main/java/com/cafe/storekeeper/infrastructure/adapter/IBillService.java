package com.cafe.storekeeper.infrastructure.adapter;

import java.util.List;
import java.util.Map;

import com.cafe.storekeeper.infrastructure.domain.bill.rest.model.dto.BillDTO;
import com.cafe.storekeeper.infrastructure.domain.bill.rest.model.request.StatusBillRequest;
import com.cafe.storekeeper.infrastructure.exception.ModelException;

public interface IBillService {

    List<BillDTO> find(Map<String, String> filters) throws ModelException;

    BillDTO findById(String id) throws ModelException;

    BillDTO save(BillDTO dto) throws ModelException;

    BillDTO updateStatus(StatusBillRequest request) throws ModelException;

    boolean delete(String id) throws ModelException;

}
