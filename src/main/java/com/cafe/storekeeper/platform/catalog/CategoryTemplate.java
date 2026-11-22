package com.cafe.storekeeper.platform.catalog;

import java.util.Set;

public record CategoryTemplate(String id, int version, String name, String description,
        String icon, String accent, String layout, String dashboard,
        Set<BusinessModule> requiredModules, Set<BusinessModule> optionalModules) {
    public CategoryTemplate {
        requiredModules = Set.copyOf(requiredModules);
        optionalModules = Set.copyOf(optionalModules);
    }
}
