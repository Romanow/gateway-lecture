package ru.romanow.gateway.utils

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import org.springframework.cloud.gateway.filter.ratelimit.AbstractRateLimiter
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

class InMemoryRateLimiter(replenishRate: Int, burstCapacity: Int, duration: Duration) :
    AbstractRateLimiter<InMemoryRateLimiter.Config>(Config::class.java, CONFIGURATION_PROPERTY_NAME, null) {

    private val ipBucketMap: MutableMap<String, Bucket> = ConcurrentHashMap()
    private val config: Config

    init {
        config = Config(replenishRate, burstCapacity, duration)
    }

    override fun isAllowed(routeId: String, id: String): Mono<RateLimiter.Response> {
        val bucket = ipBucketMap.computeIfAbsent(id) {
            val refill: Refill = Refill.greedy(config.replenishRate.toLong(), config.duration)
            val limit: Bandwidth = Bandwidth.classic(config.burstCapacity.toLong(), refill)
            Bucket.builder()
                .addLimit(limit)
                .build()
        }

        // tryConsume returns false immediately if no tokens available with the bucket
        val probe = bucket.tryConsumeAndReturnRemaining(1)
        return if (probe.isConsumed) {
            // the limit is not exceeded
            Mono.just<RateLimiter.Response>(RateLimiter.Response(true, headers(probe.getRemainingTokens())))
        } else {
            // limit is exceeded
            Mono.just<RateLimiter.Response>(RateLimiter.Response(false, headers(-1)))
        }
    }

    private fun headers(tokensLeft: Long): Map<String, String> {
        return mapOf(
            REMAINING_HEADER to tokensLeft.toString(),
            REPLENISH_RATE_HEADER to config.replenishRate.toString(),
            BURST_CAPACITY_HEADER to config.burstCapacity.toString()
        )
    }

    data class Config(
        var replenishRate: Int = 0,
        var burstCapacity: Int = 0,
        var duration: Duration = Duration.ofSeconds(1)
    )

    companion object {
        private const val CONFIGURATION_PROPERTY_NAME = "in-memory-rate-limiter"
        private const val REMAINING_HEADER = "X-RateLimit-Remaining"
        private const val REPLENISH_RATE_HEADER = "X-RateLimit-Replenish-Rate"
        private const val BURST_CAPACITY_HEADER = "X-RateLimit-Burst-Capacity"
    }
}
