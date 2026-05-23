package co.edu.unbosque.detectia.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.detectia.dto.UsuarioDTO;
import co.edu.unbosque.detectia.exception.CorreoInvalidoException;
import co.edu.unbosque.detectia.exception.IdExistException;
import co.edu.unbosque.detectia.exception.NombreInvalidoException;
import co.edu.unbosque.detectia.exception.PasswordNotValidException;
import co.edu.unbosque.detectia.service.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin") // Tiene todas las rutas privadas
@CrossOrigin(origins = { "http://localhost:8080", "*" })
public class AdminController {

	@Autowired
	private UsuarioService usuarioSer;

	@PostMapping("/crearusuario")
	public ResponseEntity<String> crearUsuario(@RequestBody UsuarioDTO dto) {

		try {
			UsuarioDTO nuevo = new UsuarioDTO();
			nuevo.setNombreUsuario(dto.getNombreUsuario());
			nuevo.setCorreo(dto.getCorreo());
			nuevo.setContrasena(dto.getContrasena());
			nuevo.setRole(dto.getRole());
			usuarioSer.create(nuevo);

			return new ResponseEntity<>("Usuario creado con éxito", HttpStatus.CREATED);
		} catch (NombreInvalidoException | PasswordNotValidException | CorreoInvalidoException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/mostrarusuarios")
	public ResponseEntity<List<UsuarioDTO>> mostrarUsuarios() {
		List<UsuarioDTO> usuarios = usuarioSer.getAll();
		if (usuarios.isEmpty()) {
			return new ResponseEntity<List<UsuarioDTO>>(usuarios, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<List<UsuarioDTO>>(usuarios, HttpStatus.ACCEPTED);
		}
	}

	@PutMapping("/actualizarusuarios")
	public ResponseEntity<String> actualizarUsuarios(@RequestParam Long id, @RequestBody UsuarioDTO newUser) {
		try {
			usuarioSer.updateById(id, newUser);
			return new ResponseEntity<>("Usuario actualizado correctamente", HttpStatus.ACCEPTED);
		} catch (NombreInvalidoException | PasswordNotValidException | CorreoInvalidoException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/eliminarusuarios")
	public ResponseEntity<String> eliminarUsuarios(@RequestParam Long id) {

		try {
			usuarioSer.delateById(id);
			return new ResponseEntity<>("Usuario eliminado correctamente", HttpStatus.ACCEPTED);
		} catch (IdExistException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);

		}
	}

}
