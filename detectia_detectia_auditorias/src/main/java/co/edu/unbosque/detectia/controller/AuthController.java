package co.edu.unbosque.detectia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.detectia.dto.UsuarioDTO;
import co.edu.unbosque.detectia.entity.Usuario;
import co.edu.unbosque.detectia.exception.CorreoInvalidoException;
import co.edu.unbosque.detectia.exception.NombreInvalidoException;
import co.edu.unbosque.detectia.exception.PasswordNotValidException;
import co.edu.unbosque.detectia.security.JwtUtil;
import co.edu.unbosque.detectia.service.AuditoriaService;
import co.edu.unbosque.detectia.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/public")
@CrossOrigin(origins = { "http://localhost:8080", "*" })
public class AuthController {

    @Autowired
    private UsuarioService usuarioSer;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuditoriaService auditoriaLogSer;

    @GetMapping("home")
    public String home() {
        return "Metodo publico";
    }

    @PostMapping("/registrarusuario")
    public ResponseEntity<String> registrarUsuario(@RequestBody UsuarioDTO dto,
            HttpServletRequest request) {
        try {
            if (usuarioSer.findUsernameAlreadyTaken(dto.getNombreUsuario())) {
                auditoriaLogSer.registrarAuditoria(dto.getCorreo(), dto.getNombreUsuario(),
                        "REGISTRO_USUARIO", "USER", "Registro fallido: nombre de usuario ya existe",
                        request.getRemoteAddr(), request.getHeader("User-Agent"),
                        null, null, "no disponible", null, false);
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El nombre de usuario ya existe");
            }

            UsuarioDTO nuevo = new UsuarioDTO();
            nuevo.setNombreUsuario(dto.getNombreUsuario());
            nuevo.setCorreo(dto.getCorreo());
            nuevo.setContrasena(dto.getContrasena());
            usuarioSer.create(nuevo, request);

            return new ResponseEntity<>("Usuario registrado con éxito", HttpStatus.CREATED);

        } catch (NombreInvalidoException | PasswordNotValidException | CorreoInvalidoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UsuarioDTO dto, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String navegador = request.getHeader("User-Agent");

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getNombreUsuario(), dto.getContrasena()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails);

            String role = null;
            String correo = dto.getNombreUsuario();

            if (userDetails instanceof Usuario user) {
                role = user.getRole().name();
                correo = user.getCorreo();
            }

            auditoriaLogSer.registrarAuditoria(correo, dto.getNombreUsuario(),
                    "LOGIN", role != null ? role : "USER",
                    "Login exitoso para el usuario: " + dto.getNombreUsuario(),
                    ip, navegador, null, null, "no disponible", null, true);

            return ResponseEntity.ok(new AuthResponse(jwt, role));

        } catch (AuthenticationException e) {
            auditoriaLogSer.registrarAuditoria("desconocido", dto.getNombreUsuario(),
                    "LOGIN", "USER",
                    "Login fallido para el usuario: " + dto.getNombreUsuario(),
                    ip, navegador, null, null, "no disponible", null, false);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Nombre de usuario o contraseña inválidos o usuario no encontrado");
        }
    }

    private static class AuthResponse {
        private final String token;
        private final String role;

        public AuthResponse(String token) {
            this.token = token;
            this.role = null;
        }

        public AuthResponse(String token, String role) {
            this.token = token;
            this.role = role;
        }

        public String getToken() { return token; }
        public String getRole() { return role; }
    }
}