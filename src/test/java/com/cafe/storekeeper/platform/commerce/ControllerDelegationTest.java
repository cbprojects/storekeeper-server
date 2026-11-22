package com.cafe.storekeeper.platform.commerce;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.cafe.storekeeper.platform.business.*;
class ControllerDelegationTest {
 @Test void commerceRoutesAlwaysUseAuthorizedScope(){
  var businesses=mock(BusinessService.class);var service=mock(CommerceService.class);var scope=mock(BusinessScope.class);var user=UsernamePasswordAuthenticationToken.unauthenticated("owner",null);when(businesses.authorize("owner","shop")).thenReturn(scope);
  var identity=mock(com.cafe.storekeeper.platform.identity.IdentityService.class);var controller=new CommerceController(businesses,service,identity);var product=new CommerceController.ProductInput("SKU","Item","PRODUCT",BigDecimal.ONE,null);var stock=new CommerceController.StockInput(1,"initial");var sale=new CommerceController.SaleInput(List.of(),BigDecimal.ZERO,BigDecimal.ZERO);var payment=new CommerceController.PaymentInput(BigDecimal.ONE,"CASH");var returned=new CommerceController.ReturnAuthorizationInput("reason","owner@example.test","secret-password");org.junit.jupiter.api.Assertions.assertFalse(returned.toString().contains("secret-password"));
  controller.products(user,"shop");controller.product(user,"shop",product);controller.image(user,"shop","id",new CommerceController.ImageInput(null));controller.stock(user,"shop","id","key",stock);controller.movements(user,"shop");controller.sales(user,"shop");controller.sale(user,"shop","key",sale);controller.payment(user,"shop","sale","key",payment);controller.fullReturn(user,"shop","sale","key",returned);verify(identity).reauthorize("owner","owner@example.test","secret-password");controller.report(user,"shop",Instant.EPOCH,Instant.MAX);
  verify(businesses,times(10)).authorize("owner","shop");verify(service).products(scope);verify(service).product(scope,product);verify(service).image(scope,"id",null);verify(service).stock(scope,"id","key",stock);verify(service).movements(scope);verify(service).sales(scope);verify(service).sale(scope,"key",sale);verify(service).payment(scope,"sale","key",payment);verify(service).fullReturn(scope,"sale","key",new CommerceController.ReturnInput("reason"));verify(service).report(scope,Instant.EPOCH,Instant.MAX);
 }
 @Test void directoryReportsAndImportsAlwaysUseAuthorizedScope(){
  var businesses=mock(BusinessService.class);var scope=mock(BusinessScope.class);var user=UsernamePasswordAuthenticationToken.unauthenticated("owner",null);when(businesses.authorize("owner","shop")).thenReturn(scope);
  var directory=mock(DirectoryService.class);var input=new DirectoryController.Input("Name",null,null,null,true,0);var controller=new DirectoryController(businesses,directory);controller.list(user,"shop","employees");controller.create(user,"shop","employees",input);controller.update(user,"shop","employees","id",input);verify(directory).list(scope,"employees");verify(directory).create(scope,"employees",input);verify(directory).update(scope,"employees","id",input);
  var reports=mock(BusinessReports.class);new BusinessReportsController(businesses,reports).get(user,"shop","sales",Instant.EPOCH,Instant.MAX);verify(reports).generate(scope,"sales",Instant.EPOCH,Instant.MAX);
  var imports=mock(BatchImportService.class);var batch=new BatchImportService.Input("products","abcdefghijklmnop",List.of());new BatchImportController(businesses,imports).save(user,"shop",batch);verify(imports).save(scope,batch);
 }
}
