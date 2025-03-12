package com.ghpham11a.xp_springboot_apigateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        //.pathMatchers("/public/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }

//    private static final String SECRET_KEY = "THIS_IS_A_256_BIT_SECRET_FOR_JWT_PURPOSES_ONLY_1234";

//    @Bean
//    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//        // 1) Create Authentication Manager
//        ReactiveAuthenticationManager jwtManager = new JwtReactiveAuthenticationManager(SECRET_KEY);
//
//        // 2) Create an AuthenticationWebFilter to process JWT tokens
//        AuthenticationWebFilter jwtAuthFilter = new AuthenticationWebFilter(jwtManager);
//        jwtAuthFilter.setServerAuthenticationConverter(exchange -> {
//            // Extract token from the Authorization header
//            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//            if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                String token = authHeader.substring(7);
//                return Mono.just(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(null, token));
//            }
//            return Mono.empty();
//        });
//
//        // 3) (Optional) Set a SecurityContextRepository so the authenticated user is stored in the session
//        ServerSecurityContextRepository securityContextRepository = new WebSessionServerSecurityContextRepository();
//        jwtAuthFilter.setSecurityContextRepository(securityContextRepository);
//
//        // 4) Define your security rules
//        return http
//                .csrf(csrf -> csrf.disable())
//                .authorizeExchange(auth -> auth
//                        // Routes that need JWT auth
//                        .pathMatchers("/users/**", "/orders/**").authenticated()
//                        // Everything else is allowed without auth
//                        .anyExchange().permitAll()
//                )
//                .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
//                .build();
//    }
}
