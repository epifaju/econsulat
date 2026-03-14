package com.econsulat.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
public class LocalStorageService implements StorageService {

    private final StorageProperties props;

    @Override
    public Path root() {
        return Paths.get(props.getRoot()).toAbsolutePath().normalize();
    }

    @Override
    public Path documentsDir() {
        return ensureDir(root().resolve(props.getDocumentsDir()).normalize());
    }

    @Override
    public Path citizensDir() {
        return ensureDir(root().resolve(props.getCitizensDir()).normalize());
    }

    @Override
    public Path passportsDir() {
        return ensureDir(root().resolve(props.getPassportsDir()).normalize());
    }

    @Override
    public Path resolveInDocuments(String fileName) {
        return safeResolve(documentsDir(), fileName);
    }

    @Override
    public Path resolveInCitizens(String fileName) {
        return safeResolve(citizensDir(), fileName);
    }

    @Override
    public Path resolveInPassports(String fileName) {
        return safeResolve(passportsDir(), fileName);
    }

    @Override
    public byte[] readDocument(String fileName) throws IOException {
        return Files.readAllBytes(resolveInDocuments(fileName));
    }

    @Override
    public void writeDocument(String fileName, byte[] content) throws IOException {
        Path path = resolveInDocuments(fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, content);
    }

    @Override
    public Resource getDocumentResource(String fileName) throws IOException {
        Path path = resolveInDocuments(fileName);
        Resource resource = new UrlResource(path.toUri());
        if (!resource.exists()) {
            throw new IOException("Document non trouvé: " + fileName);
        }
        return resource;
    }

    @Override
    public String getStoredDocumentPath(String fileName) {
        return resolveInDocuments(fileName).toAbsolutePath().toString();
    }

    @Override
    public boolean documentExists(String fileName) {
        return Files.exists(resolveInDocuments(fileName));
    }

    @Override
    public byte[] readCitizenFile(String fileName) throws IOException {
        return Files.readAllBytes(resolveInCitizens(fileName));
    }

    @Override
    public void writeCitizenFile(String fileName, byte[] content) throws IOException {
        Path path = resolveInCitizens(fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, content);
    }

    @Override
    public boolean citizenFileExists(String fileName) {
        return Files.exists(resolveInCitizens(fileName));
    }

    @Override
    public byte[] readPassport(String fileName) throws IOException {
        return Files.readAllBytes(resolveInPassports(fileName));
    }

    @Override
    public void writePassport(String fileName, byte[] content) throws IOException {
        Path path = resolveInPassports(fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, content);
    }

    @Override
    public void deletePassport(String fileName) throws IOException {
        Path path = resolveInPassports(fileName);
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }

    @Override
    public boolean passportExists(String fileName) {
        return Files.exists(resolveInPassports(fileName));
    }

    @Override
    public OutputStream openDocumentOutputStream(String fileName) throws IOException {
        Path path = resolveInDocuments(fileName);
        Files.createDirectories(path.getParent());
        return Files.newOutputStream(path);
    }

    @Override
    public OutputStream openPassportOutputStream(String fileName) throws IOException {
        Path path = resolveInPassports(fileName);
        Files.createDirectories(path.getParent());
        return Files.newOutputStream(path);
    }

    private static Path ensureDir(Path dir) {
        try {
            Files.createDirectories(dir);
            return dir;
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de créer le dossier: " + dir, e);
        }
    }

    private static Path safeResolve(Path baseDir, String fileName) {
        Path resolved = baseDir.resolve(fileName).normalize();
        if (!resolved.startsWith(baseDir)) {
            throw new IllegalArgumentException("Nom de fichier invalide");
        }
        return resolved;
    }
}
