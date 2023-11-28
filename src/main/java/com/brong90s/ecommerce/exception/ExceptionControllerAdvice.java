package com.brong90s.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

@RestControllerAdvice
public class ExceptionControllerAdvice {
	@ExceptionHandler(RuntimeException.class)
	public ProblemDetail onRuntimeException(RuntimeException exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(CustomException.class)
	public ProblemDetail onCustomException(CustomException exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ProblemDetail onUsernameNotFoundException(UsernameNotFoundException exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail onMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
				exception.getMessage());
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ProblemDetail onBadCredentialException(BadCredentialsException ex) {
		ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
		errorDetail.setProperty("access_denied_reason", "Authentication failed");
		return errorDetail;
	}

	@ExceptionHandler(SignatureException.class)
	public ProblemDetail onSignatureException(SignatureException ex) {
		ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
		errorDetail.setProperty("access_denied_reason", "JWT Signature not valid");
		return errorDetail;
	}

	@ExceptionHandler(ExpiredJwtException.class)
	public ProblemDetail onExpiredJwtException(ExpiredJwtException ex) {
		ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
		errorDetail.setProperty("access_denied_reason", "JWT token already expired");
		return errorDetail;
	}

}
