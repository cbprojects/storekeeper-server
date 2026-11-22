package com.cafe.storekeeper.legacy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import com.cafe.storekeeper.infrastructure.adapter.IProviderService;
import com.cafe.storekeeper.infrastructure.domain.provider.rest.controller.v1.ProviderRestController;
import com.cafe.storekeeper.infrastructure.domain.provider.rest.model.dto.ProviderDTO;
class ProviderControllerTest {
 @Test void crudDelegatesAndReturnsResponseBodies() throws Exception {
  var service=mock(IProviderService.class);var controller=new ProviderRestController(service);var dto=new ProviderDTO();
  when(service.findById("id")).thenReturn(dto);assertSame(dto,controller.findById("id").getBody());
  when(service.save(dto)).thenReturn(dto);assertSame(dto,controller.save(dto).getBody());
  when(service.delete("id")).thenReturn(true);assertEquals(true,controller.delete("id").getBody());
  when(service.findByDocumentNumber("doc")).thenReturn(dto);assertSame(dto,controller.findByDocumentNumber("doc").getBody());
  var request=new MockHttpServletRequest();request.setQueryString("name=abc");when(service.find(Map.of("name","abc"))).thenReturn(List.of(dto));
  var response=controller.find(request);assertEquals(200,response.getStatusCode().value());assertNotNull(response.getBody());verify(service).find(Map.of("name","abc"));
 }
}
