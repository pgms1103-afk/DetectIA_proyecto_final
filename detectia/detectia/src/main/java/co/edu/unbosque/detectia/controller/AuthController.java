package co.edu.unbosque.detectia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.detectia.dto.UsuarioDTO;
import co.edu.unbosque.detectia.entity.Usuario;
import co.edu.unbosque.detectia.exception.CorreoInvalidoException;
import co.edu.unbosque.detectia.exception.NombreInvalidoException;
import co.edu.unbosque.detectia.exception.PasswordNotValidException;
import co.edu.unbosque.detectia.security.JwtUtil;
import co.edu.unbosque.detectia.service.AuditoriaLogService;
import co.edu.unbosque.detectia.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador REST que expone los endpoints públicos de autenticación.
 * <p>
 * Gestiona el registro de nuevos usuarios y el inicio de sesión mediante JWT.
 * Todas las rutas de este controlador tienen el prefijo {@code /public} y no
 * requieren token de autorización.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 * @see co.edu.unbosque.detectia.service.UsuarioService
 * @see co.edu.unbosque.detectia.security.JwtUtil
 */
@RestController
@RequestMapping("/public")
@CrossOrigin(origins = { "http://localhost:8080", "*" })
@Tag(name = "Autenticacion", description = "Endpoints publicos de registro e inicio de sesion")
public class AuthController {

	@Autowired
	private UsuarioService usuarioSer;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private AuditoriaLogService auditoriaLogSer;

	@Operation(summary = "Registro de usuario", description = """
			Registra un nuevo usuario en el sistema con rol USUARIO por defecto.

			**Posibles resultados:**
			* Usuario registrado exitosamente
			* El nombre de usuario ya existe
			* Datos invalidos (nombre, correo o contrasena incorrectos)
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Usuario registrado con exito", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Usuario registrado con exito"))),
			@ApiResponse(responseCode = "409", description = "El nombre de usuario ya existe", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "El nombre de usuario ya existe"))),
			@ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "El nombre de usuario debe tener minimo 4 caracteres"))) })

	@PostMapping("/registrarusuario")
	public ResponseEntity<String> registrarUsuario(@RequestBody UsuarioDTO dto, HttpServletRequest request) {
		try {
			if (usuarioSer.findUsernameAlreadyTaken(dto.getNombreUsuario())) {

				auditoriaLogSer.registrarAuditoria(dto.getCorreo(), dto.getNombreUsuario(), "REGISTRO", "AUTENTICACION",
						"Intento de registro fallido: nombre de usuario ya existe", request.getRemoteAddr(),
						request.getHeader("User-Agent"), null, null, "Usuario", null, false);

				return ResponseEntity.status(HttpStatus.CONFLICT).body("El nombre de usuario ya existe");
			}

			UsuarioDTO nuevo = new UsuarioDTO();
			nuevo.setNombreUsuario(dto.getNombreUsuario());
			nuevo.setCorreo(dto.getCorreo());
			nuevo.setContrasena(dto.getContrasena());
			usuarioSer.create(nuevo);

			auditoriaLogSer.registrarAuditoria(dto.getCorreo(), dto.getNombreUsuario(), "REGISTRO", "AUTENTICACION",
					"Usuario registrado exitosamente", request.getRemoteAddr(), request.getHeader("User-Agent"), null,
					dto.getNombreUsuario(), "Usuario", null, true);

			return new ResponseEntity<>("Usuario registrado con éxito", HttpStatus.CREATED);
		} catch (NombreInvalidoException | PasswordNotValidException | CorreoInvalidoException e) {

			auditoriaLogSer.registrarAuditoria(dto.getCorreo(), dto.getNombreUsuario(), "REGISTRO", "AUTENTICACION",
					"Registro fallido: " + e.getMessage(), request.getRemoteAddr(), request.getHeader("User-Agent"),
					null, null, "Usuario", null, false);

			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(summary = "Iniciar sesión de usuario", description = """
			    Este endpoint permite a los usuarios iniciar sesión en el sistema proporcionando sus credenciales.

			    **¿Qué hace?** Verifica las credenciales del usuario y, si son correctas, genera un token JWT
			    que se utilizará para autenticar solicitudes posteriores.

			    **Paso a paso:**

			    1. Envía tu nombre de usuario y contraseña en formato JSON
			    2. Si las credenciales son correctas, recibirás un token JWT
			    3. Guarda este token para usarlo en futuras peticiones
			    4. Para usar el token, inclúyelo en el encabezado de autorización: `Authorization: Bearer tu_token_jwt`

			    **Nota:** El token tiene un tiempo de expiración limitado. Si expira, necesitarás iniciar sesión nuevamente.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class), examples = @ExampleObject(value = """
					    {
					      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
					      "role": "ADMIN"
					    }
					"""))),
			@ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Nombre de usuario o contraseña inválidos o usuario no"
					+ " encontrado"))) })

	@PostMapping("/login")
	public ResponseEntity<Object> login(@RequestBody UsuarioDTO dto, HttpServletRequest request) {
		try {
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(dto.getNombreUsuario(), dto.getContrasena()));

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String jwt = jwtUtil.generateToken(userDetails);

			String role = null;
			String correoUsuario = null;
			if (userDetails instanceof Usuario) {
			    Usuario user = (Usuario) userDetails;
			    role = user.getRole().name();
			    correoUsuario = user.getCorreo();
			}

			auditoriaLogSer.registrarAuditoria(correoUsuario, dto.getNombreUsuario(), "LOGIN", "AUTENTICACION",
				    "Inicio de sesión exitoso con rol: " + role, request.getRemoteAddr(),
				    request.getHeader("User-Agent"), null, null, "Sesion", null, true);

			return ResponseEntity.ok(new AuthResponse(jwt, role));
		} catch (AuthenticationException e) {

			auditoriaLogSer.registrarAuditoria(null, dto.getNombreUsuario(), "LOGIN", "AUTENTICACION",
					"Intento de login fallido: credenciales inválidas", request.getRemoteAddr(),
					request.getHeader("User-Agent"), null, null, "Sesion", null, false);

			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Nombre de usuario o contraseña inválidos o usuario no encontrado");
		}
	}

	/**
	 * Clase interna para representar la respuesta de autenticación. Contiene el
	 * token JWT y el rol del usuario autenticado.
	 */
	private static class AuthResponse {
		/** Token JWT generado para el usuario autenticado. */
		private final String token;

		/** Rol del usuario autenticado. */
		private final String role;

		/**
		 * Constructor con solo token.
		 *
		 * @param token Token JWT generado
		 */
		public AuthResponse(String token) {
			this.token = token;
			// Extraer rol del token
			this.role = null; // Se establecerá en el constructor con el parámetro de rol
		}

		/**
		 * Constructor con token y rol.
		 *
		 * @param token Token JWT generado
		 * @param role  Rol del usuario
		 */
		public AuthResponse(String token, String role) {
			this.token = token;
			this.role = role;
		}

		/**
		 * Obtiene el token JWT.
		 *
		 * @return Token JWT
		 */
		public String getToken() {
			return token;
		}

		/**
		 * Obtiene el rol del usuario.
		 *
		 * @return Rol del usuario
		 */
		public String getRole() {
			return role;
		}
	}

}
