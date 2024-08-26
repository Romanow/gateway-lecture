package ru.romanow.gateway

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.maciejwalkowiak.wiremock.spring.ConfigureWireMock
import com.maciejwalkowiak.wiremock.spring.EnableWireMock
import com.maciejwalkowiak.wiremock.spring.InjectWireMock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebExchangeDecorator
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import ru.romanow.gateway.models.LegoSet
import ru.romanow.gateway.models.Series
import java.math.BigDecimal
import java.net.InetSocketAddress


@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableWireMock(value = [ConfigureWireMock(name = "dictionary", property = "application.routes.dictionary")])
internal class GatewayApplicationTest {

    @InjectWireMock("dictionary")
    private lateinit var wiremock: WireMockServer

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    private lateinit var webClient: WebTestClient

    @BeforeEach
    fun init() {
        webClient = WebTestClient.bindToApplicationContext(applicationContext)
            .webFilter(SetRemoteAddressWebFilter("127.0.0.1"))
            .configureClient()
            .build()

        val ferrari = buildLegoSet(FERRARI_NUMBER)
        wiremock.stubFor(
            WireMock.get("/api/v1/lego-sets")
                .willReturn(
                    aResponse()
                        .withBody(objectMapper.writeValueAsString(listOf(ferrari)))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                )
        )
        wiremock.stubFor(
            WireMock.get("/api/v1/lego-sets/$FERRARI_NUMBER")
                .willReturn(
                    aResponse()
                        .withBody(objectMapper.writeValueAsString(ferrari))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                )
        )

        val technic = buildSeries(TECHNIC_SERIES, "TECHNIC")
        val trains = buildSeries(TRAINS_SERIES, "SYSTEM")
        wiremock.stubFor(
            WireMock.get("/api/v1/series")
                .willReturn(
                    aResponse()
                        .withBody(objectMapper.writeValueAsString(listOf(technic, trains)))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                )
        )
        wiremock.stubFor(
            WireMock.get("/api/v1/series/$TECHNIC_SERIES")
                .willReturn(
                    aResponse()
                        .withBody(objectMapper.writeValueAsString(technic))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                )
        )
        wiremock.stubFor(
            WireMock.get("/api/v1/series/$TRAINS_SERIES")
                .willReturn(
                    aResponse()
                        .withFixedDelay(3000)
                        .withBody(objectMapper.writeValueAsString(trains))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                )
        )
    }

    @Test
    fun testWrongAuth() {
        webClient.get()
            .uri("/dict/v1/series")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun testHealthCheck() {
        webClient.get()
            .uri("/manage/health")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun testLegoSets() {
        webClient.get()
            .uri("/dict/v1/lego-sets")
            .headers { it.setBasicAuth("program", "test") }
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$").isArray
            .jsonPath("$[0].number").isEqualTo(FERRARI_NUMBER)
            .jsonPath("$[0].name").isEqualTo("Ferrari Daytona SP3")
            .jsonPath("$[0].age").isEqualTo(18)
            .jsonPath("$[0].partsCount").isEqualTo(3778)
            .jsonPath("$[0].suggestedPrice").isEqualTo(399)
            .jsonPath("$[0].seriesName").isEqualTo(TECHNIC_SERIES)
    }

    @Test
    fun testLegoSetById() {
        webClient.get()
            .uri("/dict/v1/lego-sets/$FERRARI_NUMBER")
            .headers { it.setBasicAuth("program", "test") }
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.number").isEqualTo(FERRARI_NUMBER)
            .jsonPath("$.name").isEqualTo("Ferrari Daytona SP3")
            .jsonPath("$.age").isEqualTo(18)
            .jsonPath("$.partsCount").isEqualTo(3778)
            .jsonPath("$.suggestedPrice").isEqualTo(399)
            .jsonPath("$.series.name").isEqualTo(TECHNIC_SERIES)
            .jsonPath("$.series.age").isEqualTo(12)
            .jsonPath("$.series.type").isEqualTo("TECHNIC")
            .jsonPath("$.series.complexity").isEqualTo("ADVANCED")
    }

    @Test
    fun testSeries() {
        webClient.get()
            .uri("/dict/v1/series")
            .headers { it.setBasicAuth("program", "test") }
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$").isArray
            .jsonPath("$[0].name").isEqualTo(TECHNIC_SERIES)
            .jsonPath("$[0].age").isEqualTo(12)
            .jsonPath("$[0].type").isEqualTo("TECHNIC")
            .jsonPath("$[0].complexity").isEqualTo("ADVANCED")
    }

    @Test
    fun testTimeout() {
        webClient.get()
            .uri("/dict/v1/series/$TRAINS_SERIES")
            .headers { it.setBasicAuth("program", "test") }
            .exchange()
            .expectStatus().isEqualTo(504)
    }

    private fun buildLegoSet(number: String) =
        LegoSet(
            number = number,
            name = "Ferrari Daytona SP3",
            age = 18,
            partsCount = 3778,
            suggestedPrice = BigDecimal(399),
            seriesName = TECHNIC_SERIES
        )

    private fun buildSeries(name: String, type: String) =
        Series(
            name = name,
            age = 12,
            type = type,
            complexity = "ADVANCED"
        )

    companion object {
        private const val FERRARI_NUMBER = "42143"
        private const val TECHNIC_SERIES = "Technic"
        private const val TRAINS_SERIES = "Trains"
    }
}

class SetRemoteAddressWebFilter(private val host: String) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return chain.filter(decorate(exchange))
    }

    private fun decorate(exchange: ServerWebExchange): ServerWebExchange {
        val decorated = object : ServerHttpRequestDecorator(exchange.request) {
            override fun getRemoteAddress(): InetSocketAddress {
                return InetSocketAddress(host, 80)
            }
        }

        return object : ServerWebExchangeDecorator(exchange) {
            override fun getRequest(): ServerHttpRequest {
                return decorated
            }
        }
    }
}
