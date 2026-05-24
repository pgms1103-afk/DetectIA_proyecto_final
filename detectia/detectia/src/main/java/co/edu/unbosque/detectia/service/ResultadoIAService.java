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

/**
 * Servicio de persistencia y consulta de resultados individuales de cada
 * servicio de IA.
 * <p>
 * Almacena en la base de datos el porcentaje de detección emitido por cada
 * servicio externo (Grok, Gemini, Mistral, etc.) para un archivo concreto,
 * y expone métodos de listado y consulta por archivo.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 * @see co.edu.unbosque.detectia.entity.ResultadoIA
 * @see co.edu.unbosque.detectia.repository.ResultadoIARepository
 */
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


	/**
	 * Persiste un {@link ResultadoIA} por cada entrada del mapa de votos,
	 * asociando cada resultado al archivo indicado.
	 * <p>
	 * Si {@code archivoReciente} es {@code null} el método retorna inmediatamente
	 * sin guardar nada.
	 * </p>
	 *
	 * @param votosIAs        mapa con los porcentajes de detección por servicio de IA
	 * @param archivoReciente archivo al que se asocian los resultados; si es
	 *                        {@code null} no se guarda ningún resultado
	 */
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
	
	
	/**
	 * Recupera todos los resultados de IA almacenados.
	 *
	 * @return lista de {@link ResultadoIADTO} con todos los resultados; lista vacía
	 *         si no hay ninguno
	 */
	public List<ResultadoIADTO> getAll() {
		List<ResultadoIA> entityList = (List<ResultadoIA>) resultadoIARepo.findAll();
		List<ResultadoIADTO> dtoList = new ArrayList<>();
		entityList.forEach(entity -> {
			ResultadoIADTO dto = new ResultadoIADTO();
			dto.setId(entity.getId());
			dto.setNombreIA(entity.getNombreIA());
			dto.setPorcentajeIA(entity.getPorcentajeIA());
			dto.setFechaAnalisis(entity.getFechaAnalisis());
			dto.setNombreArchivo(entity.getArchivo() != null ? entity.getArchivo().getNombre() : null);
			dtoList.add(dto);
		});
		return dtoList;
	}

	/**
	 * Recupera todos los resultados de IA asociados al archivo identificado por
	 * {@code id}.
	 *
	 * @param id identificador del archivo cuyos resultados se desean consultar
	 * @return lista de {@link ResultadoIADTO} con los resultados encontrados, o
	 *         lista vacía si el archivo no existe o no tiene resultados
	 */
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