package co.edu.unbosque.detectia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.detectia.entity.AuditoriaLog;
import co.edu.unbosque.detectia.repository.AuditoriaLogRepository;

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

	public List<AuditoriaLog> getAll() {
		return auditoriaLogRepo.findAll();
	}

	public List<AuditoriaLog> getByCorreo(String correo) {
		return auditoriaLogRepo.findByCorreo(correo);
	}

	public List<AuditoriaLog> getByAccion(String accion) {
		return auditoriaLogRepo.findByAccion(accion);
	}

	public List<AuditoriaLog> getByExitoso(boolean exitoso) {
		return auditoriaLogRepo.findByExitoso(exitoso);
	}

	public List<AuditoriaLog> getByModulo(String modulo) {
		return auditoriaLogRepo.findByModulo(modulo);
	}

}
