package org.npeonelove.backend.controller;

import org.npeonelove.backend.exception.ErrorResponse;
import org.npeonelove.backend.exception.auth.SignInException;
import org.npeonelove.backend.exception.auth.SignUpException;
import org.npeonelove.backend.exception.user.UserAlreadyExistsException;
import org.npeonelove.backend.exception.user.UserNotFoundException;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final HttpStatus signUpStatus = HttpStatus.BAD_REQUEST;
    private final HttpStatus signInStatus = HttpStatus.BAD_REQUEST;
    private final HttpStatus userNotFoundStatus = HttpStatus.NOT_FOUND;
    private final HttpStatus userAlreadyExistsStatus = HttpStatus.CONFLICT;
    private final HttpStatus authenticationStatus = HttpStatus.UNAUTHORIZED;

    @ExceptionHandler(SignUpException.class)
    public ResponseEntity<ErrorResponse> handleSignUp(SignUpException ex) {
        return ResponseEntity.status(signUpStatus).body(
                new ErrorResponse(
                        signUpStatus.value(),
                        "Sign up error",
                        ex.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(SignInException.class)
    public ResponseEntity<ErrorResponse> handleSignIn(SignInException ex) {
        return ResponseEntity.status(signInStatus).body(
                new ErrorResponse(
                        signInStatus.value(),
                        "Sign in error",
                        ex.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(userNotFoundStatus).body(
                new ErrorResponse(
                        userNotFoundStatus.value(),
                        "User not found",
                        ex.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return ResponseEntity.status(userAlreadyExistsStatus).body(
                new ErrorResponse(
                        userAlreadyExistsStatus.value(),
                        "User already exists",
                        ex.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
        return ResponseEntity.status(authenticationStatus).body(
                new ErrorResponse(
                        authenticationStatus.value(),
                        "Authentication error",
                        ex.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

}