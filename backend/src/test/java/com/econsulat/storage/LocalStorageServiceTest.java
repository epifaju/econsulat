package com.econsulat.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("LocalStorageService")
class LocalStorageServiceTest {

    @TempDir
    Path tempDir;

    private StorageProperties props;
    private LocalStorageService service;

    @BeforeEach
    void setUp() {
        props = new StorageProperties();
        props.setRoot(tempDir.toString());
        props.setDocumentsDir("documents");
        props.setCitizensDir("citizens");
        props.setPassportsDir("passports");
        service = new LocalStorageService(props);
    }

    @Nested
    @DisplayName("chemins (root, dirs, resolve)")
    class Paths {

        @Test
        void root_retourne_le_chemin_absolu_configure() {
            Path root = service.root();
            assertThat(root).exists();
            assertThat(root.toAbsolutePath().normalize().toString()).isEqualTo(Path.of(props.getRoot()).toAbsolutePath().normalize().toString());
        }

        @Test
        void documentsDir_cree_et_retourne_le_dossier_documents() {
            Path dir = service.documentsDir();
            assertThat(dir).exists();
            assertThat(dir.getFileName().toString()).isEqualTo("documents");
        }

        @Test
        void citizensDir_et_passportsDir_creent_les_dossiers() {
            assertThat(service.citizensDir()).exists();
            assertThat(service.citizensDir().getFileName().toString()).isEqualTo("citizens");
            assertThat(service.passportsDir()).exists();
            assertThat(service.passportsDir().getFileName().toString()).isEqualTo("passports");
        }

        @Test
        void resolveInDocuments_retourne_un_chemin_sous_documents() {
            Path p = service.resolveInDocuments("fichier.pdf");
            assertThat(p.getFileName().toString()).isEqualTo("fichier.pdf");
            assertThat(p.getParent()).isEqualTo(service.documentsDir());
        }

        @Test
        void resolve_rejette_les_noms_avec_traversal() {
            assertThatThrownBy(() -> service.resolveInDocuments("..\\evil.pdf"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("invalide");
        }
    }

    @Nested
    @DisplayName("documents (read, write, exists, resource, storedPath)")
    class Documents {

        @Test
        void writeDocument_puis_readDocument_retourne_les_memes_octets() throws Exception {
            String name = "doc.pdf";
            byte[] content = "contenu PDF".getBytes();
            service.writeDocument(name, content);
            assertThat(service.readDocument(name)).isEqualTo(content);
        }

        @Test
        void documentExists_retourne_true_apres_ecriture() throws Exception {
            service.writeDocument("a.docx", new byte[]{1, 2, 3});
            assertThat(service.documentExists("a.docx")).isTrue();
            assertThat(service.documentExists("inexistant.docx")).isFalse();
        }

        @Test
        void getDocumentResource_retourne_une_ressource_lisible() throws Exception {
            byte[] content = "data".getBytes();
            service.writeDocument("f", content);
            Resource resource = service.getDocumentResource("f");
            assertThat(resource.exists()).isTrue();
            try (var is = resource.getInputStream()) {
                assertThat(is).hasBinaryContent(content);
            }
        }

        @Test
        void getDocumentResource_throw_si_fichier_absent() {
            assertThatThrownBy(() -> service.getDocumentResource("absent.pdf"))
                    .isInstanceOf(java.io.IOException.class)
                    .hasMessageContaining("non trouvé");
        }

        @Test
        void getStoredDocumentPath_retourne_chemin_absolu() throws Exception {
            service.writeDocument("x.pdf", new byte[0]);
            String path = service.getStoredDocumentPath("x.pdf");
            assertThat(path).isNotEmpty();
            assertThat(Path.of(path)).exists();
        }

        @Test
        void openDocumentOutputStream_ecrit_dans_le_fichier() throws Exception {
            String name = "out.pdf";
            try (OutputStream out = service.openDocumentOutputStream(name)) {
                out.write("hello".getBytes());
            }
            assertThat(service.readDocument(name)).isEqualTo("hello".getBytes());
        }
    }

    @Nested
    @DisplayName("citizens (read, write, exists)")
    class Citizens {

        @Test
        void writeCitizenFile_puis_readCitizenFile() throws Exception {
            byte[] data = "piece".getBytes();
            service.writeCitizenFile("piece.pdf", data);
            assertThat(service.readCitizenFile("piece.pdf")).isEqualTo(data);
            assertThat(service.citizenFileExists("piece.pdf")).isTrue();
        }
    }

    @Nested
    @DisplayName("passports (read, write, delete, exists, stream)")
    class Passports {

        @Test
        void writePassport_puis_readPassport_et_deletePassport() throws Exception {
            String name = "p.docx";
            byte[] data = "passport".getBytes();
            service.writePassport(name, data);
            assertThat(service.passportExists(name)).isTrue();
            assertThat(service.readPassport(name)).isEqualTo(data);
            service.deletePassport(name);
            assertThat(service.passportExists(name)).isFalse();
        }

        @Test
        void deletePassport_sur_fichier_inexistant_ne_throw_pas() throws Exception {
            service.deletePassport("absent.docx");
        }

        @Test
        void openPassportOutputStream_ecrit_dans_le_fichier() throws Exception {
            String name = "p2.docx";
            try (OutputStream out = service.openPassportOutputStream(name)) {
                out.write("doc".getBytes());
            }
            assertThat(service.readPassport(name)).isEqualTo("doc".getBytes());
        }
    }
}
