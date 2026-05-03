package co.edu.unbosque.detectia.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.detectia.dto.ArchivoDTO;
import co.edu.unbosque.detectia.entity.Usuario;
import co.edu.unbosque.detectia.repository.UsuarioRepository;
import co.edu.unbosque.detectia.service.ArchivoService;

@RestController
@RequestMapping("/private/archivo")
@CrossOrigin(origins = {"http://localhost:8080", "*"})
public class ArchivoController {
	
	@Autowired
	private ArchivoService archivoSer;
	
	@Autowired
	private UsuarioRepository usuarioRepo;
	
	@PostMapping("/subir")
	public ResponseEntity<String> subirArchivo(@RequestParam String nombre, @RequestParam String tipo,
			@RequestParam String rutaAlmacenamiento){
		
		String correo = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<Usuario> usuarioEncontrado = usuarioRepo.findByCorreo(correo);
		

		if (usuarioEncontrado.isEmpty()) {
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.UNAUTHORIZED);
		}

		ArchivoDTO nuevo = new ArchivoDTO();
		nuevo.setNombre(nombre);
		nuevo.setTipo(tipo);
		nuevo.setRutaAlmacenamiento(rutaAlmacenamiento);
		nuevo.setUsuario(usuarioEncontrado.get());

		int status = archivoSer.create(nuevo);
		if (status == 0) {
			return new ResponseEntity<>("Archivo registrado correctamente", HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>("Error al registrar el archivo", HttpStatus.BAD_REQUEST);
		}
	}

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
