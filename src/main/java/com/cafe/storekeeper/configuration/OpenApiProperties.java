package com.cafe.storekeeper.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "config.openapi")
@Component
@Data
public class OpenApiProperties {
    private String title;
    private String description;
    private String terms;
    private OpenApiContactProperties contact;
    private String licence;
    private String licenceUrl;
    private String version;
}
