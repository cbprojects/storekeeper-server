package com.cafe.storekeeper.platform.commerce;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.*;
import com.cafe.storekeeper.platform.business.*;
import com.cafe.storekeeper.platform.catalog.BusinessModule;
import com.cafe.storekeeper.platform.identity.IdentityProblem;

class DirectoryServiceTest {
    @Test void directoriesEnforceKindTenantAndVersion(){
        MongoTemplate mongo=mock(MongoTemplate.class);BusinessScope scope=mock(BusinessScope.class);
        when(scope.id()).thenReturn("business");
        when(scope.business()).thenReturn(new Business("business","owner","Shop","","retail",1,"MX","MXN","America/Mexico_City","#087BFF",null,"standard",Set.of(BusinessModule.values()),"ACTIVE",Instant.now(),"key",Set.of(BusinessModule.values())));
        DirectoryService service=new DirectoryService(mongo);
        service.list(scope,"suppliers");service.list(scope,"employees");
        assertThrows(IdentityProblem.class,()->service.list(scope,"users"));
        when(mongo.insert(any(DirectoryEntry.class))).thenAnswer(inv->inv.getArgument(0));
        var input=new DirectoryController.Input(" Supplier ","supplier@example.test","123","Supplier",true,0);
        DirectoryEntry entry=service.create(scope,"suppliers",input);assertEquals("business",entry.businessId());assertEquals("Supplier",entry.name());
        assertThrows(IdentityProblem.class,()->service.update(scope,"suppliers","foreign-id",input));
        when(mongo.findAndModify(any(Query.class),any(UpdateDefinition.class),any(FindAndModifyOptions.class),eq(DirectoryEntry.class))).thenReturn(entry);
        assertEquals(entry,service.update(scope,"suppliers",entry.id(),input));
        var query=org.mockito.ArgumentCaptor.forClass(Query.class);
        verify(mongo,times(2)).findAndModify(query.capture(),any(UpdateDefinition.class),any(FindAndModifyOptions.class),eq(DirectoryEntry.class));
        assertEquals("business",query.getValue().getQueryObject().get("businessId"));assertEquals("suppliers",query.getValue().getQueryObject().get("kind"));assertEquals(0L,query.getValue().getQueryObject().get("version"));
    }
 @Test void disabledModulesCannotReadDirectory(){
    var mongo=mock(MongoTemplate.class);var scope=mock(com.cafe.storekeeper.platform.business.BusinessScope.class);var business=mock(com.cafe.storekeeper.platform.business.Business.class);when(scope.business()).thenReturn(business);when(business.modules()).thenReturn(java.util.Set.of());assertThrows(com.cafe.storekeeper.platform.identity.IdentityProblem.class,()->new DirectoryService(mongo).list(scope,"employees"));verifyNoInteractions(mongo);
 }
}
