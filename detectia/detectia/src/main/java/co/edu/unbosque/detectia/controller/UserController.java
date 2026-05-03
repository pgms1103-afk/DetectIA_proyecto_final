package co.edu.unbosque.detectia.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.detectia.dto.UsuarioDTO;
import co.edu.unbosque.detectia.service.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/private/user")
@CrossOrigin(origins = { "http://localhost:8080", "*" })
public class UserController {
	
	 @Autowired
	 private UsuarioService usuarioSer;
	 
	 @SecurityRequirement(name = "bearerAuth")
	 @GetMapping("/misdatos")
	 public ResponseEntity<UsuarioDTO> datosUsuario(Authentication authentication) {
		 String correoLogueado = authentication.getName();
		    
		    UsuarioDTO dto = usuarioSer.getLoginUser(correoLogueado);
		    return ResponseEntity.ok(dto);
	 }
	 
	 @SecurityRequirement(name = "bearerAuth")
	 @PutMapping("/actualizarcontraseña")
	 public ResponseEntity<String> actualizarContrasenia(Authentication authentication, @RequestParam String contrasena) {
	     String correoLogueado = authentication.getName();
	     
	     int resultado = usuarioSer.updateLoginPassword(correoLogueado, contrasena);
	     
	     if (resultado == 0) {
				return new ResponseEntity<>("Contraseña actulizada correctamente", HttpStatus.ACCEPTED);
			} else {
				return new ResponseEntity<>("Contraseña no existe", HttpStatus.NO_CONTENT);

			}
	 }
	 

}
