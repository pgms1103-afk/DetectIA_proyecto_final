package co.edu.unbosque.detectia.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.unbosque.detectia.dto.UsuarioDTO;
import co.edu.unbosque.detectia.entity.Usuario;
import co.edu.unbosque.detectia.entity.Usuario.Role;
import co.edu.unbosque.detectia.exception.CorreoInvalidoException;
import co.edu.unbosque.detectia.exception.IdExistException;
import co.edu.unbosque.detectia.exception.NombreInvalidoException;
import co.edu.unbosque.detectia.exception.PasswordNotValidException;
import co.edu.unbosque.detectia.repository.ArchivoRepository;
import co.edu.unbosque.detectia.repository.UsuarioRepository;

/**
 * Pruebas unitarias del contrato definido por la interfaz {@link CRUDoperation},
 * verificadas a través de {@link UsuarioService}, que es su implementación más
 * completa en el proyecto.
 * <p>
 * Cubre los cuatro métodos del contrato ({@code create}, {@code getAll},
 * {@code delateById}, {@code updateById}) incluyendo casos de éxito,
 * validaciones de entrada y lanzamiento de excepciones de negocio.
 * </p>
 *
 * @author Martín García
 * @version 1.0
 * @since 1.0
 * @see CRUDoperation
 * @see UsuarioService
 */
class CRUDOperationTest {

    /**
     * Mock del repositorio de usuarios.
     */
    @Mock
    private UsuarioRepository usuarioRepo;

    /**
     * Mock del repositorio de archivos.
     */
    @Mock
    private ArchivoRepository archivoRepo;

    /**
     * Mapper para la conversión entre entidades y DTOs.
     */
    private ModelMapper mapper;

    /**
     * Codificador de contraseñas BCrypt real.
     */
    private PasswordEncoder passwordEncoder;

    /**
     * Instancia del servicio bajo prueba (implementación de {@link CRUDoperation}).
     */
    private UsuarioService service;

    /**
     * Entidad de usuario de referencia utilizada en los tests.
     */
    private Usuario usuario;

    /**
     * DTO de usuario de referencia utilizado en los tests.
     */
    private UsuarioDTO dto;

    /**
     * Inicializa los mocks, inyecta las dependencias en el servicio por reflexión
     * y prepara los objetos de prueba antes de cada caso.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new ModelMapper();
        passwordEncoder = new BCryptPasswordEncoder();
        service = new UsuarioService();

        try {
            var f1 = UsuarioService.class.getDeclaredField("usuarioRepo");
            f1.setAccessible(true);
            f1.set(service, usuarioRepo);

            var f2 = UsuarioService.class.getDeclaredField("mapper");
            f2.setAccessible(true);
            f2.set(service, mapper);

            var f3 = UsuarioService.class.getDeclaredField("passwordEncoder");
            f3.setAccessible(true);
            f3.set(service, passwordEncoder);

            var f4 = UsuarioService.class.getDeclaredField("archivoRepo");
            f4.setAccessible(true);
            f4.set(service, archivoRepo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombreUsuario("Pedro Ramirez");
        usuario.setCorreo("pedro@correo.com");
        usuario.setContrasena(passwordEncoder.encode("Segura12"));
        usuario.setRole(Role.USER);

        dto = new UsuarioDTO();
        dto.setNombreUsuario("Pedro Ramirez");
        dto.setCorreo("pedro@correo.com");
        dto.setContrasena("Segura12");
        dto.setRole(Role.USER);
    }

    // ─── CREATE ────────────────────────────────────────────────────────────────

    /**
     * Verifica que {@code create} retorna 0 (éxito) cuando todos los datos
     * son válidos y el correo no está duplicado.
     */
    @Test
    void testCreate_Exitoso() {
        when(usuarioRepo.existsByCorreo(anyString())).thenReturn(false);
        when(usuarioRepo.findByNombreUsuario("Pedro Ramirez")).thenReturn(Optional.empty());
        when(usuarioRepo.save(any(Usuario.class))).thenReturn(usuario);

        int resultado = service.create(dto);

        assertEquals(0, resultado);
        verify(usuarioRepo).save(any(Usuario.class));
    }

    /**
     * Verifica que {@code create} retorna 1 cuando el nombre de usuario
     * ya está ocupado por otro usuario registrado.
     */
    @Test
    void testCreate_NombreYaOcupado() {
        when(usuarioRepo.existsByCorreo(anyString())).thenReturn(false);
        when(usuarioRepo.findByNombreUsuario("Pedro Ramirez")).thenReturn(Optional.of(usuario));

        int resultado = service.create(dto);

        assertEquals(1, resultado);
        verify(usuarioRepo, never()).save(any());
    }

    /**
     * Verifica que {@code create} lanza {@link CorreoInvalidoException} cuando
     * el correo ya está registrado en la base de datos.
     */
    @Test
    void testCreate_LanzaExcepcionCorreoDuplicado() {
        when(usuarioRepo.existsByCorreo(anyString())).thenReturn(true);

        assertThrows(CorreoInvalidoException.class, () -> service.create(dto));
    }

    /**
     * Verifica que {@code create} lanza {@link NombreInvalidoException} cuando
     * el nombre de usuario solo tiene una palabra (falta el apellido).
     */
    @Test
    void testCreate_LanzaExcepcionNombreSinApellido() {
        dto.setNombreUsuario("Pedro");

        assertThrows(NombreInvalidoException.class, () -> service.create(dto));
    }

