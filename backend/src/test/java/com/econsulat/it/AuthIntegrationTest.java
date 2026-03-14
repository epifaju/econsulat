package com.econsulat.it;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("econsulat")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");

        // Flyway : on veut exécuter V0/V1/V2 dans la DB de test
        registry.add("spring.flyway.enabled", () -> "true");

        // Secrets requis par application.properties
        registry.add("JWT_SECRET", () -> "test-jwt-secret-test-jwt-secret-test-jwt-secret");
        registry.add("MAIL_USERNAME", () -> "");
        registry.add("MAIL_PASSWORD", () -> "");
        registry.add("STRIPE_SECRET_KEY", () -> "");
        registry.add("STRIPE_WEBHOOK_SECRET", () -> "");
    }

    @Autowired
    private MockMvc mvc;

    @Test
    void login_with_default_user_works_and_returns_token() throws Exception {
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@econsulat.com\",\"password\":\"user123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.token", containsString(".")));
    }

    @Test
    void protected_endpoint_requires_auth() throws Exception {
        mvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }
}

