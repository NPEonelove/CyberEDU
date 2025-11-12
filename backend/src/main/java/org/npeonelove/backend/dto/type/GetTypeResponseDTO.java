package org.npeonelove.backend.dto.type;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GetTypeResponseDTO {

    private UUID typeId;
    private String title;

}
