package org.npeonelove.backend.controller;

import feign.FeignException;
import org.npeonelove.backend.exception.ErrorResponse;
import org.npeonelove.backend.exception.auth.SignInException;
import org.npeonelove.backend.exception.auth.SignUpException;
import org.npeonelove.backend.exception.auth.JwtValidationException;
import org.npeonelove.backend.exception.scenario.ScenarioNotFoundException;
import org.npeonelove.backend.exception.scenario.ScenarioValidationException;
import org.npeonelove.backend.exception.user.UserAlreadyExistsException;
import org.npeonelove.backend.exception.user.UserNotFoundException;
import org.npeonelove.backend.exception.user.UserUpdateException;
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
    private final HttpStatus jwtValidationStatus = HttpStatus.BAD_REQUEST;
    private final HttpStatus userNotFoundStatus = HttpStatus.NOT_FOUND;
    private final HttpStatus userAlreadyExistsStatus = HttpStatus.CONFLICT;
    private final HttpStatus userUpdateStatus = HttpStatus.BAD_REQUEST;
    private final HttpStatus authenticationStatus = HttpStatus.UNAUTHORIZED;
    private final HttpStatus scenarioNotFoundStatus = HttpStatus.NOT_FOUND;
    private final HttpStatus scenarioValidationStatus = HttpStatus.BAD_REQUEST;
    private final HttpStatus feignInternalServerErrorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    private final HttpStatus feignExceptionStatus = HttpStatus.SERVICE_UNAVAILABLE;

    @ExceptionHandler(FeignException.InternalServerError.class)
    public ResponseEntity<ErrorResponse> handleFeignInternalServerError(FeignException.InternalServerError ex) {
        return ResponseEntity.status(feignInternalServerErrorStatus).body(
                new ErrorResponse(
                        feignInternalServerErrorStatus.value(),
                        "ML service error",
                        "Internal error in ML service: " + ex.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException ex) {
        return ResponseEntity.status(feignExceptionStatus).body(
                new ErrorResponse(
                        feignExceptionStatus.value(),
                        "External service unavailable",
                        "ML service is currently unavailable: " + ex.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

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

    @ExceptionHandler(JwtValidationException.class)
    public ResponseEntity<ErrorResponse> handleJwtValidation(JwtValidationException ex) {
        return ResponseEntity.status(jwtValidationStatus).body(
                new ErrorResponse(
                        jwtValidationStatus.value(),
                        "JWT validation error",
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

    @ExceptionHandler(UserUpdateException.class)
    public ResponseEntity<ErrorResponse> handleUserUpdate(UserUpdateException ex) {
        return ResponseEntity.status(userUpdateStatus).body(
                new ErrorResponse(
                        userUpdateStatus.value(),
                        "User update error",
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

    @ExceptionHandler(ScenarioNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleScenarioNotFound(ScenarioNotFoundException ex) {
        return ResponseEntity.status(scenarioNotFoundStatus).body(
                new ErrorResponse(
                        scenarioNotFoundStatus.value(),
                        "Scenario not found",
                        ex.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(ScenarioValidationException.class)
    public ResponseEntity<ErrorResponse> handleScenarioValidation(ScenarioValidationException ex) {
        return ResponseEntity.status(scenarioValidationStatus).body(
                new ErrorResponse(
                        scenarioValidationStatus.value(),
                        "Scenario validation error",
                        ex.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

}