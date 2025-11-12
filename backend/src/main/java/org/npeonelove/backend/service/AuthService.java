package org.npeonelove.backend.service;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.npeonelove.backend.dto.jwt.JwtAuthenticationDTO;
import org.npeonelove.backend.dto.jwt.RefreshTokenDTO;
import org.npeonelove.backend.dto.jwt.SignInRequestDTO;
import org.npeonelove.backend.dto.jwt.SignUpRequestDTO;
import org.npeonelove.backend.exception.user.UserAlreadyExistsException;
import org.npeonelove.backend.model.user.User;
import org.npeonelove.backend.model.user.UserMode;
import org.npeonelove.backend.model.user.UserRole;
import org.npeonelove.backend.security.jwt.JwtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;

    // регистрация пользователя
    @Transactional
    public JwtAuthenticationDTO signUp(SignUpRequestDTO signUpRequestDTO) {

        // TODO: сделать нормальную логику с зашифрованной строкой

        if (userService.existsUserById(signUpRequestDTO.getUserId())) {
            throw new UserAlreadyExistsException("User with id " + signUpRequestDTO.getUserId() + " already exists");
        }

        User userEntity = userService.saveUser(User.builder()
                .userId(signUpRequestDTO.getUserId())
                .username(signUpRequestDTO.getUsername())
                .age(signUpRequestDTO.getAge())
                .mode((signUpRequestDTO.getAge() >= 18) ? UserMode.ADULT : UserMode.CHILD)
                .experience(0)
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .build());

        return jwtService.generateAuthToken(userEntity.getUserId());
    }

    public JwtAuthenticationDTO signIn(SignInRequestDTO signInRequestDTO) {

        // TODO: сделать нормальную логику с зашифрованной строкой

        User user = userService.getUserById(signInRequestDTO.getUserId());

//        if (user.isEmpty()) {
//            throw new UserNotFoundException("User with id " + signInRequestDTO.getUserId() + "does not exist");
//        }

        return jwtService.generateAuthToken(user.getUserId());
    }

    // генерация access токена по refresh токену
    public JwtAuthenticationDTO refreshAccessToken(RefreshTokenDTO refreshTokenDTO) throws AuthenticationException {

        String refreshToken = refreshTokenDTO.getRefreshToken();

        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            User user = userService.getUserById(Long.parseLong(jwtService.getUserIdFromJwtToken(refreshToken)));
            return jwtService.refreshAccessToken(user.getUserId(), refreshToken);
        }

        throw new AuthenticationException("Invalid refresh token");
    }

}
