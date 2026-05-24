package co.edu.unbosque.detectia.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.detectia.exception.ExtensionInvalidaException;

/**
 * Pruebas unitarias para la clase {@link ACRCloudService}.
 * <p>
 * Verifica el comportamiento del servicio de detección de IA musical mediante
 * ACRCloud, incluyendo la validación de formatos de audio soportados y el
 * rechazo de formatos no permitidos como imágenes o documentos.
 * </p>
 *
 * @author Martín García
 * @version 1.0
 * @since 1.0
 * @see ACRCloudService
 */
class ACRCloudServiceTest {

    /** Instancia del servicio bajo prueba. */
    private ACRCloudService service;

    /** Mock del archivo multipart utilizado como entrada en las pruebas. */
    @Mock
    private MultipartFile archivo;

    /**
     * Inicializa los mocks e inyecta los valores de configuración del servicio
     * por reflexión antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new ACRCloudService();
        try {
            var f1 = ACRCloudService.class.getDeclaredField("token");
            f1.setAccessible(true); f1.set(service, "test-token");

            var f2 = ACRCloudService.class.getDeclaredField("containerId");
            f2.setAccessible(true); f2.set(service, "container-test");

            var f3 = ACRCloudService.class.getDeclaredField("url");
            f3.setAccessible(true); f3.set(service, "https://api.test.com/");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifica que se lanza {@link ExtensionInvalidaException} cuando el
     * archivo tiene un formato no soportado por ACRCloud ({@code image/jpeg}).
     *
     * @throws Exception si ocurre un error inesperado durante la ejecución
     */
    @Test
    void testDetectarIAArchivo_FormatoInvalido() throws Exception {
        when(archivo.getContentType()).thenReturn("image/jpeg");
        assertThrows(ExtensionInvalidaException.class,
                () -> service.detectarIAArchivo(archivo));
    }

    /**
     * Verifica que no se lanza {@link ExtensionInvalidaException} cuando el
     * archivo tiene formato de audio válido ({@code audio/mpeg}). Las
     * excepciones de red son ignoradas en este contexto unitario.
     *
     * @throws Exception si ocurre un error inesperado durante la ejecución
     */
    @Test
    void testDetectarIAArchivo_FormatoAudioMpeg_Valido() throws Exception {
        when(archivo.getContentType()).thenReturn("audio/mpeg");
        when(archivo.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(archivo.getOriginalFilename()).thenReturn("pista.mp3");
        assertDoesNotThrow(() -> {
            try {
                service.detectarIAArchivo(archivo);
            } catch (ExtensionInvalidaException e) {
                throw e;
            } catch (Exception ignored) { }
        });
    }

    /**
     * Verifica que {@code audio/wav} es un formato de audio aceptado por
     * ACRCloud y no lanza {@link ExtensionInvalidaException}.
     *
     * @throws Exception si ocurre un error inesperado durante la ejecución
     */
    @Test
    void testDetectarIAArchivo_FormatoWav_Valido() throws Exception {
        when(archivo.getContentType()).thenReturn("audio/wav");
        when(archivo.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(archivo.getOriginalFilename()).thenReturn("pista.wav");
        assertDoesNotThrow(() -> {
            try {
                service.detectarIAArchivo(archivo);
            } catch (ExtensionInvalidaException e) {
                throw e;
            } catch (Exception ignored) { }
        });
    }

    /**
     * Verifica que {@code video/mp4} es rechazado con
     * {@link ExtensionInvalidaException} (ACRCloud solo acepta audio).
     *
     * @throws Exception si ocurre un error inesperado durante la ejecución
     */
    @Test
    void testDetectarIAArchivo_FormatoVideo_Invalido() throws Exception {
        when(archivo.getContentType()).thenReturn("video/mp4");
        assertThrows(ExtensionInvalidaException.class,
                () -> service.detectarIAArchivo(archivo));
    }
}
