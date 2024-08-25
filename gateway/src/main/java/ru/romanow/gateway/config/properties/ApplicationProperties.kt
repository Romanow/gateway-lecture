package ru.romanow.gateway.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application.routes")
data class ApplicationProperties(
    var dictionary: String? = null
)
