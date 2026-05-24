package co.edu.unbosque.detectia.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.unbosque.detectia.exception.ExtensionInvalidaException;
import co.edu.unbosque.detectia.exception.TamanoInvalidoException;

/**
 * Pruebas unitarias para la clase {@link GeminiService}.
 * <p>
 * Verifica que el servicio de detección de IA mediante Google Gemini valide
 * correctamente los formatos de archivo antes de realizar el análisis,
 * lanzando las excepciones correspondientes ante entradas no permitidas y
 * aceptando los formatos multimodal soportados.
 * </p>
 *
 * @author Martín García
 * @version 1.0
 * @since 1.0
 * @see GeminiService
 */
class GeminiServiceTest {

    /** Instancia del servicio bajo prueba. */
    private GeminiService service;

    /**
     * Crea una nueva instancia del servicio e inyecta los valores de
     * configuración necesarios antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        service = new GeminiService();
        try {
            var f1 = GeminiService.class.getDeclaredField("apiKey");
            f1.setAccessible(true); f1.set(service, "test-key");

            var f2 = GeminiService.class.getDeclaredField("apiUrl");
            f2.setAccessible(true); f2.set(service, "https://api.test.com");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifica que se lanza {@link ExtensionInvalidaException} cuando el
     * formato del archivo no está soportado por Gemini ({@code application/exe}).
     */
    @Test
    void testDetectarIA_FormatoNoSoportado() {
        byte[] bytes = new byte[]{1, 2, 3};
        assertThrows(ExtensionInvalidaException.class,
                () -> service.detectarIA(bytes, "application/exe"));
    }

    /**
     * Verifica que no se lanza {@link ExtensionInvalidaException} cuando el
     * formato es una imagen válida ({@code image/jpeg}). Las excepciones de
     * red son ignoradas en este contexto unitario.
     */
    @Test
    void testDetectarIA_FormatoImagenValido() {
        byte[] bytes = new byte[100];
        assertDoesNotThrow(() -> {
            try {
                service.detectarIA(bytes, "image/jpeg");
            } catch (ExtensionInvalidaException e) {
                throw e;
            } catch (Exception ignored) { }
        });
    }

    /**
     * Verifica que {@code application/pdf} es un formato aceptado por Gemini.
     */
    @Test
    void testDetectarIA_FormatoPDF_Valido() {
        byte[] bytes = new byte[100];
        assertDoesNotThrow(() -> {
            try {
                service.detectarIA(bytes, "application/pdf");
            } catch (ExtensionInvalidaException e) {
                throw e;
            } catch (Exception ignored) { }
        });
    }

    /**
     * Verifica que se lanza {@link TamanoInvalidoException} cuando el archivo
     * supera el límite de 100 MB.
     */
    @Test
    void testDetectarIA_ArchivoMuyGrande() {
        byte[] bytes = new byte[(int)(101L * 1024 * 1024)]; // 101 MB
        assertThrows(TamanoInvalidoException.class,
                () -> service.detectarIA(bytes, "image/jpeg"));
    }

    /**
     * Verifica que {@code audio/mpeg} es un formato de audio soportado por Gemini.
     */
    @Test
    void testDetectarIA_FormatoAudioValido() {
        byte[] bytes = new byte[100];
        assertDoesNotThrow(() -> {
            try {
                service.detectarIA(bytes, "audio/mpeg");
            } catch (ExtensionInvalidaException e) {
                throw e;
            } catch (Exception ignored) { }
        });
    }
}
