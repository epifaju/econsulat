package com.econsulat.controller;

import com.econsulat.dto.ContactRequest;
import com.econsulat.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@Slf4j
public class ContactController {

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<Map<String, String>> submit(@Valid @RequestBody ContactRequest request) {
        String subject = request.getSubject() != null && !request.getSubject().isBlank()
                ? request.getSubject().trim()
                : "";
        emailService.sendContactMessage(
                request.getName().trim(),
                request.getEmail().trim(),
                subject,
                request.getMessage().trim()
        );
        return ResponseEntity.ok(Map.of("message", "Votre message a bien été envoyé."));
    }
}
