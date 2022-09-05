package ru.romanow.gateway.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.romanow.gateway.config.properties.ApplicationProperties;
import ru.romanow.gateway.config.properties.RoutesProperties;
import ru.romanow.gateway.models.LegoSet;
import ru.romanow.gateway.models.LegoSetWithSeries;
import ru.romanow.gateway.models.Series;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

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
                                .stripPrefix(1)
                                .prefixPath("/api"))
                        .uri(routes.getDictionary()))
                .build();
    }

    @Bean
    public WebClient webClient() {
        return WebClient
                .builder()
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
                .build();
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
