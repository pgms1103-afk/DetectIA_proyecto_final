package co.edu.unbosque.detectia.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import co.edu.unbosque.detectia.entity.Archivo;
import co.edu.unbosque.detectia.entity.ResultadoIA;
import co.edu.unbosque.detectia.entity.Usuario;

/**
 * Repositorio JPA para la entidad {@link co.edu.unbosque.detectia.entity.ResultadoIA}.
 * <p>
 * Extiende {@link JpaRepository} y añade consultas derivadas para recuperar los
 * votos individuales de cada servicio de IA filtrados por archivo o por el correo
 * del usuario propietario.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 * @see co.edu.unbosque.detectia.service.ResultadoIAService
 */
public interface ResultadoIARepository extends JpaRepository<ResultadoIA, Long>{

	/**
	 * Recupera todos los resultados de IA asociados al archivo dado.
	 *
	 * @param archivo entidad archivo cuyos resultados se desean consultar
	 * @return lista de resultados del archivo; vacía si no tiene ninguno
	 */
	public List<ResultadoIA> findByArchivo(Archivo archivo);

	/**
	 * Recupera todos los resultados de IA cuyos archivos pertenecen al usuario con
	 * el correo indicado (navegando por la relación archivo → usuario).
	 *
	 * @param correo correo electrónico cifrado del usuario propietario
	 * @return lista de resultados asociados al correo
	 */
	List<ResultadoIA> findByArchivo_Usuario_Correo(String correo);

}
