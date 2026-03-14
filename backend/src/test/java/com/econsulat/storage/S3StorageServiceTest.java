package com.econsulat.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("S3StorageService")
class S3StorageServiceTest {

    @Mock
    private S3Client s3Client;

    private StorageProperties props;
    private S3StorageService service;

    @BeforeEach
    void setUp() {
        props = new StorageProperties();
        props.setS3Endpoint("http://localhost:9000");
        props.setS3Bucket("test-bucket");
        props.setDocumentsDir("documents");
        props.setCitizensDir("citizens");
        props.setPassportsDir("passports");
        service = new S3StorageService(props);
        ReflectionTestUtils.setField(service, "s3Client", s3Client);
    }

    @Nested
    @DisplayName("init")
    class Init {

        @Test
        void lance_IllegalStateException_quand_endpoint_null() {
            StorageProperties p = new StorageProperties();
            p.setS3Endpoint(null);
            p.setS3Bucket("b");
            S3StorageService s = new S3StorageService(p);

            assertThatThrownBy(s::init)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("s3-endpoint");
        }

        @Test
        void lance_IllegalStateException_quand_endpoint_vide() {
            StorageProperties p = new StorageProperties();
            p.setS3Endpoint("   ");
            p.setS3Bucket("b");
            S3StorageService s = new S3StorageService(p);

            assertThatThrownBy(s::init)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("s3-endpoint");
        }

        @Test
        void lance_IllegalStateException_quand_bucket_null() {
            StorageProperties p = new StorageProperties();
            p.setS3Endpoint("http://localhost:9000");
            p.setS3Bucket(null);
            S3StorageService s = new S3StorageService(p);

            assertThatThrownBy(s::init)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("s3-bucket");
        }

        @Test
        void lance_IllegalStateException_quand_bucket_vide() {
            StorageProperties p = new StorageProperties();
            p.setS3Endpoint("http://localhost:9000");
            p.setS3Bucket("");
            S3StorageService s = new S3StorageService(p);

            assertThatThrownBy(s::init)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("s3-bucket");
        }
    }

    @Nested
    @DisplayName("destroy")
    class Destroy {

        @Test
        void ferme_le_client_S3() {
            service.destroy();
            verify(s3Client).close();
        }
    }

    @Nested
    @DisplayName("méthodes Path (non supportées)")
    class PathMethods {

        @Test
        void root_lance_UnsupportedOperationException() {
            assertThatThrownBy(() -> service.root())
                    .isInstanceOf(UnsupportedOperationException.class)
                    .hasMessageContaining("pas de Path local");
        }

        @Test
        void documentsDir_lance_UnsupportedOperationException() {
            assertThatThrownBy(() -> service.documentsDir())
                    .isInstanceOf(UnsupportedOperationException.class)
                    .hasMessageContaining("pas de Path local");
        }

        @Test
        void citizensDir_et_passportsDir_lancent_UnsupportedOperationException() {
            assertThatThrownBy(() -> service.citizensDir()).isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> service.passportsDir()).isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        void resolveInDocuments_lance_UnsupportedOperationException() {
            assertThatThrownBy(() -> service.resolveInDocuments("f.pdf"))
                    .isInstanceOf(UnsupportedOperationException.class)
                    .hasMessageContaining("readDocument");
        }

        @Test
        void resolveInCitizens_et_resolveInPassports_lancent_UnsupportedOperationException() {
            assertThatThrownBy(() -> service.resolveInCitizens("f")).isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> service.resolveInPassports("f")).isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("documents (read, write, exists, resource, storedPath)")
    class Documents {

        @Test
        void readDocument_retourne_contenu_quand_objet_existe() throws Exception {
            byte[] data = "hello".getBytes();
            ResponseInputStream<GetObjectResponse> stream = new ResponseInputStream<>(
                    GetObjectResponse.builder().build(),
                    new ByteArrayInputStream(data));
            when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(stream);

            byte[] result = service.readDocument("doc.pdf");

            assertThat(result).isEqualTo(data);
        }

        @Test
        void readDocument_lance_IOException_quand_cle_inexistante() {
            when(s3Client.getObject(any(GetObjectRequest.class)))
                    .thenThrow(NoSuchKeyException.builder().build());

            assertThatThrownBy(() -> service.readDocument("missing.pdf"))
                    .isInstanceOf(IOException.class)
                    .hasMessageContaining("Document non trouvé");
        }

        @Test
        void writeDocument_appelle_putObject() throws IOException {
            byte[] content = "content".getBytes();
            service.writeDocument("f.pdf", content);

            ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);
            verify(s3Client).putObject(any(PutObjectRequest.class), bodyCaptor.capture());
            assertThat(bodyCaptor.getValue()).isNotNull();
        }

        @Test
        void getDocumentResource_retourne_ressource_avec_contenu() throws Exception {
            byte[] data = "resource data".getBytes();
            when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(
                    new ResponseInputStream<>(GetObjectResponse.builder().build(), new ByteArrayInputStream(data)));

            Resource resource = service.getDocumentResource("r.pdf");

            assertThat(resource).isNotNull();
            assertThat(resource.getInputStream().readAllBytes()).isEqualTo(data);
        }

        @Test
        void getStoredDocumentPath_retourne_uri_s3() {
            String path = service.getStoredDocumentPath("file.pdf");
            assertThat(path).startsWith("s3://").contains("test-bucket").contains("documents").contains("file.pdf");
        }

