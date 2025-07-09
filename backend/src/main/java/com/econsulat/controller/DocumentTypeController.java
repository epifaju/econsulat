package com.econsulat.controller;

import com.econsulat.dto.DocumentTypeRequest;
import com.econsulat.model.DocumentType;
import com.econsulat.service.DocumentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/document-types")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:5173")
public class DocumentTypeController {

    @Autowired
    private DocumentTypeService documentTypeService;

    @GetMapping
    public ResponseEntity<List<DocumentType>> getAllDocumentTypes() {
        List<DocumentType> documentTypes = documentTypeService.getAllDocumentTypes();
        return ResponseEntity.ok(documentTypes);
    }

    @GetMapping("/search")
    public ResponseEntity<List<DocumentType>> searchDocumentTypes(@RequestParam String q) {
        List<DocumentType> documentTypes = documentTypeService.searchDocumentTypes(q);
        return ResponseEntity.ok(documentTypes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentType> getDocumentTypeById(@PathVariable Long id) {
        DocumentType documentType = documentTypeService.getDocumentTypeById(id);
        return ResponseEntity.ok(documentType);
    }

    @PostMapping
    public ResponseEntity<DocumentType> createDocumentType(@RequestBody DocumentTypeRequest request) {
        DocumentType documentType = documentTypeService.createDocumentType(request);
        return ResponseEntity.ok(documentType);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentType> updateDocumentType(
            @PathVariable Long id,
            @RequestBody DocumentTypeRequest request) {

        DocumentType documentType = documentTypeService.updateDocumentType(id, request);
        return ResponseEntity.ok(documentType);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocumentType(@PathVariable Long id) {
        documentTypeService.deleteDocumentType(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<DocumentType> activateDocumentType(@PathVariable Long id) {
        documentTypeService.activateDocumentType(id);
        return ResponseEntity.ok().build();
    }
}