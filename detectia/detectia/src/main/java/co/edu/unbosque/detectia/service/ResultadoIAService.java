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
public class ResultadoIAService{

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


	public void guardarResultados(Map<String, Double> votosIAs, Archivo archivoReciente) {
	    
	    if (archivoReciente == null) return;

	    for (Map.Entry<String, Double> voto : votosIAs.entrySet()) {
	        ResultadoIA entity = new ResultadoIA();
	        entity.setNombreIA(voto.getKey());
	        entity.setPorcentajeIA(voto.getValue());
	        entity.setFechaAnalisis(LocalDateTime.now());	    
	        entity.setArchivo(archivoReciente);              
	        
	        resultadoIARepo.save(entity);
	    }
	}
	

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
	
	public List<ResultadoIADTO> getResultadosByArchivoId(long id) {
	    List<ResultadoIADTO> dtoList = new ArrayList<>();
	    
	    Optional<Archivo> archivoOpt = archivoRepo.findById(id);

	    if (archivoOpt.isPresent()) {
	        Archivo archivoEncontrado = archivoOpt.get();
	        
	        List<ResultadoIA> resultados = resultadoIARepo.findByArchivo(archivoEncontrado);
	        
	        resultados.forEach(entity -> {
	            ResultadoIADTO dto = mapper.map(entity, ResultadoIADTO.class);
	            dtoList.add(dto);
	        });
	    }
	    
	    return dtoList;
	}
	

	
	
	



}