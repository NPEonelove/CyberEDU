package org.npeonelove.backend.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.npeonelove.backend.client.MlFeignClient;
import org.npeonelove.backend.dto.ml.HealthResponseDTO;
import org.npeonelove.backend.dto.ml.PromptRequestDTO;
import org.npeonelove.backend.dto.ml.PromptResponseDTO;
import org.npeonelove.backend.dto.scenario.ScenarioListResponseDTO;
import org.npeonelove.backend.dto.scenario.ScenarioPageResponseDTO;
import org.npeonelove.backend.dto.type.GetTypeResponseDTO;
import org.npeonelove.backend.exception.scenario.ScenarioNotFoundException;
import org.npeonelove.backend.model.scenario.Scenario;
import org.npeonelove.backend.repository.ScenarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScenarioService {

    private final ScenarioRepository scenarioRepository;
    private final MlFeignClient mlFeignClient;
    private final ModelMapper modelMapper;

    // получение всех сценариев
    public List<ScenarioListResponseDTO>  getAllScenarios() {
        List<Scenario> scenarios = scenarioRepository.findAll();
        List<ScenarioListResponseDTO> scenarioListResponseDTOList = new ArrayList<>();

        for (Scenario scenario : scenarios) {
            ScenarioListResponseDTO scenarioListResponseDTO = modelMapper.map(scenario, ScenarioListResponseDTO.class);
            scenarioListResponseDTO.setScenarioType(modelMapper.map(scenario.getType(), GetTypeResponseDTO.class));

            scenarioListResponseDTOList.add(scenarioListResponseDTO);
        }

        return scenarioListResponseDTOList;
    }

    // получение всех сценариев по определенному типу
    public List<ScenarioListResponseDTO> getScenariosByTypeId(UUID typeId) {
        List<Scenario> scenarios = scenarioRepository.findScenarioByType_TypeId(typeId);
        List<ScenarioListResponseDTO> scenarioListResponseDTOList = new ArrayList<>();

        for (Scenario scenario : scenarios) {
            if (scenario.getType().getTypeId().equals(typeId)) {
                ScenarioListResponseDTO scenarioListResponseDTO = modelMapper.map(scenario, ScenarioListResponseDTO.class);
                scenarioListResponseDTO.setScenarioType(modelMapper.map(scenario.getType(), GetTypeResponseDTO.class));

                scenarioListResponseDTOList.add(scenarioListResponseDTO);
            }
        }

        return scenarioListResponseDTOList;
    }

    // получить конкретный сценарий по id
    public ScenarioPageResponseDTO getScenarioById(UUID scenarioId) {
        Scenario scenario = scenarioRepository.findById(scenarioId).orElseThrow(
                () -> new ScenarioNotFoundException("Scenario with id " + scenarioId + " not found")
        );

        ScenarioPageResponseDTO scenarioPageResponseDTO = modelMapper.map(scenario, ScenarioPageResponseDTO.class);
        scenarioPageResponseDTO.setScenarioType(modelMapper.map(scenario.getType(), GetTypeResponseDTO.class));

        return scenarioPageResponseDTO;
    }

    // получить фидбек по сценарию (неважно с текстом пользователя или нет)
    public PromptResponseDTO sendPrompt(PromptRequestDTO promptRequestDTO) {
        return mlFeignClient.sendPrompt(promptRequestDTO);
    }

    // проверить, что ml сервис жив
    public HealthResponseDTO healthCheck() {
        return mlFeignClient.healthCheck();
    }
}
