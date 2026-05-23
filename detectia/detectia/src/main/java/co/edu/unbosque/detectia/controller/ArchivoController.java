package co.edu.unbosque.detectia.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.detectia.dto.ArchivoDTO;
import co.edu.unbosque.detectia.dto.UsuarioDTO;
import co.edu.unbosque.detectia.entity.Archivo;
import co.edu.unbosque.detectia.exception.ExtensionInvalidaException;
import co.edu.unbosque.detectia.exception.IdExistException;
import co.edu.unbosque.detectia.exception.TamanoInvalidoException;
import co.edu.unbosque.detectia.service.AnalisisService;
import co.edu.unbosque.detectia.service.ArchivoService;
import co.edu.unbosque.detectia.service.EleccionService;
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

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/private/archivo")
@CrossOrigin(origins = { "http://localhost:8080", "*" })
@Tag(name = "Archivos", description = "Endpoints para el analizis y gestion de archivos del usuario ")
public class ArchivoController {

	@Autowired
	private EleccionService eleccionSer;

	@Autowired
	private ArchivoService archivoSer;

	@Autowired
	private UsuarioService usuarioSer;

	@Autowired
	private ResultadoIAService resultadoIAser;

	@Autowired
	private AnalisisService analisisSer;

	@Operation(summary = "Analizar archivo", description = """
			Sube un archivo y lo analiza con multiples modelos de IA para detectar contenido generado artificialmente.
			Devuelve un resumen con los votos de cada modelo.

			**Formatos soportados:** imagenes, audio y video (segun configuracion del sistema).

			**Posibles resultados:**
			* Analisis completado con resumen de resultados
			* Extension de archivo no permitida
			* Archivo demasiado grande

			**Nota:** Requiere token JWT valido.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Analisis completado exitosamente", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
						{
						  "esIA": true,
						  "porcentajeIA": 87.5,
						  "votos": {
						    "Gemini": 0.92,
						    "Mistral": 0.83,
						    "Grok": 0.85
						  }
						}
					"""))),
			@ApiResponse(responseCode = "400", description = "Extension o tamano invalido", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Extension de archivo no permitida"))) })

	@PostMapping(value = "/analizar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> analizar(@RequestParam String nombre, @RequestParam MultipartFile archivo,
			Authentication authentication) throws Exception {

		try {
			String username = authentication.getName();
			UsuarioDTO usuario = usuarioSer.getLoginUser(username);

			Map<String, Double> votosIAs = eleccionSer.analizar(archivo);

			ArchivoDTO nuevo = new ArchivoDTO();
			nuevo.setRutaAlmacenamiento(archivo.getOriginalFilename());
			nuevo.setNombre(nombre);
			nuevo.setUsuarioId(usuario.getId());
			Archivo archivoGuardado = archivoSer.createAndReturn(nuevo);
			resultadoIAser.guardarResultados(votosIAs, archivoGuardado);
			return new ResponseEntity<>(analisisSer.calcularResumen(votosIAs, archivoGuardado), HttpStatus.CREATED);
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(summary = "Analizar texto plano", description = """
			Analiza un texto plano con multiples modelos de IA para detectar si fue generado artificialmente.
			Devuelve un resumen con los votos de cada modelo.

			**Posibles resultados:**
			* Analisis completado con resumen de resultados
			* Texto vacio
			* Usuario no encontrado

			**Nota:** Requiere token JWT valido.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Analisis completado exitosamente", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
					    {
					      "esIA": true,
					      "porcentajeIA": 91.3,
					      "votos": {
					        "Gemini": 0.95,
					        "Mistral": 0.88,
					        "Winston": 0.91
					      }
					    }
					"""))),
			@ApiResponse(responseCode = "400", description = "El texto no puede estar vacio", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "El texto no puede estar vacio"))),
			@ApiResponse(responseCode = "204", description = "Usuario no encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "El usuario no existe"))) })

	@PostMapping(value = "/analizartexto")
	public ResponseEntity<?> analizarTextoPlano(@RequestParam String nombre, @RequestParam String texto,
			Authentication authentication) {
		try {
			if (texto == null || texto.isBlank()) {
				return new ResponseEntity<>("El texto no puede estar vacio", HttpStatus.BAD_REQUEST);
			}
			String username = authentication.getName();
			UsuarioDTO usuario = usuarioSer.getLoginUser(username);

			Map<String, Double> votosIAs = eleccionSer.analizarTextoPlano(texto);

			ArchivoDTO nuevo = new ArchivoDTO();
			nuevo.setRutaAlmacenamiento("[TEXTO PLANO]");
			nuevo.setNombre(nombre);
			nuevo.setUsuarioId(usuario.getId());
			Archivo archivoGuardado = archivoSer.createAndReturn(nuevo);
			resultadoIAser.guardarResultados(votosIAs, archivoGuardado);
			return new ResponseEntity<>(analisisSer.calcularResumen(votosIAs, archivoGuardado), HttpStatus.CREATED);
		} catch (IdExistException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	@Operation(summary = "Analizar archivo por URL", description = """
			Analiza un archivo a partir de una URL publica con multiples modelos de IA.
			Devuelve un resumen con los votos de cada modelo.

			**Posibles resultados:**
			* Analisis completado con resumen de resultados
			* Extension de archivo en la URL no permitida
			* Archivo demasiado grande

			**Nota:** Requiere token JWT valido.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Analisis completado exitosamente", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
						{
						  "esIA": false,
						  "porcentajeIA": 12.3,
						  "votos": {
						    "Gemini": 0.10,
						    "Mistral": 0.15,
						    "Grok": 0.12
						  }
						}
					"""))),
			@ApiResponse(responseCode = "400", description = "Extension o tamano invalido", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Extension de archivo no permitida"))) })

	@PostMapping("/analizarimagenurl")
	public ResponseEntity<?> analizarImagenURL(@RequestParam String nombre, @RequestParam String url,
			Authentication authentication) throws Exception {

		try {
			String username = authentication.getName();
			UsuarioDTO usuario = usuarioSer.getLoginUser(username);

			Map<String, Double> votosIAs = eleccionSer.analizar(url);

			ArchivoDTO nuevo = new ArchivoDTO();
			nuevo.setRutaAlmacenamiento(url);
			nuevo.setNombre(nombre);
			nuevo.setUsuarioId(usuario.getId());
			Archivo archivoGuardado = archivoSer.createAndReturn(nuevo);
			resultadoIAser.guardarResultados(votosIAs, archivoGuardado);
			return new ResponseEntity<>(analisisSer.calcularResumen(votosIAs, archivoGuardado), HttpStatus.CREATED);
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}

	}

	@Operation(summary = "Ver mis archivos", description = """
			Devuelve la lista de archivos subidos por el usuario actualmente autenticado.

			**Posibles resultados:**
			* Lista de archivos del usuario
			* Lista vacia si no ha subido archivos

			**Nota:** Requiere token JWT valido.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Lista de archivos encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArchivoDTO.class), examples = @ExampleObject(value = """
						[
						  {
						    "id": 1,
						    "nombre": "foto_perfil",
						    "rutaAlmacenamiento": "foto.jpg",
						    "fechaSubida": "2025-05-15T10:30:00",
						    "usuarioId": 2,
						    "username": "juanperez"
						  }
						]
					"""))),
			@ApiResponse(responseCode = "404", description = "El usuario no tiene archivos", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "[]"))) })

	@GetMapping("/mis-archivos")
	public ResponseEntity<List<ArchivoDTO>> misArchivos(Authentication authentication) {
		String user = authentication.getName();
		List<ArchivoDTO> archivos = archivoSer.getArchivosByuser(user);

		if (archivos.isEmpty()) {
			return new ResponseEntity<>(archivos, HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(archivos, HttpStatus.OK);
		}
	}

	@Operation(summary = "Buscar archivo por nombre", description = """
			Busca archivos del usuario autenticado por nombre.

			**Posibles resultados:**
			* Lista de archivos que coinciden con el nombre
			* Sin resultados si no hay coincidencias

			**Nota:** Requiere token JWT valido.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Archivos encontrados", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArchivoDTO.class))),
			@ApiResponse(responseCode = "204", description = "No se encontraron archivos con ese nombre") })

	@GetMapping("/buscarpornombre")
	public ResponseEntity<List<ArchivoDTO>> buscarPorNombre(@RequestParam String nombreArchivo,
			Authentication authentication) {
		String user = authentication.getName();
		List<ArchivoDTO> archivos = archivoSer.findArchivoByNombre(nombreArchivo, user);
		if (archivos == null) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(archivos);
	}

	@Operation(summary = "Eliminar archivo", description = """
			Elimina un archivo del sistema por su ID.

			**Advertencia:** Esta accion no se puede deshacer.

			**Posibles resultados:**
			* Archivo eliminado correctamente
			* Archivo no encontrado

			**Nota:** Requiere token JWT valido.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Archivo eliminado correctamente", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Archivo eliminado correctamente"))),
			@ApiResponse(responseCode = "404", description = "Archivo no existe", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Archivo no existe"))) })

	@DeleteMapping("/eliminar")
	public ResponseEntity<String> eliminarArchivo(@RequestParam Long id) {
		int status = archivoSer.delateById(id);
		if (status == 0) {
			return new ResponseEntity<>("Archivo eliminado correctamente", HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>("Archivo no existe", HttpStatus.NOT_FOUND);
		}
	}

}
