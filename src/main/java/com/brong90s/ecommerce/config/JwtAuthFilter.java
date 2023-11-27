package com.brong90s.ecommerce.config;

import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.brong90s.ecommerce.repository.TokenRepository;
import com.brong90s.ecommerce.service.impl.JwtServiceImpl;

import reactor.util.annotation.NonNull;

// @Component
// @RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private HandlerExceptionResolver exceptionResolver;
    @Autowired
    private JwtServiceImpl jwtService;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthFilter(HandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        try {

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            jwt = authHeader.split(" ")[1].trim();
            userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var isTokenValid = tokenRepository.findByToken(jwt)
                        .map(token -> !token.getRevoked() && !token.getExpired())
                        .orElseThrow(() -> new SignatureException("Please enter valid token"));

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            exceptionResolver.resolveException(request, response, null, ex);
        }
    }

}
