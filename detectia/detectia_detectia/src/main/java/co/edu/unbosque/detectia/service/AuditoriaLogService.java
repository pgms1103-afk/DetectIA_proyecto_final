package co.edu.unbosque.detectia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.detectia.entity.AuditoriaLog;
import co.edu.unbosque.detectia.repository.AuditoriaLogRepository;
import co.edu.unbosque.detectia.util.AESUtil;

@Service
public class AuditoriaLogService {

	@Autowired
	private AuditoriaLogRepository auditoriaLogRepo;

	public void registrarAuditoria(String correo, String nombreUsuario, String accion, String modulo,
			String descripcion, String ip, String navegador, String datoAnterior, String datoNuevo, String recurso,
			String idRecurso, boolean exitoso) {

		AuditoriaLog log = new AuditoriaLog(correo, nombreUsuario, accion, modulo, descripcion, ip, navegador,
				datoAnterior, datoNuevo, recurso, idRecurso, exitoso);
		auditoriaLogRepo.save(log);
	}

	/** Intenta descifrar el correo de un log. Si ya está en texto plano, lo deja igual. */
	private void descifrarCorreo(AuditoriaLog log) {
		if (log.getCorreo() != null && !log.getCorreo().isBlank()) {
			try {
				log.setCorreo(AESUtil.decrypt(log.getCorreo()));
			} catch (Exception ignored) {
				// Ya está en texto plano, no hace nada
			}
		}
	}

	public List<AuditoriaLog> getAll() {
		List<AuditoriaLog> lista = auditoriaLogRepo.findAll();
		lista.forEach(this::descifrarCorreo);
		return lista;
	}

	public List<AuditoriaLog> getByCorreo(String correo) {
		List<AuditoriaLog> lista = auditoriaLogRepo.findByCorreo(correo);
		lista.forEach(this::descifrarCorreo);
		return lista;
	}

	public List<AuditoriaLog> getByAccion(String accion) {
		List<AuditoriaLog> lista = auditoriaLogRepo.findByAccion(accion);
		lista.forEach(this::descifrarCorreo);
		return lista;
	}

	public List<AuditoriaLog> getByExitoso(boolean exitoso) {
		List<AuditoriaLog> lista = auditoriaLogRepo.findByExitoso(exitoso);
		lista.forEach(this::descifrarCorreo);
		return lista;
	}

	public List<AuditoriaLog> getByModulo(String modulo) {
		List<AuditoriaLog> lista = auditoriaLogRepo.findByModulo(modulo);
		lista.forEach(this::descifrarCorreo);
		return lista;
	}

}
