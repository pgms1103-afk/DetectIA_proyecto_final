package co.edu.unbosque.detectia.configuration;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import co.edu.unbosque.detectia.entity.Usuario;
import co.edu.unbosque.detectia.repository.UsuarioRepository;

@Configuration
public class LoadDatabase {
  /** Logger para registrar mensajes durante la carga de datos. */
  private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

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
    	  Usuario adminUser = new Usuario("admin", "admin@gmail", passwordEncoder.encode("1234567890"), Usuario.Role.ADMIN);
        userRepo.save(adminUser);
        log.info("Precargando usuario administrador");
      }
      Optional<Usuario> found2 = userRepo.findByNombreUsuario("normaluser");
      if (found2.isPresent()) {
        log.info("El usuario normal ya existe, omitiendo la creación del usuario normal...");
      } else {
    	  Usuario normalUser =
            new Usuario("normaluser", "user@gmail", passwordEncoder.encode("1234567890"), Usuario.Role.USER);
        userRepo.save(normalUser);
        log.info("Precargando usuario normal");
      }
    };
  }
}
