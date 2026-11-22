package com.cafe.storekeeper.platform.business;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import com.mongodb.client.result.DeleteResult;
import com.cafe.storekeeper.platform.catalog.*;
import com.cafe.storekeeper.platform.identity.IdentityProblem;

class BusinessServiceTest {
    MongoTemplate mongo=mock(MongoTemplate.class);
    BusinessService service=new BusinessService(mongo,new CategoryCatalog());
    BusinessController.Create input(String country,String currency,String zone) {
        return new BusinessController.Create("Shop","Description","retail",country,currency,zone,"#087BFF",null,"standard",Set.of());
    }
    Business business(String id,String owner) {
        return new Business(id,owner,"Shop","Description","retail",1,"MX","MXN","America/Mexico_City","#087BFF",null,"standard",new CategoryCatalog().require("retail").requiredModules(),"ACTIVE",Instant.now(),"abcdefghijklmnop",new CategoryCatalog().require("retail").requiredModules());
    }
    @Test void validatesRegionTimezoneAndIdempotency() {
        assertThrows(IdentityProblem.class,()->service.create("owner","short",input("MX","MXN","America/Mexico_City")));
        assertThrows(IdentityProblem.class,()->service.create("owner","abcdefghijklmnop",input("MX","COP","America/Mexico_City")));
        assertThrows(IdentityProblem.class,()->service.create("owner","abcdefghijklmnop",input("MX","MXN","invalid")));
        when(mongo.insert(any(Business.class))).thenAnswer(inv->inv.getArgument(0));
        Business mx=service.create("owner","abcdefghijklmnop",input("MX","MXN","America/Mexico_City"));
        assertEquals("owner",mx.ownerId());assertTrue(mx.modules().contains(BusinessModule.INVENTORY));
        when(mongo.findById(mx.id(),Business.class)).thenReturn(mx);
        assertEquals(mx,service.create("owner","abcdefghijklmnop",input("MX","MXN","America/Mexico_City")));
        assertThrows(IdentityProblem.class,()->service.create("owner","abcdefghijklmnop",input("CO","COP","America/Bogota")));
    }
    @Test void disabledModulesInvalidStyleAndConcurrentDuplicateAreRejected() {
        var scope=mock(BusinessScope.class);var business=mock(Business.class);when(scope.business()).thenReturn(business);when(business.modules()).thenReturn(Set.of());
        assertThrows(IdentityProblem.class,()->new CustomerService(mongo).list(scope));
        var data=input("MX","MXN","America/Mexico_City");
        var invalid=new BusinessController.Create(data.name(),data.description(),data.categoryId(),data.country(),data.currency(),data.timezone(),data.accent(),null,"invalid",Set.of());
        assertThrows(IdentityProblem.class,()->service.create("owner","abcdefghijklmnop",invalid));
        when(mongo.insert(any(Business.class))).thenThrow(new org.springframework.dao.DuplicateKeyException("concurrent"));
        assertThrows(IdentityProblem.class,()->service.create("owner","abcdefghijklmnop",data));
    }
    @Test void accessAndListsAlwaysRequireOwnerAndActiveBusiness() {
        service.list("owner");
        var query=ArgumentCaptor.forClass(Query.class);verify(mongo).find(query.capture(),eq(Business.class));
        assertEquals("owner",query.getValue().getQueryObject().get("ownerId"));
        assertThrows(IdentityProblem.class,()->service.authorize("attacker","id"));
        when(mongo.findOne(any(Query.class),eq(Business.class))).thenReturn(business("id","owner"));
        BusinessScope scope=service.authorize("owner","id");assertEquals("id",scope.id());
        verify(mongo,times(2)).findOne(query.capture(),eq(Business.class));
        assertEquals("owner",query.getValue().getQueryObject().get("ownerId"));
        assertEquals("ACTIVE",query.getValue().getQueryObject().get("status"));
    }
    @Test void customerQueriesIncludeTenantAndOptimisticVersion() {
        CustomerService customers=new CustomerService(mongo);BusinessScope scope=new BusinessScope(business("a","owner"));
        customers.list(scope);
        var query=ArgumentCaptor.forClass(Query.class);verify(mongo).find(query.capture(),eq(Customer.class));
        assertEquals("a",query.getValue().getQueryObject().get("businessId"));
        when(mongo.insert(any(Customer.class))).thenAnswer(inv->inv.getArgument(0));
        var input=new CustomerController.Input(" Client ","client@example.test","123",0);
        Customer customer=customers.create(scope,input);assertEquals("a",customer.businessId());assertEquals("Client",customer.name());
        assertThrows(IdentityProblem.class,()->customers.update(scope,"foreign-id",input));
        when(mongo.findAndModify(any(Query.class),any(UpdateDefinition.class),any(FindAndModifyOptions.class),eq(Customer.class))).thenReturn(customer);
        assertEquals(customer,customers.update(scope,customer.id(),input));
        verify(mongo,times(2)).findAndModify(query.capture(),any(UpdateDefinition.class),any(FindAndModifyOptions.class),eq(Customer.class));
        assertEquals("a",query.getValue().getQueryObject().get("businessId"));assertEquals(0L,query.getValue().getQueryObject().get("version"));
        when(mongo.remove(any(Query.class),eq(Customer.class))).thenReturn(DeleteResult.acknowledged(0));
        assertThrows(IdentityProblem.class,()->customers.delete(scope,"foreign-id",0));
        when(mongo.remove(any(Query.class),eq(Customer.class))).thenReturn(DeleteResult.acknowledged(1));
        customers.delete(scope,customer.id(),0);
    }
 @Test void concurrentCreationReplaysTheWinnerWithoutDuplicatingBusiness(){
 var winner=new java.util.concurrent.atomic.AtomicReference<Business>();when(mongo.findById(anyString(),eq(Business.class))).thenAnswer(inv->winner.get());when(mongo.insert(any(Business.class))).thenAnswer(inv->{winner.set(inv.getArgument(0));throw new org.springframework.dao.DuplicateKeyException("race");});
 var result=service.create("owner","abcdefghijklmnop",input("MX","MXN","America/Mexico_City"));assertEquals(winner.get().id(),result.id());verify(mongo,times(2)).findById(anyString(),eq(Business.class));
 }
}
