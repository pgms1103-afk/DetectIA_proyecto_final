package co.edu.unbosque.detectia.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import co.edu.unbosque.detectia.entity.Usuario;


import java.util.Optional;


/**
 * Repositorio JPA para la entidad {@link co.edu.unbosque.detectia.entity.Usuario}.
 * <p>
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD estándar y
 * añade consultas derivadas por correo electrónico (cifrado AES) y nombre de
 * usuario, así como un método de existencia por correo para validar unicidad.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 * @see co.edu.unbosque.detectia.service.UsuarioService
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

	/**
	 * Busca un usuario cuyo correo electrónico (cifrado) coincida con el valor dado.
	 *
	 * @param correo correo electrónico cifrado con AES
	 * @return {@link Optional} con el usuario encontrado, o vacío si no existe
	 */
	public Optional<Usuario> findByCorreo(String correo);

	/**
	 * Busca un usuario por su nombre de usuario único.
	 *
	 * @param nombreUsuario nombre de usuario a buscar
	 * @return {@link Optional} con el usuario encontrado, o vacío si no existe
	 */
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
