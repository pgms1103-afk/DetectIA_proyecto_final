package co.edu.unbosque.detectia.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.detectia.dto.UsuarioDTO;
import co.edu.unbosque.detectia.service.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;



@PreAuthorize("hasAuthority('ADMIN')")  //Permite usar expresiones q se evaluan en tiempo de ejecucion para ver si el usuario tiene acceso a ese recurso
@RestController
@RequestMapping("/private/admin") //Tiene todas las rutas privadas
@CrossOrigin(origins = { "http://localhost:8080", "*" })
public class AdminController {
	
    @Autowired
    private UsuarioService usuarioSer;
	
	@SecurityRequirement(name = "bearerAuth")
    @GetMapping("/admin") //Manejar a usuarios q quieren ingresar a la ruta admin y q sean admin
    public String admin() {
        return "Soy admin juasjuas";
    }
	
	@SecurityRequirement(name = "bearerAuth")
	@GetMapping("/mostrarusuarios")
	public ResponseEntity<List<UsuarioDTO>> mostrarUsuarios() {
		List<UsuarioDTO> usuarios = usuarioSer.getAll();
		if (usuarios.isEmpty()) {
			return new ResponseEntity<List<UsuarioDTO>>(usuarios, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<List<UsuarioDTO>>(usuarios, HttpStatus.ACCEPTED);
		}
	}
	
	@SecurityRequirement(name = "bearerAuth")
	@PutMapping("/actualizarusuarios")
	public ResponseEntity<String> actualizarUsuarios(@RequestParam Long id, 
			@RequestParam String correo, @RequestParam String contrasena, @RequestParam String rol){
		UsuarioDTO actualizado = new UsuarioDTO (correo, contrasena, rol);
		int status = usuarioSer.updateById(id, actualizado);
		if (status == 0) {
			return new ResponseEntity<>("Usuario actualizada correctamente", HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<>("Usuario no existe", HttpStatus.NO_CONTENT);
		}
	}
	
	@SecurityRequirement(name = "bearerAuth")
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
