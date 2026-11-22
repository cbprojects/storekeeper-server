package com.cafe.storekeeper.platform.business;

import java.util.List;
import java.util.Set;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.cafe.storekeeper.platform.catalog.BusinessModule;

@RestController
@RequestMapping("/api/v1/businesses")
public class BusinessController {
    private final BusinessService businesses;
    public BusinessController(BusinessService businesses) { this.businesses = businesses; }
    public record Create(@NotBlank @Size(max=120) String name, @Size(max=1000) String description,
            @NotBlank String categoryId, @NotBlank String country, @NotBlank String currency,
            @NotBlank @Size(max=80) String timezone, @NotBlank @Pattern(regexp="#[0-9a-fA-F]{6}") String accent,
            @Size(max=300000) @Pattern(regexp="^data:image/(png|jpeg|webp);base64,[A-Za-z0-9+/=]+$") String logo,
            @NotBlank String visualStyle, @NotNull Set<BusinessModule> optionalModules) {}
    @GetMapping public List<Business> list(Authentication auth) { return businesses.list(auth.getName()); }
    @PostMapping public Business create(Authentication auth, @RequestHeader("Idempotency-Key") String key, @Valid @RequestBody Create data) {
        return businesses.create(auth.getName(), key, data);
    }
    @GetMapping("/{businessId}") public Business get(Authentication auth, @PathVariable String businessId) {
        return businesses.authorize(auth.getName(), businessId).business();
    }
}
