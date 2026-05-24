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

import co.edu.unbosque.detectia.dto.ResultadoIADTO;
import co.edu.unbosque.detectia.entity.Archivo;
import co.edu.unbosque.detectia.entity.ResultadoIA;
import co.edu.unbosque.detectia.repository.ArchivoRepository;
import co.edu.unbosque.detectia.repository.ResultadoIARepository;
import co.edu.unbosque.detectia.repository.UsuarioRepository;

/**
 * Pruebas unitarias para la clase {@link ResultadoIAService}.
 * <p>
 * Valida el comportamiento del servicio encargado de persistir y consultar
 * los resultados individuales de cada herramienta de IA, incluyendo casos
 * con archivo nulo, múltiples votos y búsquedas por ID de archivo.
 * </p>
 *
 * @author Martín García
 * @version 1.0
 * @since 1.0
 * @see ResultadoIAService
 */
class ResultadoIAServiceTest {

    /** Mock del repositorio de resultados de IA. */
    @Mock private ResultadoIARepository resultadoIARepo;

    /** Mock del repositorio de archivos. */
    @Mock private ArchivoRepository archivoRepo;

    /** Mock del repositorio de usuarios. */
    @Mock private UsuarioRepository usuarioRepo;

    /** Mapper para conversión entidad-DTO. */
    private ModelMapper mapper;

    /** Instancia del servicio bajo prueba. */
    private ResultadoIAService service;

    /** Archivo de referencia para los tests. */
    private Archivo archivo;

    /** Resultado de IA de referencia para los tests. */
    private ResultadoIA resultadoIA;

    /**
     * Inicializa mocks, inyecta dependencias y prepara objetos de prueba
     * antes de cada caso.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper  = new ModelMapper();
        service = new ResultadoIAService();

        try {
            var f1 = ResultadoIAService.class.getDeclaredField("resultadoIARepo");
            f1.setAccessible(true); f1.set(service, resultadoIARepo);

            var f2 = ResultadoIAService.class.getDeclaredField("archivoRepo");
            f2.setAccessible(true); f2.set(service, archivoRepo);

            var f3 = ResultadoIAService.class.getDeclaredField("usuarioRepo");
            f3.setAccessible(true); f3.set(service, usuarioRepo);

            var f4 = ResultadoIAService.class.getDeclaredField("mapper");
            f4.setAccessible(true); f4.set(service, mapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        archivo = new Archivo();
        archivo.setId(1L);
        archivo.setNombre("audio.mp3");

        resultadoIA = new ResultadoIA();
        resultadoIA.setId(1L);
        resultadoIA.setNombreIA("Gemini");
        resultadoIA.setPorcentajeIA(85.0);
        resultadoIA.setFechaAnalisis(LocalDateTime.now());
        resultadoIA.setArchivo(archivo);
    }

    // ─── GUARDAR RESULTADOS ────────────────────────────────────────────────────

    /**
     * Verifica que no se persiste ningún resultado cuando el archivo
     * proporcionado es {@code null}.
     */
    @Test
    void testGuardarResultados_ArchivoNulo() {
        service.guardarResultados(Map.of("Gemini", 80.0), null);

        verify(resultadoIARepo, never()).save(any());
    }

    /**
     * Verifica que se persiste exactamente un resultado por cada IA del mapa
     * cuando el archivo de referencia no es nulo.
     */
    @Test
    void testGuardarResultados_ConDosVotos() {
        Map<String, Double> votos = new HashMap<>();
        votos.put("Gemini", 80.0);
        votos.put("Groq",   60.0);

        when(resultadoIARepo.save(any(ResultadoIA.class))).thenReturn(resultadoIA);

        service.guardarResultados(votos, archivo);

        verify(resultadoIARepo, times(2)).save(any(ResultadoIA.class));
    }

    /**
     * Verifica que con un mapa de un único voto se persiste exactamente
     * un resultado.
     */
    @Test
    void testGuardarResultados_UnSoloVoto() {
        when(resultadoIARepo.save(any(ResultadoIA.class))).thenReturn(resultadoIA);

        service.guardarResultados(Map.of("Winston", 55.0), archivo);

        verify(resultadoIARepo, times(1)).save(any(ResultadoIA.class));
    }

    // ─── GET ALL ───────────────────────────────────────────────────────────────

    /**
     * Verifica que {@code getAll} retorna la lista completa de resultados de IA
     * con la información del nombre de herramienta correctamente mapeada.
     */
    @Test
    void testGetAll() {
        when(resultadoIARepo.findAll()).thenReturn(Arrays.asList(resultadoIA));

        List<ResultadoIADTO> resultado = service.getAll();

        assertFalse(resultado.isEmpty());
        assertEquals("Gemini", resultado.get(0).getNombreIA());
        assertEquals(85.0, resultado.get(0).getPorcentajeIA());
    }

    // ─── GET BY ARCHIVO ID ─────────────────────────────────────────────────────

    /**
     * Verifica que {@code getResultadosByArchivoId} retorna lista vacía cuando
     * el ID del archivo no existe en el repositorio.
     */
    @Test
    void testGetResultadosByArchivoId_ArchivoNoExiste() {
        when(archivoRepo.findById(99L)).thenReturn(Optional.empty());

        assertTrue(service.getResultadosByArchivoId(99L).isEmpty());
    }

    /**
     * Verifica que {@code getResultadosByArchivoId} retorna los resultados
     * correctamente cuando el archivo existe.
     */
    @Test
    void testGetResultadosByArchivoId_ArchivoExiste() {
        when(archivoRepo.findById(1L)).thenReturn(Optional.of(archivo));
        when(resultadoIARepo.findByArchivo(archivo)).thenReturn(Arrays.asList(resultadoIA));

        List<ResultadoIADTO> resultado = service.getResultadosByArchivoId(1L);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }
}
