package com.cafe.storekeeper.platform.commerce;
import java.time.Instant;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.cafe.storekeeper.platform.business.BusinessService;

@RestController
@RequestMapping("/api/v1/businesses/{businessId}/reports")
public class BusinessReportsController {
    private final BusinessService businesses;private final BusinessReports reports;
    public BusinessReportsController(BusinessService businesses,BusinessReports reports){this.businesses=businesses;this.reports=reports;}
    @GetMapping public BusinessReports.Document get(Authentication auth,@PathVariable String businessId,@RequestParam String kind,@RequestParam Instant from,@RequestParam Instant to){return reports.generate(businesses.authorize(auth.getName(),businessId),kind,from,to);}
}
