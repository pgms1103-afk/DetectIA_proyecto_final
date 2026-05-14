package co.edu.unbosque.detectia.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import co.edu.unbosque.detectia.entity.Usuario;


import java.util.Optional;


public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	
	public Optional<Usuario> findByCorreo(String correo);
	public Optional<Usuario> findByNombreUsuario(String nombreUsuario);
	
	

}
