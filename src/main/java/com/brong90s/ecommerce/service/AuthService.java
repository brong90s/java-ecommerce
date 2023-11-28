package com.brong90s.ecommerce.service;

import java.io.IOException;

import com.brong90s.ecommerce.dto.ResponseDataDto;
import com.brong90s.ecommerce.dto.ResponseMessageDto;
import com.brong90s.ecommerce.dto.user.ResponseUserDto;
import com.brong90s.ecommerce.dto.user.SignInDto;
import com.brong90s.ecommerce.dto.user.SignUpDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
  ResponseUserDto fetchLoggedInUserByToken(HttpServletRequest httpServletRequest);

  ResponseMessageDto signUp(SignUpDto signUpDto);

  ResponseDataDto signIn(SignInDto signInDto);

  ResponseDataDto refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
