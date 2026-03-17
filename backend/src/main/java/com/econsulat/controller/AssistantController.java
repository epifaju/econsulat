package com.econsulat.controller;

import com.econsulat.dto.AssistantResponseDTO;
import com.econsulat.service.AssistantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assistant")
@CrossOrigin(origins = "*")
public class AssistantController {

    private final AssistantService assistantService;

    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @GetMapping("/steps")
    public ResponseEntity<AssistantResponseDTO> getSteps() {
        AssistantResponseDTO steps = assistantService.getSteps();
        return ResponseEntity.ok(steps);
    }
}
