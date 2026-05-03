package co.edu.unbosque.detectia.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.detectia.dto.UsuarioDTO;
import co.edu.unbosque.detectia.security.JwtUtil;
import co.edu.unbosque.detectia.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;




@RestController
@RequestMapping("/public") //Todas las rutas que maneje este controlador van a tener el prefijo "public"
@CrossOrigin(origins = { "http://localhost:8080", "*" })
public class PublicController {
	
	@Autowired
	private UsuarioService usuarioSer;
	
	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@GetMapping("home") //No requiere autenticación
	public String home() {
		return "Metodo publico";
	}
	
	@PostMapping("/registrarusuario")
	public ResponseEntity<String> registrarUsuario(@RequestParam String nombreUsuario, @RequestParam String correo, @RequestParam String contrasena) {
		
		UsuarioDTO nuevo = new UsuarioDTO ();
		nuevo.setNombreUsuario(nombreUsuario);
		nuevo.setCorreo(correo);
		nuevo.setContrasena(contrasena);
		int status = usuarioSer.create(nuevo);
		if (status == 0) {
			return new ResponseEntity<>("Usuario registrado con éxito", HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>("Error al registrarse", HttpStatus.BAD_REQUEST);
		}	
	}
	
	@PostMapping("/login")
	public ResponseEntity<Object> login(@RequestParam String correo, @RequestParam String contrasena) {
	    try {
	        authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(correo, contrasena));
	        String token = jwtUtil.generateToken(correo);
	        return new ResponseEntity<>(token, HttpStatus.OK);
	    } catch (Exception e) {
	        return new ResponseEntity<>("Credenciales invalidas", HttpStatus.UNAUTHORIZED);
	    }
	}
	
	

}
