package com.ghpham11a.xp_springboot_apigateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;

public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final Key signingKey;

    public JwtReactiveAuthenticationManager(String secretKey) {
        // For simplicity, using a static key. In production, store/rotate securely!
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.fromCallable(() -> {
            String token = (String) authentication.getCredentials();
            try {
                // Parse the token
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(signingKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                // Example: read an authority/role from the claims
                String role = claims.get("role", String.class);

                // Build an Authentication object
                // You could also build a collection of authorities from the token.
                AbstractAuthenticationToken authToken = new AbstractAuthenticationToken(
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))) {
                    @Override
                    public Object getCredentials() {
                        return token;
                    }

                    @Override
                    public Object getPrincipal() {
                        return claims.getSubject(); // e.g. "userId"
                    }
                };
                authToken.setAuthenticated(true);

                return authToken;
            } catch (Exception e) {
                throw new BadCredentialsException("Invalid JWT token", e);
            }
        });
    }
}