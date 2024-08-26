package ru.romanow.gateway.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application")
data class ApplicationProperties(
    val routes: Routes,
    val retry: Retry,
    val rateLimiter: RateLimiter
)

data class Routes(
    val dictionary: String
)

data class Retry(
    val retryCount: Int
)

data class RateLimiter(
    var replenishRate: Int,
    var burstCapacity: Int
)
