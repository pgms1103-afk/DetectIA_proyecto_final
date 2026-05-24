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

/**
 * Servicio de negocio para la gestión del ciclo de vida de los archivos
 * subidos a la plataforma DetectIA.
 * <p>
 * Implementa las operaciones CRUD básicas definidas en
 * {@link CRUDoperation}&lt;{@link ArchivoDTO}&gt; y extiende la funcionalidad
 * con creación con retorno de entidad, consulta por usuario y renombrado.
 * Cada operación de escritura registra el evento en el sistema de auditoría
 * mediante {@link AuditoriaLogService}.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 * @see CRUDoperation
 * @see co.edu.unbosque.detectia.entity.Archivo
 * @see co.edu.unbosque.detectia.repository.ArchivoRepository
 */
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

	/**
	 * Crea y persiste un archivo asociándolo al usuario dueño, registra el evento
	 * en auditoría y retorna la entidad guardada.
	 *
	 * @param data DTO con los datos del archivo a crear, incluyendo el ID de usuario
	 * @return la entidad {@link co.edu.unbosque.detectia.entity.Archivo} persistida,
	 *         o {@code null} si el usuario no existe
	 */
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

	/**
	 * Recupera todos los archivos pertenecientes al usuario identificado por
	 * {@code username}.
	 *
	 * @param username nombre de usuario del propietario
	 * @return lista de {@link ArchivoDTO} con los archivos del usuario, o lista
	 *         vacía si el usuario no existe o no tiene archivos
	 */
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

	/**
	 * Recupera el archivo identificado por {@code id} y lo retorna como DTO.
	 *
	 * @param id identificador del archivo
	 * @return {@link ArchivoDTO} correspondiente, o {@code null} si no existe
	 */
	public ArchivoDTO getById(Long id) {
		Optional<Archivo> encontrado = archivoRepo.findById(id);
		if (encontrado.isPresent()) {
			return mapper.map(encontrado.get(), ArchivoDTO.class);
		}
		return null;
	}

	/**
	 * Busca archivos por nombre dentro de los archivos del usuario indicado.
	 *
	 * @param nombreArchivo nombre del archivo a buscar
	 * @param nombreUsuario nombre de usuario propietario de los archivos
	 * @return lista de {@link ArchivoDTO} que coinciden con el nombre; lista vacía
	 *         si no hay coincidencias
	 * @throws org.springframework.security.core.userdetails.UsernameNotFoundException
	 *         si el usuario no existe
	 */
	public List<ArchivoDTO> findArchivoByNombre(String nombreArchivo, String nombreUsuario) {
		Optional<Usuario> entity = usuarioRepo.findByNombreUsuario(nombreUsuario);
		if (entity.isEmpty()) {
			throw new UsernameNotFoundException("Usuario no encontrado");
		}
		List<Archivo> archivos = archivoRepo.findByNombreAndUsuarioId(nombreArchivo, entity.get().getId());
		if (archivos.isEmpty()) {
			return Collections.emptyList();
		}
		return archivos.stream().map(a -> mapper.map(a, ArchivoDTO.class)).toList();
	}

	/**
	 * Actualiza únicamente el nombre del archivo identificado por {@code id},
	 * verificando que el usuario propietario exista.
	 *
	 * @param id            identificador del archivo a renombrar
	 * @param nombre        nuevo nombre para el archivo
	 * @param nombreUsuario nombre de usuario del propietario (para verificación)
	 * @return {@code 1} si la actualización fue exitosa; {@code 0} si el archivo
	 *         no fue encontrado
	 * @throws org.springframework.security.core.userdetails.UsernameNotFoundException
	 *         si el usuario no existe
	 */
	public int updateNombreById(Long id, String nombre, String nombreUsuario) {
	    Optional<Usuario> entity = usuarioRepo.findByNombreUsuario(nombreUsuario);
	    if (entity.isEmpty()) {
	        throw new UsernameNotFoundException("Usuario no encontrado");
	    }

	    Optional<Archivo> encontrado = archivoRepo.findById(id);
	    if (encontrado.isEmpty()) {
	        return 0;
	    }

	    Archivo archivo = encontrado.get();
	    archivo.setNombre(nombre);
	    archivoRepo.save(archivo);
	    return 1;
	}

}
