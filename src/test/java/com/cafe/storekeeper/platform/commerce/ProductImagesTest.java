package com.cafe.storekeeper.platform.commerce;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.transaction.support.TransactionTemplate;
import com.cafe.storekeeper.platform.business.*;
import com.cafe.storekeeper.platform.catalog.BusinessModule;
import com.cafe.storekeeper.platform.identity.IdentityProblem;
import com.cafe.storekeeper.platform.commerce.CommerceModels.Product;

class ProductImagesTest {
    static final String PNG="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+aL1sAAAAASUVORK5CYII=";
    @Test void optionalImagesRejectWrongTypesAndOversizedPayloads() {
        assertNull(CommerceService.validateImage(null));assertNull(CommerceService.validateImage(""));assertEquals(PNG,CommerceService.validateImage(PNG));
        for(String invalid:new String[]{"data:image/svg+xml;base64,PHN2Zz4=","data:image/png;base64,SGVsbG8=","data:image/png;base64,abc",PNG.replace("image/png","image/jpeg")}) assertThrows(IdentityProblem.class,()->CommerceService.validateImage(invalid));
        byte[] bytes=new byte[201*1024];assertThrows(IdentityProblem.class,()->CommerceService.validateImage("data:image/png;base64,"+java.util.Base64.getEncoder().encodeToString(bytes)));
    }
    @Test void imageChangesAreScopedToTheBusinessAndMayBeRemoved() {
        MongoTemplate mongo=mock(MongoTemplate.class);CommerceService service=new CommerceService(mongo,mock(TransactionTemplate.class));
        Business business=new Business("shop","owner","Shop","","retail",1,"MX","MXN","UTC","#087BFF",null,"standard",Set.of(BusinessModule.CATALOG),"ACTIVE",Instant.now(),"key",Set.of(BusinessModule.CATALOG));
        BusinessScope scope=mock(BusinessScope.class);when(scope.business()).thenReturn(business);when(scope.id()).thenReturn("shop");
        when(mongo.insert(any(Product.class))).thenAnswer(inv->inv.getArgument(0));
        Product product=service.product(scope,new CommerceController.ProductInput("SKU","Item","PRODUCT",BigDecimal.ONE,PNG));assertEquals(PNG,product.image());
        when(mongo.findAndModify(any(Query.class),any(UpdateDefinition.class),any(FindAndModifyOptions.class),eq(Product.class))).thenReturn(product);
        service.image(scope,product.id(),null);
        var query=ArgumentCaptor.forClass(Query.class);var update=ArgumentCaptor.forClass(UpdateDefinition.class);
        verify(mongo).findAndModify(query.capture(),update.capture(),any(FindAndModifyOptions.class),eq(Product.class));
        assertEquals("shop",query.getValue().getQueryObject().getString("businessId"));assertEquals(product.id(),query.getValue().getQueryObject().getString("_id"));
        assertTrue(update.getValue().getUpdateObject().get("$set",org.bson.Document.class).containsKey("image"));
        when(mongo.findAndModify(any(Query.class),any(UpdateDefinition.class),any(FindAndModifyOptions.class),eq(Product.class))).thenReturn(null);
        assertThrows(IdentityProblem.class,()->service.image(scope,"another-tenant-item",PNG));
    }
 @Test void malformedBase64IsRejected(){assertThrows(com.cafe.storekeeper.platform.identity.IdentityProblem.class,()->CommerceService.validateImage("data:image/png;base64,="));}
}
