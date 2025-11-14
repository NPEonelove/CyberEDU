package org.npeonelove.backend.client;

import org.npeonelove.backend.dto.ml.HealthResponseDTO;
import org.npeonelove.backend.dto.ml.PromptRequestDTO;
import org.npeonelove.backend.dto.ml.PromptResponseDTO;
import org.npeonelove.backend.dto.scenario.GenerateScenarioResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "mlFeignClient",
        url = "http://localhost:8000"
)
public interface MlFeignClient {

    @PostMapping("/feedback")
    PromptResponseDTO getFeedback(@RequestBody PromptRequestDTO request);

    @PostMapping("/explain-scenario")
    PromptResponseDTO explainScenario(@RequestBody PromptRequestDTO request);

    @PostMapping("/generate-scenario")
    GenerateScenarioResponseDTO generateScenario(@RequestBody PromptRequestDTO request);

    @GetMapping("/health")
    HealthResponseDTO healthCheck();

}
