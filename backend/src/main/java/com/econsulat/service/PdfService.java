package com.econsulat.service;

import com.econsulat.storage.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PdfService {

    private final StorageService storageService;

    public PdfService(StorageService storageService) {
        this.storageService = storageService;
    }

    public Resource loadFileAsResource(String fileName) throws IOException {
        return storageService.getDocumentResource(fileName);
    }
}