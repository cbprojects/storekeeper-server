package com.cafe.storekeeper.platform.commerce;

import static com.cafe.storekeeper.platform.commerce.CommerceModels.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import com.cafe.storekeeper.platform.business.BusinessScope;
import com.cafe.storekeeper.platform.catalog.BusinessModule;
import com.cafe.storekeeper.platform.identity.IdentityProblem;
import com.cafe.storekeeper.platform.identity.IdentityService;

@Service
public class CommerceService {
    private final MongoTemplate mongo;
    private final TransactionTemplate transactions;
    public CommerceService(MongoTemplate mongo,TransactionTemplate transactions) { this.mongo=mongo;this.transactions=transactions; }
    private Criteria tenant(BusinessScope scope,BusinessModule module) {
        if(!scope.business().modules().contains(module))throw new IdentityProblem(403,"Módulo no habilitado");
        return Criteria.where("businessId").is(scope.id());
    }
    private String operation(BusinessScope scope,String key,String type) {
        if(key==null || !key.matches("[A-Za-z0-9_-]{16,100}"))throw new IdentityProblem(400,"Se requiere una clave de idempotencia válida");
        return IdentityService.hash(scope.id()+":"+type+":"+key);
    }
    public List<Product> products(BusinessScope scope) { return mongo.find(Query.query(tenant(scope,BusinessModule.CATALOG)).limit(500),Product.class); }
    public Product product(BusinessScope scope,CommerceController.ProductInput data) {
        tenant(scope,BusinessModule.CATALOG);
        return mongo.insert(new Product(UUID.randomUUID().toString(),scope.id(),data.sku().strip(),data.name().strip(),data.type(),money(data.price()),0,true,validateImage(data.image())));
    }
    public Product image(BusinessScope scope,String productId,String image) {
        Query query=Query.query(tenant(scope,BusinessModule.CATALOG).and("_id").is(productId));
        Product updated=mongo.findAndModify(query,new Update().set("image",validateImage(image)),FindAndModifyOptions.options().returnNew(true),Product.class);
        if(updated==null)throw new IdentityProblem(404,"Artículo no disponible");
        return updated;
    }
    static String validateImage(String image) {
        if(image==null || image.isBlank())return null;
        if(image.length()>300000 || !image.matches("^data:image/(png|jpeg|webp);base64,[A-Za-z0-9+/=]+$"))throw new IdentityProblem(400,"Usa una imagen PNG, JPEG o WebP de máximo 200 KB");
        byte[] bytes;
        try { bytes=java.util.Base64.getDecoder().decode(image.substring(image.indexOf(',')+1)); }
        catch(IllegalArgumentException invalid){throw new IdentityProblem(400,"Imagen inválida");}
        if(bytes.length>200*1024)throw new IdentityProblem(400,"La imagen supera 200 KB");
        String prefix=java.util.HexFormat.of().formatHex(java.util.Arrays.copyOf(bytes,Math.min(bytes.length,12)));
        boolean valid=image.startsWith("data:image/png;") && prefix.startsWith("89504e470d0a1a0a")
            || image.startsWith("data:image/jpeg;") && prefix.startsWith("ffd8ff")
            || image.startsWith("data:image/webp;") && prefix.startsWith("52494646") && prefix.endsWith("57454250");
        if(!valid)throw new IdentityProblem(400,"El archivo no corresponde a una imagen válida");
        return image;
    }
    public Movement stock(BusinessScope scope,String productId,String key,CommerceController.StockInput data) {
        tenant(scope,BusinessModule.INVENTORY);String id=operation(scope,key,"stock");
        return transactions.execute(tx->{
            Movement prior=mongo.findOne(Query.query(tenant(scope,BusinessModule.INVENTORY).and("_id").is(id)),Movement.class);
            if(prior!=null){if(!prior.productId().equals(productId)||prior.quantity()!=data.quantity()||!prior.reason().equals(data.reason()))throw new IdentityProblem(409,"Clave usada con otros datos");return prior;}
            if(data.quantity()==0)throw new IdentityProblem(400,"El movimiento no puede ser cero");
            Criteria criteria=tenant(scope,BusinessModule.INVENTORY).and("_id").is(productId).and("type").is("PRODUCT").and("active").is(true);
            if(data.quantity()<0)criteria.and("stock").gte(-data.quantity());
            var result=mongo.updateFirst(Query.query(criteria),new Update().inc("stock",data.quantity()),Product.class);
            if(result.getModifiedCount()!=1)throw new IdentityProblem(409,"Producto no disponible o existencias insuficientes");
            return mongo.insert(new Movement(id,scope.id(),productId,data.quantity(),data.reason(),Instant.now()));
        });
    }
    public List<Movement> movements(BusinessScope scope) { return mongo.find(Query.query(tenant(scope,BusinessModule.INVENTORY)).limit(500),Movement.class); }
    public List<Sale> sales(BusinessScope scope) { return mongo.find(Query.query(tenant(scope,BusinessModule.SALES)).limit(500),Sale.class); }
    public Sale sale(BusinessScope scope,String key,CommerceController.SaleInput data) {
        tenant(scope,BusinessModule.SALES);String id=operation(scope,key,"sale");String hash=IdentityService.hash(data.toString());
        return transactions.execute(tx->{
            Sale prior=mongo.findOne(Query.query(tenant(scope,BusinessModule.SALES).and("_id").is(id)),Sale.class);
            if(prior!=null){if(!prior.requestHash().equals(hash))throw new IdentityProblem(409,"Clave usada con otros datos");return prior;}
            List<Line> lines=new ArrayList<>();BigDecimal subtotal=BigDecimal.ZERO;
            for(var item:data.lines()){
                Product product=mongo.findOne(Query.query(tenant(scope,BusinessModule.CATALOG).and("_id").is(item.productId()).and("active").is(true)),Product.class);
                if(product==null)throw new IdentityProblem(404,"Producto no disponible");
                if(product.type().equals("PRODUCT")){
                    tenant(scope,BusinessModule.INVENTORY);
                    var result=mongo.updateFirst(Query.query(tenant(scope,BusinessModule.INVENTORY).and("_id").is(product.id()).and("stock").gte(item.quantity())),new Update().inc("stock",-item.quantity()),Product.class);
                    if(result.getModifiedCount()!=1)throw new IdentityProblem(409,"Existencias insuficientes; actualiza e intenta nuevamente");
                    mongo.insert(new Movement(UUID.randomUUID().toString(),scope.id(),product.id(),-item.quantity(),"SALE:"+id,Instant.now()));
                }
                BigDecimal total=money(product.price().multiply(BigDecimal.valueOf(item.quantity())));subtotal=subtotal.add(total);
                lines.add(new Line(product.id(),product.name(),product.type(),item.quantity(),product.price(),total));
            }
            BigDecimal discount=money(data.discount());
            if(discount.compareTo(subtotal)>0)throw new IdentityProblem(400,"El descuento excede el subtotal");
            BigDecimal taxable=subtotal.subtract(discount);BigDecimal tax=money(taxable.multiply(data.taxPercent()).divide(BigDecimal.valueOf(100)));
            return mongo.insert(new Sale(id,scope.id(),scope.business().currency(),List.copyOf(lines),money(subtotal),tax,discount,money(taxable.add(tax)),money(BigDecimal.ZERO),"CONFIRMED",0,Instant.now(),hash));
        });
    }
    public Payment payment(BusinessScope scope,String saleId,String key,CommerceController.PaymentInput data) {
        tenant(scope,BusinessModule.PAYMENTS);String id=operation(scope,key,"payment");
        return transactions.execute(tx->{
            Payment prior=mongo.findOne(Query.query(tenant(scope,BusinessModule.PAYMENTS).and("_id").is(id)),Payment.class);
            BigDecimal amount=money(data.amount());
            if(prior!=null){if(!prior.saleId().equals(saleId)||prior.amount().compareTo(amount)!=0||!prior.method().equals(data.method()))throw new IdentityProblem(409,"Clave usada con otros datos");return prior;}
            Sale sale=mongo.findOne(Query.query(tenant(scope,BusinessModule.SALES).and("_id").is(saleId)),Sale.class);
            if(sale==null||sale.status().equals("RETURNED"))throw new IdentityProblem(404,"Venta no disponible");
            BigDecimal paid=sale.paid().add(amount);
            if(paid.compareTo(sale.total())>0)throw new IdentityProblem(400,"El pago supera el saldo");
            var result=mongo.updateFirst(Query.query(tenant(scope,BusinessModule.SALES).and("_id").is(saleId).and("version").is(sale.version())),new Update().set("paid",paid).set("status",paid.compareTo(sale.total())==0?"PAID":"PARTIAL").inc("version",1),Sale.class);
            if(result.getModifiedCount()!=1)throw new IdentityProblem(409,"La venta cambió; vuelve a intentar");
            return mongo.insert(new Payment(id,scope.id(),saleId,amount,data.method(),Instant.now()));
        });
    }
    public Return fullReturn(BusinessScope scope,String saleId,String key,CommerceController.ReturnInput data) {
        tenant(scope,BusinessModule.SALES);String id=operation(scope,key,"return");
        return transactions.execute(tx->{
            Return prior=mongo.findOne(Query.query(tenant(scope,BusinessModule.SALES).and("_id").is(id)),Return.class);
            if(prior!=null){if(!prior.saleId().equals(saleId)||!prior.reason().equals(data.reason()))throw new IdentityProblem(409,"Clave usada con otros datos");return prior;}
            Sale sale=mongo.findOne(Query.query(tenant(scope,BusinessModule.SALES).and("_id").is(saleId)),Sale.class);
            if(sale==null||sale.status().equals("RETURNED"))throw new IdentityProblem(409,"Venta no disponible para devolución");
            var result=mongo.updateFirst(Query.query(tenant(scope,BusinessModule.SALES).and("_id").is(saleId).and("version").is(sale.version())),new Update().set("status","RETURNED").inc("version",1),Sale.class);
            if(result.getModifiedCount()!=1)throw new IdentityProblem(409,"La venta cambió; vuelve a intentar");
            for(Line line:sale.lines())if(line.type().equals("PRODUCT")){
                var stock=mongo.updateFirst(Query.query(tenant(scope,BusinessModule.INVENTORY).and("_id").is(line.productId())),new Update().inc("stock",line.quantity()),Product.class);
                if(stock.getModifiedCount()!=1)throw new IdentityProblem(409,"No fue posible restituir existencias");
                mongo.insert(new Movement(UUID.randomUUID().toString(),scope.id(),line.productId(),line.quantity(),"RETURN:"+saleId,Instant.now()));
            }
            return mongo.insert(new Return(id,scope.id(),saleId,data.reason(),sale.paid(),Instant.now()));
        });
    }
    public Report report(BusinessScope scope,Instant from,Instant to) {
        if(!from.isBefore(to))throw new IdentityProblem(400,"Rango de fechas inválido");
        List<Sale> sales=mongo.find(Query.query(tenant(scope,BusinessModule.REPORTS).and("createdAt").gte(from).lt(to)),Sale.class);
        BigDecimal total=BigDecimal.ZERO,paid=BigDecimal.ZERO;long count=0,returned=0;
        for(Sale sale:sales){if(sale.status().equals("RETURNED")){returned++;continue;}count++;total=total.add(sale.total());paid=paid.add(sale.paid());}
        return new Report(scope.business().currency(),count,money(total),money(paid),returned);
    }
    private BigDecimal money(BigDecimal amount){return amount.setScale(2,RoundingMode.HALF_UP);}
}
