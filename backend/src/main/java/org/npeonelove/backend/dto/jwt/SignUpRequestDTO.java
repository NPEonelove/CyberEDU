package org.npeonelove.backend.dto.jwt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequestDTO {

//    private String token;
    private Long userId;
    private String username;
    private Integer age;

}
