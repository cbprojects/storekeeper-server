package com.cafe.storekeeper.platform.commerce;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import com.cafe.storekeeper.platform.business.*;
import com.cafe.storekeeper.platform.catalog.BusinessModule;
import com.cafe.storekeeper.platform.identity.IdentityProblem;
import static com.cafe.storekeeper.platform.commerce.CommerceModels.*;

class BusinessReportsTest {
    MongoTemplate mongo=mock(MongoTemplate.class);BusinessReports reports=new BusinessReports(mongo);
    Instant from=Instant.parse("2026-10-01T00:00:00Z"),to=Instant.parse("2026-11-01T00:00:00Z");
    BusinessScope scope(Set<BusinessModule> modules){var scope=mock(BusinessScope.class);var business=mock(Business.class);when(scope.id()).thenReturn("shop");when(scope.business()).thenReturn(business);when(business.modules()).thenReturn(modules);when(business.currency()).thenReturn("MXN");return scope;}
    BusinessScope all(){return scope(Set.of(BusinessModule.values()));}
    Sale sale(String status){return new Sale("sale","shop","MXN",List.of(new Line("p","Item", "PRODUCT",2,BigDecimal.ONE,BigDecimal.TWO)),BigDecimal.TEN,BigDecimal.ZERO,BigDecimal.ZERO,new BigDecimal("10.25"),new BigDecimal("4.10"),status,0,from,"hash");}
    @Test void salesScopeDatesAndExactTotalsExcludeReturnedSales(){
        when(mongo.find(any(Query.class),eq(Sale.class))).thenReturn(List.of(sale("PARTIAL"),sale("RETURNED")));
        var result=reports.generate(all(),"sales",from,to);
        assertEquals(2,result.rows().size());assertEquals("10.25",result.metrics().get(1).value());assertEquals("6.15",result.metrics().get(3).value());assertEquals("10.25",result.series().getFirst().value());
        var query=ArgumentCaptor.forClass(Query.class);verify(mongo).find(query.capture(),eq(Sale.class));
        assertEquals("shop",query.getValue().getQueryObject().getString("businessId"));assertTrue(query.getValue().getQueryObject().containsKey("createdAt"));
    }
    @Test void paymentAndReturnReportsUseTheirOwnEvents(){
        when(mongo.find(any(Query.class),eq(Payment.class))).thenReturn(List.of(new Payment("p","shop","sale",new BigDecimal("4.10"),"CASH",from)));
        when(mongo.find(any(Query.class),eq(Return.class))).thenReturn(List.of(new Return("r","shop","sale","Return",new BigDecimal("4.10"),from)));
        assertEquals("4.10",reports.generate(all(),"payments",from,to).metrics().get(1).value());
        assertEquals("4.10",reports.generate(all(),"returns",from,to).series().getFirst().value());
    }
    @Test void inventoryIsCurrentStockAndValuesAtSellingPrice(){
        when(mongo.find(any(Query.class),eq(Product.class))).thenReturn(List.of(new Product("p","shop","SKU","Item","PRODUCT",new BigDecimal("2.25"),3,true,null)));
        var result=reports.generate(all(),"inventory",from,to);assertEquals("6.75",result.metrics().get(2).value());assertEquals("3",result.series().getFirst().value());
        var query=ArgumentCaptor.forClass(Query.class);verify(mongo).find(query.capture(),eq(Product.class));assertFalse(query.getValue().getQueryObject().containsKey("createdAt"));assertEquals("PRODUCT",query.getValue().getQueryObject().getString("type"));
    }
    @Test void invalidRangesModulesAndOversizedExportsAreRejected(){
        assertThrows(IdentityProblem.class,()->reports.generate(all(),"sales",to,from));
        assertThrows(IdentityProblem.class,()->reports.generate(all(),"other",from,to));
        assertThrows(IdentityProblem.class,()->reports.generate(scope(Set.of()),"sales",from,to));
        assertThrows(IdentityProblem.class,()->reports.generate(scope(Set.of(BusinessModule.REPORTS)),"inventory",from,to));
        when(mongo.find(any(Query.class),eq(Sale.class))).thenReturn(Collections.nCopies(10001,sale("PAID")));
        assertThrows(IdentityProblem.class,()->reports.generate(all(),"sales",from,to));
    }
}
