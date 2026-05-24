package co.edu.unbosque.detectia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.detectia.entity.AuditoriaLog;

/**
 * Repositorio JPA para la entidad {@link co.edu.unbosque.detectia.entity.AuditoriaLog}.
 * <p>
 * Extiende {@link JpaRepository} y añade consultas derivadas para filtrar los
 * registros de auditoría por correo, acción, resultado y módulo, soportando las
 * vistas de administración de auditoría.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 * @see co.edu.unbosque.detectia.service.AuditoriaLogService
 */
public interface AuditoriaLogRepository extends JpaRepository<AuditoriaLog, Long> {

	/**
	 * Recupera los registros de auditoría cuyo correo (cifrado) coincida.
	 *
	 * @param correo correo electrónico cifrado con AES
	 * @return lista de registros asociados al correo
	 */
	List<AuditoriaLog> findByCorreo(String correo);

	/**
	 * Recupera los registros de auditoría filtrados por tipo de acción.
	 *
	 * @param accion nombre de la acción (p. ej. {@code "SUBIR_ARCHIVO"})
	 * @return lista de registros con esa acción
	 */
	List<AuditoriaLog> findByAccion(String accion);

	/**
	 * Recupera los registros de auditoría filtrados por resultado de la operación.
	 *
	 * @param exitoso {@code true} para operaciones exitosas; {@code false} para fallidas
	 * @return lista de registros con ese resultado
	 */
	List<AuditoriaLog> findByExitoso(boolean exitoso);

	/**
	 * Recupera los registros de auditoría filtrados por módulo del sistema.
	 *
	 * @param modulo nombre del módulo (p. ej. {@code "ARCHIVOS"}, {@code "USUARIOS"})
	 * @return lista de registros del módulo
	 */
	List<AuditoriaLog> findByModulo(String modulo);

}
