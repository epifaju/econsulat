package com.econsulat.storage;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * Abstraction du stockage des fichiers (local ou S3/MinIO).
 * Les méthodes Path sont utilisées par le stockage local ; en S3, utiliser les méthodes read/write/exists.
 */
public interface StorageService {

    // ---------- Chemins (local uniquement ; S3 lance UnsupportedOperationException) ----------
    Path root();
    Path documentsDir();
    Path citizensDir();
    Path passportsDir();
    Path resolveInDocuments(String fileName);
    Path resolveInCitizens(String fileName);
    Path resolveInPassports(String fileName);

    // ---------- Documents (lecture/écriture abstraites) ----------
    byte[] readDocument(String fileName) throws IOException;
    void writeDocument(String fileName, byte[] content) throws IOException;
    /** Ressource pour servir un document (téléchargement, etc.). */
    Resource getDocumentResource(String fileName) throws IOException;
    /** Chemin ou clé à enregistrer en base (path local ou clé S3). */
    String getStoredDocumentPath(String fileName);
    boolean documentExists(String fileName);

    // ---------- Fichiers citoyens ----------
    byte[] readCitizenFile(String fileName) throws IOException;
    void writeCitizenFile(String fileName, byte[] content) throws IOException;
    boolean citizenFileExists(String fileName);

    // ---------- Passeports ----------
    byte[] readPassport(String fileName) throws IOException;
    void writePassport(String fileName, byte[] content) throws IOException;
    void deletePassport(String fileName) throws IOException;
    boolean passportExists(String fileName);

    /**
     * Ouvre un flux d'écriture pour un document (génération directe sans tout charger en mémoire).
     * Fermer le flux après écriture. En local : écrit vers le fichier ; en S3 : buffer puis upload au close.
     */
    OutputStream openDocumentOutputStream(String fileName) throws IOException;

    /**
     * Ouvre un flux d'écriture pour un fichier passeport.
     */
    OutputStream openPassportOutputStream(String fileName) throws IOException;
}
