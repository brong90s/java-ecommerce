package com.brong90s.ecommerce.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static com.brong90s.ecommerce.entity.Permission.*;
import static com.brong90s.ecommerce.entity.Role.CUSTOMER;
import static com.brong90s.ecommerce.entity.Role.SHOPKEEPER;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.DELETE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
        @Autowired
        @Qualifier("handlerExceptionResolver")
        private HandlerExceptionResolver exceptionResolver;
        private final AuthenticationProvider authenticationProvider;
        private final LogoutHandler logoutHandler;

        @Bean
        public JwtAuthFilter jwtAuthFilter() {
                return new JwtAuthFilter(exceptionResolver);
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests((authorize) -> authorize
                                                .requestMatchers("/api/v1/auth/**")
                                                .permitAll()
                                                // SHOPKEEPER ENDPOINTS
                                                .requestMatchers("/api/v1/shopkeeper/**").hasRole(SHOPKEEPER.name())
                                                .requestMatchers(GET, "/api/v1/shopkeeper/**")
                                                .hasAuthority(SHOPKEEPER_READ.name())
                                                .requestMatchers(POST, "/api/v1/shopkeeper/**")
                                                .hasAuthority(SHOPKEEPER_CREATE.name())
                                                .requestMatchers(PUT, "/api/v1/shopkeeper/**")
                                                .hasAuthority(SHOPKEEPER_UPDATE.name())
                                                .requestMatchers(DELETE, "/api/v1/shopkeeper/**")
                                                .hasAuthority(SHOPKEEPER_DELETE.name())

                                                // CUSTOMER ENDPOINTS
                                                .requestMatchers("/api/v1/customer/**").hasRole(CUSTOMER.name())
                                                .requestMatchers(GET, "/api/v1/customer/**")
                                                .hasAuthority(CUSTOMER_READ.name())
                                                .requestMatchers(POST, "/api/v1/customer/**")
                                                .hasAuthority(CUSTOMER_CREATE.name())
                                                .requestMatchers(PUT, "/api/v1/customer/**")
                                                .hasAuthority(CUSTOMER_UPDATE.name())
                                                .requestMatchers(DELETE, "/api/v1/customer/**")
                                                .hasAuthority(CUSTOMER_DELETE.name())

                                                .anyRequest()
                                                .authenticated())
                                .sessionManagement((session) -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                                .logout((logout) -> logout
                                                .logoutUrl("/api/v1/auth/logout")
                                                .addLogoutHandler(logoutHandler)
                                                .logoutSuccessHandler(((request, response,
                                                                authentication) -> SecurityContextHolder
                                                                                .clearContext())));
                return http.build();
        }
}
