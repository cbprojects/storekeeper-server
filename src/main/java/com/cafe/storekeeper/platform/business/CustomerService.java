package com.cafe.storekeeper.platform.business;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import com.cafe.storekeeper.platform.catalog.BusinessModule;
import com.cafe.storekeeper.platform.identity.IdentityProblem;

@Service
public class CustomerService {
    private final MongoTemplate mongo;
    public CustomerService(MongoTemplate mongo) { this.mongo = mongo; }
    private Criteria tenant(BusinessScope scope) {
        if (!scope.business().modules().contains(BusinessModule.CUSTOMERS)) throw new IdentityProblem(403, "Módulo no habilitado");
        return Criteria.where("businessId").is(scope.id());
    }
    public List<Customer> list(BusinessScope scope) {
        return mongo.find(Query.query(tenant(scope)).limit(500), Customer.class);
    }
    public Customer create(BusinessScope scope, CustomerController.Input data) {
        tenant(scope);
        return mongo.insert(new Customer(UUID.randomUUID().toString(), scope.id(), data.name().strip(), data.email(), data.phone(), 0, Instant.now()));
    }
    public Customer update(BusinessScope scope, String id, CustomerController.Input data) {
        Customer updated = mongo.findAndModify(Query.query(tenant(scope).and("_id").is(id).and("version").is(data.version())),
                new Update().set("name",data.name().strip()).set("email",data.email()).set("phone",data.phone()).inc("version",1),
                FindAndModifyOptions.options().returnNew(true), Customer.class);
        if (updated == null) throw new IdentityProblem(409, "Registro no disponible o modificado; vuelve a cargarlo");
        return updated;
    }
    public void delete(BusinessScope scope, String id, long version) {
        var result = mongo.remove(Query.query(tenant(scope).and("_id").is(id).and("version").is(version)), Customer.class);
        if (result.getDeletedCount() != 1) throw new IdentityProblem(409, "Registro no disponible o modificado; vuelve a cargarlo");
    }
}
