package co.edu.unbosque.detectia.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.unbosque.detectia.exception.ExtensionInvalidaException;
import co.edu.unbosque.detectia.exception.TamanoInvalidoException;

/**
 * Pruebas unitarias para la clase {@link MistralService}.
 * <p>
 * Verifica que el servicio de detección de IA mediante Mistral (Pixtral) gestione
 * correctamente las validaciones de formato de imagen y tamaño del archivo antes
 * de enviar la solicitud a la API, garantizando el lanzamiento de las excepciones
 * adecuadas ante entradas inválidas.
 * </p>
 *
 * @author Martín García
 * @version 1.0
 * @since 1.0
 * @see MistralService
 */
class MistralServiceTest {

    /** Instancia del servicio bajo prueba. */
    private MistralService service;

    /**
     * Crea una nueva instancia del servicio e inyecta los valores de
     * configuración necesarios antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        service = new MistralService();
        try {
            var f1 = MistralService.class.getDeclaredField("apiKey");
            f1.setAccessible(true); f1.set(service, "test-key");

            var f2 = MistralService.class.getDeclaredField("agentId");
            f2.setAccessible(true); f2.set(service, "agent-test");

            var f3 = MistralService.class.getDeclaredField("apiUrl");
            f3.setAccessible(true); f3.set(service, "https://api.test.com");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifica que se lanza {@link ExtensionInvalidaException} cuando el
     * archivo de imagen tiene formato no soportado ({@code image/tiff}).
     */
    @Test
    void testDetectarIA_ImagenFormatoInvalido() {
        byte[] bytes = new byte[]{1, 2, 3};
        assertThrows(ExtensionInvalidaException.class,
                () -> service.detectarIA("texto", bytes, "image/tiff"));
    }

    /**
     * Verifica que se lanza {@link TamanoInvalidoException} cuando el archivo
     * supera el límite de 512 MB permitido por la API de Mistral.
     */
    @Test
    void testDetectarIA_ArchivoDemasiadoGrande() {
        byte[] bytes = new byte[(int)(513L * 1024 * 1024)];
        assertThrows(TamanoInvalidoException.class,
                () -> service.detectarIA("texto", bytes, "image/jpeg"));
    }

    /**
     * Verifica que no se lanza {@link ExtensionInvalidaException} ni
     * {@link TamanoInvalidoException} cuando el archivo tiene formato de imagen
     * válido ({@code image/jpeg}) y tamaño dentro del límite.
     */
    @Test
    void testDetectarIA_FormatoImagenValido() {
        byte[] bytes = new byte[100];
        assertDoesNotThrow(() -> {
            try {
                service.detectarIA("texto de prueba", bytes, "image/jpeg");
            } catch (ExtensionInvalidaException | TamanoInvalidoException e) {
                throw e;
            } catch (Exception ignored) { }
        });
    }

    /**
     * Verifica que {@code image/webp} es un formato de imagen aceptado por Mistral.
     */
    @Test
    void testDetectarIA_FormatoWebp_Valido() {
        byte[] bytes = new byte[100];
        assertDoesNotThrow(() -> {
            try {
                service.detectarIA("texto de prueba", bytes, "image/webp");
            } catch (ExtensionInvalidaException | TamanoInvalidoException e) {
                throw e;
            } catch (Exception ignored) { }
        });
    }

    /**
     * Verifica que cuando el mimeType es texto puro ({@code text/plain}) no
     * se lanza ninguna excepción de validación de formato de imagen.
     */
    @Test
    void testDetectarIA_TextoPlano_NoLanzaExcepcionFormato() {
        byte[] bytes = new byte[100];
        assertDoesNotThrow(() -> {
            try {
                service.detectarIA("Análisis de texto plano", bytes, "text/plain");
            } catch (ExtensionInvalidaException | TamanoInvalidoException e) {
                throw e;
            } catch (Exception ignored) { }
        });
    }
}
