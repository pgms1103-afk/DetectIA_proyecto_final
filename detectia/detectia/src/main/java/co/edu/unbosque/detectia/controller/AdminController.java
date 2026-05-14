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
import co.edu.unbosque.detectia.service.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin") //Tiene todas las rutas privadas
@CrossOrigin(origins = { "http://localhost:8080", "*" })
public class AdminController {
	
    @Autowired
    private UsuarioService usuarioSer;
	
    
	@PostMapping("/crearusuario")
	public ResponseEntity<String> crearUsuario(@RequestParam String nombreUsuario, @RequestParam String correo, @RequestParam String contrasena) {
		
		UsuarioDTO nuevo = new UsuarioDTO ();
		nuevo.setNombreUsuario(nombreUsuario);
		nuevo.setCorreo(correo);
		nuevo.setContrasena(contrasena);
		int status = usuarioSer.create(nuevo);
		if (status == 0) {
			return new ResponseEntity<>("Usuario creado con éxito", HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>("Error al crear usuario", HttpStatus.BAD_REQUEST);
		}	
	}
	
    @GetMapping("/admin") //Manejar a usuarios q quieren ingresar a la ruta admin y q sean admin
    public String admin() {
        return "Soy admin juasjuas";
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
	public ResponseEntity<String> actualizarUsuarios(@RequestParam
	          Long id, @RequestBody UsuarioDTO newUser){
		int status = usuarioSer.updateById(id, newUser);
		if (status == 0) {
			return new ResponseEntity<>("Usuario actualizado correctamente", HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>("Usuario no existe", HttpStatus.NO_CONTENT);
		}
	}

	@DeleteMapping("/eliminarusuarios")
	public ResponseEntity<String> eliminarUsuarios (@RequestParam Long id){
		int status = usuarioSer.delateById(id);
		if (status == 0) {
			return new ResponseEntity<>("Usuario eliminado correctamente", HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>("Usuario no existe", HttpStatus.NO_CONTENT);

		}
	}
	
	
	
	

}
