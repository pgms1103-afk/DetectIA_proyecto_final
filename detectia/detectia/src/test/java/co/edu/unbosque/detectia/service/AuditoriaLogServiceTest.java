package co.edu.unbosque.detectia.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import co.edu.unbosque.detectia.entity.AuditoriaLog;
import co.edu.unbosque.detectia.repository.AuditoriaLogRepository;
import co.edu.unbosque.detectia.util.AESUtil;

/**
 * Pruebas unitarias para la clase {@link AuditoriaLogService}.
 * <p>
 * Verifica el registro de eventos de auditoría, la recuperación y descifrado
 * automático del campo {@code correo} en todos los métodos de consulta, y
 * el filtrado de logs por correo, acción, resultado y módulo.
 * </p>
 *
 * @author Martín García
 * @version 1.0
 * @since 1.0
 * @see AuditoriaLogService
 */
class AuditoriaLogServiceTest {

    /** Mock del repositorio de auditoría. */
    @Mock
    private AuditoriaLogRepository auditoriaLogRepo;

    /** Instancia del servicio bajo prueba. */
    private AuditoriaLogService service;

    /** Log de auditoría con correo encriptado, usado como referencia en los tests. */
    private AuditoriaLog logEncriptado;

    /** Correo en texto plano usado para construir el log de prueba. */
    private static final String CORREO_PLANO = "usuario@correo.com";

    /**
     * Inicializa mocks, inyecta dependencias y prepara objetos de prueba
     * antes de cada caso.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AuditoriaLogService();

        try {
            var f = AuditoriaLogService.class.getDeclaredField("auditoriaLogRepo");
            f.setAccessible(true);
            f.set(service, auditoriaLogRepo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // El correo se almacena cifrado en la BD
        logEncriptado = new AuditoriaLog(
                AESUtil.encrypt(CORREO_PLANO), "Carlos Lopez",
                "LOGIN", "AUTH", "Inicio de sesión exitoso",
                "192.168.1.1", "Chrome", null, null,
                "Usuario", "1", true);
    }

    // ─── REGISTRAR ─────────────────────────────────────────────────────────────

    /**
     * Verifica que {@code registrarAuditoria} persiste un log en el repositorio
     * cuando se le proporcionan datos válidos.
     */
    @Test
    void testRegistrarAuditoria_Exitoso() {
        when(auditoriaLogRepo.save(any(AuditoriaLog.class))).thenReturn(logEncriptado);

        service.registrarAuditoria(
                AESUtil.encrypt(CORREO_PLANO), "Carlos Lopez",
                "LOGIN", "AUTH", "Inicio de sesión exitoso",
                "192.168.1.1", "Chrome", null, null,
                "Usuario", "1", true);

        verify(auditoriaLogRepo).save(any(AuditoriaLog.class));
    }

    // ─── GET ALL ───────────────────────────────────────────────────────────────

    /**
     * Verifica que {@code getAll} retorna todos los logs con el correo
     * correctamente descifrado (de AES a texto plano).
     */
    @Test
    void testGetAll_DescifraCorreo() {
        when(auditoriaLogRepo.findAll()).thenReturn(Arrays.asList(logEncriptado));

        List<AuditoriaLog> resultado = service.getAll();

        assertFalse(resultado.isEmpty());
        assertEquals(CORREO_PLANO, resultado.get(0).getCorreo());
    }

    /**
     * Verifica que {@code getAll} retorna lista vacía cuando no hay registros.
     */
    @Test
    void testGetAll_ListaVacia() {
        when(auditoriaLogRepo.findAll()).thenReturn(Collections.emptyList());

        assertTrue(service.getAll().isEmpty());
    }

    // ─── GET BY CORREO ─────────────────────────────────────────────────────────

    /**
     * Verifica que {@code getByCorreo} retorna logs con el correo descifrado
     * cuando existe al menos un registro para ese correo.
     */
    @Test
    void testGetByCorreo_Exitoso() {
        when(auditoriaLogRepo.findByCorreo(anyString())).thenReturn(Arrays.asList(logEncriptado));

        List<AuditoriaLog> resultado = service.getByCorreo(AESUtil.encrypt(CORREO_PLANO));

        assertFalse(resultado.isEmpty());
        assertEquals(CORREO_PLANO, resultado.get(0).getCorreo());
    }

    // ─── GET BY ACCION ─────────────────────────────────────────────────────────

    /**
     * Verifica que {@code getByAccion} retorna los logs que coinciden con
     * la acción especificada, con el correo descifrado.
     */
    @Test
    void testGetByAccion_Exitoso() {
        when(auditoriaLogRepo.findByAccion("LOGIN")).thenReturn(Arrays.asList(logEncriptado));

        List<AuditoriaLog> resultado = service.getByAccion("LOGIN");

        assertFalse(resultado.isEmpty());
        assertEquals("LOGIN", resultado.get(0).getAccion());
        assertEquals(CORREO_PLANO, resultado.get(0).getCorreo());
    }

    // ─── GET BY EXITOSO ────────────────────────────────────────────────────────

    /**
     * Verifica que {@code getByExitoso} retorna únicamente los logs marcados
     * como exitosos cuando se consulta con {@code true}.
     */
    @Test
    void testGetByExitoso_Exitoso() {
        when(auditoriaLogRepo.findByExitoso(true)).thenReturn(Arrays.asList(logEncriptado));

        List<AuditoriaLog> resultado = service.getByExitoso(true);

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.get(0).isExitoso());
    }

    // ─── GET BY MODULO ─────────────────────────────────────────────────────────

    /**
     * Verifica que {@code getByModulo} retorna los logs del módulo especificado
     * con el correo descifrado.
     */
    @Test
    void testGetByModulo_Exitoso() {
        when(auditoriaLogRepo.findByModulo("AUTH")).thenReturn(Arrays.asList(logEncriptado));

        List<AuditoriaLog> resultado = service.getByModulo("AUTH");

        assertFalse(resultado.isEmpty());
        assertEquals("AUTH", resultado.get(0).getModulo());
        assertEquals(CORREO_PLANO, resultado.get(0).getCorreo());
    }

    // ─── DESCIFRADO TOLERANTE ──────────────────────────────────────────────────

    /**
     * Verifica que el servicio no lanza ninguna excepción cuando el correo
     * almacenado no está cifrado (texto plano), y lo devuelve tal cual.
     */
    @Test
    void testGetAll_CorreoNoEncriptado_NoLanzaExcepcion() {
        AuditoriaLog logPlano = new AuditoriaLog(
                "correo_plano_sin_cifrar", "Test User",
                "ACCION", "MODULO", "desc",
                null, null, null, null, "R", "1", true);

        when(auditoriaLogRepo.findAll()).thenReturn(Arrays.asList(logPlano));

        assertDoesNotThrow(() -> service.getAll());
    }
}
