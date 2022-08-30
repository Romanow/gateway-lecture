package ru.romanow.gateway.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("application.routes")
public class RoutesProperties {
    private String dictionary;
}
