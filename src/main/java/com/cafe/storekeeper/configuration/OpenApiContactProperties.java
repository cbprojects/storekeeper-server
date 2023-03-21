package com.cafe.storekeeper.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "config.openapi.contact")
@Component
@Data
public class OpenApiContactProperties {
    private String name;
    private String url;
    private String email;
}
