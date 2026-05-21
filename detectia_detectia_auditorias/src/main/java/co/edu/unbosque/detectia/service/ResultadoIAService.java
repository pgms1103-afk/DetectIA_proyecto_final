package co.edu.unbosque.detectia.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.detectia.dto.ResultadoIADTO;
import co.edu.unbosque.detectia.entity.Archivo;
import co.edu.unbosque.detectia.entity.ResultadoIA;
import co.edu.unbosque.detectia.entity.Usuario;
import co.edu.unbosque.detectia.repository.ArchivoRepository;
import co.edu.unbosque.detectia.repository.ResultadoIARepository;
import co.edu.unbosque.detectia.repository.UsuarioRepository;

@Service
public class ResultadoIAService implements CRUDoperation<ResultadoIADTO> {

	@Autowired
	private ResultadoIARepository resultadoIARepo;

	@Autowired
	private ArchivoRepository archivoRepo;
	
	@Autowired
	private UsuarioRepository usuarioRepo;
	
	@Autowired
	private ModelMapper mapper;

	public ResultadoIAService() {
	}

	@Override
	public int create(ResultadoIADTO data) {
		ResultadoIA entity = mapper.map(data, ResultadoIA.class);
		entity.setFechaAnalisis(LocalDateTime.now());
		resultadoIARepo.save(entity);
		return 0;
	}

	@Override
	public List<ResultadoIADTO> getAll() {
		List<ResultadoIA> entityList = resultadoIARepo.findAll();
		List<ResultadoIADTO> dtoList = new ArrayList<>();
		entityList.forEach((entity) -> {
			ResultadoIADTO dto = new ResultadoIADTO();
			dto.setId(entity.getId());
			dto.setNombreArchivo(entity.getArchivo() != null ? entity.getArchivo().getNombre() : null);
			dto.setNombreIA(entity.getNombreIA());
			dto.setPorcentajeIA(entity.getPorcentajeIA());
			dto.setFechaAnalisis(entity.getFechaAnalisis());
			dtoList.add(dto);
		});
		return dtoList;
	}

	@Override
	public int delateById(Long id) {
		Optional<ResultadoIA> encontrado = resultadoIARepo.findById(id);
		if (encontrado.isPresent()) {
			ResultadoIADTO dto = mapper.map(encontrado.get(), ResultadoIADTO.class);
			ResultadoIA entity = mapper.map(dto, ResultadoIA.class);
			resultadoIARepo.delete(entity);
			return 0;
		}
		return 1;
	}

	@Override
	public int updateById(Long id, ResultadoIADTO data) {
		Optional<ResultadoIA> encontrado = resultadoIARepo.findById(id);
		if (encontrado.isPresent()) {
			ResultadoIA temp = encontrado.get();
			temp.setNombreIA(data.getNombreIA());
			temp.setPorcentajeIA(data.getPorcentajeIA());
			resultadoIARepo.save(temp);
			return 0;
		}
		return 1;
	}

	public List<ResultadoIADTO> getResultadosByUsuario(String username) {
	    Optional<Usuario> usuario = usuarioRepo.findByNombreUsuario(username);
	    if (usuario.isEmpty()) return new ArrayList<>();

	    List<Archivo> archivos = archivoRepo.findByUsuario(usuario.get());

	    List<ResultadoIADTO> dtoList = new ArrayList<>();
	    archivos.forEach(archivo -> {
	        List<ResultadoIA> resultados = resultadoIARepo.findByArchivo(archivo);
	        resultados.forEach(entity -> {
	            ResultadoIADTO dto = mapper.map(entity, ResultadoIADTO.class);
	            dtoList.add(dto);
	        });
	    });
	    return dtoList;
	}

//	public List<ResultadoIADTO> getResultadosByUser(String user) {
//
//		List<ResultadoIA> entityList = archivoRepo.findResultadoByUsuario(user);
//
//		List<ResultadoIADTO> dtoList = new ArrayList<>();
//
//		entityList.forEach(entity -> {
//			dtoList.add(mapper.map(entity, ResultadoIADTO.class));
//		});
//
//		return dtoList;
//	}

	public void guardarResultados(Map<String, Double> votosIAs, String nombreArchivo) {
	    List<Archivo> archivo = archivoRepo.findByNombre(nombreArchivo);
	    if (archivo.isEmpty()) return;

	    for (Map.Entry<String, Double> voto : votosIAs.entrySet()) {
	        ResultadoIA entity = new ResultadoIA();
	        entity.setNombreIA(voto.getKey());
	        entity.setPorcentajeIA(voto.getValue());
	        entity.setFechaAnalisis(LocalDateTime.now());
	        entity.setArchivo(archivo.get(0));              
	        resultadoIARepo.save(entity);
	    }
	}

//	public List<ResultadoIADTO> getResultadosByCorreo (String correo){{
//	Optional<Usuario> usuarioEncontrado = usuarioRepo.findByCorreo(correo);	
//	if(usuarioEncontrado.isEmpty()) {
//		return new ArrayList<>();
//	}
//	
//	List<ResultadoIA> entityList = resultadoIARepo.findByUsuario(usuarioEncontrado.get());
//	List<ResultadoIADTO> dtoList = new ArrayList<>();
//	entityList.forEach((entity) -> {
//		ResultadoIADTO dto = mapper.map(entity, ResultadoIADTO.class);
//		dtoList.add(dto);
//	});
//	return dtoList;
//}


}