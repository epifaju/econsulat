package com.econsulat.config;

import com.econsulat.storage.LocalStorageService;
import com.econsulat.storage.S3StorageService;
import com.econsulat.storage.StorageProperties;
import com.econsulat.storage.StorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class StorageConfig {

    @Bean
    @ConditionalOnProperty(name = "app.storage.type", havingValue = "s3")
    public StorageService s3StorageService(StorageProperties storageProperties) {
        return new S3StorageService(storageProperties);
    }

    @Bean
    @ConditionalOnProperty(name = "app.storage.type", havingValue = "local", matchIfMissing = true)
    public StorageService localStorageService(StorageProperties storageProperties) {
        return new LocalStorageService(storageProperties);
    }
}
