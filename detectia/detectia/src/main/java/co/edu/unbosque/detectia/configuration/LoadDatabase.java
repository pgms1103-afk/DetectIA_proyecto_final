package co.edu.unbosque.detectia.configuration;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import co.edu.unbosque.detectia.entity.Usuario;
import co.edu.unbosque.detectia.repository.UsuarioRepository;
import co.edu.unbosque.detectia.util.AESUtil;

/**
 * Inicializador de datos predeterminados de la plataforma DetectIA.
 * <p>
 * Al arrancar la aplicación, verifica si existen los usuarios de sistema
 * ({@code admin} y {@code normaluser}) y los crea en caso de no encontrarlos,
 * asignando sus correos cifrados con AES y sus contraseñas codificadas con
 * BCrypt. La contraseña por defecto se lee de la propiedad
 * {@code app.default-password}.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 * @see co.edu.unbosque.detectia.entity.Usuario
 * @see co.edu.unbosque.detectia.util.AESUtil
 */
@Configuration
public class LoadDatabase {
  /** Logger para registrar mensajes durante la carga de datos. */
  private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);
  
  @Value("${app.default-password}")
  private String defaultPassword;
  

  /**
   * Inicializa la base de datos con usuarios predeterminados. Crea un usuario administrador y un
   * usuario normal si no existen.
   *
   * @param userRepo Repositorio de usuarios para acceder a la base de datos
   * @param passwordEncoder Codificador de contraseñas para encriptar las contraseñas de los
   *     usuarios
   * @return Un CommandLineRunner que se ejecuta al iniciar la aplicación
   */
  @Bean
  CommandLineRunner initDatabase(UsuarioRepository userRepo, PasswordEncoder passwordEncoder) {

    return args -> {
      Optional<Usuario> found = userRepo.findByNombreUsuario("admin");
      if (found.isPresent()) {
        log.info("El administrador ya existe, omitiendo la creación del administrador...");
      } else {
    	  Usuario adminUser = new Usuario("admin", AESUtil.encrypt("admin@gmail"), passwordEncoder.encode(defaultPassword), Usuario.Role.ADMIN, 0);
        userRepo.save(adminUser);
        log.info("Precargando usuario administrador");
      }
      Optional<Usuario> found2 = userRepo.findByNombreUsuario("normaluser");
      if (found2.isPresent()) {
        log.info("El usuario normal ya existe, omitiendo la creación del usuario normal...");
      } else {
    	  Usuario normalUser =
            new Usuario("normaluser", AESUtil.encrypt("user@gmail"), passwordEncoder.encode(defaultPassword), Usuario.Role.USER, 0);
        userRepo.save(normalUser);
        log.info("Precargando usuario normal");
      }
    };
  }
}
