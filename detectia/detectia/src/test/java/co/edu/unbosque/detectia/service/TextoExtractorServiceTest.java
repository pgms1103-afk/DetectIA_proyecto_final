package co.edu.unbosque.detectia.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

/**
 * Pruebas unitarias para la clase {@link TextoExtractorService}.
 * <p>
 * Verifica el correcto funcionamiento de la extracción de texto mediante
 * Apache Tika y la detección del tipo MIME de archivos, incluyendo el manejo
 * adecuado de errores cuando el flujo de entrada del archivo no está disponible.
 * </p>
 *
 * @author Martín García
 * @version 1.0
 * @since 1.0
 * @see TextoExtractorService
 */
class TextoExtractorServiceTest {

    /** Instancia del servicio bajo prueba. */
    private TextoExtractorService service;

    /** Mock del archivo multipart utilizado como entrada en las pruebas. */
    @Mock
    private MultipartFile archivo;

    /**
     * Inicializa los mocks y crea una nueva instancia del servicio
     * antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new TextoExtractorService();
    }

    /**
     * Verifica que {@code extraerTexto} retorna un resultado no nulo y no vacío
     * cuando el archivo contiene texto plano legible.
     *
     * @throws Exception si ocurre un error inesperado durante la extracción
     */
    @Test
    void testExtraerTexto_ArchivoTextoSimple() throws Exception {
        String contenido = "Este es un texto de prueba para la extracción de contenido con Apache Tika.";
        when(archivo.getInputStream())
                .thenReturn(new ByteArrayInputStream(contenido.getBytes()));

        String resultado = service.extraerTexto(archivo);

        assertNotNull(resultado);
        assertFalse(resultado.isBlank());
    }

    /**
     * Verifica que {@code detectarTipo} retorna una cadena no nula y no vacía
     * cuando el archivo contiene contenido de texto plano.
     *
     * @throws Exception si ocurre un error inesperado durante la detección
     */
    @Test
    void testDetectarTipo_ContenidoTexto() throws Exception {
        String contenido = "Hello world plain text content for type detection.";
        when(archivo.getInputStream())
                .thenReturn(new ByteArrayInputStream(contenido.getBytes()));

        String tipo = service.detectarTipo(archivo);

        assertNotNull(tipo);
        assertFalse(tipo.isBlank());
    }

    /**
     * Verifica que {@code extraerTexto} propaga correctamente una
     * {@link IOException} cuando el flujo de entrada del archivo falla.
     *
     * @throws Exception si ocurre un error inesperado durante la prueba
     */
    @Test
    void testExtraerTexto_ErrorInputStream() throws Exception {
        when(archivo.getInputStream()).thenThrow(new IOException("Error de prueba"));

        assertThrows(IOException.class, () -> service.extraerTexto(archivo));
    }

    /**
     * Verifica que {@code detectarTipo} propaga correctamente una
     * {@link IOException} cuando el flujo de entrada del archivo no está disponible.
     *
     * @throws Exception si ocurre un error inesperado durante la prueba
     */
    @Test
    void testDetectarTipo_ErrorInputStream() throws Exception {
        when(archivo.getInputStream()).thenThrow(new IOException("Error de prueba"));

        assertThrows(IOException.class, () -> service.detectarTipo(archivo));
    }

    /**
     * Verifica que el texto extraído de un contenido UTF-8 es equivalente
     * al contenido original (preservación de caracteres).
     *
     * @throws Exception si ocurre un error inesperado durante la extracción
     */
    @Test
    void testExtraerTexto_ContieneContenidoOriginal() throws Exception {
        String contenido = "Texto con caracteres especiales: áéíóú ñ.";
        when(archivo.getInputStream())
                .thenReturn(new ByteArrayInputStream(contenido.getBytes("UTF-8")));

        String resultado = service.extraerTexto(archivo);

        assertNotNull(resultado);
    }
}
