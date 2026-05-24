package co.edu.unbosque.detectia.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.unbosque.detectia.exception.ExtensionInvalidaException;
import co.edu.unbosque.detectia.exception.TamanoInvalidoException;

/**
 * Pruebas unitarias para la clase {@link SightengineService}.
 * <p>
 * Verifica que el servicio de detección de imágenes generadas por IA mediante
 * Sightengine aplique correctamente las restricciones de formato y tamaño,
 * rechazando formatos no soportados (video, audio) e imágenes demasiado grandes.
 * </p>
 *
 * @author Martín García
 * @version 1.0
 * @since 1.0
 * @see SightengineService
 */
class SightengineServiceTest {

    /** Instancia del servicio bajo prueba. */
    private SightengineService service;

    /**
     * Crea una nueva instancia del servicio e inyecta los valores de
     * configuración necesarios antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        service = new SightengineService();
        try {
            var f1 = SightengineService.class.getDeclaredField("apiUser");
            f1.setAccessible(true); f1.set(service, "test-user");

            var f2 = SightengineService.class.getDeclaredField("apiKey");
            f2.setAccessible(true); f2.set(service, "test-key");

            var f3 = SightengineService.class.getDeclaredField("model");
            f3.setAccessible(true); f3.set(service, "genai");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifica que se lanza {@link ExtensionInvalidaException} cuando el
     * archivo tiene formato de video ({@code video/mp4}), no admitido
     * por el servicio de análisis de imágenes.
     */
    @Test
    void testDetectarIA_FormatoVideoInvalido() {
        byte[] bytes = new byte[]{1, 2, 3};
        assertThrows(ExtensionInvalidaException.class,
                () -> service.detectarIA(bytes, "video/mp4"));
    }

    /**
     * Verifica que se lanza {@link ExtensionInvalidaException} cuando el
     * archivo tiene formato de audio ({@code audio/mpeg}).
     */
    @Test
    void testDetectarIA_FormatoAudioInvalido() {
        byte[] bytes = new byte[]{1, 2, 3};
        assertThrows(ExtensionInvalidaException.class,
                () -> service.detectarIA(bytes, "audio/mpeg"));
    }

    /**
     * Verifica que se lanza {@link TamanoInvalidoException} cuando el archivo
     * de imagen supera el límite de 50 MB permitido por Sightengine.
     */
    @Test
    void testDetectarIA_ArchivoMuyGrande() {
        byte[] bytes = new byte[(int)(51L * 1024 * 1024)]; // 51 MB
        assertThrows(TamanoInvalidoException.class,
                () -> service.detectarIA(bytes, "image/jpeg"));
    }

    /**
     * Verifica que no se lanza {@link ExtensionInvalidaException} ni
     * {@link TamanoInvalidoException} cuando el archivo es una imagen PNG
     * válida dentro del límite de tamaño.
     */
    @Test
    void testDetectarIA_ImagenPng_Valida() {
        byte[] bytes = new byte[100];
        assertDoesNotThrow(() -> {
            try {
                service.detectarIA(bytes, "image/png");
            } catch (ExtensionInvalidaException | TamanoInvalidoException e) {
                throw e;
            } catch (Exception ignored) { }
        });
    }

    /**
     * Verifica que {@code image/gif} también es aceptado por Sightengine.
     */
    @Test
    void testDetectarIA_ImagenGif_Valida() {
        byte[] bytes = new byte[100];
        assertDoesNotThrow(() -> {
            try {
                service.detectarIA(bytes, "image/gif");
            } catch (ExtensionInvalidaException | TamanoInvalidoException e) {
                throw e;
            } catch (Exception ignored) { }
        });
    }
}
