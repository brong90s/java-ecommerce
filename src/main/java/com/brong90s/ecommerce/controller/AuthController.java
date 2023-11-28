package com.brong90s.ecommerce.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brong90s.ecommerce.dto.ResponseDataDto;
import com.brong90s.ecommerce.dto.ResponseMessageDto;
import com.brong90s.ecommerce.dto.user.SignInDto;
import com.brong90s.ecommerce.dto.user.SignUpDto;
import com.brong90s.ecommerce.service.AuthService;
import com.brong90s.ecommerce.service.LogoutService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final LogoutService logoutService;

    @PostMapping("/signup")
    public ResponseMessageDto signup(
            @Valid @RequestBody SignUpDto signUpDto) {
        return authService.signUp(signUpDto);
    }

    @PostMapping("/signin")
    public ResponseDataDto signIn(
            @RequestBody SignInDto signInDto) {
        return authService.signIn(signInDto);
    }

    @PostMapping("/refresh-token")
    public ResponseDataDto refreshToken(
            HttpServletRequest request,
            HttpServletResponse response

    ) throws IOException {
        return authService.refreshToken(request, response);
    }

    @PostMapping("/logout")
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {
        logoutService.logout(request, response, authentication);
    }
}