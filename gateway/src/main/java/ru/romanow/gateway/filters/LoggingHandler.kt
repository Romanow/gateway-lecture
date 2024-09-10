package ru.romanow.gateway.filters

import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class LoggingHandler(private val operation: String) : RewriteFunction<String, String> {
    private val logger = LoggerFactory.getLogger(LoggingHandler::class.java)

    override fun apply(exchange: ServerWebExchange, body: String?): Publisher<String> {
        logger.info(
            "{} {} {}: {}", operation, exchange.request.method, exchange.request.path,
            if (body != null) ellipsis(body) else "[empty body]"
        )
        return if (body != null) Mono.just(body) else Mono.empty()
    }

    private fun ellipsis(text: String): String =
        if (text.length > SIZE) text.substring(0, SIZE) + "..." else text

    companion object {
        private const val SIZE = 255
    }
}
