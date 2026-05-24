package co.edu.unbosque.detectia.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.unbosque.detectia.exception.ExtensionInvalidaException;

/**
 * Pruebas unitarias para la clase {@link HiveModerationService}.
 * <p>
 * Verifica que el servicio de detección de contenido sintético mediante Hive
 * Moderation valide correctamente los tipos de contenido admitidos (imágenes
 * y videos), rechazando formatos de audio y otros no soportados por el
 * endpoint de análisis imagen/video.
 * </p>
 *
 * @author Martín García
 * @version 1.0
 * @since 1.0
 * @see HiveModerationService
 */
class HiveModerationServiceTest {

    /** Instancia del servicio bajo prueba. */
    private HiveModerationService service;

    /**
     * Crea una nueva instancia del servicio e inyecta los valores de
     * configuración necesarios antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        service = new HiveModerationService();
        try {
            var f1 = HiveModerationService.class.getDeclaredField("apiKey");
            f1.setAccessible(true); f1.set(service, "test-key");

            var f2 = HiveModerationService.class.getDeclaredField("apiUrl");
            f2.setAccessible(true); f2.set(service, "https://api.test.com");

            var f3 = HiveModerationService.class.getDeclaredField("apiUrlAudio");
            f3.setAccessible(true); f3.set(service, "https://api.test.com/audio");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifica que se lanza {@link ExtensionInvalidaException} cuando se
     * intenta analizar audio ({@code audio/mpeg}), formato no admitido por
     * el endpoint de imagen/video de Hive.
     */
    @Test
    void testDetectarIA_FormatoAudioInvalido() {
        byte[] bytes = new byte[]{1, 2, 3};
        assertThrows(ExtensionInvalidaException.class,
                () -> service.detectarIA(bytes, "audio/mpeg"));
    }

    /**
     * Verifica que no se lanza {@link ExtensionInvalidaException} cuando
     * el archivo tiene formato de imagen válido ({@code image/jpeg}).
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
     * Verifica que no se lanza {@link ExtensionInvalidaException} cuando
     * el archivo tiene formato de video válido ({@code video/mp4}).
     */
    @Test
    void testDetectarIA_FormatoVideoValido() {
        byte[] bytes = new byte[100];
        assertDoesNotThrow(() -> {
            try {
                service.detectarIA(bytes, "video/mp4");
            } catch (ExtensionInvalidaException e) {
                throw e;
            } catch (Exception ignored) { }
        });
    }

    /**
     * Verifica que {@code video/quicktime} (formato MOV de Apple) es aceptado
     * por Hive Moderation.
     */
    @Test
    void testDetectarIA_FormatoQuicktime_Valido() {
        byte[] bytes = new byte[100];
        assertDoesNotThrow(() -> {
            try {
                service.detectarIA(bytes, "video/quicktime");
            } catch (ExtensionInvalidaException e) {
                throw e;
            } catch (Exception ignored) { }
        });
    }
}
