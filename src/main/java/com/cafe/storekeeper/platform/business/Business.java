package com.cafe.storekeeper.platform.business;

import java.time.Instant;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import com.cafe.storekeeper.platform.catalog.BusinessModule;

@Document("platform_businesses")
@CompoundIndex(name="owner_created", def="{'ownerId':1,'createdAt':-1}")
public record Business(@Id String id, String ownerId, String name, String description,
        String categoryId, int templateVersion, String country, String currency,
        String timezone, String accent, String logo, String visualStyle,
        Set<BusinessModule> modules, String status, Instant createdAt, String creationKey,
        Set<BusinessModule> configuredModules) {}
