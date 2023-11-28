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

import com.brong90s.ecommerce.security.jwt.AuthEntryPointJwt;
import com.brong90s.ecommerce.security.jwt.JwtAuthFilter;

import static com.brong90s.ecommerce.entity.enums.Permission.*;
import static com.brong90s.ecommerce.entity.enums.Role.CUSTOMER;
import static com.brong90s.ecommerce.entity.enums.Role.SHOPKEEPER;
import static org.springframework.http.HttpMethod.*;

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

    @Autowired
    private final AuthEntryPointJwt authEntryPointJwt;

    /**
     * Bean for JWT authentication filter.
     * 
     * @return The JWT authentication filter bean.
     */
    @Bean
    public JwtAuthFilter jwtAuthenticationFilter() {
        return new JwtAuthFilter(exceptionResolver);
    }

    /***
     * Configure security filters and policies for HTTP requests.
     * 
     * @param httpSecurity The HTTP security configuration.
     * @return The security filter chain.
     * @throws Exception If an exception occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .exceptionHandling(customizer -> customizer.authenticationEntryPoint(authEntryPointJwt))
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
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .logout((logout) -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler(((request, response,
                                authentication) -> SecurityContextHolder
                                        .clearContext())));
        return httpSecurity.build();
    }
}
