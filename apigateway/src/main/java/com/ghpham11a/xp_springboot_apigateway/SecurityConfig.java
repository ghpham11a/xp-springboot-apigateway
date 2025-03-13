package com.ghpham11a.xp_springboot_apigateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

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
                // Insert your custom filter before Spring Security's Authentication phase
                // .addFilterBefore(customWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }

    /**
     * Example custom filter bean.
     */
    @Bean
    public WebFilter customWebFilter() {
        return (exchange, chain) -> {
            // Always add this response header for all requests
            exchange.getResponse().getHeaders().add("X-Response-Inspected", "true");

            HttpMethod method = exchange.getRequest().getMethod();

            // Only handle the request body if it's a POST or PATCH
            if (HttpMethod.POST.equals(method) || HttpMethod.PATCH.equals(method)) {
                return DataBufferUtils.join(exchange.getRequest().getBody())
                        .flatMap(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            DataBufferUtils.release(dataBuffer);

                            String originalBody = new String(bytes, StandardCharsets.UTF_8);
                            // e.g., "scan" or manipulate the body
                            System.out.println("Request Body: " + originalBody);

                            // If you still want the downstream handlers to read the body,
                            // you must re-inject it:
                            Flux<DataBuffer> patchedBody = Flux.defer(() -> {
                                DataBuffer buffer = exchange.getResponse()
                                        .bufferFactory().wrap(bytes);
                                return Mono.just(buffer);
                            });

                            // Mutate the request (e.g. add headers)
                            ServerHttpRequest mutatedRequest = exchange.getRequest()
                                    .mutate()
                                    .header("X-Body-Inspected", "true")
                                    .build();

                            // Decorate the request so its body is now the new flux
                            ServerWebExchange mutatedExchange = exchange.mutate()
                                    .request(new ServerHttpRequestDecorator(mutatedRequest) {
                                        @Override
                                        public Flux<DataBuffer> getBody() {
                                            return patchedBody;
                                        }
                                    })
                                    .build();

                            return chain.filter(mutatedExchange);
                        });
            } else {
                // For other HTTP methods, do not read/mutate the body; just continue
                return chain.filter(exchange);
            }
        };
    }
}
