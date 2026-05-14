package co.edu.unbosque.detectia.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unbosque.detectia.dto.ArchivoDTO;
import co.edu.unbosque.detectia.dto.ResultadoIADTO;
import co.edu.unbosque.detectia.entity.Archivo;
import co.edu.unbosque.detectia.entity.Usuario;
import co.edu.unbosque.detectia.repository.UsuarioRepository;
import co.edu.unbosque.detectia.service.ArchivoService;
import co.edu.unbosque.detectia.service.EleccionService;
import co.edu.unbosque.detectia.service.ResultadoIAService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/private/archivo")
@CrossOrigin(origins = {"http://localhost:8080", "*"})
public class ArchivoController {
	
	@Autowired
	private EleccionService eleccionSer;
	
	@Autowired
	private UsuarioRepository usuarioRepo;
	
	@Autowired
	private ArchivoService archivoSer;
	
	@Autowired
	private ResultadoIAService resultadoIAser;
	
	
	@PostMapping(value = "/analizar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> analizar(@RequestParam String nombre, @RequestParam String tipo,
			@RequestParam MultipartFile archivo) throws Exception{
		
		String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuarioEncontrado = usuarioRepo.findByCorreo(correo);
        Map<String, Double> votosIAs = eleccionSer.analizar(archivo);

       
        ArchivoDTO nuevo = new ArchivoDTO();
        nuevo.setNombre(nombre);
        nuevo.setTipo(tipo);
        nuevo.setRutaAlmacenamiento(archivo.getOriginalFilename());
        nuevo.setUsuario(usuarioEncontrado.get());

        Archivo archivoGuardado = archivoSer.createAndReturn(nuevo);
        int status;
        if (archivoGuardado != null) {
            status = 0;
        } else {
            status = 1;
        }

        if (status == 0) {
            List<ResultadoIADTO> resultados = new ArrayList<>();
            for (Map.Entry<String, Double> voto : votosIAs.entrySet()) {
                ResultadoIADTO resultado = new ResultadoIADTO();
                resultado.setNombreIA(voto.getKey());
                resultado.setPorcentajeIA(voto.getValue());
                resultado.setFechaAnalisis(LocalDateTime.now());
                resultado.setArchivo(archivoGuardado);
                resultadoIAser.create(resultado);
                resultados.add(resultado);
            }
            return new ResponseEntity<>(votosIAs, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
	}


	@GetMapping("/mis-archivos")
	public ResponseEntity<List<ArchivoDTO>> misArchivos(Authentication authentication) {
		String correo = authentication.getName();
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