    /**
     * Verifica que {@code create} lanza {@link PasswordNotValidException} cuando
     * la contraseña tiene menos de 8 caracteres.
     */
    @Test
    void testCreate_LanzaExcepcionContrasenaCorta() {
        dto.setContrasena("Ab1");

        assertThrows(PasswordNotValidException.class, () -> service.create(dto));
    }

    /**
     * Verifica que {@code create} lanza {@link PasswordNotValidException} cuando
     * la contraseña no contiene ninguna letra mayúscula.
     */
    @Test
    void testCreate_LanzaExcepcionContrasenaSinMayuscula() {
        dto.setContrasena("sinmayuscula1");

        assertThrows(PasswordNotValidException.class, () -> service.create(dto));
    }

    /**
     * Verifica que {@code create} lanza {@link PasswordNotValidException} cuando
     * la contraseña no contiene ningún dígito numérico.
     */
    @Test
    void testCreate_LanzaExcepcionContrasenaSinNumero() {
        dto.setContrasena("SinNumeroooo");

        assertThrows(PasswordNotValidException.class, () -> service.create(dto));
    }

    /**
     * Verifica que {@code create} lanza {@link CorreoInvalidoException} cuando
     * el correo no respeta el formato {@code ejemplo@dominio.com}.
     */
    @Test
    void testCreate_LanzaExcepcionCorreoMalFormato() {
        dto.setCorreo("correo_invalido");

        assertThrows(CorreoInvalidoException.class, () -> service.create(dto));
    }

    // ─── GET ALL ───────────────────────────────────────────────────────────────

    /**
     * Verifica que {@code getAll} retorna una lista con todos los usuarios
     * existentes, con el nombre de usuario correcto en el DTO.
     */
    @Test
    void testGetAll_ListaConElementos() {
        String correoEncriptado = co.edu.unbosque.detectia.util.AESUtil.encrypt("pedro@correo.com");
        usuario.setCorreo(correoEncriptado);

        when(usuarioRepo.findAll()).thenReturn(Arrays.asList(usuario));
        when(archivoRepo.countByUsuarioId(1L)).thenReturn(2L);

        List<UsuarioDTO> resultado = service.getAll();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Pedro Ramirez", resultado.get(0).getNombreUsuario());
    }

    /**
     * Verifica que {@code getAll} retorna una lista vacía cuando no hay
     * usuarios registrados en el sistema.
     */
    @Test
    void testGetAll_ListaVacia() {
        when(usuarioRepo.findAll()).thenReturn(Collections.emptyList());

        List<UsuarioDTO> resultado = service.getAll();

        assertTrue(resultado.isEmpty());
    }

    // ─── DELETE ────────────────────────────────────────────────────────────────

    /**
     * Verifica que {@code delateById} retorna 0 y elimina el usuario cuando
     * el ID proporcionado existe en la base de datos.
     */
    @Test
    void testDelateById_Exitoso() {
        when(usuarioRepo.existsById(1L)).thenReturn(true);
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuario));

        int resultado = service.delateById(1L);

        assertEquals(0, resultado);
        verify(usuarioRepo).delete(usuario);
    }

    /**
     * Verifica que {@code delateById} lanza {@link IdExistException} cuando
     * el ID no existe, sin llamar al repositorio para borrar.
     */
    @Test
    void testDelateById_IdNoExiste() {
        when(usuarioRepo.existsById(99L)).thenReturn(false);

        assertThrows(IdExistException.class, () -> service.delateById(99L));
        verify(usuarioRepo, never()).delete(any());
    }

    // ─── UPDATE ────────────────────────────────────────────────────────────────

    /**
     * Verifica que {@code updateById} retorna 0 y guarda los cambios
     * cuando el ID existe y los nuevos datos son válidos.
     */
    @Test
    void testUpdateById_Exitoso() {
        when(usuarioRepo.existsById(1L)).thenReturn(true);
        when(usuarioRepo.existsByCorreo(anyString())).thenReturn(false);
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepo.save(any(Usuario.class))).thenReturn(usuario);

        int resultado = service.updateById(1L, dto);

        assertEquals(0, resultado);
        verify(usuarioRepo).save(any(Usuario.class));
    }

    /**
     * Verifica que {@code updateById} lanza {@link IdExistException} cuando
     * el ID proporcionado no existe en la base de datos.
     */
    @Test
    void testUpdateById_IdNoExiste() {
        when(usuarioRepo.existsById(99L)).thenReturn(false);

        assertThrows(IdExistException.class, () -> service.updateById(99L, dto));
        verify(usuarioRepo, never()).save(any());
    }

    /**
     * Verifica que {@code updateById} lanza {@link CorreoInvalidoException}
     * cuando el nuevo correo ya está asociado a otro usuario.
     */
    @Test
    void testUpdateById_CorreoFormatoInvalido() {
        when(usuarioRepo.existsById(1L)).thenReturn(true);
        dto.setCorreo("correo_invalido");
        dto.setContrasena("Valida12");
        assertThrows(CorreoInvalidoException.class, () -> service.updateById(1L, dto));
    }

    /**
     * Verifica que {@code updateById} lanza {@link NombreInvalidoException}
     * cuando el nuevo nombre de usuario no cumple el formato requerido.
     */
    @Test
    void testUpdateById_NombreInvalido() {
        when(usuarioRepo.existsById(1L)).thenReturn(true);
        dto.setNombreUsuario("pedro");

        assertThrows(NombreInvalidoException.class, () -> service.updateById(1L, dto));
    }
}
