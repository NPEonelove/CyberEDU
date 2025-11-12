package org.npeonelove.backend.dto.scenario;

import lombok.Getter;
import lombok.Setter;
import org.npeonelove.backend.dto.type.GetTypeResponseDTO;
import org.npeonelove.backend.model.scenario.Type;

import java.util.UUID;

@Getter
@Setter
public class ScenarioPageResponseDTO {

    private UUID scenarioId;
    private String title;
    private String text;
    private Boolean scam;
    private GetTypeResponseDTO scenarioType;

}
