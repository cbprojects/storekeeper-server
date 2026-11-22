package com.cafe.storekeeper.platform.commerce;

import java.util.List;
import java.util.UUID;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Service;
import com.cafe.storekeeper.platform.business.BusinessScope;
import com.cafe.storekeeper.platform.catalog.BusinessModule;
import com.cafe.storekeeper.platform.identity.IdentityProblem;

@Service
public class DirectoryService {
    private final MongoTemplate mongo;
    public DirectoryService(MongoTemplate mongo){this.mongo=mongo;}
    private Criteria criteria(BusinessScope scope,String kind){
        BusinessModule module=switch(kind){case "suppliers"->BusinessModule.SUPPLIERS;case "employees"->BusinessModule.EMPLOYEES;default->throw new IdentityProblem(404,"Directorio no disponible");};
        if(!scope.business().modules().contains(module))throw new IdentityProblem(403,"Módulo no habilitado");
        return Criteria.where("businessId").is(scope.id()).and("kind").is(kind);
    }
    public List<DirectoryEntry> list(BusinessScope scope,String kind){return mongo.find(Query.query(criteria(scope,kind)).limit(500),DirectoryEntry.class);}
    public DirectoryEntry create(BusinessScope scope,String kind,DirectoryController.Input data){
        criteria(scope,kind);
        return mongo.insert(new DirectoryEntry(UUID.randomUUID().toString(),scope.id(),kind,data.name().strip(),data.email(),data.phone(),data.role(),data.active(),0));
    }
    public DirectoryEntry update(BusinessScope scope,String kind,String id,DirectoryController.Input data){
        DirectoryEntry result=mongo.findAndModify(Query.query(criteria(scope,kind).and("_id").is(id).and("version").is(data.version())),
                new Update().set("name",data.name().strip()).set("email",data.email()).set("phone",data.phone()).set("role",data.role()).set("active",data.active()).inc("version",1),FindAndModifyOptions.options().returnNew(true),DirectoryEntry.class);
        if(result==null)throw new IdentityProblem(409,"Registro no disponible o modificado; recarga");
        return result;
    }
}
