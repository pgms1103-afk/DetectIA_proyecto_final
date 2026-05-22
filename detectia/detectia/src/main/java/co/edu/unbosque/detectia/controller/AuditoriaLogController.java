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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "beareAuth")
@RestController
@RequestMapping("/admin/auditoria")
@CrossOrigin(origins = { "http://localhost:8080", "*" })
public class AuditoriaLogController {

	@Autowired
	private AuditoriaLogService auditoriaLogSer;

	@GetMapping("/todos")
	public ResponseEntity<List<AuditoriaLog>> getTodos() {
		List<AuditoriaLog> lista = auditoriaLogSer.getAll();
		if (lista.isEmpty()) {
			return new ResponseEntity<>(lista, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(lista, HttpStatus.OK);
	}

	@GetMapping("/porcorreo")
	public ResponseEntity<List<AuditoriaLog>> getPorCorreo(@RequestParam String correo) {
		List<AuditoriaLog> lista = auditoriaLogSer.getByCorreo(correo);
		if (lista.isEmpty()) {
			return new ResponseEntity<>(lista, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(lista, HttpStatus.OK);
	}

	@GetMapping("/poraccion")
	public ResponseEntity<List<AuditoriaLog>> getPorAccion(@RequestParam String accion) {
		List<AuditoriaLog> lista = auditoriaLogSer.getByAccion(accion);
		if (lista.isEmpty()) {
			return new ResponseEntity<>(lista, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(lista, HttpStatus.OK);
	}

	@GetMapping("/pormodulo")
	public ResponseEntity<List<AuditoriaLog>> getPorModulo(@RequestParam String modulo) {
		List<AuditoriaLog> lista = auditoriaLogSer.getByModulo(modulo);
		if (lista.isEmpty()) {
			return new ResponseEntity<>(lista, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(lista, HttpStatus.OK);
	}

	@GetMapping("/porexitoso")
	public ResponseEntity<List<AuditoriaLog>> getPorExitoso(@RequestParam boolean exitoso) {
		List<AuditoriaLog> lista = auditoriaLogSer.getByExitoso(exitoso);
		if (lista.isEmpty()) {
			return new ResponseEntity<>(lista, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(lista, HttpStatus.OK);
	}

}
