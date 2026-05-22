package co.edu.unbosque.detectia.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import co.edu.unbosque.detectia.dto.ArchivoDTO;
import co.edu.unbosque.detectia.entity.Archivo;
import co.edu.unbosque.detectia.entity.ResultadoIA;
import co.edu.unbosque.detectia.entity.Usuario;
import co.edu.unbosque.detectia.repository.ArchivoRepository;
import co.edu.unbosque.detectia.repository.ResultadoIARepository;
import co.edu.unbosque.detectia.repository.UsuarioRepository;

@Service
public class ArchivoService implements CRUDoperation<ArchivoDTO> {

	@Autowired
	private ArchivoRepository archivoRepo;

	@Autowired
	private UsuarioRepository usuarioRepo;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private AuditoriaLogService auditoriaLogSer;

	@Override
	public int create(ArchivoDTO data) {

		Optional<Usuario> encontrado = usuarioRepo.findById(data.getUsuarioId());
		if (encontrado.isEmpty()) {
			return 1;
		} else {
			Archivo entity = mapper.map(data, Archivo.class);
			archivoRepo.save(entity);
			return 0;
		}

	}

	public Archivo createAndReturn(ArchivoDTO data) {

		Optional<Usuario> encontrado = usuarioRepo.findById(data.getUsuarioId());
		if (encontrado.isEmpty()) {
			return null;
		} else {
			Archivo entity = new Archivo();
			entity.setNombre(data.getNombre());
			entity.setRutaAlmacenamiento(data.getRutaAlmacenamiento());
			entity.setFechaSubida(data.getFechaSubida());
			entity.setUsuario(encontrado.get()); // ← relación correctamente seteada

			Archivo guardado = archivoRepo.save(entity);

			String nombreUsuario = encontrado.get().getNombreUsuario();
			String correoUsuario = encontrado.get().getCorreo();
			auditoriaLogSer.registrarAuditoria(correoUsuario, nombreUsuario, "SUBIR_ARCHIVO", "ARCHIVOS",
					"Se subió el archivo: " + data.getNombre(), null, null, null, data.getNombre(), "Archivo",
					String.valueOf(guardado.getId()), true);

			return guardado;
		}
	}

	@Override
	public List<ArchivoDTO> getAll() {
		List<Archivo> entityList = (List<Archivo>) archivoRepo.findAll();
		List<ArchivoDTO> dtoList = new ArrayList<>();
		entityList.forEach((entity) -> {
			ArchivoDTO dto = new ArchivoDTO();
			dto.setId(entity.getId());
			dto.setNombre(entity.getNombre());
			dto.setRutaAlmacenamiento(entity.getRutaAlmacenamiento());
			dto.setFechaSubida(entity.getFechaSubida());
			dto.setUsername(entity.getUsuario() != null ? entity.getUsuario().getNombreUsuario() : null);
			dto.setUsuarioId(entity.getUsuario() != null ? entity.getUsuario().getId() : null);
			dtoList.add(dto);
		});
		return dtoList;
	}

	@Override
	public int delateById(Long id) {
		Optional<Archivo> encontrado = archivoRepo.findById(id);
		if (encontrado.isPresent()) {
			Archivo archivo = encontrado.get();
			String nombreUsuario = archivo.getUsuario() != null ? archivo.getUsuario().getNombreUsuario()
					: "desconocido";
			String correoUsuario = archivo.getUsuario() != null ? archivo.getUsuario().getCorreo() : null;
			auditoriaLogSer.registrarAuditoria(correoUsuario, nombreUsuario, "ELIMINAR_ARCHIVO", "ARCHIVOS",
					"Se eliminó el archivo: " + archivo.getNombre(), null, null, archivo.getNombre(), null, "Archivo",
					String.valueOf(id), true);

			archivoRepo.delete(encontrado.get());
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public int updateById(Long id, ArchivoDTO data) {
		Optional<Archivo> encontrado = (Optional<Archivo>) archivoRepo.findById(id);
		if (encontrado.isPresent()) {
			Archivo temp = encontrado.get();
			
			String nombreAnterior = temp.getNombre();
			
			temp.setNombre(data.getNombre());
			temp.setFechaSubida(data.getFechaSubida());
			temp.setRutaAlmacenamiento(data.getRutaAlmacenamiento());
			archivoRepo.save(temp);

			String nombreUsuario = temp.getUsuario() != null ? temp.getUsuario().getNombreUsuario() : "desconocido";
			String correoUsuario = temp.getUsuario() != null ? temp.getUsuario().getCorreo() : null;
			auditoriaLogSer.registrarAuditoria(correoUsuario, nombreUsuario, "ACTUALIZAR_ARCHIVO", "ARCHIVOS",
					"Se actualizó el archivo con id: " + id, null, null, nombreAnterior, data.getNombre(), "Archivo",
					String.valueOf(id), true);

			return 0;
		}
		return 1;
	}

	public List<ArchivoDTO> getArchivosByuser(String username) {
		Optional<Usuario> usuarioEncontrado = usuarioRepo.findByNombreUsuario(username);
		if (usuarioEncontrado.isEmpty()) {
			return new ArrayList<>();
		}

		List<Archivo> entityList = archivoRepo.findByUsuario(usuarioEncontrado.get());
		List<ArchivoDTO> dtoList = new ArrayList<>();
		entityList.forEach((entity) -> {
			ArchivoDTO dto = new ArchivoDTO();
			dto.setId(entity.getId());
			dto.setNombre(entity.getNombre());
			dto.setRutaAlmacenamiento(entity.getRutaAlmacenamiento());
			dto.setFechaSubida(entity.getFechaSubida());
			dto.setUsername(entity.getUsuario() != null ? entity.getUsuario().getNombreUsuario() : null);
			dto.setUsuarioId(entity.getUsuario() != null ? entity.getUsuario().getId() : null);
			dtoList.add(dto);
		});

		return dtoList;
	}

	public ArchivoDTO getById(Long id) {
		Optional<Archivo> encontrado = archivoRepo.findById(id);
		if (encontrado.isPresent()) {
			return mapper.map(encontrado.get(), ArchivoDTO.class);
		}
		return null;
	}
	
	public List<ArchivoDTO> findArchivoByNombre(String nombreArchivo, String nombreUsuario) {
	    Optional<Usuario> entity = usuarioRepo.findByNombreUsuario(nombreUsuario);
	    if (entity.isEmpty()) {
	        throw new UsernameNotFoundException("Usuario no encontrado");
	    }
	    List<Archivo> archivos = archivoRepo.findByNombreAndUsuarioId(nombreArchivo, entity.get().getId());
	    if (archivos.isEmpty()) {
	    	return Collections.emptyList();
	    }
	    return archivos.stream()
	            .map(a -> mapper.map(a, ArchivoDTO.class))
	            .toList();
	}

}
