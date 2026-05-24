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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
 * Pruebas unitarias para la clase {@link UsuarioService}.
 * <p>
 * Verifica la lógica de negocio del servicio de usuarios, incluyendo la
 * creación con cifrado AES del correo, validaciones de nombre, contraseña
 * y formato de correo, operaciones CRUD, y la recuperación del usuario
 * autenticado actualmente en sesión.
 * </p>
 *
 * @author Martín García
 * @version 1.0
 * @since 1.0
 * @see UsuarioService
 */
class UsuarioServiceTest {

    /**
     * Mock del repositorio de usuarios.
     */
    @Mock
    private UsuarioRepository usuarioRepo;

    /**
     * Mock del repositorio de archivos (para contar archivos por usuario).
     */
    @Mock
    private ArchivoRepository archivoRepo;

    /**
     * Mapper para la conversión entre entidades y DTOs.
     */
    private ModelMapper mapper;

    /**
     * Codificador de contraseñas BCrypt real (no mockeado, ya que es una utilidad pura).
     */
    private PasswordEncoder passwordEncoder;

    /**
     * Instancia del servicio bajo prueba.
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
     * Inicializa los mocks, inyecta las dependencias en el servicio y
     * prepara los objetos de prueba antes de cada caso.
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
        usuario.setNombreUsuario("Ana Lopez");
        usuario.setCorreo("ana@correo.com");
        usuario.setContrasena(passwordEncoder.encode("Segura12"));
        usuario.setRole(Role.USER);

        dto = new UsuarioDTO();
        dto.setNombreUsuario("Ana Lopez");
        dto.setCorreo("ana@correo.com");
        dto.setContrasena("Segura12");
        dto.setRole(Role.USER);
    }

    // ─── CREATE ────────────────────────────────────────────────────────────────

    /**
     * Verifica que la creación de un usuario nuevo retorna 0 cuando todos
     * los datos son válidos y el correo no está duplicado.
     */
    @Test
    void testCreate_Exitoso() {
        when(usuarioRepo.existsByCorreo(anyString())).thenReturn(false);
        when(usuarioRepo.findByNombreUsuario("Ana Lopez")).thenReturn(Optional.empty());
        when(usuarioRepo.save(any(Usuario.class))).thenReturn(usuario);

        int resultado = service.create(dto);

        assertEquals(0, resultado);
        verify(usuarioRepo).save(any(Usuario.class));
    }

    /**
     * Verifica que la creación retorna 1 cuando el nombre de usuario ya está
     * registrado en el sistema (sin importar si el correo es nuevo).
     */
    @Test
    void testCreate_NombreYaOcupado() {
        when(usuarioRepo.existsByCorreo(anyString())).thenReturn(false);
        when(usuarioRepo.findByNombreUsuario("Ana Lopez")).thenReturn(Optional.of(usuario));

        int resultado = service.create(dto);

        assertEquals(1, resultado);
        verify(usuarioRepo, never()).save(any());
    }

    /**
     * Verifica que se lanza {@link CorreoInvalidoException} cuando el correo
     * ya existe en la base de datos (el repositorio devuelve {@code true}).
     */
    @Test
    void testCreate_LanzaExcepcionCorreoDuplicado() {
        when(usuarioRepo.existsByCorreo(anyString())).thenReturn(true);

        assertThrows(CorreoInvalidoException.class, () -> service.create(dto));
    }

    /**
     * Verifica que se lanza {@link NombreInvalidoException} cuando el nombre
     * de usuario no contiene al menos dos palabras (nombre y apellido).
     */
    @Test
    void testCreate_LanzaExcepcionNombreSinApellido() {
        dto.setNombreUsuario("Ana");

        assertThrows(NombreInvalidoException.class, () -> service.create(dto));
    }

    /**
     * Verifica que se lanza {@link PasswordNotValidException} cuando la
     * contraseña tiene menos de 8 caracteres.
     */
    @Test
    void testCreate_LanzaExcepcionContrasenaCorta() {
        dto.setContrasena("Ab1");

        assertThrows(PasswordNotValidException.class, () -> service.create(dto));
    }

