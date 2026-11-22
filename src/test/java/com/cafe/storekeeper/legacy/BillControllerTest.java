package com.cafe.storekeeper.legacy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import com.cafe.storekeeper.infrastructure.adapter.IBillService;
import com.cafe.storekeeper.infrastructure.domain.bill.rest.controller.v1.BillRestController;
import com.cafe.storekeeper.infrastructure.domain.bill.rest.model.dto.BillDTO;
class BillControllerTest {
 @Test void crudDelegatesAndReturnsResponseBodies() throws Exception {
  var service=mock(IBillService.class);var controller=new BillRestController(service);var dto=new BillDTO();
  when(service.findById("id")).thenReturn(dto);assertSame(dto,controller.findById("id").getBody());
  when(service.save(dto)).thenReturn(dto);assertSame(dto,controller.save(dto).getBody());
  when(service.delete("id")).thenReturn(true);assertEquals(true,controller.delete("id").getBody());
  var status=new com.cafe.storekeeper.infrastructure.domain.bill.rest.model.request.StatusBillRequest();when(service.updateStatus(status)).thenReturn(dto);assertSame(dto,controller.updateStatus(status).getBody());
  var request=new MockHttpServletRequest();request.setQueryString("name=abc");when(service.find(Map.of("name","abc"))).thenReturn(List.of(dto));
  var response=controller.find(request);assertEquals(200,response.getStatusCode().value());assertNotNull(response.getBody());verify(service).find(Map.of("name","abc"));
 }
}
