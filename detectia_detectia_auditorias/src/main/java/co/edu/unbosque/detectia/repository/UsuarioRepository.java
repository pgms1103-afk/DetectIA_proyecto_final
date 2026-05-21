package co.edu.unbosque.detectia.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import co.edu.unbosque.detectia.entity.Usuario;


import java.util.Optional;


public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	
	public Optional<Usuario> findByCorreo(String correo);
	public Optional<Usuario> findByNombreUsuario(String nombreUsuario);
	/**
	 * Verifica la existencia de un usuario con el correo electrónico especificado.
	 * <p>
	 * Útil para validar la unicidad del correo durante procesos de registro
	 * o actualización de datos.
	 * </p>
	 *
	 * @param correo correo electrónico a validar
	 * @return {@code true} si el correo ya está registrado;
	 *         {@code false} en caso contrario
	 */
	public boolean existsByCorreo(String correo);

	

}
