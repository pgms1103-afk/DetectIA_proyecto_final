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

import co.edu.unbosque.detectia.dto.ArchivoDTO;
import co.edu.unbosque.detectia.entity.Archivo;
import co.edu.unbosque.detectia.entity.Usuario;
import co.edu.unbosque.detectia.repository.ArchivoRepository;
import co.edu.unbosque.detectia.repository.UsuarioRepository;

/**
 * Pruebas unitarias para la clase {@link ArchivoService}.
 * <p>
 * Verifica las operaciones CRUD sobre archivos, incluyendo creación con
 * validación de usuario existente, recuperación total y por usuario,
 * eliminación y actualización por ID, y búsqueda individual por identificador.
 * </p>
 *
 * @author Martín García
 * @version 1.0
 * @since 1.0
 * @see ArchivoService
 */
class ArchivoServiceTest {

    /** Mock del repositorio de archivos. */
    @Mock private ArchivoRepository archivoRepo;

    /** Mock del repositorio de usuarios. */
    @Mock private UsuarioRepository usuarioRepo;

    /** Mock del servicio de auditoría. */
    @Mock private AuditoriaLogService auditoriaLogSer;

    /** Mapper para conversión entidad-DTO. */
    private ModelMapper mapper;

    /** Instancia del servicio bajo prueba. */
    private ArchivoService service;

    /** Usuario de referencia para los tests. */
    private Usuario usuario;

    /** Archivo de referencia para los tests. */
    private Archivo archivo;

    /** DTO de archivo de referencia para los tests. */
    private ArchivoDTO dto;

    /**
     * Inicializa mocks, inyecta dependencias y prepara objetos de prueba
     * antes de cada caso.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper  = new ModelMapper();
        service = new ArchivoService();

        try {
            var f1 = ArchivoService.class.getDeclaredField("archivoRepo");
            f1.setAccessible(true); f1.set(service, archivoRepo);

            var f2 = ArchivoService.class.getDeclaredField("usuarioRepo");
            f2.setAccessible(true); f2.set(service, usuarioRepo);

            var f3 = ArchivoService.class.getDeclaredField("mapper");
            f3.setAccessible(true); f3.set(service, mapper);

            var f4 = ArchivoService.class.getDeclaredField("auditoriaLogSer");
            f4.setAccessible(true); f4.set(service, auditoriaLogSer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombreUsuario("Carlos Lopez");
        usuario.setCorreo("carlos@correo.com");

        archivo = new Archivo();
        archivo.setId(1L);
        archivo.setNombre("imagen.png");
        archivo.setRutaAlmacenamiento("/uploads/imagen.png");
        archivo.setFechaSubida(LocalDateTime.now());
        archivo.setUsuario(usuario);

        dto = new ArchivoDTO();
        dto.setNombre("imagen.png");
        dto.setRutaAlmacenamiento("/uploads/imagen.png");
        dto.setFechaSubida(LocalDateTime.now());
        dto.setUsuarioId(1L);
    }

    // ─── CREATE ────────────────────────────────────────────────────────────────

    /**
     * Verifica que {@code create} retorna 1 cuando el usuario asociado
     * no existe en el repositorio.
     */
    @Test
    void testCreate_UsuarioNoExiste() {
        when(usuarioRepo.findById(1L)).thenReturn(Optional.empty());

        assertEquals(1, service.create(dto));
    }

