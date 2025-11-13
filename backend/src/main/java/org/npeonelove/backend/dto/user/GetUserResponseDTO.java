package org.npeonelove.backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.npeonelove.backend.model.user.UserMode;
import org.npeonelove.backend.model.user.UserRole;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserResponseDTO {

    private Long userId;
    private String userName;
    private Integer age;
    private UserMode mode;
    private Integer experience;
    private UserRole role;
    private LocalDateTime createdAt;

}