    /**
     * Verifica que se lanza {@link PasswordNotValidException} cuando la
     * contraseña no contiene ninguna letra mayúscula.
     */
    @Test
    void testCreate_LanzaExcepcionContrasenaSinMayuscula() {
        dto.setContrasena("sinmayuscula1");

        assertThrows(PasswordNotValidException.class, () -> service.create(dto));
    }

    /**
     * Verifica que se lanza {@link PasswordNotValidException} cuando la
     * contraseña no contiene ningún dígito numérico.
     */
    @Test
    void testCreate_LanzaExcepcionContrasenaSinNumero() {
        dto.setContrasena("SinNumerooo");

        assertThrows(PasswordNotValidException.class, () -> service.create(dto));
    }

    /**
     * Verifica que se lanza {@link CorreoInvalidoException} cuando el correo
     * no tiene un formato válido (ej. sin el símbolo {@code @}).
     */
    @Test
    void testCreate_LanzaExcepcionCorreoMalFormato() {
        dto.setCorreo("correo_invalido");

        assertThrows(CorreoInvalidoException.class, () -> service.create(dto));
    }

    // ─── GET ALL ───────────────────────────────────────────────────────────────

    /**
     * Verifica que {@code getAll} retorna una lista no vacía y que el correo
     * del usuario es desencriptado correctamente antes de devolver el DTO.
     */
    @Test
    void testGetAll_ListaConElementos() {
        // El correo en BD está encriptado con AES
        String correoEncriptado = co.edu.unbosque.detectia.util.AESUtil.encrypt("ana@correo.com");
        usuario.setCorreo(correoEncriptado);

        when(usuarioRepo.findAll()).thenReturn(Arrays.asList(usuario));
        when(archivoRepo.countByUsuarioId(1L)).thenReturn(3L);

        List<UsuarioDTO> resultado = service.getAll();

        assertFalse(resultado.isEmpty());
        assertEquals("ana@correo.com", resultado.get(0).getCorreo());
        assertEquals(3L, resultado.get(0).getTotalArchivos());
    }

    /**
     * Verifica que {@code getAll} retorna una lista vacía cuando no existen
     * usuarios registrados en el sistema.
     */
    @Test
    void testGetAll_ListaVacia() {
        when(usuarioRepo.findAll()).thenReturn(Collections.emptyList());

        List<UsuarioDTO> resultado = service.getAll();

        assertTrue(resultado.isEmpty());
    }

    // ─── GET LOGIN USER ────────────────────────────────────────────────────────

    /**
     * Verifica que {@code getLoginUser} retorna el DTO del usuario con el correo
     * desencriptado cuando el nombre de usuario existe.
     */
    @Test
    void testGetLoginUser_UsuarioExiste() {
        String correoEncriptado = co.edu.unbosque.detectia.util.AESUtil.encrypt("ana@correo.com");
        usuario.setCorreo(correoEncriptado);

        when(usuarioRepo.findByNombreUsuario("Ana Lopez")).thenReturn(Optional.of(usuario));
        when(archivoRepo.countByUsuarioId(1L)).thenReturn(5L);

        UsuarioDTO resultado = service.getLoginUser("Ana Lopez");

        assertNotNull(resultado);
        assertEquals("Ana Lopez", resultado.getNombreUsuario());
        assertEquals("ana@correo.com", resultado.getCorreo());
        assertEquals(5L, resultado.getTotalArchivos());
    }

    /**
     * Verifica que {@code getLoginUser} lanza {@link UsernameNotFoundException}
     * cuando el nombre de usuario no existe en el sistema.
     */
    @Test
    void testGetLoginUser_UsuarioNoExiste() {
        when(usuarioRepo.findByNombreUsuario("Fantasma")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.getLoginUser("Fantasma"));
    }

    // ─── DELETE ────────────────────────────────────────────────────────────────

