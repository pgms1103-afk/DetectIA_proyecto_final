package co.edu.unbosque.detectia.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import co.edu.unbosque.detectia.dto.AnalisisDTO;
import co.edu.unbosque.detectia.entity.Analisis;
import co.edu.unbosque.detectia.entity.Archivo;
import co.edu.unbosque.detectia.entity.Usuario;
import co.edu.unbosque.detectia.repository.AnalisisRepository;
import co.edu.unbosque.detectia.repository.ArchivoRepository;
import co.edu.unbosque.detectia.repository.UsuarioRepository;

/**
 * Pruebas unitarias para la clase {@link AnalisisService}.
 * <p>
 * Verifica el cálculo del resumen de análisis de detección de IA, incluyendo
 * la asignación correcta del veredicto según el promedio de votos de los
 * modelos, el manejo de mapas vacíos, y la recuperación de resultados filtrados
 * por usuario o por identificador de archivo.
 * </p>
 *
 * @author Martín García
 * @version 1.0
 * @since 1.0
 * @see AnalisisService
 */
class AnalisisServiceTest {

    /** Mock del repositorio de análisis. */
    @Mock
    private AnalisisRepository analisisRepo;

    /** Mock del repositorio de archivos. */
    @Mock
    private ArchivoRepository archivoRepo;

    /** Mock del repositorio de usuarios. */
    @Mock
    private UsuarioRepository usuarioRepo;

    /** Mapper para conversión entidad-DTO. */
    private ModelMapper mapper;

    /** Instancia del servicio bajo prueba. */
    private AnalisisService service;

    /** Archivo de referencia para los tests. */
    private Archivo archivo;

    /** Análisis de referencia para los tests. */
    private Analisis analisis;

    /** Usuario de referencia para los tests. */
    private Usuario usuario;

    /**
     * Inicializa mocks, inyecta dependencias y prepara objetos de prueba
     * antes de cada caso.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper  = new ModelMapper();
        service = new AnalisisService();

        try {
            var f1 = AnalisisService.class.getDeclaredField("analisisRepo");
            f1.setAccessible(true); f1.set(service, analisisRepo);

            var f2 = AnalisisService.class.getDeclaredField("archivoRepo");
            f2.setAccessible(true); f2.set(service, archivoRepo);

            var f3 = AnalisisService.class.getDeclaredField("usuarioRepo");
            f3.setAccessible(true); f3.set(service, usuarioRepo);

            var f4 = AnalisisService.class.getDeclaredField("mapper");
            f4.setAccessible(true); f4.set(service, mapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        usuario = new Usuario();
        usuario.setNombreUsuario("Juan Perez");
        usuario.setCorreo("juan@correo.com");

        archivo = new Archivo();
        archivo.setId(1L);
        archivo.setNombre("foto.jpg");
        archivo.setUsuario(usuario);

        analisis = new Analisis();
        analisis.setId(1L);
        analisis.setVeredicto("PROBABLE IA");
        analisis.setPorcentajeFinal(75.0);
        analisis.setArchivo(archivo);
        analisis.setFechaAnalisis(LocalDateTime.now());
    }

    // ─── CALCULAR RESUMEN ──────────────────────────────────────────────────────

    /**
     * Verifica que cuando el promedio de votos supera el 50 %, el veredicto
     * resultante es {@code "PROBABLE IA"} y el promedio está presente en el mapa.
     */
    @Test
    void testCalcularResumen_PromedioAlto_VeredictoIA() {
        Map<String, Double> votos = new HashMap<>();
        votos.put("Gemini", 80.0);
        votos.put("Groq",   70.0);

        when(analisisRepo.save(any(Analisis.class))).thenReturn(analisis);

        Map<String, Object> resultado = service.calcularResumen(votos, archivo);

        assertEquals("PROBABLE IA", resultado.get("veredicto"));
        assertNotNull(resultado.get("promedio"));
        verify(analisisRepo).save(any(Analisis.class));
    }

