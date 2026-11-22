package com.cafe.storekeeper.platform.commerce;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Validator;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import com.cafe.storekeeper.platform.business.BusinessScope;
import com.cafe.storekeeper.platform.catalog.BusinessModule;
import com.cafe.storekeeper.platform.identity.IdentityProblem;
import com.cafe.storekeeper.platform.identity.IdentityService;
import com.cafe.storekeeper.platform.commerce.CommerceModels.Product;

@Service
public class BatchImportService {
    private final MongoTemplate mongo;
    private final CommerceService commerce;
    private final Validator validator;
    public BatchImportService(MongoTemplate mongo, CommerceService commerce, Validator validator) {
        this.mongo=mongo; this.commerce=commerce; this.validator=validator;
    }
    public record Row(int row, String sku, String name, String type, String price, String quantity, String reason) {}
    public record Input(String kind, String batchId, List<Row> rows) {}
    public record Result(int row, String sku, boolean saved, String detail) {}
    public record Summary(int saved, int rejected, List<Result> results) {}

    public Summary save(BusinessScope scope, Input input) {
        if(input==null || !List.of("products","inventory").contains(input.kind()==null?"":input.kind())
                || input.batchId()==null || !input.batchId().matches("[A-Za-z0-9_-]{16,80}")
                || input.rows()==null || input.rows().isEmpty() || input.rows().size()>500)
            throw new IdentityProblem(400,"Selecciona productos o inventario y entre 1 y 500 filas con un identificador de carga válido");
        BusinessModule module=input.kind().equals("products")?BusinessModule.CATALOG:BusinessModule.INVENTORY;
        if(!scope.business().modules().contains(module))throw new IdentityProblem(403,"Módulo no habilitado");
        List<Result> results=new ArrayList<>();int saved=0;
        for(int index=0;index<input.rows().size();index++) {
            Row row=input.rows().get(index);int number=row==null?index+2:row.row();
            try {
                if(row==null || number<2 || row.sku()==null || row.sku().isBlank() || row.sku().length()>80)
                    throw new IdentityProblem(400,"SKU obligatorio, de máximo 80 caracteres");
                String key=IdentityService.hash(input.batchId()+":"+index);
                if(input.kind().equals("products"))product(scope,key,row);
                else stock(scope,key,row);
                saved++;results.add(new Result(number,row.sku(),true,"Guardado"));
            } catch(IdentityProblem invalid) {
                results.add(new Result(number,row==null?"":row.sku(),false,invalid.getMessage()));
            } catch(DuplicateKeyException duplicate) {
                results.add(new Result(number,row.sku(),false,"El SKU ya existe en este negocio"));
            } catch(NumberFormatException invalid) {
                results.add(new Result(number,row.sku(),false,"Precio o cantidad inválidos; usa punto decimal y cantidades enteras"));
            }
        }
        return new Summary(saved,results.size()-saved,List.copyOf(results));
    }
    private void product(BusinessScope scope,String key,Row row) {
        var data=new CommerceController.ProductInput(row.sku(),row.name(),row.type(),new BigDecimal(row.price()==null?"":row.price()),null);
        validate(data);
        String id=IdentityService.hash(scope.id()+":import-product:"+key);
        Product prior=mongo.findOne(Query.query(Criteria.where("businessId").is(scope.id()).and("_id").is(id)),Product.class);
        if(prior!=null) {
            if(!prior.sku().equals(data.sku().strip()) || !prior.name().equals(data.name().strip())
                    || !prior.type().equals(data.type()) || prior.price().compareTo(data.price())!=0)
                throw new IdentityProblem(409,"Esta fila ya se cargó con otros datos; inicia una carga nueva");
            return;
        }
        mongo.insert(new Product(id,scope.id(),data.sku().strip(),data.name().strip(),data.type(),data.price().setScale(2),0,true,null));
    }
    private void stock(BusinessScope scope,String key,Row row) {
        long quantity=Long.parseLong(row.quantity()==null?"":row.quantity());
        var data=new CommerceController.StockInput(quantity,row.reason());validate(data);
        Product product=mongo.findOne(Query.query(Criteria.where("businessId").is(scope.id()).and("sku").is(row.sku().strip()).and("active").is(true)),Product.class);
        if(product==null)throw new IdentityProblem(400,"SKU no encontrado en este negocio; crea primero el producto");
        commerce.stock(scope,product.id(),key,data);
    }
    private void validate(Object data) {
        if(!validator.validate(data).isEmpty())throw new IdentityProblem(400,"Revisa los campos obligatorios, longitudes, tipo PRODUCT/SERVICE, precio (máximo 2 decimales) o cantidad (±1 000 000 000)");
    }
}
