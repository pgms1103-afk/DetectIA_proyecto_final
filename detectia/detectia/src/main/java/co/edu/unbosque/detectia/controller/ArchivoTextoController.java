package co.edu.unbosque.detectia.controller;

import java.io.IOException;
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
import co.edu.unbosque.detectia.dto.ZeroGPTResponseDTO;
import co.edu.unbosque.detectia.entity.Usuario;
import co.edu.unbosque.detectia.repository.UsuarioRepository;
import co.edu.unbosque.detectia.service.ArchivoService;
import co.edu.unbosque.detectia.service.TextoExtractorService;
import co.edu.unbosque.detectia.service.ZeroGPTService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/private/texto")
@CrossOrigin(origins = {"http://localhost:8080", "*"})
public class ArchivoTextoController {
	
	@Autowired
    private ZeroGPTService zeroGPTService;
	@Autowired
	private ArchivoService archivoSer;
	@Autowired
	private UsuarioRepository usuarioRepo;
	
	@Autowired
	private TextoExtractorService extractorSer;
	
	@PostMapping(value = "/subir", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ZeroGPTResponseDTO> subirArchivo(@RequestParam String nombre, @RequestParam String tipo,
			@RequestParam MultipartFile archivo) throws IOException{
		
		String correo = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<Usuario> usuarioEncontrado = usuarioRepo.findByCorreo(correo);
		

		if (usuarioEncontrado.isEmpty()) {
			return new ResponseEntity<>( HttpStatus.UNAUTHORIZED);
		}
		
		String textoExtraido = extractorSer.extraerTexto(archivo);

		 ZeroGPTResponseDTO respuesta = zeroGPTService.detectarIA(textoExtraido);
		 
		ArchivoDTO nuevo = new ArchivoDTO();
		nuevo.setNombre(nombre);
		nuevo.setTipo(tipo);
		nuevo.setRutaAlmacenamiento(archivo.getOriginalFilename());
		nuevo.setIs_human_written(respuesta.getData().getIs_human_written());
		nuevo.setIs_gpt_generated(respuesta.getData().getIs_gpt_generated());
		nuevo.setUsuario(usuarioEncontrado.get());

		
		 
		int status = archivoSer.create(nuevo);
		if (status == 0) {
			return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>( HttpStatus.BAD_REQUEST);
		}
	}
	

}
