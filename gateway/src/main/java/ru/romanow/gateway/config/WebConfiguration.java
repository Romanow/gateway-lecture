package ru.romanow.gateway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.romanow.gateway.config.properties.ApplicationProperties;
import ru.romanow.gateway.config.properties.RoutesProperties;

import java.net.ConnectException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

import static java.time.Duration.ofSeconds;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

@Configuration
@EnableConfigurationProperties({
        ApplicationProperties.class,
        RoutesProperties.class
})
public class WebConfiguration {

    @Bean
    public RouteLocator routers(RouteLocatorBuilder builder, RoutesProperties routes) {
        return builder
                .routes()
                .route("dictionary", pathSpec -> pathSpec
                        .path("/dict/**")
                        .filters(filterSpec -> filterSpec
                                .addRequestHeader("X-Gateway-Timestamp", ISO_DATE_TIME.format(now()))
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setBackoff(ofSeconds(1), ofSeconds(10), 2, false))
                                .stripPrefix(1))
                        .uri(routes.getDictionary()))
                .build();
    }
}
