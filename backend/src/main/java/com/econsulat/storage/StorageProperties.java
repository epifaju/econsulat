package com.econsulat.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage")
public class StorageProperties {
    /** Type de stockage : local ou s3 (MinIO compatible). */
    private String type = "local";

    /**
     * Racine du stockage (local). En prod, monter un volume persistant ou utiliser S3/MinIO.
     */
    private String root = "uploads";

    private String documentsDir = "documents";
    private String citizensDir = "citizens";
    private String passportsDir = "passports";

    /** S3 / MinIO : endpoint (ex. https://minio.example.com ou http://minio:9000). */
    private String s3Endpoint;
    /** S3 / MinIO : nom du bucket. */
    private String s3Bucket;
    /** S3 / MinIO : access key. */
    private String s3AccessKey;
    /** S3 / MinIO : secret key. */
    private String s3SecretKey;
    /** S3 : région (ex. eu-west-1). MinIO : peut rester vide ou us-east-1. */
    private String s3Region = "us-east-1";
    /** MinIO exige path-style access (true). AWS S3 peut utiliser virtual-hosted (false). */
    private boolean s3PathStyleAccess = true;

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getDocumentsDir() {
        return documentsDir;
    }

    public void setDocumentsDir(String documentsDir) {
        this.documentsDir = documentsDir;
    }

    public String getCitizensDir() {
        return citizensDir;
    }

    public void setCitizensDir(String citizensDir) {
        this.citizensDir = citizensDir;
    }

    public String getPassportsDir() {
        return passportsDir;
    }

    public void setPassportsDir(String passportsDir) {
        this.passportsDir = passportsDir;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getS3Endpoint() {
        return s3Endpoint;
    }

    public void setS3Endpoint(String s3Endpoint) {
        this.s3Endpoint = s3Endpoint;
    }

    public String getS3Bucket() {
        return s3Bucket;
    }

    public void setS3Bucket(String s3Bucket) {
        this.s3Bucket = s3Bucket;
    }

    public String getS3AccessKey() {
        return s3AccessKey;
    }

    public void setS3AccessKey(String s3AccessKey) {
        this.s3AccessKey = s3AccessKey;
    }

    public String getS3SecretKey() {
        return s3SecretKey;
    }

    public void setS3SecretKey(String s3SecretKey) {
        this.s3SecretKey = s3SecretKey;
    }

    public String getS3Region() {
        return s3Region;
    }

    public void setS3Region(String s3Region) {
        this.s3Region = s3Region;
    }

    public boolean isS3PathStyleAccess() {
        return s3PathStyleAccess;
    }

    public void setS3PathStyleAccess(boolean s3PathStyleAccess) {
        this.s3PathStyleAccess = s3PathStyleAccess;
    }
}

