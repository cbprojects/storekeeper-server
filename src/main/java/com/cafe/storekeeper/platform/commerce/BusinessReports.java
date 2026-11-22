package com.cafe.storekeeper.platform.commerce;

import static com.cafe.storekeeper.platform.commerce.CommerceModels.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Service;
import com.cafe.storekeeper.platform.business.BusinessScope;
import com.cafe.storekeeper.platform.catalog.BusinessModule;
import com.cafe.storekeeper.platform.identity.IdentityProblem;

@Service
public class BusinessReports {
    public record Column(String key,String label,String type) {}
    public record Metric(String label,String value,String type) {}
    public record Point(String label,String value) {}
    public record Document(String kind,String title,String currency,Instant from,Instant to,Instant generatedAt,
            List<Column> columns,List<Map<String,Object>> rows,List<Metric> metrics,List<Point> series,String note) {}
    private final MongoTemplate mongo;
    public BusinessReports(MongoTemplate mongo){this.mongo=mongo;}
    private void require(BusinessScope scope,BusinessModule module) {
        if(!scope.business().modules().contains(module))throw new IdentityProblem(403,"Módulo no habilitado");
    }
    private Query query(BusinessScope scope,Instant from,Instant to) {
        Criteria criteria=Criteria.where("businessId").is(scope.id());
        if(from!=null)criteria.and("createdAt").gte(from).lt(to);
        return Query.query(criteria).with(Sort.by(Sort.Direction.ASC,"createdAt","_id")).limit(10001);
    }
    private <T> List<T> find(Query query,Class<T> type) {
        List<T> rows=mongo.find(query,type);
        if(rows.size()>10000)throw new IdentityProblem(400,"El reporte supera 10 000 registros. Reduce el rango de fechas.");
        return rows;
    }
    private static String money(BigDecimal value){return value.setScale(2,java.math.RoundingMode.HALF_UP).toPlainString();}
    private static Column text(String key,String label){return new Column(key,label,"text");}
    private static Column amount(String key,String label){return new Column(key,label,"money");}
    private static Map<String,Object> row(Object... values) {
        Map<String,Object> result=new LinkedHashMap<>();for(int i=0;i<values.length;i+=2)result.put((String)values[i],values[i+1]);return result;
    }
    private static void daily(Map<String,BigDecimal> series,Instant date,BigDecimal amount){series.merge(date.atZone(ZoneOffset.UTC).toLocalDate().toString(),amount,BigDecimal::add);}
    public Document generate(BusinessScope scope,String kind,Instant from,Instant to) {
        require(scope,BusinessModule.REPORTS);
        if(from==null || to==null || !from.isBefore(to))throw new IdentityProblem(400,"Rango de fechas inválido");
        List<Map<String,Object>> rows=new ArrayList<>();List<Column> columns;List<Metric> metrics;
        Map<String,BigDecimal> series=new TreeMap<>();String title,note;
        switch(kind) {
            case "sales" -> {
                require(scope,BusinessModule.SALES);title="Reporte de ventas";
                columns=List.of(new Column("date","Fecha UTC","date"),text("id","Venta"),text("items","Artículos"),text("status","Estado"),amount("total","Total"),amount("paid","Pagado"),amount("balance","Saldo"));
                BigDecimal total=BigDecimal.ZERO,paid=BigDecimal.ZERO;long confirmed=0,returned=0;
                for(Sale sale:find(query(scope,from,to),Sale.class)) {
                    boolean isReturn=sale.status().equals("RETURNED");
                    rows.add(row("date",sale.createdAt().toString(),"id",sale.id(),"items",String.join(", ",sale.lines().stream().map(line->line.name()+" × "+line.quantity()).toList()),"status",sale.status(),"total",money(sale.total()),"paid",money(sale.paid()),"balance",money(isReturn?BigDecimal.ZERO:sale.total().subtract(sale.paid()))));
                    if(isReturn){returned++;continue;}confirmed++;total=total.add(sale.total());paid=paid.add(sale.paid());daily(series,sale.createdAt(),sale.total());
                }
                metrics=List.of(new Metric("Ventas confirmadas",Long.toString(confirmed),"number"),new Metric("Importe de ventas",money(total),"money"),new Metric("Pagado de ventas del periodo",money(paid),"money"),new Metric("Saldo pendiente",money(total.subtract(paid)),"money"),new Metric("Devoluciones",Long.toString(returned),"number"));
                note="Ventas creadas en el periodo UTC. Los importes y la gráfica excluyen ventas devueltas. Pagado y saldo corresponden al estado actual de esas ventas.";
            }
            case "payments" -> {
                require(scope,BusinessModule.PAYMENTS);title="Reporte de pagos";
                columns=List.of(new Column("date","Fecha UTC","date"),text("id","Pago"),text("sale","Venta"),text("method","Método"),amount("amount","Importe"));BigDecimal total=BigDecimal.ZERO;
                for(Payment payment:find(query(scope,from,to),Payment.class)) {
                    rows.add(row("date",payment.createdAt().toString(),"id",payment.id(),"sale",payment.saleId(),"method",payment.method(),"amount",money(payment.amount())));total=total.add(payment.amount());daily(series,payment.createdAt(),payment.amount());
                }
                metrics=List.of(new Metric("Pagos registrados",Integer.toString(rows.size()),"number"),new Metric("Importe recibido",money(total),"money"));
                note="Pagos registrados en el periodo UTC, aunque la venta sea anterior. Los reembolsos se consultan en Devoluciones.";
            }
            case "returns" -> {
                require(scope,BusinessModule.SALES);title="Reporte de devoluciones";
                columns=List.of(new Column("date","Fecha UTC","date"),text("id","Devolución"),text("sale","Venta"),text("reason","Motivo"),amount("refund","Reembolso registrado"));BigDecimal total=BigDecimal.ZERO;
                for(Return returned:find(query(scope,from,to),Return.class)) {
                    rows.add(row("date",returned.createdAt().toString(),"id",returned.id(),"sale",returned.saleId(),"reason",returned.reason(),"refund",money(returned.refund())));total=total.add(returned.refund());daily(series,returned.createdAt(),returned.refund());
                }
                metrics=List.of(new Metric("Devoluciones",Integer.toString(rows.size()),"number"),new Metric("Reembolsos registrados",money(total),"money"));note="Devoluciones registradas en el periodo UTC. Los reembolsos representan importes registrados, sin procesar pagos externos.";
            }
            case "inventory" -> {
                require(scope,BusinessModule.INVENTORY);title="Reporte de inventario";
                columns=List.of(text("sku","SKU"),text("name","Artículo"),new Column("stock","Existencias","number"),amount("price","Precio de venta"),amount("value","Valor a precio de venta"));BigDecimal total=BigDecimal.ZERO;long units=0;
                Query query=query(scope,null,null);query.addCriteria(Criteria.where("type").is("PRODUCT").and("active").is(true));
                for(Product product:find(query,Product.class)) {
                    BigDecimal value=product.price().multiply(BigDecimal.valueOf(product.stock()));
                    rows.add(row("sku",product.sku(),"name",product.name(),"stock",product.stock(),"price",money(product.price()),"value",money(value)));units+=product.stock();total=total.add(value);series.merge(product.name(),BigDecimal.valueOf(product.stock()),BigDecimal::add);
                }
                metrics=List.of(new Metric("Artículos físicos",Integer.toString(rows.size()),"number"),new Metric("Unidades disponibles",Long.toString(units),"number"),new Metric("Valor a precio de venta",money(total),"money"));note="Existencias actuales al consultar; no es inventario histórico ni valoración a costo. El rango de fechas no aplica. La gráfica muestra hasta 20 artículos.";
            }
            default -> throw new IdentityProblem(400,"Tipo de reporte no disponible");
        }
        return new Document(kind,title,scope.business().currency(),from,to,Instant.now(),columns,rows,metrics,series.entrySet().stream().limit(kind.equals("inventory")?20:10000).map(entry->new Point(entry.getKey(),entry.getValue().toPlainString())).toList(),note);
    }
}
