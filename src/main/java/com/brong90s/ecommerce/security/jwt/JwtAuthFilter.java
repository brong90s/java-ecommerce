package com.brong90s.ecommerce.security.jwt;

import io.jsonwebtoken.io.IOException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.brong90s.ecommerce.repository.TokenRepository;

@RequiredArgsConstructor
@Log4j2
public class JwtAuthFilter extends OncePerRequestFilter {
    private HandlerExceptionResolver exceptionResolver;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthFilter(HandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

    /**
     * Filter incoming HTTP requests to process JWT tokens and set the authenticated
     * user if the token if valid.
     * 
     * @param request     The incoming HttpServletRequest.
     * @param response    The HttpServeltResponse for sending responses.
     * @param filterChain The filter chain for processing subsequent filters.
     * @throws ServeltException If there's a servlet-related exception.
     * @throws IOException      If there's an I/O error.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // final String authHeader = request.getHeader("Authorization");
        // final String jwt;
        final String userEmail;

        try {

            String jwt = parseJwt(request);
            log.error("JwtAuthFilter | doFilterInternal | jwt: {}", jwt);

            if (jwt == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // jwt = authHeader.split(" ")[1].trim();
            userEmail = jwtUtils.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var isTokenValid = tokenRepository.findByToken(jwt)
                        .map(token -> !token.getRevoked() && !token.getExpired())
                        .orElseThrow(() -> new SignatureException("Please enter valid token"));

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtUtils.isTokenValid(jwt, userDetails) && isTokenValid) {
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

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        log.info("JwtAuthFilter | parseJwt | headerAuth: {}", headerAuth);

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            log.info("JwtAuthFilter | parseJwt | parseJwt: {}", headerAuth.substring(7, headerAuth.length()));

            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }

}