    /**
     * Verifica que cuando el promedio de votos es inferior al 50 %, el
     * veredicto resultante es {@code "PROBABLE HUMANO"}.
     */
    @Test
    void testCalcularResumen_PromedioBajo_VeredictoHumano() {
        Map<String, Double> votos = new HashMap<>();
        votos.put("Gemini", 20.0);
        votos.put("Groq",   30.0);

        when(analisisRepo.save(any(Analisis.class))).thenReturn(analisis);

        Map<String, Object> resultado = service.calcularResumen(votos, archivo);

        assertEquals("PROBABLE HUMANO", resultado.get("veredicto"));
    }

    /**
     * Verifica que cuando el mapa de votos está vacío el promedio retornado
     * es exactamente {@code 0.0}.
     */
    @Test
    void testCalcularResumen_VotosVacios() {
        when(analisisRepo.save(any(Analisis.class))).thenReturn(analisis);

        Map<String, Object> resultado = service.calcularResumen(new HashMap<>(), archivo);

        assertEquals(0.0, resultado.get("promedio"));
    }

    /**
     * Verifica que el promedio es exactamente 50 cuando todos los votos
     * son iguales a 50, y que el veredicto es {@code "PROBABLE IA"}
     * (umbral inclusivo).
     */
    @Test
    void testCalcularResumen_PromedioExacto50_VeredictoIA() {
        Map<String, Double> votos = new HashMap<>();
        votos.put("Gemini", 50.0);
        votos.put("Winston", 50.0);

        when(analisisRepo.save(any(Analisis.class))).thenReturn(analisis);

        Map<String, Object> resultado = service.calcularResumen(votos, archivo);

        assertEquals("PROBABLE IA", resultado.get("veredicto"));
        assertEquals(50.0, resultado.get("promedio"));
    }

    // ─── GET RESULTADOS BY USUARIO ─────────────────────────────────────────────

    /**
     * Verifica que {@code getResultadosByUsuario} retorna una lista vacía
     * cuando el nombre de usuario no existe en el sistema.
     */
    @Test
    void testGetResultadosByUsuario_UsuarioNoExiste() {
        when(usuarioRepo.findByNombreUsuario("fantasma")).thenReturn(Optional.empty());

        List<AnalisisDTO> resultado = service.getResultadosByUsuario("fantasma");

        assertTrue(resultado.isEmpty());
    }

    /**
     * Verifica que {@code getResultadosByUsuario} retorna los análisis del
     * usuario cuando este existe y tiene archivos analizados.
     */
    @Test
    void testGetResultadosByUsuario_UsuarioExiste() {
        when(usuarioRepo.findByNombreUsuario("Juan Perez")).thenReturn(Optional.of(usuario));
        when(archivoRepo.findByUsuario(usuario)).thenReturn(Arrays.asList(archivo));
        when(analisisRepo.findByArchivo(archivo)).thenReturn(Arrays.asList(analisis));

        List<AnalisisDTO> resultado = service.getResultadosByUsuario("Juan Perez");

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    // ─── GET ANALISIS BY ID ────────────────────────────────────────────────────

    /**
     * Verifica que {@code getAnalisisById} retorna una lista vacía cuando
     * el ID del archivo no existe.
     */
    @Test
    void testGetAnalisisById_ArchivoNoExiste() {
        when(archivoRepo.findById(99L)).thenReturn(Optional.empty());

        List<AnalisisDTO> resultado = service.getAnalisisById(99L);

        assertTrue(resultado.isEmpty());
    }

    /**
     * Verifica que {@code getAnalisisById} retorna los análisis vinculados
     * cuando el archivo con el ID especificado existe.
     */
    @Test
    void testGetAnalisisById_ArchivoExiste() {
        when(archivoRepo.findById(1L)).thenReturn(Optional.of(archivo));
        when(analisisRepo.findByArchivo(archivo)).thenReturn(Arrays.asList(analisis));

        List<AnalisisDTO> resultado = service.getAnalisisById(1L);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }
}
