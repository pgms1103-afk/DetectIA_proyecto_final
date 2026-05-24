package co.edu.unbosque.detectia.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.detectia.dto.ArchivoDTO;
import co.edu.unbosque.detectia.dto.ResultadoIADTO;
import co.edu.unbosque.detectia.dto.UsuarioDTO;
import co.edu.unbosque.detectia.exception.CorreoInvalidoException;
import co.edu.unbosque.detectia.exception.IdExistException;
import co.edu.unbosque.detectia.exception.NombreInvalidoException;
import co.edu.unbosque.detectia.exception.PasswordNotValidException;
import co.edu.unbosque.detectia.service.ArchivoService;
import co.edu.unbosque.detectia.service.ResultadoIAService;
import co.edu.unbosque.detectia.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST de administración para la gestión de usuarios y recursos del sistema.
 * <p>
 * Todas las rutas tienen el prefijo {@code /admin} y solo son accesibles para
 * usuarios con rol {@code ADMIN}. Expone operaciones CRUD completas sobre
 * usuarios, listado de archivos y resultados de IA para la vista del panel de
 * administración.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 * @see co.edu.unbosque.detectia.service.UsuarioService
 * @see co.edu.unbosque.detectia.service.ArchivoService
 * @see co.edu.unbosque.detectia.service.ResultadoIAService
 */
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin") // Tiene todas las rutas privadas
@CrossOrigin(origins = { "http://localhost:8080", "*" })
@Tag(name = "Administrador-usuarios", description = "Endpoints de gestion de usuarios. Solo accesible con rol ADMIN")
public class AdminController {

	@Autowired
	private UsuarioService usuarioSer;

	@Autowired
	private ArchivoService archivoSer;

	@Autowired
	private ResultadoIAService resultadoIASer;
	@Operation(summary = "Crear usuario", description = """
			Crea un nuevo usuario con cualquier rol. Solo accesible por administradores.

			**Posibles resultados:**
			* Usuario creado con exito
			* Datos invalidos (nombre, correo o contrasena incorrectos)

			**Nota:** Requiere token JWT con rol ADMIN.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Usuario creado con exito", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Usuario creado con exito"))),
			@ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "El correo no tiene un formato valido"))) })

	@PostMapping("/crearusuario")
	public ResponseEntity<String> crearUsuario(@RequestBody UsuarioDTO dto) {

		try {
			UsuarioDTO nuevo = new UsuarioDTO();
			nuevo.setNombreUsuario(dto.getNombreUsuario());
			nuevo.setCorreo(dto.getCorreo());
			nuevo.setContrasena(dto.getContrasena());
			nuevo.setRole(dto.getRole());
			usuarioSer.create(nuevo);

			return new ResponseEntity<>("Usuario creado con éxito", HttpStatus.CREATED);
		} catch (NombreInvalidoException | PasswordNotValidException | CorreoInvalidoException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(summary = "Mostrar todos los usuarios", description = """
			Devuelve la lista completa de usuarios registrados en el sistema.

			**Posibles resultados:**
			* Lista de usuarios
			* Lista vacia si no hay usuarios registrados

			**Nota:** Requiere token JWT con rol ADMIN.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Lista de usuarios", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioDTO.class), examples = @ExampleObject(value = """
						[
						  {
						    "id": 1,
						    "nombreUsuario": "admin",
						    "correo": "admin@email.com",
						    "role": "ADMIN",
						    "totalArchivos": 0
						  },
						  {
						    "id": 2,
						    "nombreUsuario": "juanperez",
						    "correo": "juan@email.com",
						    "role": "USUARIO",
						    "totalArchivos": 5
						  }
						]
					"""))),
			@ApiResponse(responseCode = "204", description = "No hay usuarios registrados", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "[]"))) })

	@GetMapping("/mostrarusuarios")
	public ResponseEntity<List<UsuarioDTO>> mostrarUsuarios() {
		List<UsuarioDTO> usuarios = usuarioSer.getAll();
		if (usuarios.isEmpty()) {
			return new ResponseEntity<List<UsuarioDTO>>(usuarios, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<List<UsuarioDTO>>(usuarios, HttpStatus.ACCEPTED);
		}
	}

	@Operation(summary = "Actualizar usuario", description = """
			Actualiza los datos de un usuario existente por su ID.

			**Posibles resultados:**
			* Usuario actualizado correctamente
			* Datos invalidos (nombre, correo o contrasena incorrectos)

			**Nota:** Requiere token JWT con rol ADMIN.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Usuario actualizado correctamente", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Usuario actualizado correctamente"))),
			@ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "El nombre de usuario no puede contener espacios"))) })

	@PutMapping("/actualizarusuarios")
	public ResponseEntity<String> actualizarUsuarios(@RequestParam Long id, @RequestBody UsuarioDTO newUser) {
		try {
			usuarioSer.updateById(id, newUser);
			return new ResponseEntity<>("Usuario actualizado correctamente", HttpStatus.ACCEPTED);
		} catch (NombreInvalidoException | PasswordNotValidException | CorreoInvalidoException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(summary = "Eliminar usuario", description = """
			Elimina un usuario del sistema por su ID.

			**Advertencia:** Esta accion no se puede deshacer.

			**Posibles resultados:**
			* Usuario eliminado correctamente
			* Usuario no encontrado

			**Nota:** Requiere token JWT con rol ADMIN.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Usuario eliminado correctamente", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Usuario eliminado correctamente"))),
			@ApiResponse(responseCode = "204", description = "Usuario no encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "El usuario con ese id no existe"))) })

	@DeleteMapping("/eliminarusuarios")
	public ResponseEntity<String> eliminarUsuarios(@RequestParam Long id) {

		try {
			usuarioSer.delateById(id);
			return new ResponseEntity<>("Usuario eliminado correctamente", HttpStatus.ACCEPTED);
		} catch (IdExistException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);
		}
	}

	@GetMapping("/archivos")
	public ResponseEntity<List<ArchivoDTO>> mostrarArchivos() {
		List<ArchivoDTO> archivos = archivoSer.getAll();
		if (archivos.isEmpty()) {
			return new ResponseEntity<>(archivos, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(archivos, HttpStatus.OK);
	}

	@GetMapping("/resultados")
	public ResponseEntity<List<ResultadoIADTO>> mostrarResultados() {
		List<ResultadoIADTO> resultados = resultadoIASer.getAll();
		if (resultados.isEmpty()) {
			return new ResponseEntity<>(resultados, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(resultados, HttpStatus.OK);
	}

}
