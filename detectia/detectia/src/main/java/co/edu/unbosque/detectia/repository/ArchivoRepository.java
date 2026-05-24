package co.edu.unbosque.detectia.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.detectia.entity.Archivo;
import co.edu.unbosque.detectia.entity.ResultadoIA;
import co.edu.unbosque.detectia.entity.Usuario;

/**
 * Repositorio JPA para la entidad {@link co.edu.unbosque.detectia.entity.Archivo}.
 * <p>
 * Extiende {@link org.springframework.data.repository.CrudRepository} y añade
 * consultas por usuario, por nombre y combinadas para soportar la lógica de
 * búsqueda de archivos del servicio de archivos.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 * @see co.edu.unbosque.detectia.service.ArchivoService
 */
public interface ArchivoRepository extends CrudRepository<Archivo, Long>{

	/**
	 * Recupera todos los archivos pertenecientes al usuario dado.
	 *
	 * @param usuario entidad propietaria de los archivos
	 * @return lista de archivos del usuario; vacía si no tiene ninguno
	 */
	public List<Archivo> findByUsuario(Usuario usuario);

	/**
	 * Recupera todos los archivos cuyo nombre coincida exactamente.
	 *
	 * @param nombre nombre del archivo a buscar
	 * @return lista de archivos con ese nombre
	 */
	public List<Archivo> findByNombre(String nombre);

	/**
	 * Cuenta el número de archivos asociados al usuario identificado por {@code id}.
	 *
	 * @param id identificador del usuario
	 * @return número de archivos del usuario
	 */
	public long countByUsuarioId(Long id);

	/**
	 * Recupera los archivos que coincidan con el nombre dado y pertenezcan al
	 * usuario identificado por {@code id}.
	 *
	 * @param nombre nombre del archivo
	 * @param id     identificador del usuario propietario
	 * @return lista de archivos que cumplen ambos criterios
	 */
	public List<Archivo> findByNombreAndUsuarioId(String nombre, Long id);


}
