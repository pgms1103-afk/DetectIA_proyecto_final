package co.edu.unbosque.detectia.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.unbosque.detectia.exception.ExtensionInvalidaException;
import co.edu.unbosque.detectia.exception.TamanoInvalidoException;

/**
 * Pruebas unitarias para la clase {@link GrokService}.
 * <p>
 * Verifica el comportamiento del servicio de detección de IA mediante Groq/Grok,
 * incluyendo la validación de formatos de imagen permitidos, restricciones de
 * tamaño de archivo y el procesamiento de imágenes con extensiones válidas.
 * </p>
 *
 * @author Martín García
 * @version 1.0
 * @since 1.0
 * @see GrokService
 */
class GrokServiceTest {

    /** Instancia del servicio bajo prueba. */
    private GrokService service;

    /**
     * Crea una nueva instancia del servicio e inyecta los valores de
     * configuración necesarios (clave de API, URL y modelos) antes de cada
     * prueba mediante reflexión.
     */
    @BeforeEach
    void setUp() {
        service = new GrokService();
        try {
            var f1 = GrokService.class.getDeclaredField("apiKey");
            f1.setAccessible(true); f1.set(service, "test-key");

            var f2 = GrokService.class.getDeclaredField("apiUrl");
            f2.setAccessible(true); f2.set(service, "https://api.test.com");

            var f3 = GrokService.class.getDeclaredField("modelText");
            f3.setAccessible(true); f3.set(service, "llama-test");

            var f4 = GrokService.class.getDeclaredField("modelVision");
            f4.setAccessible(true); f4.set(service, "llama-vision-test");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifica que se lanza {@link ExtensionInvalidaException} cuando se
     * proporciona una imagen con formato no soportado ({@code image/bmp}).
     */
    @Test
    void testDetectarIAImagen_FormatoInvalido() {
        byte[] bytes = new byte[]{1, 2, 3};
        assertThrows(ExtensionInvalidaException.class,
                () -> service.detectarIAImagen(bytes, "image/bmp"));
    }

    /**
     * Verifica que se lanza {@link TamanoInvalidoException} cuando el archivo
     * de imagen supera el límite de 20 MB permitido por Groq.
     */
    @Test
    void testDetectarIAImagen_ArchivoMuyGrande() {
        byte[] bytes = new byte[21 * 1024 * 1024]; // 21 MB
        assertThrows(TamanoInvalidoException.class,
                () -> service.detectarIAImagen(bytes, "image/jpeg"));
    }

    /**
     * Verifica que no se lanza {@link ExtensionInvalidaException} ni
     * {@link TamanoInvalidoException} cuando el archivo tiene formato
     * {@code image/png} y tamaño dentro del límite. Las excepciones de red
     * son ignoradas en este contexto unitario.
     */
    @Test
    void testDetectarIAImagen_FormatoValido() {
        byte[] bytes = new byte[100];
        assertDoesNotThrow(() -> {
            try {
                service.detectarIAImagen(bytes, "image/png");
            } catch (ExtensionInvalidaException | TamanoInvalidoException e) {
                throw e;
            } catch (Exception ignored) { }
        });
    }

    /**
     * Verifica que {@code image/webp} es un formato aceptado por Groq y no
     * lanza {@link ExtensionInvalidaException}.
     */
    @Test
    void testDetectarIAImagen_FormatoWebp_Valido() {
        byte[] bytes = new byte[100];
        assertDoesNotThrow(() -> {
            try {
                service.detectarIAImagen(bytes, "image/webp");
            } catch (ExtensionInvalidaException | TamanoInvalidoException e) {
                throw e;
            } catch (Exception ignored) { }
        });
    }
}
