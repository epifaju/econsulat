package com.econsulat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaqEntryDTO {
    private String id;
    private String categoryId;
    private String questionKey;
    private String answerKey;
    private Integer order;
    private String keywords;
}
