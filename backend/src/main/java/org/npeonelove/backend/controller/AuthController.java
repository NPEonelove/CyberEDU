package org.npeonelove.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.npeonelove.backend.dto.jwt.JwtAuthenticationDTO;
import org.npeonelove.backend.dto.jwt.RefreshTokenDTO;
import org.npeonelove.backend.dto.jwt.SignInRequestDTO;
import org.npeonelove.backend.dto.jwt.SignUpRequestDTO;
import org.npeonelove.backend.exception.auth.JwtValidationException;
import org.npeonelove.backend.exception.auth.SignInException;
import org.npeonelove.backend.exception.auth.SignUpException;
import org.npeonelove.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/sign-up")
    public ResponseEntity<JwtAuthenticationDTO> signUp(@RequestBody @Valid SignUpRequestDTO signUpRequestDTO,
                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new SignUpException(validateBindingResult(bindingResult));
        }

        return ResponseEntity.ok(authService.signUp(signUpRequestDTO));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<JwtAuthenticationDTO> signIn(@RequestBody @Valid SignInRequestDTO signInRequestDTO,
                                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new SignInException(validateBindingResult(bindingResult));
        }

        return ResponseEntity.ok(authService.signIn(signInRequestDTO));
    }

    @PostMapping("/refresh-access-token")
    public ResponseEntity<JwtAuthenticationDTO> refreshAccessToken(@RequestBody @Valid RefreshTokenDTO refreshTokenDTO,
                                                                   BindingResult bindingResult) throws AuthenticationException {
        if (bindingResult.hasErrors()) {
            throw new JwtValidationException(validateBindingResult(bindingResult));
        }

        return ResponseEntity.ok(authService.refreshAccessToken(refreshTokenDTO));
    }

    // получение строки с ошибками валидации для исключений
    private String validateBindingResult(BindingResult bindingResult) {
        StringBuilder errors = new StringBuilder();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.append(error.getDefaultMessage());
            errors.append(" ");
        }
        return errors.toString();
    }
}
