package com.econsulat.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Path;

/**
 * Implémentation S3/MinIO du stockage.
 * Compatible MinIO via endpoint personnalisé et path-style access.
 */
public class S3StorageService implements StorageService {

    private static final Logger log = LoggerFactory.getLogger(S3StorageService.class);

    private final StorageProperties props;
    private S3Client s3Client;

    public S3StorageService(StorageProperties props) {
        this.props = props;
    }

    @PostConstruct
    public void init() {
        String endpoint = props.getS3Endpoint();
        String bucket = props.getS3Bucket();
        if (endpoint == null || endpoint.isBlank() || bucket == null || bucket.isBlank()) {
            throw new IllegalStateException("app.storage.s3-endpoint et app.storage.s3-bucket sont requis pour le stockage S3");
        }
        S3Configuration config = S3Configuration.builder()
                .pathStyleAccessEnabled(props.isS3PathStyleAccess())
                .build();
        var builder = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(props.getS3Region() != null && !props.getS3Region().isBlank() ? props.getS3Region() : "us-east-1"))
                .serviceConfiguration(config);
        if (props.getS3AccessKey() != null && !props.getS3AccessKey().isBlank()
                && props.getS3SecretKey() != null && !props.getS3SecretKey().isBlank()) {
            builder.credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(props.getS3AccessKey(), props.getS3SecretKey())));
        }
        this.s3Client = builder.build();
        ensureBucketExists();
        log.info("Stockage S3/MinIO initialisé: bucket={}", bucket);
    }

    @PreDestroy
    public void destroy() {
        if (s3Client != null) {
            s3Client.close();
        }
    }

    private void ensureBucketExists() {
        String bucket = props.getS3Bucket();
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
        } catch (NoSuchBucketException e) {
            try {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
                log.info("Bucket S3 créé: {}", bucket);
            } catch (S3Exception ex) {
                log.warn("Impossible de créer le bucket {} (il peut déjà exister): {}", bucket, ex.getMessage());
            }
        }
    }

    private String keyDocuments(String fileName) {
        return props.getDocumentsDir() + "/" + sanitizeKey(fileName);
    }

    private String keyCitizens(String fileName) {
        return props.getCitizensDir() + "/" + sanitizeKey(fileName);
    }

    private String keyPassports(String fileName) {
        return props.getPassportsDir() + "/" + sanitizeKey(fileName);
    }

    private static String sanitizeKey(String fileName) {
        if (fileName == null) return "";
        return fileName.replace("..", "").replaceAll("^/", "");
    }

    private String bucket() {
        return props.getS3Bucket();
    }

    @Override
    public Path root() {
        throw new UnsupportedOperationException("Stockage S3 : pas de Path local");
    }

    @Override
    public Path documentsDir() {
        throw new UnsupportedOperationException("Stockage S3 : pas de Path local");
    }

    @Override
    public Path citizensDir() {
        throw new UnsupportedOperationException("Stockage S3 : pas de Path local");
    }

    @Override
    public Path passportsDir() {
        throw new UnsupportedOperationException("Stockage S3 : pas de Path local");
    }

    @Override
    public Path resolveInDocuments(String fileName) {
        throw new UnsupportedOperationException("Stockage S3 : utiliser readDocument/writeDocument");
    }

    @Override
    public Path resolveInCitizens(String fileName) {
        throw new UnsupportedOperationException("Stockage S3 : utiliser readCitizenFile/writeCitizenFile");
    }

    @Override
    public Path resolveInPassports(String fileName) {
        throw new UnsupportedOperationException("Stockage S3 : utiliser readPassport/writePassport");
    }

    @Override
    public byte[] readDocument(String fileName) throws IOException {
        String key = keyDocuments(fileName);
        try (ResponseInputStream<GetObjectResponse> stream = s3Client.getObject(GetObjectRequest.builder().bucket(bucket()).key(key).build())) {
            return stream.readAllBytes();
        } catch (NoSuchKeyException e) {
            throw new IOException("Document non trouvé: " + fileName, e);
        }
    }

    @Override
    public void writeDocument(String fileName, byte[] content) throws IOException {
        String key = keyDocuments(fileName);
        s3Client.putObject(
                PutObjectRequest.builder().bucket(bucket()).key(key).contentLength((long) content.length).build(),
                RequestBody.fromBytes(content));
    }

    @Override
    public Resource getDocumentResource(String fileName) throws IOException {
        byte[] bytes = readDocument(fileName);
        return new ByteArrayResource(bytes);
    }

    @Override
    public String getStoredDocumentPath(String fileName) {
        return "s3://" + bucket() + "/" + keyDocuments(fileName);
    }

    @Override
    public boolean documentExists(String fileName) {
        return objectExists(bucket(), keyDocuments(fileName));
    }

    @Override
    public byte[] readCitizenFile(String fileName) throws IOException {
        String key = keyCitizens(fileName);
        try (ResponseInputStream<GetObjectResponse> stream = s3Client.getObject(GetObjectRequest.builder().bucket(bucket()).key(key).build())) {
            return stream.readAllBytes();
        } catch (NoSuchKeyException e) {
            throw new IOException("Fichier citoyen non trouvé: " + fileName, e);
        }
    }

    @Override
    public void writeCitizenFile(String fileName, byte[] content) throws IOException {
        String key = keyCitizens(fileName);
        s3Client.putObject(
                PutObjectRequest.builder().bucket(bucket()).key(key).contentLength((long) content.length).build(),
                RequestBody.fromBytes(content));
    }

    @Override
    public boolean citizenFileExists(String fileName) {
        return objectExists(bucket(), keyCitizens(fileName));
    }

    @Override
    public byte[] readPassport(String fileName) throws IOException {
        String key = keyPassports(fileName);
        try (ResponseInputStream<GetObjectResponse> stream = s3Client.getObject(GetObjectRequest.builder().bucket(bucket()).key(key).build())) {
            return stream.readAllBytes();
        } catch (NoSuchKeyException e) {
            throw new IOException("Passeport non trouvé: " + fileName, e);
        }
    }

    @Override
    public void writePassport(String fileName, byte[] content) throws IOException {
        String key = keyPassports(fileName);
        s3Client.putObject(
                PutObjectRequest.builder().bucket(bucket()).key(key).contentLength((long) content.length).build(),
                RequestBody.fromBytes(content));
    }

    @Override
    public void deletePassport(String fileName) throws IOException {
        String key = keyPassports(fileName);
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket()).key(key).build());
        } catch (S3Exception e) {
            log.warn("Suppression S3 {} : {}", key, e.getMessage());
        }
    }

    @Override
    public boolean passportExists(String fileName) {
        return objectExists(bucket(), keyPassports(fileName));
    }

    private boolean objectExists(String bucket, String key) {
        try {
            s3Client.headObject(HeadObjectRequest.builder().bucket(bucket).key(key).build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            if (e.awsErrorDetails() != null && e.awsErrorDetails().sdkHttpResponse() != null
                    && e.awsErrorDetails().sdkHttpResponse().statusCode() == 404) {
                return false;
            }
            throw e;
        }
    }

    @Override
    public OutputStream openDocumentOutputStream(String fileName) throws IOException {
        return new S3UploadOutputStream(s3Client, bucket(), keyDocuments(fileName));
    }

    @Override
    public OutputStream openPassportOutputStream(String fileName) throws IOException {
        return new S3UploadOutputStream(s3Client, bucket(), keyPassports(fileName));
    }

    /**
     * OutputStream qui bufferise en mémoire et envoie vers S3 à la fermeture.
     */
    private static final class S3UploadOutputStream extends OutputStream {
        private final S3Client s3;
        private final String bucket;
        private final String key;
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        S3UploadOutputStream(S3Client s3, String bucket, String key) {
            this.s3 = s3;
            this.bucket = bucket;
            this.key = key;
        }

        @Override
        public void write(int b) {
            buffer.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) {
            buffer.write(b, off, len);
        }

        @Override
        public void close() throws IOException {
            if (buffer.size() == 0) return;
            byte[] content = buffer.toByteArray();
            s3.putObject(
                    PutObjectRequest.builder().bucket(bucket).key(key).contentLength((long) content.length).build(),
                    RequestBody.fromBytes(content));
        }
    }
}
