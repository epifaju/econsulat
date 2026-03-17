package com.econsulat.service;

import com.econsulat.dto.AssistantChoiceDTO;
import com.econsulat.dto.AssistantResponseDTO;
import com.econsulat.dto.AssistantResultDTO;
import com.econsulat.dto.AssistantStepDTO;
import com.econsulat.model.AssistantChoice;
import com.econsulat.model.AssistantStep;
import com.econsulat.repository.AssistantStepRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssistantService {

    private final AssistantStepRepository assistantStepRepository;

    public AssistantService(AssistantStepRepository assistantStepRepository) {
        this.assistantStepRepository = assistantStepRepository;
    }

    @Transactional(readOnly = true)
    public AssistantResponseDTO getSteps() {
        List<AssistantStep> steps = assistantStepRepository.findAllWithChoicesOrdered();
        List<AssistantStepDTO> dtos = steps.stream()
                .map(this::toStepDTO)
                .collect(Collectors.toList());
        AssistantResponseDTO response = new AssistantResponseDTO();
        response.setSteps(dtos);
        return response;
    }

    private AssistantStepDTO toStepDTO(AssistantStep s) {
        AssistantStepDTO dto = new AssistantStepDTO();
        dto.setId(s.getStepId());
        dto.setQuestionKey(s.getQuestionKey());
        dto.setChoices(s.getChoices().stream()
                .map(this::toChoiceDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private AssistantChoiceDTO toChoiceDTO(AssistantChoice c) {
        AssistantChoiceDTO dto = new AssistantChoiceDTO();
        dto.setChoiceKey(c.getChoiceKey());
        if (c.getNextStep() != null) {
            dto.setNextStepId(c.getNextStep().getStepId());
            dto.setResult(null);
        } else {
            dto.setNextStepId(null);
            AssistantResultDTO result = new AssistantResultDTO();
            result.setDocumentTypeId(c.getResultDocumentType() != null ? c.getResultDocumentType().getId() : null);
            result.setSummaryKey(c.getResultSummaryKey());
            dto.setResult(result);
        }
        return dto;
    }
}
