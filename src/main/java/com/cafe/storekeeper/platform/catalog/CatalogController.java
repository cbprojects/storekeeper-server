package com.cafe.storekeeper.platform.catalog;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Public, non-sensitive metadata. Does not expose or enable business operations. */
@RestController
@RequestMapping("/api/v1/catalog")
public class CatalogController {
    private final CategoryCatalog catalog;

    public CatalogController(CategoryCatalog catalog) {
        this.catalog = catalog;
    }

    @GetMapping
    public CatalogResponse catalog() {
        return new CatalogResponse(1, catalog.categories(), catalog.modules(), new CapabilityEngine().available());
    }

    public record CatalogResponse(int version, List<CategoryTemplate> categories,
            List<BusinessModule> modules, java.util.Set<BusinessModule> availableModules) {}
}
