package com.brong90s.ecommerce.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.brong90s.ecommerce.entity.User;
import com.brong90s.ecommerce.repository.UserRepository;
import com.brong90s.ecommerce.service.impl.JwtServiceImpl;

@RequiredArgsConstructor
public class Util {
    public static User getUserByToken(HttpServletRequest request, JwtServiceImpl jwtService,
            UserRepository userRepository) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String userEmail;
        final String jwtToken;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Please enter a valid token");
        }

        jwtToken = authHeader.split(" ")[1].trim();
        userEmail = jwtService.extractUsername(jwtToken);

        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
