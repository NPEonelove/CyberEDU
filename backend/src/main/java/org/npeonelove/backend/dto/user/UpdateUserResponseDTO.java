package org.npeonelove.backend.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.npeonelove.backend.model.user.UserMode;
import org.npeonelove.backend.model.user.UserRole;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateUserResponseDTO {

    private Long userId;
    private String userName;
    private Integer age;
    private UserMode mode;
    private Integer experience;
    private UserRole role;
    private LocalDateTime createdAt;

}
