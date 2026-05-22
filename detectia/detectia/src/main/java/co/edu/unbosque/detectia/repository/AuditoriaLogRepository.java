package co.edu.unbosque.detectia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.detectia.entity.AuditoriaLog;

public interface AuditoriaLogRepository extends JpaRepository<AuditoriaLog, Long> {

	List<AuditoriaLog> findByCorreo(String correo);

	List<AuditoriaLog> findByAccion(String accion);

	List<AuditoriaLog> findByExitoso(boolean exitoso);

	List<AuditoriaLog> findByModulo(String modulo);

}
