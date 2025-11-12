package org.npeonelove.backend.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.npeonelove.backend.dto.user.GetJwtUserClaimsResponseDTO;
import org.npeonelove.backend.dto.user.GetUserResponseDTO;
import org.npeonelove.backend.dto.user.UpdateUserRequestDTO;
import org.npeonelove.backend.dto.user.UpdateUserResponseDTO;
import org.npeonelove.backend.exception.user.UserNotFoundException;
import org.npeonelove.backend.model.user.User;
import org.npeonelove.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    // получение юзера для фронта
    public GetUserResponseDTO getUser(Long userId) {
        return modelMapper.map(getUserById(userId), GetUserResponseDTO.class);
    }

    // обновление данных юзера
    @Transactional
    public UpdateUserResponseDTO updateUser(Long userId,
                                           UpdateUserRequestDTO updateUserRequestDTO) {
        User user = getUserById(userId);

        user.setUsername(updateUserRequestDTO.getUsername());
        user.setAge(updateUserRequestDTO.getAge());
        user.setMode(updateUserRequestDTO.getMode());

        return modelMapper.map(saveUser(user), UpdateUserResponseDTO.class);
    }

    // сохранение нового пользователя в системе
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // получение айди и роли для генерации jwt токенов
    public GetJwtUserClaimsResponseDTO getJwtUserClaims(Long userId) {
        return modelMapper.map(getUserById(userId), GetJwtUserClaimsResponseDTO.class);
    }

    // получение юзера (всей сущности) по id
    public User getUserById(Long userId) {
        return userRepository.findUserByUserId(userId).orElseThrow(
                () -> new UserNotFoundException("User with id " + userId.toString() + " not found"));
    }

    // проверка что юзер существует
    public Boolean existsUserById(Long userId) {
        return userRepository.existsById(userId);
    }

}
