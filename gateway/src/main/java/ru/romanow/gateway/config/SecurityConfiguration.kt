package ru.romanow.gateway.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
class SecurityConfiguration {
    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.authorizeExchange { spec: AuthorizeExchangeSpec ->
            spec
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/manage/**").permitAll()
                .pathMatchers("/dict/**").authenticated()
        }
            .httpBasic()
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun userDetailsService(): ReactiveUserDetailsService {
        val passwordEncoder = passwordEncoder()
        val userDetails = User.withUsername(USER_NAME)
            .passwordEncoder { rawPassword: String? -> passwordEncoder.encode(rawPassword) }
            .password(PASSWORD)
            .authorities("ROLE_USER")
            .build()
        return MapReactiveUserDetailsService(userDetails)
    }

    companion object {
        const val USER_NAME = "ronin"
        const val PASSWORD = "test"
    }
}
