package co.edu.unbosque.detectia.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.detectia.dto.ResultadoIADTO;
import co.edu.unbosque.detectia.service.ResultadoIAService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/private/resultadoporia")
@CrossOrigin(origins = { "http://localhost:8080", "*" })
public class ResultadoIAController {

	@Autowired
	private ResultadoIAService resultadoSer;

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("mostrarresultados")
	public ResponseEntity<List<ResultadoIADTO>> mostrarResultados() {
		List<ResultadoIADTO> resultados = resultadoSer.getAll();
		if (resultados.isEmpty()) {
			return new ResponseEntity<>(resultados, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(resultados, HttpStatus.ACCEPTED);
		}
	}
	
	@GetMapping("mostrarresultadosporcorreo")
	public ResponseEntity<List<ResultadoIADTO>> mostrarResultadosPorCorreo(Authentication authentication) {

	    String name = authentication.getName();

	    List<ResultadoIADTO> resultados = resultadoSer.getResultadosByUsuario(name);

	    if (resultados.isEmpty()) {
	        return ResponseEntity.noContent().build();
	    }

	    return ResponseEntity.ok(resultados);
	}
	
	
}
