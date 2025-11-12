package org.npeonelove.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.npeonelove.backend.dto.user.GetUserResponseDTO;
import org.npeonelove.backend.dto.user.UpdateUserRequestDTO;
import org.npeonelove.backend.dto.user.UpdateUserResponseDTO;
import org.npeonelove.backend.exception.user.UserUpdateException;
import org.npeonelove.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<GetUserResponseDTO> getUser(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UpdateUserResponseDTO> updateUser(@PathVariable("userId") Long userId,
                                                            @RequestBody @Valid UpdateUserRequestDTO updateUserRequestDTO,
                                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new UserUpdateException(validateBindingResult(bindingResult));
        }

        return ResponseEntity.ok(userService.updateUser(userId, updateUserRequestDTO));
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
