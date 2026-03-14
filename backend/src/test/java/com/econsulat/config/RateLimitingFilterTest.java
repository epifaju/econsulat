package com.econsulat.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("RateLimitingFilter")
class RateLimitingFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private RateLimitingFilter filter;
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws IOException {
        filter = new RateLimitingFilter();
        ReflectionTestUtils.setField(filter, "authRequestsPerMinute", 2);
        ReflectionTestUtils.setField(filter, "webhookRequestsPerMinute", 5);
        responseWriter = new StringWriter();
        lenient().when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Nested
    @DisplayName("shouldNotFilter")
    class ShouldNotFilter {

        @Test
        void retourne_true_pour_chemin_hors_auth_et_webhook() {
            when(request.getRequestURI()).thenReturn("/api/demandes");

            boolean result = filter.shouldNotFilter(request);

            assertThat(result).isTrue();
        }

        @Test
        void retourne_false_pour_login() {
            when(request.getRequestURI()).thenReturn("/api/auth/login");

            boolean result = filter.shouldNotFilter(request);

            assertThat(result).isFalse();
        }

        @Test
        void retourne_false_pour_register() {
            when(request.getRequestURI()).thenReturn("/api/auth/register");

            boolean result = filter.shouldNotFilter(request);

            assertThat(result).isFalse();
        }

        @Test
        void retourne_false_pour_webhook() {
            when(request.getRequestURI()).thenReturn("/api/payment/webhook");

            boolean result = filter.shouldNotFilter(request);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("doFilterInternal")
    class DoFilterInternal {

        @Test
        void laisse_passer_et_appelle_chain_pour_chemin_non_limite() throws ServletException, IOException {
            when(request.getRequestURI()).thenReturn("/api/demandes");
            when(request.getRemoteAddr()).thenReturn("192.168.1.1");

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            verify(response, never()).setStatus(anyInt());
        }

        @Test
        void laisse_passer_premiere_requete_auth_puis_retourne_429_quand_limite_atteinte() throws ServletException, IOException {
            ReflectionTestUtils.setField(filter, "authRequestsPerMinute", 1);
            when(request.getRequestURI()).thenReturn("/api/auth/login");
            when(request.getRemoteAddr()).thenReturn("10.0.0.1");

            filter.doFilterInternal(request, response, filterChain);
            verify(filterChain).doFilter(request, response);

            when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
            filter.doFilterInternal(request, response, filterChain);

            verify(response, atLeastOnce()).setStatus(429);
            assertThat(responseWriter.toString()).contains("Trop de requêtes");
        }

        @Test
        void webhook_retourne_429_quand_limite_atteinte() throws ServletException, IOException {
            ReflectionTestUtils.setField(filter, "webhookRequestsPerMinute", 1);
            when(request.getRequestURI()).thenReturn("/api/payment/webhook");
            when(request.getRemoteAddr()).thenReturn("10.0.0.2");

            filter.doFilterInternal(request, response, filterChain);
            when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
            filter.doFilterInternal(request, response, filterChain);

            verify(response).setStatus(429);
            assertThat(responseWriter.toString()).contains("Trop de requêtes");
        }

        @Test
        void utilise_X_Forwarded_For_comme_clientId_quand_present() throws ServletException, IOException {
            when(request.getRequestURI()).thenReturn("/api/auth/login");
            when(request.getHeader("X-Forwarded-For")).thenReturn(" 203.0.113.1 , 70.41.3.18 ");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");

            ReflectionTestUtils.setField(filter, "authRequestsPerMinute", 1);
            filter.doFilterInternal(request, response, filterChain);
            verify(filterChain).doFilter(request, response);

            when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
            filter.doFilterInternal(request, response, filterChain);
            verify(response).setStatus(429);
        }
    }
}
