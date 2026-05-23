package co.edu.unbosque.detectia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.detectia.dto.UsuarioDTO;
import co.edu.unbosque.detectia.exception.IdExistException;
import co.edu.unbosque.detectia.exception.PasswordNotValidException;
import co.edu.unbosque.detectia.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/private/user")
@CrossOrigin(origins = { "http://localhost:8080", "*" })
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Usuario autenticado", description = "Endpoints para que el usuario logueado consulte y actualize dus propios datos")
public class UserController {

	@Autowired
	private UsuarioService usuarioSer;

	@Operation(summary = "Ver mis datos", description = """
			Devuelve los datos del usuario actualmente autenticado.

			**Nota:** Requiere token JWT valido.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Datos del usuario encontrados", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioDTO.class), examples = @ExampleObject(value = """
						{
						  "id": 1,
						  "nombreUsuario": "juanperez",
						  "correo": "juan@email.com",
						  "role": "USUARIO",
						  "totalArchivos": 3
						}
					"""))) })

	@GetMapping("/misdatos")
	public ResponseEntity<UsuarioDTO> datosUsuario(Authentication authentication) {
		String correoLogueado = authentication.getName();

		UsuarioDTO dto = usuarioSer.getLoginUser(correoLogueado);
		return ResponseEntity.ok(dto);
	}

	@Operation(summary = "Actualizar mi contrasena", description = """
			Permite al usuario autenticado actualizar su propia contrasena.

			**Posibles resultados:**
			* Contrasena actualizada correctamente
			* Contrasena invalida (no cumple los requisitos)
			* Usuario no encontrado

			**Nota:** Requiere token JWT valido.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Contrasena actualizada correctamente", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Contraseña actualizada correctamente"))),
			@ApiResponse(responseCode = "400", description = "Contrasena invalida", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "La contrasena debe tener minimo 8 caracteres"))),
			@ApiResponse(responseCode = "204", description = "Usuario no encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "El usuario no existe"))) })

	@PutMapping("/actualizarcontraseña")
	public ResponseEntity<String> actualizarContrasenia(Authentication authentication,
			@RequestParam String contrasena) {
		try {
			String correoLogueado = authentication.getName();
			usuarioSer.updateLoginPassword(correoLogueado, contrasena);
			return new ResponseEntity<>("Contraseña actulizada correctamente", HttpStatus.ACCEPTED);
		} catch (PasswordNotValidException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

		} catch (IdExistException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);
		}
	}

}
