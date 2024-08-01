package ru.romanow.gateway.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory
import org.springframework.cloud.gateway.filter.factory.RetryGatewayFilterFactory.RetryConfig
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter
import org.springframework.cloud.gateway.route.Route
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.Buildable
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec
import org.springframework.cloud.gateway.route.builder.PredicateSpec
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.support.RouteMetadataUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Mono
import ru.romanow.gateway.config.properties.ApplicationProperties
import ru.romanow.gateway.config.properties.RoutesProperties
import ru.romanow.gateway.models.LegoSet
import ru.romanow.gateway.models.LegoSetWithSeries
import ru.romanow.gateway.models.Series
import ru.romanow.gateway.utils.InMemoryRateLimiter
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.function.Function

@Configuration
@EnableConfigurationProperties(ApplicationProperties::class)
class WebConfiguration {

//    @Bean
//    fun routers(builder: RouteLocatorBuilder, routes: RoutesProperties): RouteLocator {
//        return builder
//            .routes()
//            .route("dictionary-lego-sets", Function<PredicateSpec, Buildable<Route>> { pathSpec: PredicateSpec ->
//                pathSpec
//                    .path("/dict/v1/lego-sets/{id}")
//                    .filters { filterSpec: GatewayFilterSpec ->
//                        filterSpec
//                            .addRequestHeader(
//                                "X-Gateway-Timestamp",
//                                DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now())
//                            )
//                            .requestRateLimiter { rateLimiterConfig: RequestRateLimiterGatewayFilterFactory.Config ->
//                                rateLimiterConfig
//                                    .setRateLimiter(rateLimiter()).keyResolver = keyResolver()
//                            }
//                            .retry { retryConfig: RetryConfig ->
//                                retryConfig
//                                    .setRetries(3)
//                                    .setStatuses(HttpStatus.NOT_FOUND)
//                                    .setSeries(HttpStatus.Series.SERVER_ERROR)
//                                    .setBackoff(Duration.ofSeconds(1), Duration.ofSeconds(10), 2, false)
//                            }
//                            .modifyResponseBody<LegoSet, LegoSetWithSeries>(
//                                LegoSet::class.java,
//                                LegoSetWithSeries::class.java,
//                                responseRewriteFunction(routes)
//                            )
//                            .stripPrefix(1)
//                            .prefixPath("/api")
//                    }
//                    .uri(routes.getDictionary())
//            })
//            .route("dictionary", Function<PredicateSpec, Buildable<Route>> { pathSpec: PredicateSpec ->
//                pathSpec
//                    .path("/dict/**")
//                    .filters { filterSpec: GatewayFilterSpec ->
//                        filterSpec
//                            .addRequestHeader(
//                                "X-Gateway-Timestamp",
//                                DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now())
//                            )
//                            .requestRateLimiter { rateLimiterConfig: RequestRateLimiterGatewayFilterFactory.Config ->
//                                rateLimiterConfig
//                                    .setRateLimiter(rateLimiter()).keyResolver = keyResolver()
//                            }
//                            .retry { retryConfig: RetryConfig ->
//                                retryConfig
//                                    .setRetries(3)
//                                    .setStatuses(HttpStatus.NOT_FOUND)
//                                    .setSeries(HttpStatus.Series.SERVER_ERROR)
//                                    .setBackoff(Duration.ofSeconds(1), Duration.ofSeconds(10), 2, false)
//                            }
//                            .stripPrefix(1)
//                            .prefixPath("/api")
//                    }
//                    .metadata(RouteMetadataUtils.RESPONSE_TIMEOUT_ATTR, 2000)
//                    .uri(routes.getDictionary())
//            })
//            .build()
//    }

//    @Bean
//    fun rateLimiter(): RateLimiter<InMemoryRateLimiter.Config> {
//        return InMemoryRateLimiter(1, 2, Duration.ofSeconds(10))
//    }

//    @Bean
//    fun keyResolver(): KeyResolver {
//        return KeyResolver { exchange: ServerWebExchange ->
//            Mono.just(
//                exchange.request
//                    .remoteAddress
//                    .address
//                    .hostAddress
//            )
//        }
//    }

//    @Bean
//    fun webClient(): WebClient {
//        return WebClient
//            .builder()
//            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
//            .build()
//    }

//    @Bean
//    fun responseRewriteFunction(routes: RoutesProperties): RewriteFunction<LegoSet, LegoSetWithSeries> {
//        return RewriteFunction<LegoSet, LegoSetWithSeries> { _, legoSet ->
//            webClient()
//                .get()
//                .uri(routes.dictionary) { uriBuilder: UriBuilder ->
//                    uriBuilder
//                        .path("/api/v1/series/{series}")
//                        .build(legoSet.seriesName)
//                }
//                .retrieve()
//                .bodyToMono<Series>(Series::class.java)
//                .map<LegoSetWithSeries> { series: Series? -> buildLegoWithSeries(legoSet, series) }
//        }
//    }

    private fun buildLegoWithSeries(legoSet: LegoSet, series: Series) =
        LegoSetWithSeries(
            number = legoSet.number,
            name = legoSet.name,
            partsCount = legoSet.partsCount,
            age = legoSet.age,
            suggestedPrice = legoSet.suggestedPrice,
            series = series
        )
}
