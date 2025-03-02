package com.example.gateway_service.auth.config;

import com.example.gateway_service.auth.filters.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static com.example.gateway_service.auth.consts.SecuredUrls.USER_SECURED_URLS_NO_AUTHORITIES;

/**
 * Конфигурация безопасности приложения.
 */
@Configuration
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Возвращает цепочку фильтров безопасности.
     *
     * @param http объект для настройки безопасности
     * @return {@link SecurityWebFilterChain} с настроенными правилами
     */
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorizeExchangeSpec ->
                        authorizeExchangeSpec
                                .pathMatchers(USER_SECURED_URLS_NO_AUTHORITIES).authenticated()
                                .anyExchange().permitAll()
                )
                .addFilterBefore(jwtFilter, SecurityWebFiltersOrder.AUTHORIZATION)
                .build();
    }

}
