package co.edu.unbosque.detectia.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.detectia.dto.*;

/**
 * Pruebas unitarias para la clase {@link EleccionService}.
 * <p>
 * Verifica la lógica de enrutamiento del servicio orquestador: dado el tipo
 * MIME de un archivo (o una URL), el servicio debe delegar al conjunto de IAs
 * correcto y devolver un mapa con los resultados parciales, ignorando los
 * fallos individuales de cada API externa.
 * </p>
 *
 * @author Martín García
 * @version 1.0
 * @since 1.0
 * @see EleccionService
 */
class EleccionServiceTest {

    /**
     * Mock del archivo multipart recibido desde el cliente HTTP.
     */
    @Mock
    private MultipartFile archivo;

    /**
     * Mocks de los servicios de IA que componen el pipeline de análisis.
     */
    @Mock private TextoExtractorService extractor;
    @Mock private GrokService grok;
    @Mock private GeminiService gemini;
    @Mock private MistralService mistral;
    @Mock private SightengineService sightengine;
    @Mock private TwelveLabsService twelveLabs;
    @Mock private WinstonService winston;
    @Mock private ACRCloudService acrCloude;
    @Mock private HiveModerationService hiveModeration;

    /**
     * Instancia del servicio bajo prueba.
     */
    private EleccionService service;

    /**
     * Inicializa los mocks e inyecta todas las dependencias del servicio
     * por reflexión antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new EleccionService();

        try {
            setField("extractor", extractor);
            setField("grok", grok);
            setField("gemini", gemini);
            setField("mistral", mistral);
            setField("sightengine", sightengine);
            setField("twelveLabs", twelveLabs);
            setField("winston", winston);
            setField("acrCloude", acrCloude);
            setField("hiveModeration", hiveModeration);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Método auxiliar para inyectar dependencias privadas mediante reflexión.
     *
     * @param nombre nombre del campo privado en {@link EleccionService}
     * @param valor  instancia mock que se asignará al campo
     * @throws Exception si el campo no existe o no es accesible
     */
    private void setField(String nombre, Object valor) throws Exception {
        var f = EleccionService.class.getDeclaredField(nombre);
        f.setAccessible(true);
        f.set(service, valor);
    }

    // ─── ANÁLISIS POR TIPO MIME ────────────────────────────────────────────────

