package com.cafe.storekeeper.platform.commerce;

import static com.cafe.storekeeper.platform.commerce.CommerceModels.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import com.mongodb.client.result.UpdateResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import com.cafe.storekeeper.platform.business.*;
import com.cafe.storekeeper.platform.catalog.BusinessModule;
import com.cafe.storekeeper.platform.identity.IdentityProblem;

class CommerceServiceTest {
    MongoTemplate mongo=mock(MongoTemplate.class);TransactionTemplate transactions=mock(TransactionTemplate.class);
    BusinessScope scope=mock(BusinessScope.class);CommerceService commerce=new CommerceService(mongo,transactions);
    Product product=new Product("product","business","SKU","Product","PRODUCT",new BigDecimal("100.25"),10,true,null);
    String key="abcdefghijklmnop";
    @BeforeEach void setup(){
        when(scope.id()).thenReturn("business");when(scope.business()).thenReturn(new Business("business","owner","Shop","","retail",1,"MX","MXN","America/Mexico_City","#087BFF",null,"standard",Set.of(BusinessModule.values()),"ACTIVE",Instant.now(),key,Set.of(BusinessModule.values())));
        when(transactions.execute(any())).thenAnswer(inv->((TransactionCallback<?>)inv.getArgument(0)).doInTransaction(mock(TransactionStatus.class)));
        when(mongo.insert(any(Product.class))).thenAnswer(inv->inv.getArgument(0));
        when(mongo.insert(any(Movement.class))).thenAnswer(inv->inv.getArgument(0));
        when(mongo.insert(any(Sale.class))).thenAnswer(inv->inv.getArgument(0));
        when(mongo.insert(any(Payment.class))).thenAnswer(inv->inv.getArgument(0));
        when(mongo.insert(any(Return.class))).thenAnswer(inv->inv.getArgument(0));
        when(mongo.updateFirst(any(Query.class),any(UpdateDefinition.class),eq(Product.class))).thenReturn(UpdateResult.acknowledged(1,1L,null));
        when(mongo.updateFirst(any(Query.class),any(UpdateDefinition.class),eq(Sale.class))).thenReturn(UpdateResult.acknowledged(1,1L,null));
        when(mongo.findOne(any(Query.class),eq(Product.class))).thenReturn(product);
    }
    Sale sampleSale(String status){return new Sale("sale","business","MXN",List.of(new Line("product","Product","PRODUCT",2,product.price(),new BigDecimal("200.50"))),new BigDecimal("200.50"),new BigDecimal("32.08"),BigDecimal.ZERO,new BigDecimal("232.58"),BigDecimal.ZERO,status,0,Instant.now(),"hash");}
    @Test void catalogAndStockEnforceScopeAndReplayWithoutSecondMovement(){
        commerce.products(scope);commerce.sales(scope);commerce.movements(scope);
        var created=commerce.product(scope,new CommerceController.ProductInput(" SKU "," Product ","PRODUCT",new BigDecimal("100.25"),null));assertEquals("business",created.businessId());assertEquals("SKU",created.sku());
        var input=new CommerceController.StockInput(3,"Initial stock");Movement movement=commerce.stock(scope,"product",key,input);assertEquals(3,movement.quantity());
        when(mongo.findOne(any(Query.class),eq(Movement.class))).thenReturn(movement);
        assertEquals(movement,commerce.stock(scope,"product",key,input));
        assertThrows(IdentityProblem.class,()->commerce.stock(scope,"other",key,input));
        when(mongo.findOne(any(Query.class),eq(Movement.class))).thenReturn(null);
        assertThrows(IdentityProblem.class,()->commerce.stock(scope,"product",key,new CommerceController.StockInput(0,"Zero")));
        when(mongo.updateFirst(any(Query.class),any(UpdateDefinition.class),eq(Product.class))).thenReturn(UpdateResult.acknowledged(0,0L,null));
        assertThrows(IdentityProblem.class,()->commerce.stock(scope,"product",key,new CommerceController.StockInput(-30,"Adjustment")));
        assertThrows(IdentityProblem.class,()->commerce.stock(scope,"product","short",input));
    }
    @Test void salesUseServerPricesDecimalTaxesAndIdempotency(){
        var input=new CommerceController.SaleInput(List.of(new CommerceController.SaleLine("product",2)),new BigDecimal("16"),BigDecimal.ZERO);
        Sale sale=commerce.sale(scope,key,input);assertEquals(new BigDecimal("232.58"),sale.total());assertEquals("MXN",sale.currency());
        when(mongo.findOne(any(Query.class),eq(Sale.class))).thenReturn(sale);
        assertEquals(sale,commerce.sale(scope,key,input));
        assertThrows(IdentityProblem.class,()->commerce.sale(scope,key,new CommerceController.SaleInput(input.lines(),BigDecimal.ZERO,BigDecimal.ZERO)));
        when(mongo.findOne(any(Query.class),eq(Sale.class))).thenReturn(null);
        assertThrows(IdentityProblem.class,()->commerce.sale(scope,key,new CommerceController.SaleInput(input.lines(),BigDecimal.ZERO,new BigDecimal("1000"))));
        when(mongo.updateFirst(any(Query.class),any(UpdateDefinition.class),eq(Product.class))).thenReturn(UpdateResult.acknowledged(0,0L,null));
        assertThrows(IdentityProblem.class,()->commerce.sale(scope,key,input));
        when(mongo.findOne(any(Query.class),eq(Product.class))).thenReturn(null);assertThrows(IdentityProblem.class,()->commerce.sale(scope,key,input));
        when(mongo.findOne(any(Query.class),eq(Product.class))).thenReturn(new Product("service","business","SERVICE","Service","SERVICE",new BigDecimal("10.00"),0,true,null));
        assertEquals(new BigDecimal("23.20"),commerce.sale(scope,key,input).total());
    }
    @Test void paymentsRejectOverpaymentAndReplaysWithDifferentPayload(){
        var input=new CommerceController.PaymentInput(new BigDecimal("100.00"),"CASH");
        assertThrows(IdentityProblem.class,()->commerce.payment(scope,"sale",key,input));
        when(mongo.findOne(any(Query.class),eq(Sale.class))).thenReturn(sampleSale("CONFIRMED"));
        Payment payment=commerce.payment(scope,"sale",key,input);assertEquals(new BigDecimal("100.00"),payment.amount());
        when(mongo.findOne(any(Query.class),eq(Payment.class))).thenReturn(payment);assertEquals(payment,commerce.payment(scope,"sale",key,input));
        assertThrows(IdentityProblem.class,()->commerce.payment(scope,"other",key,input));
        when(mongo.findOne(any(Query.class),eq(Payment.class))).thenReturn(null);
        assertThrows(IdentityProblem.class,()->commerce.payment(scope,"sale",key,new CommerceController.PaymentInput(new BigDecimal("300"),"CASH")));
        commerce.payment(scope,"sale",key,new CommerceController.PaymentInput(new BigDecimal("232.58"),"TRANSFER"));
        when(mongo.updateFirst(any(Query.class),any(UpdateDefinition.class),eq(Sale.class))).thenReturn(UpdateResult.acknowledged(0,0L,null));
        assertThrows(IdentityProblem.class,()->commerce.payment(scope,"sale",key,input));
        when(mongo.findOne(any(Query.class),eq(Sale.class))).thenReturn(sampleSale("RETURNED"));assertThrows(IdentityProblem.class,()->commerce.payment(scope,"sale",key,input));
    }
    @Test void returnsRestoreInventoryExactlyOnceAndReportsExcludeReturnedSales(){
        var input=new CommerceController.ReturnInput("Returned by customer");
        assertThrows(IdentityProblem.class,()->commerce.fullReturn(scope,"sale",key,input));
        when(mongo.findOne(any(Query.class),eq(Sale.class))).thenReturn(sampleSale("CONFIRMED"));
        Return returned=commerce.fullReturn(scope,"sale",key,input);assertEquals("sale",returned.saleId());
        when(mongo.findOne(any(Query.class),eq(Return.class))).thenReturn(returned);assertEquals(returned,commerce.fullReturn(scope,"sale",key,input));
        assertThrows(IdentityProblem.class,()->commerce.fullReturn(scope,"other",key,input));
        when(mongo.findOne(any(Query.class),eq(Return.class))).thenReturn(null);
        when(mongo.updateFirst(any(Query.class),any(UpdateDefinition.class),eq(Product.class))).thenReturn(UpdateResult.acknowledged(0,0L,null));
        assertThrows(IdentityProblem.class,()->commerce.fullReturn(scope,"sale",key,input));
        when(mongo.updateFirst(any(Query.class),any(UpdateDefinition.class),eq(Sale.class))).thenReturn(UpdateResult.acknowledged(0,0L,null));
        assertThrows(IdentityProblem.class,()->commerce.fullReturn(scope,"sale",key,input));
        when(mongo.find(any(Query.class),eq(Sale.class))).thenReturn(List.of(sampleSale("CONFIRMED"),sampleSale("RETURNED")));
        Report report=commerce.report(scope,Instant.EPOCH,Instant.now());assertEquals(1,report.confirmedSales());assertEquals(1,report.returnedSales());assertEquals(new BigDecimal("232.58"),report.sales());
        assertThrows(IdentityProblem.class,()->commerce.report(scope,Instant.now(),Instant.EPOCH));
    }
 @Test void catalogIsUnavailableWhenBusinessModuleIsDisabled(){var business=mock(Business.class);when(scope.business()).thenReturn(business);when(business.modules()).thenReturn(Set.of());assertThrows(IdentityProblem.class,()->commerce.products(scope));verifyNoInteractions(mongo);}
}
