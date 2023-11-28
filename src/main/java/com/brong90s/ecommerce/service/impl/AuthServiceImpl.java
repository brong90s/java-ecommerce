package com.brong90s.ecommerce.service.impl;

import com.brong90s.ecommerce.dto.ResponseDataDto;
import com.brong90s.ecommerce.dto.ResponseMessageDto;
import com.brong90s.ecommerce.dto.user.ResponseTokenDto;
import com.brong90s.ecommerce.dto.user.ResponseUserDto;
import com.brong90s.ecommerce.dto.user.SignInDto;
import com.brong90s.ecommerce.dto.user.SignUpDto;
import com.brong90s.ecommerce.entity.Token;
import com.brong90s.ecommerce.entity.User;
import com.brong90s.ecommerce.entity.enums.Role;
import com.brong90s.ecommerce.entity.enums.TokenType;
import com.brong90s.ecommerce.exception.CustomException;
import com.brong90s.ecommerce.repository.TokenRepository;
import com.brong90s.ecommerce.repository.UserRepository;
import com.brong90s.ecommerce.security.jwt.JwtUtils;
import com.brong90s.ecommerce.service.AuthService;

import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.brong90s.ecommerce.util.Util.getUserByToken;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
        private final UserRepository userRepository;
        private final TokenRepository tokenRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtils jwtUtils;

        private void saveUserToken(User user, String jwtToken, TokenType type) {
                var token = Token
                                .builder()
                                .user(user)
                                .token(jwtToken)
                                .tokenType(type)
                                .expired(false)
                                .revoked(false)
                                .build();
                tokenRepository.save(token);
        }

        public ResponseUserDto fetchLoggedInUserByToken(
                        HttpServletRequest request) {
                User user = getUserByToken(request, jwtUtils, this.userRepository);
                return ResponseUserDto.builder()
                                .id(user.getId())
                                .firstname(user.getFirstName())
                                .lastname(user.getLastName())
                                .email(user.getEmail())
                                .mobileNumber(user.getMobileNumber())
                                .build();
        }

        private void revokeAllUserTokens(User user) {
                var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
                System.out.println(user.getId());
                if (validUserTokens.isEmpty())
                        return;

                validUserTokens.forEach(token -> {
                        token.setExpired(true);
                        token.setRevoked(true);
                });

                tokenRepository.saveAll(validUserTokens);
        }

        public ResponseMessageDto signUp(SignUpDto signUpDto) {
                Optional<User> userEmail = userRepository.findByEmail(signUpDto.getEmail());
                if (userEmail.isPresent()) {
                        throw new CustomException("User email already exists");
                }

                User user = User.builder()
                                .firstName(signUpDto.getFirstName())
                                .lastName(signUpDto.getLastName())
                                .mobileNumber(signUpDto.getMobileNumber())
                                .email(signUpDto.getEmail())
                                .password(passwordEncoder.encode(signUpDto.getPassword()))
                                .role(Role.CUSTOMER)
                                // .role(Role.SHOPKEEPER)
                                .build();

                User savedUser = userRepository.save(user);
                String jwtToken = jwtUtils.generateToken(savedUser);
                String refreshToken = jwtUtils.generateRefreshToken(savedUser);
                saveUserToken(savedUser, jwtToken, TokenType.ACCESS);
                saveUserToken(savedUser, refreshToken, TokenType.REFRESH);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                                savedUser.getEmail(), savedUser.getPassword());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                ResponseMessageDto responseDto = new ResponseMessageDto(HttpStatus.OK.value(),
                                "User created successefully");
                return responseDto;
        }

        public ResponseDataDto signIn(SignInDto signInDto) {

                User user = userRepository.findByEmail(signInDto.getEmail())
                                .orElseThrow(() -> new CustomException("Invalid email or password"));

                if (passwordEncoder.matches(signInDto.getPassword(), user.getPassword())) {

                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                        signInDto.getEmail(), signInDto.getPassword());

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        revokeAllUserTokens(user);
                        String jwtToken = jwtUtils.generateToken(user);
                        String refreshToken = jwtUtils.generateRefreshToken(user);
                        saveUserToken(user, jwtToken, TokenType.ACCESS);
                        saveUserToken(user, refreshToken, TokenType.REFRESH);

                        ResponseTokenDto token = ResponseTokenDto.builder()
                                        .accessToken(jwtToken)
                                        .refreshToken(refreshToken)
                                        .build();

                        return ResponseDataDto.builder().status(HttpStatus.OK.value()).data(token).build();
                }
                throw new CustomException("Invalid email or password");
        }

        public ResponseDataDto refreshToken(
                        HttpServletRequest request,
                        HttpServletResponse response) throws IOException {
                final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                final String refreshToken;
                final String userEmail;

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        throw new SignatureException("Please enter valid refresh token");
                }

                refreshToken = authHeader.split(" ")[1].trim();
                userEmail = jwtUtils.extractUsername(refreshToken);

                if (userEmail != null) {
                        User user = this.userRepository.findByEmail(userEmail)
                                        .orElseThrow(() -> new SignatureException("Please enter valid token"));

                        Boolean isTokenValid = tokenRepository.findByToken(refreshToken)
                                        .map(token -> !token.getRevoked() && !token.getExpired()
                                                        && token.getTokenType().equals(TokenType.REFRESH))
                                        .orElse(false);

                        if (jwtUtils.isTokenValid(refreshToken, user) && isTokenValid) {
                                String accessToken = jwtUtils.generateToken(user);
                                revokeAllUserTokens(user);
                                saveUserToken(user, accessToken, TokenType.ACCESS);
                                saveUserToken(user, refreshToken, TokenType.REFRESH);

                                ResponseTokenDto token = ResponseTokenDto
                                                .builder()
                                                .accessToken(accessToken)
                                                .refreshToken(refreshToken)
                                                .build();
                                return ResponseDataDto.builder().status(HttpStatus.OK.value()).data(token).build();
                        }

                }
                throw new SignatureException("Please enter valid refresh token");
        }
}
