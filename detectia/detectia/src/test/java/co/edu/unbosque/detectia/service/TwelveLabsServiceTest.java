package co.edu.unbosque.detectia.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.unbosque.detectia.exception.ExtensionInvalidaException;
import co.edu.unbosque.detectia.exception.TamanoInvalidoException;

/**
 * Pruebas unitarias para la clase {@link TwelveLabsService}.
 * <p>
 * Verifica que el servicio de detección de deepfakes mediante TwelveLabs
 * aplique correctamente las restricciones de formato (solo video, sin audio)
 * y de tamaño (máximo 200 MB), rechazando formatos incompatibles y videos
 * que superen el límite de peso.
 * </p>
 *
 * @author Martín García
 * @version 1.0
 * @since 1.0
 * @see TwelveLabsService
 */
class TwelveLabsServiceTest {

    /** Instancia del servicio bajo prueba. */
    private TwelveLabsService service;

    /**
     * Crea una nueva instancia del servicio e inyecta los valores de
     * configuración necesarios antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        service = new TwelveLabsService();
        try {
            var f1 = TwelveLabsService.class.getDeclaredField("apiKey");
            f1.setAccessible(true); f1.set(service, "test-key");

            var f2 = TwelveLabsService.class.getDeclaredField("apiUrl");
            f2.setAccessible(true); f2.set(service, "https://api.test.com");

            var f3 = TwelveLabsService.class.getDeclaredField("indexId");
            f3.setAccessible(true); f3.set(service, "index-test");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifica que se lanza {@link ExtensionInvalidaException} cuando el
     * archivo tiene formato de audio ({@code audio/mpeg}), no compatible
     * con TwelveLabs que opera exclusivamente con video.
     */
    @Test
    void testDetectarIA_FormatoAudioInvalido() {
        byte[] bytes = new byte[]{1, 2, 3};
        assertThrows(ExtensionInvalidaException.class,
                () -> service.detectarIA(bytes, "audio/mpeg"));
    }

    /**
     * Verifica que se lanza {@link TamanoInvalidoException} cuando el video
     * supera el límite de 200 MB permitido por TwelveLabs.
     */
    @Test
    void testDetectarIA_VideoMuyGrande() {
        byte[] bytes = new byte[(int)(201L * 1024 * 1024)]; // 201 MB
        assertThrows(TamanoInvalidoException.class,
                () -> service.detectarIA(bytes, "video/mp4"));
    }

    /**
     * Verifica que no se lanza {@link ExtensionInvalidaException} ni
     * {@link TamanoInvalidoException} cuando el archivo es un video MP4
     * válido dentro del límite de tamaño.
     */
    @Test
    void testDetectarIA_FormatoVideoValido() {
        byte[] bytes = new byte[100];
        assertDoesNotThrow(() -> {
            try {
                service.detectarIA(bytes, "video/mp4");
            } catch (ExtensionInvalidaException | TamanoInvalidoException e) {
                throw e;
            } catch (Exception ignored) { }
        });
    }

    /**
     * Verifica que {@code video/quicktime} (formato MOV) es aceptado
     * por TwelveLabs.
     */
    @Test
    void testDetectarIA_FormatoQuicktime_Valido() {
        byte[] bytes = new byte[100];
        assertDoesNotThrow(() -> {
            try {
                service.detectarIA(bytes, "video/quicktime");
            } catch (ExtensionInvalidaException | TamanoInvalidoException e) {
                throw e;
            } catch (Exception ignored) { }
        });
    }
}
