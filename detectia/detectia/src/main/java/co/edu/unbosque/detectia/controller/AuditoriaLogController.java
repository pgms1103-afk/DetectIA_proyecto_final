package co.edu.unbosque.detectia.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.detectia.entity.AuditoriaLog;
import co.edu.unbosque.detectia.service.AuditoriaLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para la consulta de registros de auditoría del sistema.
 * <p>
 * Todas las rutas tienen el prefijo {@code /admin/auditoria} y son exclusivas
 * para usuarios con rol {@code ADMIN}. Expone endpoints de listado completo y
 * filtrado por correo, acción, módulo y resultado de la operación.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 * @see co.edu.unbosque.detectia.service.AuditoriaLogService
 */
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/auditoria")
@CrossOrigin(origins = { "http://localhost:8080", "*" })
@Tag(name = "Auditorias", description = "Enpoints para consultar los registros de auditorias del sistema. Solo accesibles para ADMIN")
public class AuditoriaLogController {

	@Autowired
	private AuditoriaLogService auditoriaLogSer;

	@Operation(summary = "Ver todos los registros de auditoria", description = """
			Devuelve todos los registros de auditoria del sistema.

			**Nota:** Requiere token JWT con rol ADMIN.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Lista de registros de auditoria", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
						[
						  {
						    "id": 1,
						    "correo": "juan@email.com",
						    "nombreUsuario": "juanperez",
						    "accion": "LOGIN",
						    "modulo": "AUTENTICACION",
						    "descripcion": "Inicio de sesion exitoso",
						    "exitoso": true
						  }
						]
					"""))),
			@ApiResponse(responseCode = "204", description = "No hay registros de auditoria") })

	@GetMapping("/todos")
	public ResponseEntity<List<AuditoriaLog>> getTodos() {
		List<AuditoriaLog> lista = auditoriaLogSer.getAll();
		if (lista.isEmpty()) {
			return new ResponseEntity<>(lista, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(lista, HttpStatus.OK);
	}

	@Operation(summary = "Filtrar auditoria por correo", description = """
			Devuelve los registros de auditoria de un correo especifico.

			**Nota:** Requiere token JWT con rol ADMIN.
			""")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Registros encontrados"),
			@ApiResponse(responseCode = "204", description = "No hay registros para ese correo") })

	@GetMapping("/porcorreo")
	public ResponseEntity<List<AuditoriaLog>> getPorCorreo(@RequestParam String correo) {
		List<AuditoriaLog> lista = auditoriaLogSer.getByCorreo(correo);
		if (lista.isEmpty()) {
			return new ResponseEntity<>(lista, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(lista, HttpStatus.OK);
	}

	@Operation(summary = "Filtrar auditoria por accion", description = """
			Devuelve los registros de auditoria de una accion especifica.

			**Acciones posibles:** LOGIN, REGISTRO, ANALISIS, ELIMINACION

			**Nota:** Requiere token JWT con rol ADMIN.
			""")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Registros encontrados"),
			@ApiResponse(responseCode = "204", description = "No hay registros para esa accion") })

	@GetMapping("/poraccion")
	public ResponseEntity<List<AuditoriaLog>> getPorAccion(@RequestParam String accion) {
		List<AuditoriaLog> lista = auditoriaLogSer.getByAccion(accion);
		if (lista.isEmpty()) {
			return new ResponseEntity<>(lista, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(lista, HttpStatus.OK);
	}

	@Operation(summary = "Filtrar auditoria por modulo", description = """
			Devuelve los registros de auditoria de un modulo especifico.

			**Modulos posibles:** AUTENTICACION, ARCHIVOS, USUARIOS

			**Nota:** Requiere token JWT con rol ADMIN.
			""")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Registros encontrados"),
			@ApiResponse(responseCode = "204", description = "No hay registros para ese modulo") })

	@GetMapping("/pormodulo")
	public ResponseEntity<List<AuditoriaLog>> getPorModulo(@RequestParam String modulo) {
		List<AuditoriaLog> lista = auditoriaLogSer.getByModulo(modulo);
		if (lista.isEmpty()) {
			return new ResponseEntity<>(lista, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(lista, HttpStatus.OK);
	}

	@Operation(summary = "Filtrar auditoria por resultado", description = """
			Devuelve los registros de auditoria segun si la accion fue exitosa o no.

			**Nota:** Requiere token JWT con rol ADMIN.
			""")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Registros encontrados"),
			@ApiResponse(responseCode = "204", description = "No hay registros para ese filtro") })

	@GetMapping("/porexitoso")
	public ResponseEntity<List<AuditoriaLog>> getPorExitoso(@RequestParam boolean exitoso) {
		List<AuditoriaLog> lista = auditoriaLogSer.getByExitoso(exitoso);
		if (lista.isEmpty()) {
			return new ResponseEntity<>(lista, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(lista, HttpStatus.OK);
	}

}
