package co.edu.unbosque.detectia.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import co.edu.unbosque.detectia.dto.ArchivoDTO;

import co.edu.unbosque.detectia.service.ArchivoService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;


@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/private/archivo")
@CrossOrigin(origins = {"http://localhost:8080", "*"})
public class ArchivoController {
	
	@Autowired
	private ArchivoService archivoSer;
	


	@GetMapping("/mis-archivos")
	public ResponseEntity<List<ArchivoDTO>> misArchivos() {
		String correo = SecurityContextHolder.getContext().getAuthentication().getName();
		List<ArchivoDTO> archivos = archivoSer.getArchivosByCorreo(correo);

		if (archivos.isEmpty()) {
			return new ResponseEntity<>(archivos, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(archivos, HttpStatus.ACCEPTED);
		}
	}

	@DeleteMapping("/eliminar")
	public ResponseEntity<String> eliminarArchivo(@RequestParam Long id) {
		int status = archivoSer.delateById(id);
		if (status == 0) {
			return new ResponseEntity<>("Archivo eliminado correctamente", HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>("Archivo no existe", HttpStatus.NO_CONTENT);
		}
	}

	
	
	

}