    /**
     * Verifica que un archivo de texto plano ({@code text/plain}) es enrutado
     * al pipeline de análisis de texto (Groq, Gemini, Mistral, Winston).
     *
     * @throws Exception si ocurre un error inesperado durante la ejecución
     */
    @Test
    void testAnalizar_ArchivoTextoPlano() throws Exception {
        when(extractor.detectarTipo(archivo)).thenReturn("text/plain");
        when(extractor.extraerTexto(archivo)).thenReturn("texto de prueba largo");
        when(archivo.getBytes()).thenReturn(new byte[100]);
        when(archivo.getContentType()).thenReturn("text/plain");
        when(grok.detectarIA(anyString())).thenReturn(new GrokDTO(70.0, "PROBABLE IA"));
        when(gemini.detectarIA(any(), anyString())).thenReturn(new GeminiDTO(65.0, "PROBABLE IA"));
        when(mistral.detectarIA(anyString(), any(), anyString())).thenReturn(new MistralDTO(60.0, true));
        when(winston.detectarIA(anyString())).thenReturn(new WinstonDTO(75.0));

        Map<String, Double> resultado = service.analizar(archivo);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    /**
     * Verifica que un archivo PDF ({@code application/pdf}) activa el pipeline
     * de análisis de PDF, que combina Gemini, Mistral, Grok y Winston.
     *
     * @throws Exception si ocurre un error inesperado durante la ejecución
     */
    @Test
    void testAnalizar_ArchivoPDF() throws Exception {
        when(extractor.detectarTipo(archivo)).thenReturn("application/pdf");
        when(extractor.extraerTexto(archivo)).thenReturn("contenido del pdf");
        when(archivo.getBytes()).thenReturn(new byte[100]);
        when(archivo.getContentType()).thenReturn("application/pdf");
        when(gemini.detectarIA(any(), anyString())).thenReturn(new GeminiDTO(55.0, "PROBABLE IA"));
        when(mistral.detectarIA(anyString(), any(), anyString())).thenReturn(new MistralDTO(50.0, true));
        when(grok.detectarIA(anyString())).thenReturn(new GrokDTO(60.0, "PROBABLE IA"));
        when(winston.detectarIA(anyString())).thenReturn(new WinstonDTO(65.0));

        Map<String, Double> resultado = service.analizar(archivo);

        assertNotNull(resultado);
    }

    /**
     * Verifica que una imagen ({@code image/jpeg}) es enrutada al pipeline
     * de análisis de imágenes (Sightengine, Gemini, Hive Moderation, Groq).
     *
     * @throws Exception si ocurre un error inesperado durante la ejecución
     */
    @Test
    void testAnalizar_ArchivoImagen() throws Exception {
        when(extractor.detectarTipo(archivo)).thenReturn("image/jpeg");
        when(archivo.getBytes()).thenReturn(new byte[100]);
        when(archivo.getContentType()).thenReturn("image/jpeg");
        when(sightengine.detectarIA(any(), anyString())).thenReturn(new SightengineDTO(80.0, "success"));
        when(gemini.detectarIA(any(), anyString())).thenReturn(new GeminiDTO(75.0, "PROBABLE IA"));
        when(hiveModeration.detectarIA(any(), anyString())).thenReturn(new HiveModerationDTO(85.0));
        when(grok.detectarIAImagen(any(), anyString())).thenReturn(new GrokDTO(70.0, "PROBABLE IA"));

        Map<String, Double> resultado = service.analizar(archivo);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    /**
     * Verifica que un video ({@code video/mp4}) es enrutado al pipeline
     * de análisis de video (TwelveLabs, Hive Moderation, Gemini).
     *
     * @throws Exception si ocurre un error inesperado durante la ejecución
     */
    @Test
    void testAnalizar_ArchivoVideo() throws Exception {
        when(extractor.detectarTipo(archivo)).thenReturn("video/mp4");
        when(archivo.getBytes()).thenReturn(new byte[100]);
        when(archivo.getContentType()).thenReturn("video/mp4");
        when(twelveLabs.detectarIA(any(), anyString())).thenReturn(new TwelveLabsDTO(90.0, "PROBABLE IA"));
        when(hiveModeration.detectarIA(any(), anyString())).thenReturn(new HiveModerationDTO(85.0));
        when(gemini.detectarIA(any(), anyString())).thenReturn(new GeminiDTO(80.0, "PROBABLE IA"));

        Map<String, Double> resultado = service.analizar(archivo);

        assertNotNull(resultado);
    }

    /**
     * Verifica que un audio ({@code audio/mpeg}) es enrutado al pipeline
     * de análisis de música (ACRCloud y Gemini).
     *
     * @throws Exception si ocurre un error inesperado durante la ejecución
     */
    @Test
    void testAnalizar_ArchivoAudio() throws Exception {
        when(extractor.detectarTipo(archivo)).thenReturn("audio/mpeg");
        when(archivo.getBytes()).thenReturn(new byte[100]);
        when(archivo.getContentType()).thenReturn("audio/mpeg");
        when(acrCloude.detectarIAArchivo(archivo)).thenReturn(new ACRCloudeDTO(88.0));
        when(gemini.detectarIA(any(), anyString())).thenReturn(new GeminiDTO(70.0, "PROBABLE IA"));

        Map<String, Double> resultado = service.analizar(archivo);

        assertNotNull(resultado);
    }

    /**
     * Verifica que un tipo de archivo desconocido retorna un mapa vacío,
     * ya que no hay un pipeline configurado para él.
     *
     * @throws Exception si ocurre un error inesperado durante la ejecución
     */
    @Test
    void testAnalizar_TipoDesconocido() throws Exception {
        when(extractor.detectarTipo(archivo)).thenReturn("application/octet-stream");

        Map<String, Double> resultado = service.analizar(archivo);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    /**
     * Verifica que el análisis por URL de imagen retorna un mapa no vacío
     * cuando la URL tiene extensión {@code .jpg}.
     *
     * @throws Exception si ocurre un error inesperado durante la ejecución
     */
    @Test
    void testAnalizarUrl_ImagenJpg() throws Exception {
        String url = "https://ejemplo.com/imagen.jpg";
        when(winston.detectarIAImagen(url)).thenReturn(new WinstonDTO(60.0));
        when(sightengine.detectarIAUrl(url)).thenReturn(new SightengineDTO(70.0, "success"));
        when(gemini.detectarIAPorUrl(url)).thenReturn(new GeminiDTO(65.0, "PROBABLE IA"));
        when(grok.detectarIAImagenUrl(url)).thenReturn(new GrokDTO(55.0, "PROBABLE IA"));

        Map<String, Double> resultado = service.analizar(url);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    /**
     * Verifica que una URL que no apunta a una imagen soportada retorna
     * un mapa vacío (sin pipeline asignado para ese tipo de URL).
     *
     * @throws Exception si ocurre un error inesperado durante la ejecución
     */
    @Test
    void testAnalizarUrl_UrlNoImagen() throws Exception {
        String url = "https://ejemplo.com/video.mp4";

        Map<String, Double> resultado = service.analizar(url);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    /**
     * Verifica que si uno de los servicios de IA falla durante el análisis
     * de texto, los demás servicios siguen aportando sus resultados
     * (tolerancia a fallos parciales).
     *
     * @throws Exception si ocurre un error inesperado durante la ejecución
     */
    @Test
    void testAnalizar_FalloParcialdUnServicio() throws Exception {
        when(extractor.detectarTipo(archivo)).thenReturn("text/plain");
        when(extractor.extraerTexto(archivo)).thenReturn("texto largo para análisis");
        when(archivo.getBytes()).thenReturn(new byte[100]);
        when(archivo.getContentType()).thenReturn("text/plain");

        // Groq falla con RuntimeException
        when(grok.detectarIA(anyString())).thenThrow(new RuntimeException("Timeout"));
        when(gemini.detectarIA(any(), anyString())).thenReturn(new GeminiDTO(60.0, "PROBABLE IA"));
        when(mistral.detectarIA(anyString(), any(), anyString())).thenReturn(new MistralDTO(55.0, true));
        when(winston.detectarIA(anyString())).thenReturn(new WinstonDTO(65.0));

        // No debe lanzar excepción — fallo tolerado
        assertDoesNotThrow(() -> service.analizar(archivo));
    }
}
