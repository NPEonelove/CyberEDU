package org.npeonelove.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.npeonelove.backend.dto.ml.HealthResponseDTO;
import org.npeonelove.backend.dto.ml.PromptRequestDTO;
import org.npeonelove.backend.dto.ml.PromptResponseDTO;
import org.npeonelove.backend.dto.scenario.ScenarioListResponseDTO;
import org.npeonelove.backend.dto.scenario.ScenarioPageResponseDTO;
import org.npeonelove.backend.exception.scenario.ScenarioValidationException;
import org.npeonelove.backend.service.ScenarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/scenarios")
@RequiredArgsConstructor
public class ScenarioController {

    private final ScenarioService scenarioService;

    // получение всех сценариев
    @GetMapping
    public ResponseEntity<List<ScenarioListResponseDTO>> getAllScenarios() {
        return ResponseEntity.ok(scenarioService.getAllScenarios());
    }

    // получение всех сценариев по определенному типу
    @GetMapping("/type/{typeId}")
    public ResponseEntity<List<ScenarioListResponseDTO>> getScenariosByTypeId(@PathVariable("typeId") UUID typeId) {
        return ResponseEntity.ok(scenarioService.getScenariosByTypeId(typeId));
    }

    // получить конкретный сценарий по id
    @GetMapping("/{scenarioId}")
    public ResponseEntity<ScenarioPageResponseDTO> getScenarioById(@PathVariable("scenarioId") UUID scenarioId) {
        return ResponseEntity.ok(scenarioService.getScenarioById(scenarioId));
    }

    // получить фидбек по сценарию (неважно с текстом пользователя или нет)
    @PostMapping("/feedback")
    public ResponseEntity<PromptResponseDTO> sendPrompt(@RequestBody @Valid PromptRequestDTO promptRequestDTO,
                                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ScenarioValidationException(validateBindingResult(bindingResult));
        }

        return ResponseEntity.ok(scenarioService.sendPrompt(promptRequestDTO));
    }

    // проверить, что ml сервис жив
    @GetMapping("/health")
    public ResponseEntity<HealthResponseDTO> healthCheck() {
        return ResponseEntity.ok(scenarioService.healthCheck());
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