        @Test
        void documentExists_retourne_true_quand_headObject_reussit() {
            when(s3Client.headObject(any(HeadObjectRequest.class)))
                    .thenReturn(HeadObjectResponse.builder().build());

            assertThat(service.documentExists("doc.pdf")).isTrue();
        }

        @Test
        void documentExists_retourne_false_quand_NoSuchKey() {
            when(s3Client.headObject(any(HeadObjectRequest.class)))
                    .thenThrow(NoSuchKeyException.builder().build());

            assertThat(service.documentExists("missing.pdf")).isFalse();
        }
    }

    @Nested
    @DisplayName("citoyens (read, write, exists)")
    class Citizens {

        @Test
        void readCitizenFile_retourne_contenu() throws Exception {
            byte[] data = "citizen".getBytes();
            when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(
                    new ResponseInputStream<>(GetObjectResponse.builder().build(), new ByteArrayInputStream(data)));

            assertThat(service.readCitizenFile("c.pdf")).isEqualTo(data);
        }

        @Test
        void readCitizenFile_lance_IOException_quand_cle_inexistante() {
            when(s3Client.getObject(any(GetObjectRequest.class)))
                    .thenThrow(NoSuchKeyException.builder().build());

            assertThatThrownBy(() -> service.readCitizenFile("x.pdf"))
                    .isInstanceOf(IOException.class)
                    .hasMessageContaining("Fichier citoyen non trouvé");
        }

        @Test
        void writeCitizenFile_appelle_putObject() throws IOException {
            service.writeCitizenFile("c.pdf", "data".getBytes());
            ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);
            verify(s3Client).putObject(any(PutObjectRequest.class), bodyCaptor.capture());
            assertThat(bodyCaptor.getValue()).isNotNull();
        }

        @Test
        void citizenFileExists_retourne_true_ou_false_selon_headObject() {
            when(s3Client.headObject(any(HeadObjectRequest.class)))
                    .thenReturn(HeadObjectResponse.builder().build());
            assertThat(service.citizenFileExists("c.pdf")).isTrue();

            when(s3Client.headObject(any(HeadObjectRequest.class)))
                    .thenThrow(NoSuchKeyException.builder().build());
            assertThat(service.citizenFileExists("missing.pdf")).isFalse();
        }
    }

    @Nested
    @DisplayName("passeports (read, write, delete, exists)")
    class Passports {

        @Test
        void readPassport_retourne_contenu() throws Exception {
            byte[] data = "passport".getBytes();
            when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(
                    new ResponseInputStream<>(GetObjectResponse.builder().build(), new ByteArrayInputStream(data)));

            assertThat(service.readPassport("p.pdf")).isEqualTo(data);
        }

        @Test
        void readPassport_lance_IOException_quand_cle_inexistante() {
            when(s3Client.getObject(any(GetObjectRequest.class)))
                    .thenThrow(NoSuchKeyException.builder().build());

            assertThatThrownBy(() -> service.readPassport("x.pdf"))
                    .isInstanceOf(IOException.class)
                    .hasMessageContaining("Passeport non trouvé");
        }

        @Test
        void writePassport_appelle_putObject() throws IOException {
            service.writePassport("p.pdf", "data".getBytes());
            ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);
            verify(s3Client).putObject(any(PutObjectRequest.class), bodyCaptor.capture());
            assertThat(bodyCaptor.getValue()).isNotNull();
        }

        @Test
        void deletePassport_appelle_deleteObject() throws IOException {
            service.deletePassport("p.pdf");
            verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
        }

        @Test
        void passportExists_retourne_true_ou_false_selon_headObject() {
            when(s3Client.headObject(any(HeadObjectRequest.class)))
                    .thenReturn(HeadObjectResponse.builder().build());
            assertThat(service.passportExists("p.pdf")).isTrue();

            when(s3Client.headObject(any(HeadObjectRequest.class)))
                    .thenThrow(NoSuchKeyException.builder().build());
            assertThat(service.passportExists("missing.pdf")).isFalse();
        }
    }

    @Nested
    @DisplayName("openDocumentOutputStream / openPassportOutputStream")
    class OutputStreams {

        @Test
        void openDocumentOutputStream_ecrit_vers_S3_au_close() throws IOException {
            OutputStream out = service.openDocumentOutputStream("stream.pdf");
            out.write("streamed".getBytes());
            out.close();

            ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);
            verify(s3Client).putObject(any(PutObjectRequest.class), bodyCaptor.capture());
            assertThat(bodyCaptor.getValue()).isNotNull();
        }

        @Test
        void openPassportOutputStream_ecrit_vers_S3_au_close() throws IOException {
            OutputStream out = service.openPassportOutputStream("pass.pdf");
            out.write(65);
            out.write(new byte[]{66, 67}, 0, 2);
            out.close();

            ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);
            verify(s3Client).putObject(any(PutObjectRequest.class), bodyCaptor.capture());
            assertThat(bodyCaptor.getValue()).isNotNull();
        }

        @Test
        void close_sans_ecriture_n_envoie_pas_putObject() throws IOException {
            OutputStream out = service.openDocumentOutputStream("empty.pdf");
            out.close();
            verifyNoInteractions(s3Client);
        }
    }

    @Nested
    @DisplayName("sanitizeKey (via stored path)")
    class SanitizeKey {

        @Test
        void getStoredDocumentPath_sanitize_supprime_traversal_et_slash_initial() {
            String path = service.getStoredDocumentPath("../../etc/passwd");
            assertThat(path).doesNotContain("..").contains("etc").contains("passwd");
        }
    }
}
