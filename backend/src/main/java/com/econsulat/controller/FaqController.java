package com.econsulat.controller;

import com.econsulat.dto.FaqResponseDTO;
import com.econsulat.service.FaqService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/faq")
@CrossOrigin(origins = "*")
public class FaqController {

    private final FaqService faqService;

    public FaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    @GetMapping
    public ResponseEntity<FaqResponseDTO> getFaq() {
        FaqResponseDTO faq = faqService.getFaq();
        return ResponseEntity.ok(faq);
    }
}
