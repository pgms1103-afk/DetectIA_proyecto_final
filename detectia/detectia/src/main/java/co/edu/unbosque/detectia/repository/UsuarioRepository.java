package co.edu.unbosque.detectia.repository;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.detectia.entity.Usuario;
import java.util.Optional;


public interface UsuarioRepository extends CrudRepository<Usuario, Long>{
	
	public Optional<Usuario> findByCorreo(String correo);
	
	

}
