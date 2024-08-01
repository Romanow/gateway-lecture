package ru.romanow.gateway.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.config.GlobalCorsProperties;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.support.RouteMetadataUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import ru.romanow.gateway.config.properties.ApplicationProperties;
import ru.romanow.gateway.config.properties.RoutesProperties;
import ru.romanow.gateway.models.LegoSet;
import ru.romanow.gateway.models.LegoSetWithSeries;
import ru.romanow.gateway.models.Series;
import ru.romanow.gateway.utils.InMemoryRateLimiter;

import static java.time.Duration.ofSeconds;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
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
                .route("dictionary-lego-sets", pathSpec -> pathSpec
                        .path("/dict/v1/lego-sets/{id}")
                        .filters(filterSpec -> filterSpec
                                .addRequestHeader("X-Gateway-Timestamp", ISO_DATE_TIME.format(now()))
                                .requestRateLimiter(rateLimiterConfig -> rateLimiterConfig
                                        .setRateLimiter(rateLimiter())
                                        .setKeyResolver(keyResolver()))
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setStatuses(HttpStatus.NOT_FOUND)
                                        .setSeries(HttpStatus.Series.SERVER_ERROR)
                                        .setBackoff(ofSeconds(1), ofSeconds(10), 2, false))
                                .modifyResponseBody(LegoSet.class,
                                                    LegoSetWithSeries.class,
                                                    responseRewriteFunction(routes))
                                .stripPrefix(1)
                                .prefixPath("/api"))
                        .uri(routes.getDictionary()))
                .route("dictionary", pathSpec -> pathSpec
                        .path("/dict/**")
                        .filters(filterSpec -> filterSpec
                                .addRequestHeader("X-Gateway-Timestamp", ISO_DATE_TIME.format(now()))
                                .requestRateLimiter(rateLimiterConfig -> rateLimiterConfig
                                        .setRateLimiter(rateLimiter())
                                        .setKeyResolver(keyResolver()))
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setStatuses(HttpStatus.NOT_FOUND)
                                        .setSeries(HttpStatus.Series.SERVER_ERROR)
                                        .setBackoff(ofSeconds(1), ofSeconds(10), 2, false))
                                .stripPrefix(1)
                                .prefixPath("/api"))
                        .metadata(RouteMetadataUtils.RESPONSE_TIMEOUT_ATTR, 2000)
                        .uri(routes.getDictionary()))
                .build();
    }

    @Bean
    public RateLimiter<InMemoryRateLimiter.Config> rateLimiter() {
        return new InMemoryRateLimiter(1, 2, ofSeconds(10));
    }

    @Bean
    public KeyResolver keyResolver() {
        return exchange -> just(exchange.getRequest()
                                        .getRemoteAddress()
                                        .getAddress()
                                        .getHostAddress());
    }

    @Bean
    public WebClient webClient() {
        return WebClient
                .builder()
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public RewriteFunction<LegoSet, LegoSetWithSeries> responseRewriteFunction(RoutesProperties routes) {
        return (exchange, legoSet) -> webClient()
                .get()
                .uri(routes.getDictionary(), uriBuilder -> uriBuilder
                        .path("/api/v1/series/{series}")
                        .build(legoSet.getSeriesName()))
                .retrieve()
                .bodyToMono(Series.class)
                .map(series -> buildLegoWithSeries(legoSet, series));
    }

    @NotNull
    private LegoSetWithSeries buildLegoWithSeries(@NotNull LegoSet legoSet, @Nullable Series series) {
        return new LegoSetWithSeries()
                .setNumber(legoSet.getNumber())
                .setName(legoSet.getName())
                .setPartsCount(legoSet.getPartsCount())
                .setAge(legoSet.getAge())
                .setSuggestedPrice(legoSet.getSuggestedPrice())
                .setSeries(series);
    }

}
