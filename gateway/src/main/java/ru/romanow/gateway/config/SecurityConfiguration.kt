package ru.romanow.gateway.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.OPTIONS
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
class SecurityConfiguration {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        http.authorizeExchange {
            it.pathMatchers(OPTIONS).permitAll()
            it.pathMatchers(GET, "/manage/**").permitAll()
            it.anyExchange().authenticated()
        }
            .httpBasic { }
            .csrf { it.disable() }
            .build()

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun userDetailsService(): ReactiveUserDetailsService {
        val userDetails = User.withUsername(USER_NAME)
            .passwordEncoder { passwordEncoder().encode(it) }
            .password(PASSWORD)
            .authorities("ROLE_USER")
            .build()
        return MapReactiveUserDetailsService(userDetails)
    }

    companion object {
        const val USER_NAME = "program"
        const val PASSWORD = "test"
    }
}
