package co.edu.unbosque.detectia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.detectia.entity.AuditoriaLog;
import co.edu.unbosque.detectia.repository.AuditoriaLogRepository;
import co.edu.unbosque.detectia.util.AESUtil;

/**
 * Servicio de auditoría de eventos de la plataforma DetectIA.
 * <p>
 * Registra cada acción relevante del sistema (subida de archivos, inicio de
 * sesión, eliminaciones, etc.) en la tabla {@code auditoria_log} y expone
 * métodos de consulta filtrada. Los correos electrónicos se almacenan cifrados
 * con AES y se descifran automáticamente antes de devolver los resultados.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 * @see co.edu.unbosque.detectia.entity.AuditoriaLog
 * @see co.edu.unbosque.detectia.repository.AuditoriaLogRepository
 * @see co.edu.unbosque.detectia.util.AESUtil
 */
@Service
public class AuditoriaLogService {

	@Autowired
	private AuditoriaLogRepository auditoriaLogRepo;

	/**
	 * Crea y persiste un registro de auditoría con todos los detalles del evento.
	 *
	 * @param correo        correo electrónico (ya cifrado) del usuario que realiza
	 *                      la acción
	 * @param nombreUsuario nombre de usuario del actor del evento
	 * @param accion        tipo de acción realizada (p. ej. {@code "SUBIR_ARCHIVO"})
	 * @param modulo        módulo del sistema donde ocurrió la acción
	 * @param descripcion   descripción legible del evento
	 * @param ip            dirección IP del cliente (puede ser {@code null})
	 * @param navegador     agente de usuario del navegador (puede ser {@code null})
	 * @param datoAnterior  valor anterior del dato modificado (puede ser {@code null})
	 * @param datoNuevo     nuevo valor del dato tras la modificación (puede ser {@code null})
	 * @param recurso       tipo de recurso afectado (p. ej. {@code "Archivo"})
	 * @param idRecurso     identificador en cadena del recurso afectado
	 * @param exitoso       {@code true} si la operación finalizó correctamente
	 */
	public void registrarAuditoria(String correo, String nombreUsuario, String accion, String modulo,
			String descripcion, String ip, String navegador, String datoAnterior, String datoNuevo, String recurso,
			String idRecurso, boolean exitoso) {

		AuditoriaLog log = new AuditoriaLog(correo, nombreUsuario, accion, modulo, descripcion, ip, navegador,
				datoAnterior, datoNuevo, recurso, idRecurso, exitoso);
		auditoriaLogRepo.save(log);
	}

	/**
	 * Recupera todos los registros de auditoría, descifrando los correos antes de
	 * devolverlos.
	 *
	 * @return lista con todos los registros de auditoría; lista vacía si no hay
	 *         ninguno
	 */
	public List<AuditoriaLog> getAll() {
		List<AuditoriaLog> lista = auditoriaLogRepo.findAll();
		lista.forEach(this::descifrarCorreo);
		return lista;
	}

	/**
	 * Recupera todos los registros de auditoría asociados al correo dado,
	 * descifrando los correos antes de devolverlos.
	 *
	 * @param correo correo electrónico (cifrado) por el que filtrar
	 * @return lista de registros cuyo correo coincide; lista vacía si no hay
	 *         ninguno
	 */
	public List<AuditoriaLog> getByCorreo(String correo) {
		List<AuditoriaLog> lista = auditoriaLogRepo.findByCorreo(correo);
		lista.forEach(this::descifrarCorreo);
		return lista;
	}

	/**
	 * Recupera los registros de auditoría filtrados por tipo de acción.
	 *
	 * @param accion nombre de la acción (p. ej. {@code "SUBIR_ARCHIVO"})
	 * @return lista de registros cuya acción coincide; lista vacía si no hay ninguno
	 */
	public List<AuditoriaLog> getByAccion(String accion) {
		List<AuditoriaLog> lista = auditoriaLogRepo.findByAccion(accion);
		lista.forEach(this::descifrarCorreo);
		return lista;
	}

	/**
	 * Recupera los registros de auditoría filtrados por resultado de la operación.
	 *
	 * @param exitoso {@code true} para obtener las operaciones exitosas; {@code false}
	 *                para obtener las fallidas
	 * @return lista de registros que coinciden con el filtro; lista vacía si no hay
	 *         ninguno
	 */
	public List<AuditoriaLog> getByExitoso(boolean exitoso) {
		List<AuditoriaLog> lista = auditoriaLogRepo.findByExitoso(exitoso);
		lista.forEach(this::descifrarCorreo);
		return lista;
	}

	/**
	 * Recupera los registros de auditoría filtrados por módulo del sistema.
	 *
	 * @param modulo nombre del módulo (p. ej. {@code "ARCHIVOS"}, {@code "USUARIOS"})
	 * @return lista de registros cuyo módulo coincide; lista vacía si no hay ninguno
	 */
	public List<AuditoriaLog> getByModulo(String modulo) {
		List<AuditoriaLog> lista = auditoriaLogRepo.findByModulo(modulo);
		lista.forEach(this::descifrarCorreo);
		return lista;
	}
	
	private void descifrarCorreo(AuditoriaLog log) {
		if (log.getCorreo() != null && !log.getCorreo().isBlank()) {
			try {
				log.setCorreo(AESUtil.decrypt(log.getCorreo()));
			} catch (Exception ignored) {
				// Ya está en texto plano, no hace nada
			}
		}
	}

}
