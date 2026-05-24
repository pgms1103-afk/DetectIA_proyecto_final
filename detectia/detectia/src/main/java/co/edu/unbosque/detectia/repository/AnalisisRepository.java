package co.edu.unbosque.detectia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.detectia.entity.Analisis;
import co.edu.unbosque.detectia.entity.Archivo;
import co.edu.unbosque.detectia.entity.ResultadoIA;
import co.edu.unbosque.detectia.entity.Usuario;

/**
 * Repositorio JPA para la entidad {@link co.edu.unbosque.detectia.entity.Analisis}.
 * <p>
 * Extiende {@link JpaRepository} y añade consultas derivadas para recuperar
 * análisis filtrados por archivo o usuario, soportando los informes de resultados
 * del servicio de análisis.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 * @see co.edu.unbosque.detectia.service.AnalisisService
 */
public interface AnalisisRepository extends JpaRepository<Analisis, Long>{

	/**
	 * Recupera todos los análisis asociados al archivo dado.
	 *
	 * @param archivo entidad archivo cuyos análisis se desean consultar
	 * @return lista de análisis del archivo; vacía si no tiene ninguno
	 */
	public List<Analisis> findByArchivo(Archivo archivo);

	/**
	 * Recupera todos los análisis asociados al usuario dado.
	 *
	 * @param usuario entidad usuario cuyos análisis se desean consultar
	 * @return lista de análisis del usuario; vacía si no tiene ninguno
	 */
	public List<Analisis> findByUsuario(Usuario usuario);

}
