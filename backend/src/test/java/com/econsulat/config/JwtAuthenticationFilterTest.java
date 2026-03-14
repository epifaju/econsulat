package com.econsulat.config;

import com.econsulat.model.User;
import com.econsulat.service.JwtService;
import com.econsulat.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter")
class JwtAuthenticationFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    private JwtAuthenticationFilter filter;
    private User user;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtService, userService);
        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setRole(User.Role.USER);
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("sans token ou token invalide")
    class SansTokenOuInvalide {

        @Test
        void laisse_passer_quand_Authorization_absent() throws Exception {
            when(request.getHeader("Authorization")).thenReturn(null);

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            verify(jwtService, never()).extractUsername(anyString());
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }

        @Test
        void laisse_passer_quand_Authorization_pas_Bearer() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Basic xxx");

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            verify(jwtService, never()).extractUsername(anyString());
        }

        @Test
        void laisse_passer_quand_Bearer_token_vide() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Bearer ");

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            verify(jwtService, never()).extractUsername(anyString());
        }

        @Test
        void laisse_passer_quand_extractUsername_throw() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Bearer invalid.jwt");
            when(jwtService.extractUsername("invalid.jwt")).thenThrow(new RuntimeException("Invalid token"));

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            verify(userService, never()).loadUserByUsername(anyString());
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }

        @Test
        void laisse_passer_quand_token_invalide_isTokenValid_false() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt");
            when(jwtService.extractUsername("valid.jwt")).thenReturn("user@test.com");
            when(userService.loadUserByUsername("user@test.com")).thenReturn(user);
            when(jwtService.isTokenValid("valid.jwt", user)).thenReturn(false);

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }
    }

    @Nested
    @DisplayName("token valide")
    class TokenValide {

        @Test
        void set_authentication_et_laisse_passer_quand_token_valide() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Bearer good.jwt");
            when(jwtService.extractUsername("good.jwt")).thenReturn("user@test.com");
            when(userService.loadUserByUsername("user@test.com")).thenReturn(user);
            when(jwtService.isTokenValid("good.jwt", user)).thenReturn(true);

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
            assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("user@test.com");
            assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities()).isNotEmpty();
        }

        @Test
        void laisse_passer_sans_set_auth_quand_loadUserByUsername_throw() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Bearer good.jwt");
            when(jwtService.extractUsername("good.jwt")).thenReturn("absent@test.com");
            when(userService.loadUserByUsername("absent@test.com"))
                    .thenThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("Not found"));

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }
    }
}
