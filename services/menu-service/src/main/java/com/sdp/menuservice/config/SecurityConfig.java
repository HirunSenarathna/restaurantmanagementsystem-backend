package com.sdp.menuservice.config;


import com.sdp.menuservice.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints for customers (GET requests)
//                        .requestMatchers(HttpMethod.GET, "/api/menu/items").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/menu/items/category/{categoryId}").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/menu/items/{id}").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/menu/categories").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/menu/categories/{id}").permitAll()
//                        // Owner-only endpoints
//                        .requestMatchers(HttpMethod.POST, "/api/menu/categories").hasRole("OWNER")
//                        .requestMatchers(HttpMethod.PUT, "/api/menu/categories/{id}").hasRole("OWNER")
//                        .requestMatchers(HttpMethod.DELETE, "/api/menu/categories/{id}").hasRole("OWNER")
//                        .requestMatchers(HttpMethod.POST, "/api/menu/items").hasRole("OWNER")
//                        .requestMatchers(HttpMethod.PUT, "/api/menu/items/{id}").hasRole("OWNER")
//                        .requestMatchers(HttpMethod.DELETE, "/api/menu/items/{id}").hasRole("OWNER")
//                        // Owner and Waiter endpoints
//                        .requestMatchers(HttpMethod.PUT, "/api/menu/items/{id}/variants/{variantId}/stock").hasAnyRole("OWNER", "WAITER")
//                        .requestMatchers(HttpMethod.PUT, "/api/menu/items/{id}/availability").hasAnyRole("OWNER", "WAITER")
                                .requestMatchers("/api/menu/**").permitAll()
                        // Any other authenticated request
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
