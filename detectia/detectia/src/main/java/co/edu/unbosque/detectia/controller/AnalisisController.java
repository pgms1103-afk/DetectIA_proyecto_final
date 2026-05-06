package co.edu.unbosque.detectia.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.detectia.dto.ArchivoDTO;
import co.edu.unbosque.detectia.dto.GeminiDTO;
import co.edu.unbosque.detectia.dto.GrokDTO;
import co.edu.unbosque.detectia.dto.HuggingFaceResponseDTO;
import co.edu.unbosque.detectia.dto.ZeroGPTResponseDTO;
import co.edu.unbosque.detectia.entity.Usuario;
import co.edu.unbosque.detectia.repository.UsuarioRepository;
import co.edu.unbosque.detectia.service.EleccionService;
import co.edu.unbosque.detectia.service.ArchivoService;
import co.edu.unbosque.detectia.service.GeminiService;
import co.edu.unbosque.detectia.service.GrokService;
import co.edu.unbosque.detectia.service.HuggingFaceService;
import co.edu.unbosque.detectia.service.TextoExtractorService;
import co.edu.unbosque.detectia.service.ZeroGPTService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/private/analisis")
@CrossOrigin(origins = {"http://localhost:8080", "*"})
public class AnalisisController {
	
	@Autowired
	private EleccionService eleccionSer;
	
	@Autowired
	private UsuarioRepository usuarioRepo;
	
	@Autowired
	private ArchivoService archivoSer;
	
	@PostMapping(value = "/analizar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> analizar(@RequestParam String nombre, @RequestParam String tipo,
			@RequestParam MultipartFile archivo) throws Exception{
		
		String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuarioEncontrado = usuarioRepo.findByCorreo(correo);
        Map<String, Double> votosIAs = eleccionSer.analizar(archivo);

        // 3. Crear el registro del Archivo
        ArchivoDTO nuevo = new ArchivoDTO();
        nuevo.setNombre(nombre);
        nuevo.setTipo(tipo);
        nuevo.setRutaAlmacenamiento(archivo.getOriginalFilename());
        nuevo.setUsuario(usuarioEncontrado.get());

        int status = archivoSer.create(nuevo);
        
        if (status == 0) {
            return new ResponseEntity<>(votosIAs, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
	}
	

}
