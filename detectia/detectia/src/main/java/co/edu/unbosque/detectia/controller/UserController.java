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

	

}
