package com.cafe.storekeeper.platform.business;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("business_customers")
@CompoundIndex(name="business_name", def="{'businessId':1,'name':1}")
public record Customer(@Id String id, String businessId, String name, String email,
        String phone, long version, Instant createdAt) {}
