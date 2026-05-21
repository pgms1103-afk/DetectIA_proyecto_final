package co.edu.unbosque.detectia.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.detectia.dto.AuditoriaDTO;
import co.edu.unbosque.detectia.entity.Auditoria;
import co.edu.unbosque.detectia.repository.AuditoriaRepository;

@Service
public class AuditoriaService {

    @Autowired
    private AuditoriaRepository auditoriaLogRepo;

    @Autowired
    private ModelMapper mapper;

    public AuditoriaService() {
    }

    public void registrarAuditoria(String usuarioCorreo, String usuarioNombre, String accion,
            String modulo, String descripcion, String ip, String navegador,
            Double latitud, Double longitud, String ubicacion,
            String conversacionId, boolean exitoso) {

        Auditoria log = new Auditoria();
        log.setUsuarioCorreo(usuarioCorreo != null ? usuarioCorreo : "desconocido");
        log.setUsuarioNombre(usuarioNombre != null ? usuarioNombre : "desconocido");
        log.setAccion(accion);
        log.setModulo(modulo != null ? modulo : "USER");
        log.setDescripcion(descripcion);
        log.setFechaAccion(LocalDateTime.now());
        log.setIp(ip != null ? ip : "0.0.0.0");
        log.setNavegador(navegador != null ? navegador : "desconocido");
        log.setLatitud(latitud);
        log.setLongitud(longitud);
        log.setUbicacion(ubicacion != null ? ubicacion : "no disponible");
        log.setConversacionId(conversacionId);
        log.setExitoso(exitoso);

        auditoriaLogRepo.save(log);
    }

    public List<AuditoriaDTO> getAll() {
        List<Auditoria> entityList = auditoriaLogRepo.findAll();
        List<AuditoriaDTO> dtoList = new ArrayList<>();
        entityList.forEach(entity -> dtoList.add(mapper.map(entity, AuditoriaDTO.class)));
        return dtoList;
    }

    public List<AuditoriaDTO> getByCorreo(String correo) {
        List<Auditoria> entityList = auditoriaLogRepo.findByUsuarioCorreo(correo);
        List<AuditoriaDTO> dtoList = new ArrayList<>();
        entityList.forEach(entity -> dtoList.add(mapper.map(entity, AuditoriaDTO.class)));
        return dtoList;
    }

    public List<AuditoriaDTO> getByNombre(String nombre) {
        List<Auditoria> entityList = auditoriaLogRepo.findByUsuarioNombre(nombre);
        List<AuditoriaDTO> dtoList = new ArrayList<>();
        entityList.forEach(entity -> dtoList.add(mapper.map(entity, AuditoriaDTO.class)));
        return dtoList;
    }

    public List<AuditoriaDTO> getByModulo(String modulo) {
        List<Auditoria> entityList = auditoriaLogRepo.findByModulo(modulo);
        List<AuditoriaDTO> dtoList = new ArrayList<>();
        entityList.forEach(entity -> dtoList.add(mapper.map(entity, AuditoriaDTO.class)));
        return dtoList;
    }

    public List<AuditoriaDTO> getByExitoso(boolean exitoso) {
        List<Auditoria> entityList = auditoriaLogRepo.findByExitoso(exitoso);
        List<AuditoriaDTO> dtoList = new ArrayList<>();
        entityList.forEach(entity -> dtoList.add(mapper.map(entity, AuditoriaDTO.class)));
        return dtoList;
    }
}