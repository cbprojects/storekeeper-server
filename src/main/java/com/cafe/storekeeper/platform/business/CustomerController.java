package com.cafe.storekeeper.platform.business;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/businesses/{businessId}/customers")
public class CustomerController {
    private final BusinessService businesses;
    private final CustomerService customers;
    public CustomerController(BusinessService businesses, CustomerService customers) { this.businesses = businesses; this.customers = customers; }
    public record Input(@NotBlank @Size(max=120) String name, @Email @Size(max=254) String email,
            @Size(max=40) String phone, @Min(0) long version) {}
    @GetMapping public List<Customer> list(Authentication auth, @PathVariable String businessId) {
        return customers.list(businesses.authorize(auth.getName(), businessId));
    }
    @PostMapping public Customer create(Authentication auth, @PathVariable String businessId, @Valid @RequestBody Input input) {
        return customers.create(businesses.authorize(auth.getName(), businessId),input);
    }
    @PutMapping("/{id}") public Customer update(Authentication auth, @PathVariable String businessId, @PathVariable String id, @Valid @RequestBody Input input) {
        return customers.update(businesses.authorize(auth.getName(), businessId),id,input);
    }
    @DeleteMapping("/{id}") public void delete(Authentication auth, @PathVariable String businessId, @PathVariable String id, @RequestParam long version) {
        customers.delete(businesses.authorize(auth.getName(), businessId),id,version);
    }
}
