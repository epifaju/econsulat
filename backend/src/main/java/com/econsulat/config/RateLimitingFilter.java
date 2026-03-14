package com.econsulat.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filtre de rate limiting par IP sur les endpoints sensibles (auth, webhook).
 */
@Component
@Order(1)
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);

    private static final String PATH_LOGIN = "/api/auth/login";
    private static final String PATH_REGISTER = "/api/auth/register";
    private static final String PATH_WEBHOOK = "/api/payment/webhook";

    @Value("${app.rate-limit.auth.requests-per-minute:10}")
    private int authRequestsPerMinute;

    @Value("${app.rate-limit.webhook.requests-per-minute:30}")
    private int webhookRequestsPerMinute;

    private final Map<String, Bucket> authBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> webhookBuckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String clientId = getClientId(request);

        if (PATH_LOGIN.equals(path) || PATH_REGISTER.equals(path)) {
            Bucket bucket = authBuckets.computeIfAbsent(clientId, k -> buildBucket(authRequestsPerMinute));
            if (!bucket.tryConsume(1)) {
                log.warn("Rate limit dépassé (auth) pour IP: {}", clientId);
                response.setStatus(429); // Too Many Requests
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"Trop de requêtes. Réessayez dans une minute.\"}");
                return;
            }
        } else if (PATH_WEBHOOK.equals(path)) {
            Bucket bucket = webhookBuckets.computeIfAbsent(clientId, k -> buildBucket(webhookRequestsPerMinute));
            if (!bucket.tryConsume(1)) {
                log.warn("Rate limit dépassé (webhook) pour IP: {}", clientId);
                response.setStatus(429); // Too Many Requests
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"Trop de requêtes.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private Bucket buildBucket(int capacity) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(capacity, Refill.greedy(capacity, Duration.ofMinutes(1))))
                .build();
    }

    private String getClientId(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr() != null ? request.getRemoteAddr() : "unknown";
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !PATH_LOGIN.equals(path) && !PATH_REGISTER.equals(path) && !PATH_WEBHOOK.equals(path);
    }
}
