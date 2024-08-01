package ru.romanow.gateway.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application")
data class ApplicationProperties(
    var routes: RoutesProperties
)

class RoutesProperties {
    var dictionary: String? = null
}
