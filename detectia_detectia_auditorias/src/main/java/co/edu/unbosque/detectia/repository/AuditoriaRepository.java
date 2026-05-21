package co.edu.unbosque.detectia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.detectia.entity.Auditoria;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    List<Auditoria> findByUsuarioCorreo(String usuarioCorreo);

    List<Auditoria> findByUsuarioNombre(String usuarioNombre);

    List<Auditoria> findByModulo(String modulo);

    List<Auditoria> findByExitoso(boolean exitoso);
}