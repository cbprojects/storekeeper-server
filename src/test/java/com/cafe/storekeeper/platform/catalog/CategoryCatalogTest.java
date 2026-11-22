package com.cafe.storekeeper.platform.catalog;

import static org.junit.jupiter.api.Assertions.*;
import static com.cafe.storekeeper.platform.catalog.BusinessModule.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoryCatalogTest {
    private final CategoryCatalog catalog = new CategoryCatalog();

    @Test
    void templatesAreDistinctImmutableAndConsistent() {
        assertEquals(11, catalog.categories().size());
        Set<String> ids = new HashSet<>();
        for (CategoryTemplate template : catalog.categories()) {
            assertTrue(ids.add(template.id()));
            assertEquals(1, template.version());
            assertFalse(template.name().isBlank());
            assertFalse(template.description().isBlank());
            assertTrue(template.icon().startsWith("pi pi-"));
            assertTrue(template.accent().matches("#[0-9A-Fa-f]{6}"));
            assertFalse(template.layout().isBlank());
            assertEquals(template.id(), template.dashboard());
            assertTrue(template.requiredModules().contains(REPORTS));
            assertTrue(template.requiredModules().contains(PAYMENTS));
            assertTrue(template.requiredModules().stream().noneMatch(template.optionalModules()::contains));
            assertTrue(catalog.modules().containsAll(template.requiredModules()));
            assertTrue(catalog.modules().containsAll(template.optionalModules()));
            assertEquals(template.requiredModules(), catalog.resolveModules(template.id(), Set.of()));
            var all = catalog.resolveModules(template.id(), template.optionalModules());
            assertTrue(all.containsAll(template.requiredModules()));
            assertTrue(all.containsAll(template.optionalModules()));
            assertThrows(UnsupportedOperationException.class, () -> all.clear());
        }
        assertThrows(UnsupportedOperationException.class, () -> catalog.categories().clear());
    }

    @Test
    void rejectsTamperedCategoryAndOptionalModules() {
        var capabilities=new CapabilityEngine();
        assertTrue(capabilities.available().contains(CUSTOMERS));
        assertEquals(Set.of(CUSTOMERS),capabilities.effective(Set.of(CUSTOMERS,ROOMS)));
        assertDoesNotThrow(()->capabilities.validateOptional(Set.of(INVENTORY)));
        assertThrows(IllegalArgumentException.class,()->capabilities.validateOptional(Set.of(ROOMS)));
        assertThrows(IllegalArgumentException.class, () -> catalog.require("unknown"));
        assertThrows(IllegalArgumentException.class, () -> catalog.require(null));
        assertThrows(IllegalArgumentException.class, () -> catalog.resolveModules("retail", Set.of(ROOMS)));
        assertThrows(IllegalArgumentException.class, () -> catalog.resolveModules("retail", null));
        assertThrows(IllegalArgumentException.class, () -> catalog.resolveModules("retail", Set.of(SALES)));
        assertTrue(catalog.resolveModules("generic", Set.of(INVENTORY)).contains(INVENTORY));
    }

    @Test
    void publicApiReturnsVersionedMetadata() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new CatalogController(catalog)).build();
        mvc.perform(get("/api/v1/catalog"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.version").value(1))
            .andExpect(jsonPath("$.categories.length()").value(11))
            .andExpect(jsonPath("$.categories[0].id").value("retail"))
            .andExpect(jsonPath("$.modules").isArray());
    }
}
