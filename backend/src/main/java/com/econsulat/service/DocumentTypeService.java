package com.econsulat.service;

import com.econsulat.dto.DocumentTypeRequest;
import com.econsulat.model.DocumentType;
import com.econsulat.repository.DocumentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentTypeService {

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    public List<DocumentType> getAllDocumentTypes() {
        return documentTypeRepository.findByIsActiveTrue();
    }

    public List<DocumentType> searchDocumentTypes(String searchTerm) {
        return documentTypeRepository.findActiveBySearchTerm(searchTerm);
    }

    public DocumentType getDocumentTypeById(Long id) {
        return documentTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Type de document non trouvé"));
    }

    public DocumentType createDocumentType(DocumentTypeRequest request) {
        if (documentTypeRepository.existsByLibelle(request.getLibelle())) {
            throw new RuntimeException("Un type de document avec ce libellé existe déjà");
        }

        DocumentType documentType = new DocumentType();
        documentType.setLibelle(request.getLibelle());
        documentType.setDescription(request.getDescription());
        documentType.setTemplatePath(request.getTemplatePath());
        documentType.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        return documentTypeRepository.save(documentType);
    }

    public DocumentType updateDocumentType(Long id, DocumentTypeRequest request) {
        DocumentType documentType = getDocumentTypeById(id);

        if (!documentType.getLibelle().equals(request.getLibelle()) &&
                documentTypeRepository.existsByLibelleAndIdNot(request.getLibelle(), id)) {
            throw new RuntimeException("Un type de document avec ce libellé existe déjà");
        }

        documentType.setLibelle(request.getLibelle());
        documentType.setDescription(request.getDescription());
        documentType.setTemplatePath(request.getTemplatePath());
        if (request.getIsActive() != null) {
            documentType.setIsActive(request.getIsActive());
        }

        return documentTypeRepository.save(documentType);
    }

    public void deleteDocumentType(Long id) {
        DocumentType documentType = getDocumentTypeById(id);
        documentType.setIsActive(false);
        documentTypeRepository.save(documentType);
    }

    public void activateDocumentType(Long id) {
        DocumentType documentType = getDocumentTypeById(id);
        documentType.setIsActive(true);
        documentTypeRepository.save(documentType);
    }
}