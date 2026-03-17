package com.econsulat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssistantChoiceDTO {
    private String choiceKey;
    private String nextStepId;
    private AssistantResultDTO result;
}
