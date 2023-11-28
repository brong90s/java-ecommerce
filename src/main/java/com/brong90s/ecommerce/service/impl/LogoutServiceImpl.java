package com.brong90s.ecommerce.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.brong90s.ecommerce.entity.Token;
import com.brong90s.ecommerce.entity.User;
import com.brong90s.ecommerce.entity.enums.TokenType;
import com.brong90s.ecommerce.exception.CustomException;
import com.brong90s.ecommerce.repository.TokenRepository;
import com.brong90s.ecommerce.repository.UserRepository;
import com.brong90s.ecommerce.security.jwt.JwtUtils;
import com.brong90s.ecommerce.service.LogoutService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService, LogoutHandler {
    private final TokenRepository tokenRepository;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Please enter valid bearer token");
        }

        jwt = authHeader.split(" ")[1].trim();

        var isTokenValid = tokenRepository.findByToken(jwt)
                .map(token -> !token.getRevoked() && !token.getExpired()
                        && token.getTokenType().equals(TokenType.ACCESS))
                .orElse(false);

        if (isTokenValid) {
            var userEmail = jwtUtils.extractUsername(jwt);
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            List<Token> userValidToken = tokenRepository.findAllValidTokenByUser(user.getId());

            userValidToken.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });
            tokenRepository.saveAll(userValidToken);
        }

        throw new CustomException("Please enter valid token");
    }
}
