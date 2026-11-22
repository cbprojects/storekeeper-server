package com.cafe.storekeeper.platform.business;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import com.cafe.storekeeper.platform.catalog.CategoryCatalog;
import com.cafe.storekeeper.platform.catalog.BusinessModule;
import com.cafe.storekeeper.platform.identity.IdentityProblem;
import com.cafe.storekeeper.platform.identity.IdentityService;

@Service
public class BusinessService {
    private final MongoTemplate mongo;
    private final CategoryCatalog catalog;
    public BusinessService(MongoTemplate mongo, CategoryCatalog catalog) { this.mongo = mongo; this.catalog = catalog; }

    public List<Business> list(String owner) {
        return mongo.find(Query.query(Criteria.where("ownerId").is(owner)).with(Sort.by(Sort.Direction.DESC,"createdAt")), Business.class).stream().map(this::effective).toList();
    }

    public BusinessScope authorize(String owner, String businessId) {
        Business business = mongo.findOne(Query.query(Criteria.where("_id").is(businessId).and("ownerId").is(owner).and("status").is("ACTIVE")), Business.class);
        if (business == null) throw new IdentityProblem(404, "Negocio no disponible");
        return new BusinessScope(effective(business));
    }

    private Business effective(Business business) {
        Set<BusinessModule> configured=business.configuredModules()==null ? business.modules() : business.configuredModules();
        return new Business(business.id(),business.ownerId(),business.name(),business.description(),business.categoryId(),business.templateVersion(),business.country(),business.currency(),business.timezone(),business.accent(),business.logo(),business.visualStyle(),new com.cafe.storekeeper.platform.catalog.CapabilityEngine().effective(configured),business.status(),business.createdAt(),business.creationKey(),configured);
    }

    public Business create(String owner, String key, BusinessController.Create data) {
        if (key == null || !key.matches("[A-Za-z0-9_-]{16,100}")) throw new IdentityProblem(400, "Se requiere una clave de idempotencia válida");
        if (!(data.country().equals("MX") && data.currency().equals("MXN")) && !(data.country().equals("CO") && data.currency().equals("COP")))
            throw new IdentityProblem(400, "País y moneda incompatibles");
        try { ZoneId.of(data.timezone()); }
        catch (Exception invalid) { throw new IdentityProblem(400, "Zona horaria inválida"); }
        if (!Set.of("standard", "compact").contains(data.visualStyle())) throw new IdentityProblem(400,"Estilo visual no permitido");
        var template = catalog.require(data.categoryId());
        Set<BusinessModule> modules = catalog.resolveModules(data.categoryId(), data.optionalModules());
        var capabilities = new com.cafe.storekeeper.platform.catalog.CapabilityEngine();
        capabilities.validateOptional(data.optionalModules());
        String id = IdentityService.hash(owner + ":" + key);
        Business business = new Business(id, owner, data.name().strip(), data.description(), template.id(), template.version(),
                data.country(), data.currency(), data.timezone(), data.accent(), data.logo(), data.visualStyle(), capabilities.effective(modules),
                "ACTIVE", Instant.now(), key, modules);
        Business existing = mongo.findById(id, Business.class);
        if (existing != null) return replay(existing, business);
        try { return mongo.insert(business); }
        catch (DuplicateKeyException conflict) { return replay(mongo.findById(id, Business.class), business); }
    }

    private Business replay(Business existing, Business expected) {
        if (existing == null || !existing.ownerId().equals(expected.ownerId()) || !existing.name().equals(expected.name())
                || !existing.categoryId().equals(expected.categoryId()) || !existing.country().equals(expected.country())
                || !existing.currency().equals(expected.currency()) || !existing.timezone().equals(expected.timezone())
                || !java.util.Objects.equals(existing.description(),expected.description())
                || !java.util.Objects.equals(existing.logo(),expected.logo()) || !existing.accent().equals(expected.accent())
                || !existing.visualStyle().equals(expected.visualStyle()) || !existing.configuredModules().equals(expected.configuredModules()))
            throw new IdentityProblem(409, "La clave ya se utilizó con otros datos");
        return effective(existing);
    }
}
