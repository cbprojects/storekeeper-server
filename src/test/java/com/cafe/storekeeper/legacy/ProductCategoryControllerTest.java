package com.cafe.storekeeper.legacy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import com.cafe.storekeeper.infrastructure.adapter.IProductCategoryService;
import com.cafe.storekeeper.infrastructure.domain.product_category.rest.controller.v1.ProductCategoryRestController;
import com.cafe.storekeeper.infrastructure.domain.product_category.rest.model.dto.ProductCategoryDTO;
class ProductCategoryControllerTest {
 @Test void crudDelegatesAndReturnsResponseBodies() throws Exception {
  var service=mock(IProductCategoryService.class);var controller=new ProductCategoryRestController(service);var dto=new ProductCategoryDTO();
  when(service.findById("id")).thenReturn(dto);assertSame(dto,controller.findById("id").getBody());
  when(service.save(dto)).thenReturn(dto);assertSame(dto,controller.save(dto).getBody());
  when(service.delete("id")).thenReturn(true);assertEquals(true,controller.delete("id").getBody());
  var request=new MockHttpServletRequest();request.setQueryString("name=abc");when(service.find(Map.of("name","abc"))).thenReturn(List.of(dto));
  var response=controller.find(request);assertEquals(200,response.getStatusCode().value());assertNotNull(response.getBody());verify(service).find(Map.of("name","abc"));
 }
}
