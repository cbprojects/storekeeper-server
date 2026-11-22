package com.cafe.storekeeper.platform.commerce;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.cafe.storekeeper.platform.business.BusinessService;

@RestController
@RequestMapping("/api/v1/businesses/{businessId}/commerce/import")
public class BatchImportController {
    private final BusinessService businesses;
    private final BatchImportService imports;
    public BatchImportController(BusinessService businesses,BatchImportService imports) {this.businesses=businesses;this.imports=imports;}
    @PostMapping public BatchImportService.Summary save(Authentication user,@PathVariable String businessId,@RequestBody BatchImportService.Input input) {
        return imports.save(businesses.authorize(user.getName(),businessId),input);
    }
}
