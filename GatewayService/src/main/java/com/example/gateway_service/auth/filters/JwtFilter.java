package com.example.gateway_service.auth.filters;

import com.example.gateway_service.auth.exceptions.BadRequestException;
import com.example.gateway_service.auth.services.JwtService;
import com.example.gateway_service.auth.services.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Фильтр для проверки JWT токена в запросах.
 */
@Component
public class JwtFilter implements WebFilter {
    private static final String HEADER_NAME = "Authorization";
    private static final String JWT_PREFIX = "Bearer ";
    private static final String USER_ID_HEADER_NAME = "userId";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Фильтрует входящие запросы, проверяя наличие и валидность JWT токена.
     *
     * @param exchange текущий веб-обмен
     * @param chain    цепочка фильтров
     * @return {@link Mono}, который завершается после выполнения фильтрации
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var authHeader = exchange.getRequest().getHeaders().getFirst(HEADER_NAME);

        if (authHeader != null && authHeader.startsWith(JWT_PREFIX)) {
            return ReactiveSecurityContextHolder.getContext()
                    .map(SecurityContext::getAuthentication)
                    .defaultIfEmpty(new UsernamePasswordAuthenticationToken(null, null))
                    .flatMap(authentication -> {
                        if (authentication.getPrincipal() == null) {
                            var jwt = authHeader.substring(JWT_PREFIX.length());

                            var jwtCheckResponse = jwtService.checkUserToken(jwt);

                            if (!jwtCheckResponse.isValid()) {
                                return Mono.error(new BadRequestException(jwtCheckResponse.responseString()));
                            }

                            var claims = jwtCheckResponse.claims();
                            int userId = Integer.parseInt(claims.getSubject());
                            var userDetails = userDetailsService.getUserDetailsFromClaims(claims);

                            var authenticationToken = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );

                            var modifiedRequest = exchange.getRequest().mutate()
                                    .headers(headers -> {
                                        headers.remove(HEADER_NAME);
                                        headers.add(USER_ID_HEADER_NAME, String.valueOf(userId));
                                    })
                                    .build();

                            var modifiedExchange = exchange.mutate()
                                    .request(modifiedRequest)
                                    .build();

                            return chain.filter(modifiedExchange)
                                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(
                                            Mono.just(new SecurityContextImpl(authenticationToken))));
                        }

                        return chain.filter(exchange);
                    });
        }

        return chain.filter(exchange);
    }
}
