package com.cafe.storekeeper.platform.business;
import static org.mockito.Mockito.*;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
class BusinessControllerTest {
 @Test void businessAndCustomerOperationsRequireAuthorizedScope(){
  var businesses=mock(BusinessService.class);var scope=mock(BusinessScope.class);var customers=mock(CustomerService.class);var user=UsernamePasswordAuthenticationToken.unauthenticated("owner",null);when(businesses.authorize("owner","shop")).thenReturn(scope);
  var controller=new BusinessController(businesses);var create=new BusinessController.Create("Shop","Description","retail","MX","MXN","America/Mexico_City","#000000",null,"standard",Set.of());controller.list(user);controller.create(user,"key",create);controller.get(user,"shop");verify(businesses).list("owner");verify(businesses).create("owner","key",create);
  var customerController=new CustomerController(businesses,customers);var input=new CustomerController.Input("Name",null,null,0);customerController.list(user,"shop");customerController.create(user,"shop",input);customerController.update(user,"shop","id",input);customerController.delete(user,"shop","id",2);
  verify(customers).list(scope);verify(customers).create(scope,input);verify(customers).update(scope,"id",input);verify(customers).delete(scope,"id",2);verify(businesses,times(5)).authorize("owner","shop");
 }
}
