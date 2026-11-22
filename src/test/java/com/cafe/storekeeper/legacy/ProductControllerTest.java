package com.cafe.storekeeper.legacy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import com.cafe.storekeeper.infrastructure.adapter.IProductService;
import com.cafe.storekeeper.infrastructure.domain.product.rest.controller.v1.ProductRestController;
import com.cafe.storekeeper.infrastructure.domain.product.rest.model.dto.ProductDTO;
class ProductControllerTest {
 @Test void crudDelegatesAndReturnsResponseBodies() throws Exception {
  var service=mock(IProductService.class);var controller=new ProductRestController(service);var dto=new ProductDTO();
  when(service.findById("id")).thenReturn(dto);assertSame(dto,controller.findById("id").getBody());
  when(service.save(dto)).thenReturn(dto);assertSame(dto,controller.save(dto).getBody());
  when(service.delete("id")).thenReturn(true);assertEquals(true,controller.delete("id").getBody());
  var request=new MockHttpServletRequest();request.setQueryString("name=abc");when(service.find(Map.of("name","abc"))).thenReturn(List.of(dto));
  var response=controller.find(request);assertEquals(200,response.getStatusCode().value());assertNotNull(response.getBody());verify(service).find(Map.of("name","abc"));
 }
}
