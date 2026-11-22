package com.cafe.storekeeper.platform.commerce;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.cafe.storekeeper.platform.business.BusinessService;

@RestController
@RequestMapping("/api/v1/businesses/{businessId}/directory/{kind}")
public class DirectoryController {
    private final BusinessService businesses;private final DirectoryService directory;
    public DirectoryController(BusinessService businesses,DirectoryService directory){this.businesses=businesses;this.directory=directory;}
    public record Input(@NotBlank @Size(max=120) String name,@Email @Size(max=254) String email,
            @Size(max=40) String phone,@Size(max=120) String role,boolean active,@Min(0) long version){}
    @GetMapping public List<DirectoryEntry> list(Authentication a,@PathVariable String businessId,@PathVariable String kind){return directory.list(businesses.authorize(a.getName(),businessId),kind);}
    @PostMapping public DirectoryEntry create(Authentication a,@PathVariable String businessId,@PathVariable String kind,@Valid @RequestBody Input input){return directory.create(businesses.authorize(a.getName(),businessId),kind,input);}
    @PutMapping("/{id}") public DirectoryEntry update(Authentication a,@PathVariable String businessId,@PathVariable String kind,@PathVariable String id,@Valid @RequestBody Input input){return directory.update(businesses.authorize(a.getName(),businessId),kind,id,input);}
}
