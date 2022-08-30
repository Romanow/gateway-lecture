package ru.romanow.gateway.utils;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.gateway.filter.ratelimit.AbstractRateLimiter;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.time.Duration.ofSeconds;

public class InMemoryRateLimiter
        extends AbstractRateLimiter<InMemoryRateLimiter.Config> {

    private static final String CONFIGURATION_PROPERTY_NAME = "in-memory-rate-limiter";
    private static final String REMAINING_HEADER = "X-RateLimit-Remaining";
    private static final String REPLENISH_RATE_HEADER = "X-RateLimit-Replenish-Rate";
    private static final String BURST_CAPACITY_HEADER = "X-RateLimit-Burst-Capacity";

    private final Map<String, Bucket> ipBucketMap = new ConcurrentHashMap<>();

    private final InMemoryRateLimiter.Config config;

    public InMemoryRateLimiter(int replenishRate, int burstCapacity, @NotNull Duration duration) {
        super(Config.class, CONFIGURATION_PROPERTY_NAME, null);
        this.config = new InMemoryRateLimiter.Config()
                .setReplenishRate(replenishRate)
                .setBurstCapacity(burstCapacity)
                .setDuration(duration);
    }

    @Override
    public Mono<Response> isAllowed(String routeId, String id) {
        // How many requests per second do you want a user to be allowed to do?
        int replenishRate = config.getReplenishRate();

        // How much bursting do you want to allow?
        int burstCapacity = config.getBurstCapacity();

        Bucket bucket = ipBucketMap.computeIfAbsent(id, k -> {
            var refill = Refill.greedy(replenishRate, config.getDuration());
            var limit = Bandwidth.classic(burstCapacity, refill);
            return Bucket.builder()
                    .addLimit(limit)
                    .build();
        });

        // tryConsume returns false immediately if no tokens available with the bucket
        var probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            // the limit is not exceeded
            return Mono.just(new RateLimiter.Response(true, headers(probe.getRemainingTokens())));
        } else {
            // limit is exceeded
            return Mono.just(new RateLimiter.Response(false, headers(-1)));
        }
    }

    private Map<String, String> headers(long tokensLeft) {
        return Map.of(
                REMAINING_HEADER, Long.toString(tokensLeft),
                REPLENISH_RATE_HEADER, Integer.toString(config.getReplenishRate()),
                BURST_CAPACITY_HEADER, Integer.toString(config.getReplenishRate())
        );
    }

    @Data
    @Accessors(chain = true)
    public static class Config {
        private int replenishRate;
        private int burstCapacity = 0;
        private Duration duration = ofSeconds(1);
    }
}