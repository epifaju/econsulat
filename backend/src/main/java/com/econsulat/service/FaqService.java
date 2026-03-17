package com.econsulat.service;

import com.econsulat.dto.FaqCategoryDTO;
import com.econsulat.dto.FaqEntryDTO;
import com.econsulat.dto.FaqResponseDTO;
import com.econsulat.model.FaqCategory;
import com.econsulat.model.FaqEntry;
import com.econsulat.repository.FaqCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FaqService {

    private final FaqCategoryRepository faqCategoryRepository;

    public FaqService(FaqCategoryRepository faqCategoryRepository) {
        this.faqCategoryRepository = faqCategoryRepository;
    }

    @Transactional(readOnly = true)
    public FaqResponseDTO getFaq() {
        List<FaqCategory> categories = faqCategoryRepository.findAllWithEntriesOrdered();
        List<FaqCategoryDTO> dtos = categories.stream()
                .map(this::toCategoryDTO)
                .collect(Collectors.toList());
        FaqResponseDTO response = new FaqResponseDTO();
        response.setCategories(dtos);
        return response;
    }

    private FaqCategoryDTO toCategoryDTO(FaqCategory c) {
        FaqCategoryDTO dto = new FaqCategoryDTO();
        dto.setId(c.getCode());
        dto.setOrder(c.getDisplayOrder());
        dto.setEntries(c.getEntries().stream()
                .map(e -> toEntryDTO(e, c.getCode()))
                .collect(Collectors.toList()));
        return dto;
    }

    private FaqEntryDTO toEntryDTO(FaqEntry e, String categoryId) {
        FaqEntryDTO dto = new FaqEntryDTO();
        dto.setId(String.valueOf(e.getId()));
        dto.setCategoryId(categoryId);
        dto.setQuestionKey(e.getQuestionKey());
        dto.setAnswerKey(e.getAnswerKey());
        dto.setOrder(e.getDisplayOrder());
        dto.setKeywords(e.getKeywords());
        return dto;
    }
}
