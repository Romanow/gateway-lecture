package ru.romanow.gateway.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties("application")
public class ApplicationProperties {
    private RoutesProperties routes;
}
