package com.econsulat.exception;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.MessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.context.i18n.LocaleContextHolder.setLocale;
import static org.springframework.context.i18n.LocaleContextHolder.resetLocaleContext;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private WebRequest webRequest;

    @InjectMocks
    private GlobalExceptionHandler handler;

    private static final String PATH_DESCRIPTION = "uri=/api/demandes";

    @BeforeEach
    void setUp() {
        when(webRequest.getDescription(false)).thenReturn(PATH_DESCRIPTION);
        lenient().when(messageSource.getMessage(eq("error.validation"), any(), any(Locale.class))).thenReturn("Erreur de validation");
        lenient().when(messageSource.getMessage(eq("error.invalidData"), any(), any(Locale.class))).thenReturn("Données invalides");
        lenient().when(messageSource.getMessage(eq("error.internal"), any(), any(Locale.class))).thenReturn("Erreur interne");
        lenient().when(messageSource.getMessage(eq("error.unexpected"), any(), any(Locale.class))).thenReturn("Erreur inattendue");
    }

    @AfterEach
    void tearDown() {
        resetLocaleContext();
    }

    @Nested
    @DisplayName("handleMethodArgumentNotValid")
    class HandleMethodArgumentNotValid {

        @Test
        void retourne_400_avec_champs_et_type_VALIDATION_ERROR() throws Exception {
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "demandeRequest");
            bindingResult.addError(new FieldError("demandeRequest", "firstName", "must not be blank"));
            bindingResult.addError(new FieldError("demandeRequest", "email", "must be a valid email"));
            MethodParameter param = new MethodParameter(
                    GlobalExceptionHandler.class.getMethod("handleMethodArgumentNotValid",
                            MethodArgumentNotValidException.class, WebRequest.class), 0);
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(param, bindingResult);

            ResponseEntity<Map<String, Object>> response = handler.handleMethodArgumentNotValid(ex, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody()).containsKeys("error", "message", "fields", "timestamp", "path", "type");
            assertThat(response.getBody().get("message")).isEqualTo("Erreurs de validation");
            assertThat(response.getBody().get("type")).isEqualTo("VALIDATION_ERROR");
            assertThat(response.getBody().get("path")).isEqualTo(PATH_DESCRIPTION);
            @SuppressWarnings("unchecked")
            Map<String, String> fields = (Map<String, String>) response.getBody().get("fields");
            assertThat(fields).containsEntry("firstName", "must not be blank").containsEntry("email", "must be a valid email");
            verify(messageSource).getMessage(eq("error.validation"), any(), any(Locale.class));
        }

        @Test
        void utilise_message_Erreur_validation_quand_defaultMessage_null() throws Exception {
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
            bindingResult.addError(new FieldError("target", "field", null));
            MethodParameter param = new MethodParameter(
                    GlobalExceptionHandler.class.getMethod("handleMethodArgumentNotValid",
                            MethodArgumentNotValidException.class, WebRequest.class), 0);
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(param, bindingResult);

            ResponseEntity<Map<String, Object>> response = handler.handleMethodArgumentNotValid(ex, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            @SuppressWarnings("unchecked")
            Map<String, String> fields = (Map<String, String>) response.getBody().get("fields");
            assertThat(fields).containsEntry("field", "Invalide");
        }
    }

    @Nested
    @DisplayName("handleRuntimeException")
    class HandleRuntimeException {

        @Test
        void retourne_400_avec_message_exception() {
            RuntimeException ex = new RuntimeException("Something went wrong");

            ResponseEntity<Map<String, Object>> response = handler.handleRuntimeException(ex, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("message")).isEqualTo("Something went wrong");
            assertThat(response.getBody().get("type")).isEqualTo("VALIDATION_ERROR");
            assertThat(response.getBody().get("path")).isEqualTo(PATH_DESCRIPTION);
            verify(messageSource).getMessage(eq("error.validation"), any(), any(Locale.class));
        }
    }

    @Nested
    @DisplayName("handleIllegalArgumentException")
    class HandleIllegalArgumentException {

        @Test
        void retourne_400_avec_type_INVALID_DATA() {
            IllegalArgumentException ex = new IllegalArgumentException("ID invalide");

            ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgumentException(ex, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("message")).isEqualTo("ID invalide");
            assertThat(response.getBody().get("type")).isEqualTo("INVALID_DATA");
            assertThat(response.getBody().get("path")).isEqualTo(PATH_DESCRIPTION);
            verify(messageSource).getMessage(eq("error.invalidData"), any(), any(Locale.class));
        }
    }

    @Nested
    @DisplayName("handleGenericException")
    class HandleGenericException {

        @Test
        void retourne_500_avec_messages_internes() {
            Exception ex = new Exception("Unexpected failure");

            ResponseEntity<Map<String, Object>> response = handler.handleGenericException(ex, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("error")).isEqualTo("Erreur interne");
            assertThat(response.getBody().get("message")).isEqualTo("Erreur inattendue");
            assertThat(response.getBody().get("path")).isEqualTo(PATH_DESCRIPTION);
            assertThat(response.getBody()).doesNotContainKey("type");
            verify(messageSource).getMessage(eq("error.internal"), any(), any(Locale.class));
            verify(messageSource).getMessage(eq("error.unexpected"), any(), any(Locale.class));
        }
    }

    @Nested
    @DisplayName("locale (via handlers)")
    class LocaleHandling {

        @Test
        void utilise_locale_pt_quand_contexte_pt() throws Exception {
            setLocale(Locale.forLanguageTag("pt"));
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "t");
            bindingResult.addError(new FieldError("t", "f", "msg"));
            MethodParameter param = new MethodParameter(
                    GlobalExceptionHandler.class.getMethod("handleMethodArgumentNotValid",
                            MethodArgumentNotValidException.class, WebRequest.class), 0);
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(param, bindingResult);

            handler.handleMethodArgumentNotValid(ex, webRequest);

            verify(messageSource).getMessage(eq("error.validation"), any(), any(Locale.class));
        }

        @Test
        void utilise_locale_francais_par_defaut() throws Exception {
            setLocale(Locale.FRENCH);
            RuntimeException ex = new RuntimeException("err");

            handler.handleRuntimeException(ex, webRequest);

            verify(messageSource).getMessage(eq("error.validation"), any(), any(Locale.class));
        }
    }
}
