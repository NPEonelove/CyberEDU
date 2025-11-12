package org.npeonelove.backend.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.npeonelove.backend.model.user.UserMode;

@Getter
@Setter
public class UpdateUserRequestDTO {

    private String username;
    private Integer age;
    private UserMode mode;

}