    /**
     * Verifica que {@code create} retorna 0 y persiste el archivo cuando
     * el usuario asociado existe en el repositorio.
     */
    @Test
    void testCreate_UsuarioExiste() {
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuario));
        when(archivoRepo.save(any(Archivo.class))).thenReturn(archivo);

        assertEquals(0, service.create(dto));
    }

    // ─── GET ALL ───────────────────────────────────────────────────────────────

    /**
     * Verifica que {@code getAll} retorna todos los archivos con la
     * información del nombre y usuario correctamente mapeada.
     */
    @Test
    void testGetAll() {
        when(archivoRepo.findAll()).thenReturn(Arrays.asList(archivo));

        List<ArchivoDTO> resultado = service.getAll();

        assertFalse(resultado.isEmpty());
        assertEquals("imagen.png", resultado.get(0).getNombre());
        assertEquals("Carlos Lopez", resultado.get(0).getUsername());
    }

    // ─── DELETE ────────────────────────────────────────────────────────────────

    /**
     * Verifica que {@code delateById} retorna 0 e invoca el repositorio
     * para borrar el archivo cuando este existe.
     */
    @Test
    void testDelateById_Existe() {
        when(archivoRepo.findById(1L)).thenReturn(Optional.of(archivo));
        doNothing().when(auditoriaLogSer).registrarAuditoria(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), anyBoolean());

        assertEquals(0, service.delateById(1L));
        verify(archivoRepo).delete(archivo);
    }

    /**
     * Verifica que {@code delateById} retorna 1 cuando el archivo no existe.
     */
    @Test
    void testDelateById_NoExiste() {
        when(archivoRepo.findById(99L)).thenReturn(Optional.empty());

        assertEquals(1, service.delateById(99L));
    }

    // ─── UPDATE ────────────────────────────────────────────────────────────────

    /**
     * Verifica que {@code updateById} retorna 0 y guarda los cambios
     * cuando el archivo existe.
     */
    @Test
    void testUpdateById_Existe() {
        when(archivoRepo.findById(1L)).thenReturn(Optional.of(archivo));
        when(archivoRepo.save(any(Archivo.class))).thenReturn(archivo);
        doNothing().when(auditoriaLogSer).registrarAuditoria(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), anyBoolean());

        assertEquals(0, service.updateById(1L, dto));
    }

    /**
     * Verifica que {@code updateById} retorna 1 cuando el archivo no existe.
     */
    @Test
    void testUpdateById_NoExiste() {
        when(archivoRepo.findById(99L)).thenReturn(Optional.empty());

        assertEquals(1, service.updateById(99L, dto));
    }

    // ─── GET BY USER ───────────────────────────────────────────────────────────

    /**
     * Verifica que {@code getArchivosByuser} retorna lista vacía cuando el
     * nombre de usuario no existe.
     */
    @Test
    void testGetArchivosByUser_UsuarioNoExiste() {
        when(usuarioRepo.findByNombreUsuario("nadie")).thenReturn(Optional.empty());

        assertTrue(service.getArchivosByuser("nadie").isEmpty());
    }

    /**
     * Verifica que {@code getArchivosByuser} retorna los archivos del usuario
     * cuando este existe y tiene archivos asociados.
     */
    @Test
    void testGetArchivosByUser_UsuarioExiste() {
        when(usuarioRepo.findByNombreUsuario("Carlos Lopez")).thenReturn(Optional.of(usuario));
        when(archivoRepo.findByUsuario(usuario)).thenReturn(Arrays.asList(archivo));

        List<ArchivoDTO> resultado = service.getArchivosByuser("Carlos Lopez");

        assertFalse(resultado.isEmpty());
        assertEquals("imagen.png", resultado.get(0).getNombre());
    }

    // ─── GET BY ID ─────────────────────────────────────────────────────────────

    /**
     * Verifica que {@code getById} retorna un DTO no nulo cuando el archivo
     * existe en el repositorio.
     */
    @Test
    void testGetById_Existe() {
        when(archivoRepo.findById(1L)).thenReturn(Optional.of(archivo));

        assertNotNull(service.getById(1L));
    }

    /**
     * Verifica que {@code getById} retorna {@code null} cuando el archivo
     * no existe en el repositorio.
     */
    @Test
    void testGetById_NoExiste() {
        when(archivoRepo.findById(99L)).thenReturn(Optional.empty());

        assertNull(service.getById(99L));
    }
}
