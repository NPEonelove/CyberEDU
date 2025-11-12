package org.npeonelove.backend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.npeonelove.backend.model.user.UserRole;

@Getter
@Setter
@Schema(description = "DTO containing JWT claims information for authenticated user")
public class GetJwtUserClaimsResponseDTO {

    private Long userId;
    private UserRole role;

}