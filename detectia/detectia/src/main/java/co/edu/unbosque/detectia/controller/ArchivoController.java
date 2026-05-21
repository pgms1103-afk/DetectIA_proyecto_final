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
import co.edu.unbosque.detectia.service.AnalisisService;
import co.edu.unbosque.detectia.exception.ExtensionInvalidaException;
import co.edu.unbosque.detectia.exception.TamanoInvalidoException;
import co.edu.unbosque.detectia.service.ArchivoService;
import co.edu.unbosque.detectia.service.EleccionService;
import co.edu.unbosque.detectia.service.ResultadoIAService;
import co.edu.unbosque.detectia.service.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/private/archivo")
@CrossOrigin(origins = { "http://localhost:8080", "*" })
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
	

//	@PostMapping(value = "/analizar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//	public ResponseEntity<?> analizar(@RequestParam String nombre, @RequestParam MultipartFile archivo) throws Exception {
//
//	    Map<String, Double> votosIAs = eleccionSer.analizar(archivo);
//	    return new ResponseEntity<>(votosIAs, HttpStatus.CREATED);
//	}
//
//	@PostMapping("/analizarurl")
//	public ResponseEntity<?> analizarURL(@RequestParam String nombre, @RequestParam String url) throws Exception {
//
//	    Map<String, Double> votosIAs = eleccionSer.analizar(url);
//	    return new ResponseEntity<>(votosIAs, HttpStatus.CREATED);
//	}

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
			return new ResponseEntity<>(archivoSer.calcularResumen(votosIAs), HttpStatus.CREATED);
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/analizarurl")
	public ResponseEntity<?> analizarURL(@RequestParam String nombre, @RequestParam String url,
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
			resultadoIAser.guardarResultados(votosIAs, archivoGuardado.getNombre());
			return new ResponseEntity<>(archivoSer.calcularResumen(votosIAs), HttpStatus.CREATED);
		} catch (ExtensionInvalidaException | TamanoInvalidoException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping("/mis-archivos")
	public ResponseEntity<List<ArchivoDTO>> misArchivos(Authentication authentication) {
		String user = authentication.getName();
		List<ArchivoDTO> archivos = archivoSer.getArchivosByuser(user);

		if (archivos.isEmpty()) {
			return new ResponseEntity<>(archivos, HttpStatus.NOT_FOUND);
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
			return new ResponseEntity<>("Archivo no existe", HttpStatus.NOT_FOUND);
		}
	}

}
