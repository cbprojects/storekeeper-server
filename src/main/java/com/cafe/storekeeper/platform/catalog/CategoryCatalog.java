package com.cafe.storekeeper.platform.catalog;

import static com.cafe.storekeeper.platform.catalog.BusinessModule.*;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class CategoryCatalog {
    private final List<CategoryTemplate> templates = List.of(
        template("retail", "Tienda / comercio", "Ventas e inventario", "shopping-cart", "#087BFF", "commerce", Set.of(CUSTOMERS, CATALOG, INVENTORY, SALES, PAYMENTS, POS, SUPPLIERS, PURCHASES, REPORTS, RECEIPTS), Set.of(EMPLOYEES)),
        template("gym", "Gimnasio", "Membresías y asistencia", "bolt", "#16803D", "memberships", Set.of(CUSTOMERS, MEMBERSHIPS, ATTENDANCE, PAYMENTS, EMPLOYEES, REPORTS), Set.of(CATALOG, SALES)),
        template("restaurant", "Restaurante", "Mesas y comandas", "objects-column", "#C45416", "tables", Set.of(MENU, TABLES, ORDERS, SALES, PAYMENTS, REPORTS), Set.of(INVENTORY, SUPPLIERS, PURCHASES, EMPLOYEES)),
        template("hotel", "Hotel", "Disponibilidad y estancias", "building", "#244779", "occupancy", Set.of(ROOMS, RESERVATIONS, CUSTOMERS, PAYMENTS, REPORTS), Set.of(CATALOG, EMPLOYEES)),
        template("salon", "Salón / barbería", "Agenda y profesionales", "calendar", "#9333EA", "agenda", Set.of(CATALOG, PROFESSIONALS, APPOINTMENTS, CUSTOMERS, SALES, PAYMENTS, REPORTS), Set.of(INVENTORY)),
        template("academy", "Academia / escuela", "Grupos e inscripciones", "graduation-cap", "#1872B8", "courses", Set.of(CUSTOMERS, COURSES, ENROLLMENTS, ATTENDANCE, EMPLOYEES, PAYMENTS, REPORTS), Set.of(CATALOG)),
        template("clinic", "Consultorio", "Agenda administrativa sin datos clínicos", "calendar-plus", "#087F8C", "agenda", Set.of(CUSTOMERS, CATALOG, PROFESSIONALS, APPOINTMENTS, PAYMENTS, REPORTS), Set.of(EMPLOYEES)),
        template("professional", "Servicios profesionales", "Proyectos y trabajo registrado", "briefcase", "#4F46E5", "projects", Set.of(CUSTOMERS, CATALOG, PROJECTS, TASKS, QUOTES, TIME_TRACKING, PAYMENTS, REPORTS), Set.of(EMPLOYEES)),
        template("generic", "Negocio genérico", "Operación modular configurable", "th-large", "#087BFF", "overview", Set.of(CUSTOMERS, CATALOG, SALES, PAYMENTS, REPORTS), Set.of(INVENTORY, PURCHASES, SUPPLIERS, POS, EMPLOYEES, RECEIPTS)),
        template("legal", "Despacho legal", "Asuntos administrativos y honorarios", "briefcase", "#243859", "cases", Set.of(CUSTOMERS, LEGAL_CASES, TASKS, PROFESSIONALS, PAYMENTS, REPORTS), Set.of(TIME_TRACKING, QUOTES)),
        template("pools", "Piscinas", "Productos, mantenimiento y servicios para piscinas", "waves", "#008A9B", "commerce", Set.of(CUSTOMERS, CATALOG, INVENTORY, SALES, PAYMENTS, REPORTS), Set.of(SUPPLIERS, EMPLOYEES))
    );

    public List<CategoryTemplate> categories() {
        return templates;
    }

    public List<BusinessModule> modules() {
        return List.copyOf(Arrays.asList(BusinessModule.values()));
    }

    public CategoryTemplate require(String id) {
        return templates.stream().filter(template -> template.id().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Categoría no disponible"));
    }

    /** Checks template policy only. Membership and deployed capability checks are separate. */
    public Set<BusinessModule> resolveModules(String categoryId, Set<BusinessModule> requestedOptional) {
        CategoryTemplate template = require(categoryId);
        if (requestedOptional == null || !template.optionalModules().containsAll(requestedOptional)) {
            throw new IllegalArgumentException("Módulos opcionales no permitidos");
        }
        Set<BusinessModule> resolved = new LinkedHashSet<>(template.requiredModules());
        resolved.addAll(requestedOptional);
        return Set.copyOf(resolved);
    }

    private static CategoryTemplate template(String id, String name, String description,
            String icon, String accent, String layout, Set<BusinessModule> required,
            Set<BusinessModule> optional) {
        return new CategoryTemplate(id, 1, name, description, "pi pi-" + icon,
                accent, layout, id, required, optional);
    }
}
