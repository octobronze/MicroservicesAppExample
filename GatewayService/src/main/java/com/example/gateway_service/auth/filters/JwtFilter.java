package com.example.gateway_service.auth.filters;

import com.example.gateway_service.auth.exceptions.BadRequestException;
import com.example.gateway_service.auth.other.JwtCheckResponse;
import com.example.gateway_service.auth.other.UserDetails;
import com.example.gateway_service.auth.services.JwtService;
import com.example.gateway_service.auth.services.UserDetailsService;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

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

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

            String authHeader = exchange.getRequest().getHeaders().getFirst(HEADER_NAME);

            if (authHeader != null
                    &&  authHeader.startsWith(JWT_PREFIX)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                String jwt = authHeader.substring(JWT_PREFIX.length());

                JwtCheckResponse jwtCheckResponse = jwtService.checkUserToken(jwt);

                if (!jwtCheckResponse.isValid()) {
                    throw new BadRequestException(jwtCheckResponse.getResponseString());
                }

                Integer userId = jwtService.getUserId(jwt);
                UserDetails userDetails = userDetailsService.getUserDetailsFromJWT(jwt);

                SecurityContext context = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                context.setAuthentication(authenticationToken);

                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .headers(headers -> {
                            headers.remove(HEADER_NAME);
                            headers.add(USER_ID_HEADER_NAME, String.valueOf(userId));
                        })
                        .build();

                ServerWebExchange modifiedExchange = exchange.mutate()
                        .request(modifiedRequest)
                        .build();

                return chain.filter(modifiedExchange).contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
            }

            return chain.filter(exchange);
    }
}
