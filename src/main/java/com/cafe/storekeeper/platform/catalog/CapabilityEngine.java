package com.cafe.storekeeper.platform.catalog;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/** Deployment availability is independent from a category's desired configuration. */
@Service
public class CapabilityEngine {
    public Set<BusinessModule> available() {
        return Set.of(BusinessModule.CUSTOMERS,BusinessModule.CATALOG,BusinessModule.INVENTORY,
                BusinessModule.SALES,BusinessModule.PAYMENTS,BusinessModule.REPORTS,
                BusinessModule.SUPPLIERS,BusinessModule.EMPLOYEES);
    }
    public Set<BusinessModule> effective(Set<BusinessModule> configured) {
        return configured.stream().filter(available()::contains).collect(Collectors.toUnmodifiableSet());
    }
    public void validateOptional(Set<BusinessModule> optional) {
        if (!available().containsAll(optional)) throw new IllegalArgumentException("Módulo todavía no disponible");
    }
}
