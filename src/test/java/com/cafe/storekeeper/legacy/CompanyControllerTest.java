package com.cafe.storekeeper.legacy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import com.cafe.storekeeper.infrastructure.adapter.ICompanyService;
import com.cafe.storekeeper.infrastructure.domain.company.rest.controller.v1.CompanyRestController;
import com.cafe.storekeeper.infrastructure.domain.company.rest.model.dto.CompanyDTO;
class CompanyControllerTest {
 @Test void crudDelegatesAndReturnsResponseBodies() throws Exception {
  var service=mock(ICompanyService.class);var controller=new CompanyRestController(service);var dto=new CompanyDTO();
  when(service.findById("id")).thenReturn(dto);assertSame(dto,controller.findById("id").getBody());
  when(service.save(dto)).thenReturn(dto);assertSame(dto,controller.save(dto).getBody());
  when(service.delete("id")).thenReturn(true);assertEquals(true,controller.delete("id").getBody());
  var request=new MockHttpServletRequest();request.setQueryString("name=abc");when(service.find(Map.of("name","abc"))).thenReturn(List.of(dto));
  var response=controller.find(request);assertEquals(200,response.getStatusCode().value());assertNotNull(response.getBody());verify(service).find(Map.of("name","abc"));
 }
}
