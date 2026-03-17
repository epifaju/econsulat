package com.econsulat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaqCategoryDTO {
    private String id;
    private Integer order;
    private List<FaqEntryDTO> entries = new ArrayList<>();
}