    /**
     * Verifica que la eliminación de un usuario existente retorna 0 e invoca
     * el repositorio para borrar la entidad.
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
     * Verifica que la eliminación lanza {@link IdExistException} cuando el
     * ID proporcionado no corresponde a ningún usuario registrado.
     */
    @Test
    void testDelateById_IdNoExiste() {
        when(usuarioRepo.existsById(99L)).thenReturn(false);

        assertThrows(IdExistException.class, () -> service.delateById(99L));
        verify(usuarioRepo, never()).delete(any());
    }

    // ─── UPDATE ────────────────────────────────────────────────────────────────

    /**
     * Verifica que la actualización de un usuario existente retorna 0 y
     * persiste los cambios en el repositorio.
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
     * Verifica que la actualización lanza {@link IdExistException} cuando el
     * ID proporcionado no existe en la base de datos.
     */
    @Test
    void testUpdateById_IdNoExiste() {
        when(usuarioRepo.existsById(99L)).thenReturn(false);

        assertThrows(IdExistException.class, () -> service.updateById(99L, dto));
        verify(usuarioRepo, never()).save(any());
    }

    /**
     * Verifica que la actualización lanza {@link CorreoInvalidoException}
     * cuando el nuevo correo ya está registrado por otro usuario.
     */
    @Test
    void testUpdateById_CorreoDuplicado() {
        when(usuarioRepo.existsById(1L)).thenReturn(true);
        when(usuarioRepo.existsByCorreo(anyString())).thenReturn(true);

        assertThrows(CorreoInvalidoException.class, () -> service.updateById(1L, dto));
    }

    // ─── UPDATE LOGIN PASSWORD ─────────────────────────────────────────────────

    /**
     * Verifica que la actualización de contraseña por correo retorna 0 cuando
     * el correo existe y la nueva contraseña es válida.
     */
    @Test
    void testUpdateLoginPassword_Exitoso() {
        String correoEncriptado = co.edu.unbosque.detectia.util.AESUtil.encrypt("ana@correo.com");
        when(usuarioRepo.findByCorreo(correoEncriptado)).thenReturn(Optional.of(usuario));
        when(usuarioRepo.save(any(Usuario.class))).thenReturn(usuario);

        int resultado = service.updateLoginPassword("ana@correo.com", "NuevaClave9");

        assertEquals(0, resultado);
        verify(usuarioRepo).save(any(Usuario.class));
    }

    /**
     * Verifica que se lanza {@link PasswordNotValidException} cuando la
     * nueva contraseña está vacía.
     */
    @Test
    void testUpdateLoginPassword_ContrasenaVacia() {
        assertThrows(PasswordNotValidException.class,
                () -> service.updateLoginPassword("ana@correo.com", ""));
    }

    /**
     * Verifica que se lanza {@link IdExistException} cuando el correo
     * proporcionado no corresponde a ningún usuario registrado.
     */
    @Test
    void testUpdateLoginPassword_CorreoNoExiste() {
        when(usuarioRepo.findByCorreo(anyString())).thenReturn(Optional.empty());

        assertThrows(IdExistException.class,
                () -> service.updateLoginPassword("noexiste@correo.com", "NuevaClave9"));
    }

    // ─── FIND USERNAME TAKEN ───────────────────────────────────────────────────

    /**
     * Verifica que {@code findUsernameAlreadyTaken} retorna {@code true}
     * cuando el nombre de usuario ya está registrado en la base de datos.
     */
    @Test
    void testFindUsernameAlreadyTaken_Tomado() {
        when(usuarioRepo.findByNombreUsuario("Ana Lopez")).thenReturn(Optional.of(usuario));

        assertTrue(service.findUsernameAlreadyTaken("Ana Lopez"));
    }

    /**
     * Verifica que {@code findUsernameAlreadyTaken} retorna {@code false}
     * cuando el nombre de usuario no existe en la base de datos.
     */
    @Test
    void testFindUsernameAlreadyTaken_Libre() {
        when(usuarioRepo.findByNombreUsuario("Nuevo Usuario")).thenReturn(Optional.empty());

        assertFalse(service.findUsernameAlreadyTaken("Nuevo Usuario"));
    }
}
