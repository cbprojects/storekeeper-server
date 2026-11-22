package com.cafe.storekeeper.platform.commerce;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import com.fasterxml.jackson.annotation.JsonFormat;

public final class CommerceModels {
    private CommerceModels() {}
    @Document("business_products")
    @CompoundIndex(name="business_sku",def="{'businessId':1,'sku':1}",unique=true)
    public record Product(@Id String id,String businessId,String sku,String name,String type,
            @Field(targetType=FieldType.DECIMAL128) @JsonFormat(shape=JsonFormat.Shape.STRING) BigDecimal price,long stock,boolean active,String image) {}
    public record Line(String productId,String name,String type,long quantity,
            @JsonFormat(shape=JsonFormat.Shape.STRING) BigDecimal price,@JsonFormat(shape=JsonFormat.Shape.STRING) BigDecimal total) {}
    @Document("business_sales")
    @CompoundIndex(name="business_sale_date",def="{'businessId':1,'createdAt':-1}")
    public record Sale(@Id String id,String businessId,String currency,List<Line> lines,
            @JsonFormat(shape=JsonFormat.Shape.STRING) BigDecimal subtotal,@JsonFormat(shape=JsonFormat.Shape.STRING) BigDecimal tax,
            @JsonFormat(shape=JsonFormat.Shape.STRING) BigDecimal discount,@JsonFormat(shape=JsonFormat.Shape.STRING) BigDecimal total,
            @JsonFormat(shape=JsonFormat.Shape.STRING) BigDecimal paid,
            String status,long version,Instant createdAt,String requestHash) {}
    @Document("business_stock_movements")
    public record Movement(@Id String id,String businessId,String productId,long quantity,String reason,Instant createdAt) {}
    @Document("business_payments")
    public record Payment(@Id String id,String businessId,String saleId,@JsonFormat(shape=JsonFormat.Shape.STRING) BigDecimal amount,String method,Instant createdAt) {}
    @Document("business_returns")
    public record Return(@Id String id,String businessId,String saleId,String reason,@JsonFormat(shape=JsonFormat.Shape.STRING) BigDecimal refund,Instant createdAt) {}
    public record Report(String currency,long confirmedSales,@JsonFormat(shape=JsonFormat.Shape.STRING) BigDecimal sales,@JsonFormat(shape=JsonFormat.Shape.STRING) BigDecimal payments,long returnedSales) {}
}
