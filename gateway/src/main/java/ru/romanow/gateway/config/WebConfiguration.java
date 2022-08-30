package ru.romanow.gateway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.romanow.gateway.config.properties.ApplicationProperties;
import ru.romanow.gateway.config.properties.RoutesProperties;
import ru.romanow.gateway.utils.InMemoryRateLimiter;

import static java.time.Duration.ofSeconds;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static reactor.core.publisher.Mono.just;

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
                                .requestRateLimiter(rateLimiterConfig -> rateLimiterConfig
                                        .setRateLimiter(rateLimiter())
                                        .setKeyResolver(keyResolver()))
                                .stripPrefix(1)
                                .prefixPath("/api"))
                        .uri(routes.getDictionary()))
                .build();
    }

    @Bean
    public RateLimiter<InMemoryRateLimiter.Config> rateLimiter() {
        return new InMemoryRateLimiter(1, 2, ofSeconds(10));
    }

    @Bean
    public KeyResolver keyResolver() {
        return exchange -> just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }

}
