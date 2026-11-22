package com.cafe.storekeeper.platform.commerce;

import static com.cafe.storekeeper.platform.commerce.CommerceModels.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.cafe.storekeeper.platform.business.BusinessService;
import com.cafe.storekeeper.platform.identity.IdentityService;

@RestController
@RequestMapping("/api/v1/businesses/{businessId}/commerce")
public class CommerceController {
    private final BusinessService businesses;private final CommerceService commerce;private final IdentityService identity;
    public CommerceController(BusinessService businesses,CommerceService commerce,IdentityService identity){this.businesses=businesses;this.commerce=commerce;this.identity=identity;}
    public record ProductInput(@NotBlank @Size(max=80) String sku,@NotBlank @Size(max=120) String name,
            @NotBlank @Pattern(regexp="PRODUCT|SERVICE") String type,@NotNull @DecimalMin("0.00") @Digits(integer=12,fraction=2) BigDecimal price,
            @Size(max=300000) String image){}
    public record ImageInput(@Size(max=300000) String image){}
    public record StockInput(@Min(-1000000000) @Max(1000000000) long quantity,@NotBlank @Size(max=300) String reason){}
    public record SaleLine(@NotBlank String productId,@Min(1) @Max(1000000) long quantity){}
    public record SaleInput(@NotEmpty @Size(max=100) List<@NotNull @Valid SaleLine> lines,@NotNull @DecimalMin("0") @DecimalMax("100") @Digits(integer=3,fraction=4) BigDecimal taxPercent,
            @NotNull @DecimalMin("0") @Digits(integer=14,fraction=2) BigDecimal discount){}
    public record PaymentInput(@NotNull @DecimalMin("0.01") @Digits(integer=14,fraction=2) BigDecimal amount,@NotBlank @Pattern(regexp="CASH|TRANSFER|CARD_RECORDED|OTHER") String method){}
    public record ReturnInput(@NotBlank @Size(max=300) String reason){}
    public record ReturnAuthorizationInput(@NotBlank @Size(max=300) String reason,
            @NotBlank @Email @Size(max=254) String email,
            @com.fasterxml.jackson.annotation.JsonProperty(access=com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY)
            @NotBlank @Size(max=72) String password) {
        @Override public String toString() { return "ReturnAuthorizationInput[redacted]"; }
    }
    @GetMapping("/products") public List<Product> products(Authentication a,@PathVariable String businessId){return commerce.products(businesses.authorize(a.getName(),businessId));}
    @PostMapping("/products") public Product product(Authentication a,@PathVariable String businessId,@Valid @RequestBody ProductInput input){return commerce.product(businesses.authorize(a.getName(),businessId),input);}
    @PutMapping("/products/{productId}/image") public Product image(Authentication a,@PathVariable String businessId,@PathVariable String productId,@Valid @RequestBody ImageInput input){return commerce.image(businesses.authorize(a.getName(),businessId),productId,input.image());}
    @PostMapping("/products/{productId}/stock") public Movement stock(Authentication a,@PathVariable String businessId,@PathVariable String productId,@RequestHeader("Idempotency-Key") String key,@Valid @RequestBody StockInput input){return commerce.stock(businesses.authorize(a.getName(),businessId),productId,key,input);}
    @GetMapping("/movements") public List<Movement> movements(Authentication a,@PathVariable String businessId){return commerce.movements(businesses.authorize(a.getName(),businessId));}
    @GetMapping("/sales") public List<Sale> sales(Authentication a,@PathVariable String businessId){return commerce.sales(businesses.authorize(a.getName(),businessId));}
    @PostMapping("/sales") public Sale sale(Authentication a,@PathVariable String businessId,@RequestHeader("Idempotency-Key") String key,@Valid @RequestBody SaleInput input){return commerce.sale(businesses.authorize(a.getName(),businessId),key,input);}
    @PostMapping("/sales/{saleId}/payments") public Payment payment(Authentication a,@PathVariable String businessId,@PathVariable String saleId,@RequestHeader("Idempotency-Key") String key,@Valid @RequestBody PaymentInput input){return commerce.payment(businesses.authorize(a.getName(),businessId),saleId,key,input);}
    @PostMapping("/sales/{saleId}/return") public Return fullReturn(Authentication a,@PathVariable String businessId,@PathVariable String saleId,@RequestHeader("Idempotency-Key") String key,@Valid @RequestBody ReturnAuthorizationInput input){var scope=businesses.authorize(a.getName(),businessId);identity.reauthorize(a.getName(),input.email(),input.password());return commerce.fullReturn(scope,saleId,key,new ReturnInput(input.reason()));}
    @GetMapping("/report") public Report report(Authentication a,@PathVariable String businessId,@RequestParam Instant from,@RequestParam Instant to){return commerce.report(businesses.authorize(a.getName(),businessId),from,to);}
}
