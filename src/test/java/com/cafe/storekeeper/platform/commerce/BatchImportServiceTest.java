package com.cafe.storekeeper.platform.commerce;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.*;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import com.cafe.storekeeper.platform.business.*;
import com.cafe.storekeeper.platform.catalog.BusinessModule;
import com.cafe.storekeeper.platform.identity.IdentityProblem;
import com.cafe.storekeeper.platform.commerce.CommerceModels.Product;

class BatchImportServiceTest {
    MongoTemplate mongo=mock(MongoTemplate.class);CommerceService commerce=mock(CommerceService.class);
    BusinessScope scope(){var scope=mock(BusinessScope.class);var business=mock(Business.class);when(scope.id()).thenReturn("shop");when(scope.business()).thenReturn(business);when(business.modules()).thenReturn(Set.of(BusinessModule.values()));return scope;}
    BatchImportService.Row row(String sku,String price){return new BatchImportService.Row(2,sku,"Item","PRODUCT",price,"10","Initial");}
    BatchImportService.Input input(String kind,List<BatchImportService.Row> rows){return new BatchImportService.Input(kind,"abcdefghijklmnop",rows);}
    @Test void validRowsAreSavedDespiteInvalidAndDuplicateRows(){
        try(var factory=Validation.buildDefaultValidatorFactory()){
            var service=new BatchImportService(mongo,commerce,factory.getValidator());
            when(mongo.insert(any(Product.class))).thenAnswer(inv->{Product p=inv.getArgument(0);if(p.sku().equals("DUP"))throw new DuplicateKeyException("sku");return p;});
            var result=service.save(scope(),input("products",Arrays.asList(row("OK","10.25"),row("BAD","NaN"),row("DUP","2"),row("","3"),null,row("LONG","1.234"),new BatchImportService.Row(8,"SPACES"," ","PRODUCT","1",null,null))));
            assertEquals(1,result.saved());assertEquals(6,result.rejected());assertEquals(7,result.results().size());verify(mongo,times(2)).insert(any(Product.class));
            var query=org.mockito.ArgumentCaptor.forClass(Query.class);verify(mongo,times(2)).findOne(query.capture(),eq(Product.class));assertTrue(query.getAllValues().stream().allMatch(q->q.getQueryObject().getString("businessId").equals("shop")));
        }
    }
    @Test void retryUsesDeterministicIdentityAndRejectsChangedContent(){
        try(var factory=Validation.buildDefaultValidatorFactory()){
            var service=new BatchImportService(mongo,commerce,factory.getValidator());
            when(mongo.findOne(any(Query.class),eq(Product.class))).thenReturn(new Product("id","shop","SKU","Item","PRODUCT",new BigDecimal("10.00"),0,true,null));
            assertEquals(1,service.save(scope(),input("products",List.of(row("SKU","10")))).saved());verify(mongo,never()).insert(any(Product.class));
            assertEquals(1,service.save(scope(),input("products",List.of(row("SKU","11")))).rejected());
        }
    }
    @Test void inventoryResolvesSkuWithinTenantAndDelegatesIdempotentMovement(){
        try(var factory=Validation.buildDefaultValidatorFactory()){
            var service=new BatchImportService(mongo,commerce,factory.getValidator());var scope=scope();
            when(mongo.findOne(any(Query.class),eq(Product.class))).thenReturn(new Product("id","shop","SKU","Item","PRODUCT",BigDecimal.ONE,0,true,null));
            assertEquals(1,service.save(scope,input("inventory",List.of(row("SKU","1")))).saved());verify(commerce).stock(eq(scope),eq("id"),matches("[a-f0-9]{64}"),eq(new CommerceController.StockInput(10,"Initial")));
            when(mongo.findOne(any(Query.class),eq(Product.class))).thenReturn(null);assertEquals(1,service.save(scope,input("inventory",List.of(row("UNKNOWN","1")))).rejected());
        }
    }
    @Test void malformedRequestsAndDisabledModulesWriteNothing(){
        try(var factory=Validation.buildDefaultValidatorFactory()){
            var service=new BatchImportService(mongo,commerce,factory.getValidator());var scope=scope();
            for(var input:Arrays.asList(null,input("unknown",List.of(row("SKU","1"))),input("products",List.of()),input("products",Collections.nCopies(501,row("SKU","1"))),new BatchImportService.Input("products","bad",List.of(row("SKU","1")))))assertThrows(IdentityProblem.class,()->service.save(scope,input));
            when(scope.business().modules()).thenReturn(Set.of());assertThrows(IdentityProblem.class,()->service.save(scope,input("products",List.of(row("SKU","1")))));verifyNoInteractions(mongo,commerce);
        }
    }
}
