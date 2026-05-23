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

import co.edu.unbosque.detectia.dto.AnalisisDTO;
import co.edu.unbosque.detectia.dto.ResultadoIADTO;
import co.edu.unbosque.detectia.service.AnalisisService;
import co.edu.unbosque.detectia.service.ResultadoIAService;
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
@RequestMapping("/private/resultadoporia")
@CrossOrigin(origins = { "http://localhost:8080", "*" })
@Tag(name = "Resultados-IA", description = "Enpoinys publicos para observar los resultados de analizis de las IAS por cada archivo")
public class ResultadoIAController {

	@Autowired
	private ResultadoIAService resultadoSer;

	@Autowired
	private AnalisisService analisisSer;

	@Operation(summary = "Ver resultados de IA por archivo", description = """
			Devuelve los resultados individuales de cada modelo de IA para un archivo especifico.

			**Posibles resultados:**
			* Lista con el resultado de cada modelo de IA
			* Sin contenido si el archivo no tiene resultados

			**Nota:** Requiere token JWT valido.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Resultados encontrados", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResultadoIADTO.class), examples = @ExampleObject(value = """
						[
						  {
						    "id": 1,
						    "nombreModelo": "Gemini",
						    "probabilidadIA": 0.92,
						    "archivoId": 1
						  },
						  {
						    "id": 2,
						    "nombreModelo": "Mistral",
						    "probabilidadIA": 0.83,
						    "archivoId": 1
						  }
						]
					"""))),
			@ApiResponse(responseCode = "204", description = "No hay resultados para ese archivo") })

	@GetMapping("mostrarresultadoporarchivo")
	public ResponseEntity<List<ResultadoIADTO>> mostrarResultadosPorArchivo(@RequestParam long id) {

		List<ResultadoIADTO> resultados = resultadoSer.getResultadosByArchivoId(id);

		if (resultados.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return new ResponseEntity<List<ResultadoIADTO>>(resultados, HttpStatus.OK);
	}

	@Operation(summary = "Ver analisis consolidado por archivo", description = """
			Devuelve el analisis consolidado (resumen de votos de todas las IAs) para un archivo especifico.

			**Posibles resultados:**
			* Analisis consolidado del archivo
			* Sin contenido si el archivo no tiene analisis

			**Nota:** Requiere token JWT valido.
			""")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Analisis encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnalisisDTO.class), examples = @ExampleObject(value = """
						[
						  {
						    "id": 1,
						    "esIA": true,
						    "porcentajeIA": 87.5,
						    "archivoId": 1
						  }
						]
					"""))),
			@ApiResponse(responseCode = "204", description = "No hay analisis para ese archivo") })

	@GetMapping("mostraranalisisporarchivo")
	public ResponseEntity<List<AnalisisDTO>> mostrarAnalisisPorArchivo(@RequestParam long id) {

		List<AnalisisDTO> resultados = analisisSer.getAnalisisById(id);

		if (resultados.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return new ResponseEntity<List<AnalisisDTO>>(resultados, HttpStatus.OK);
	}

}
