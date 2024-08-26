package ru.romanow.gateway.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.filters
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.cloud.gateway.support.RouteMetadataUtils.RESPONSE_TIMEOUT_ATTR
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders.ACCEPT
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import ru.romanow.gateway.config.properties.ApplicationProperties
import ru.romanow.gateway.models.LegoSet
import ru.romanow.gateway.models.LegoSetWithSeries
import ru.romanow.gateway.models.Series
import ru.romanow.gateway.utils.InMemoryRateLimiter
import java.io.IOException
import java.time.Duration.ofSeconds
import java.time.LocalDateTime.*
import java.time.format.DateTimeFormatter

@Configuration
@EnableConfigurationProperties(ApplicationProperties::class)
class WebConfiguration {

    @Bean
    fun routers(builder: RouteLocatorBuilder, properties: ApplicationProperties) =
        builder.routes {
            route(id = "dictionary-lego-sets") {
                path("/dict/v1/lego-sets/{id}")
                filters {
                    stripPrefix(1)
                    prefixPath("/api")
                    addRequestHeader(TIMESTAMP_HEADER, DateTimeFormatter.ISO_DATE_TIME.format(now()))
                    requestRateLimiter {
                        it.rateLimiter = rateLimiter(properties)
                        it.keyResolver = keyResolver()
                    }
                    retry {
                        it.retries = properties.retry.retryCount
                        it.setStatuses(HttpStatus.BAD_GATEWAY, HttpStatus.GATEWAY_TIMEOUT)
                        it.setExceptions(IOException::class.java)
                        it.setBackoff(ofSeconds(1), ofSeconds(10), 2, false)
                    }
                    modifyResponseBody(
                        LegoSet::class.java,
                        LegoSetWithSeries::class.java,
                        responseRewriteFunction(properties)
                    )
                }
                uri(properties.routes.dictionary)
                metadata(RESPONSE_TIMEOUT_ATTR, 500)
            }
            route(id = "dictionary") {
                path("/dict/**")
                filters {
                    stripPrefix(1)
                    prefixPath("/api")
                    addRequestHeader(TIMESTAMP_HEADER, DateTimeFormatter.ISO_DATE_TIME.format(now()))
                    requestRateLimiter {
                        it.rateLimiter = rateLimiter(properties)
                        it.keyResolver = keyResolver()
                    }
                    retry {
                        it.retries = properties.retry.retryCount
                        it.setStatuses(HttpStatus.BAD_GATEWAY, HttpStatus.GATEWAY_TIMEOUT)
                        it.setExceptions(IOException::class.java)
                        it.setBackoff(ofSeconds(1), ofSeconds(10), 2, false)
                    }
                }
                uri(properties.routes.dictionary)
                metadata(RESPONSE_TIMEOUT_ATTR, 500)
            }
        }

    @Bean
    fun rateLimiter(properties: ApplicationProperties) =
        InMemoryRateLimiter(properties.rateLimiter.replenishRate, properties.rateLimiter.burstCapacity, ofSeconds(10))

    @Bean
    fun keyResolver() = KeyResolver { Mono.just(it.request.remoteAddress!!.address.hostAddress) }

    @Bean
    fun webClient(): WebClient {
        return WebClient
            .builder()
            .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
            .build()
    }

    @Bean
    fun responseRewriteFunction(properties: ApplicationProperties): RewriteFunction<LegoSet, LegoSetWithSeries> {
        return RewriteFunction<LegoSet, LegoSetWithSeries> { _, legoSet ->
            webClient()
                .get()
                .uri(properties.routes.dictionary) { it.path("/api/v1/series/{series}").build(legoSet.seriesName) }
                .retrieve()
                .bodyToMono(Series::class.java)
                .map { series: Series? -> buildLegoWithSeries(legoSet, series!!) }
        }
    }

    private fun buildLegoWithSeries(legoSet: LegoSet, series: Series) =
        LegoSetWithSeries(
            number = legoSet.number,
            name = legoSet.name,
            partsCount = legoSet.partsCount,
            age = legoSet.age,
            suggestedPrice = legoSet.suggestedPrice,
            series = series
        )

    companion object {
        private const val TIMESTAMP_HEADER = "X-Gateway-Timestamp"
    }
}
