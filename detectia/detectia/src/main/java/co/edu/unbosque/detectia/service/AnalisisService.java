package co.edu.unbosque.detectia.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.detectia.dto.AnalisisDTO;
import co.edu.unbosque.detectia.entity.Analisis;
import co.edu.unbosque.detectia.entity.Archivo;
import co.edu.unbosque.detectia.entity.Usuario;
import co.edu.unbosque.detectia.repository.AnalisisRepository;
import co.edu.unbosque.detectia.repository.ArchivoRepository;
import co.edu.unbosque.detectia.repository.UsuarioRepository;

/**
 * Servicio de negocio para el cálculo, almacenamiento y consulta de análisis
 * de contenido generado por IA.
 * <p>
 * Recibe los votos de porcentaje de IA emitidos por los diferentes servicios
 * externos (Grok, Gemini, Mistral, Sightengine, etc.), calcula el promedio
 * ponderado, determina el veredicto final ({@code "PROBABLE IA"} o
 * {@code "PROBABLE HUMANO"}) y persiste la entidad {@link co.edu.unbosque.detectia.entity.Analisis}
 * en la base de datos.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 * @see co.edu.unbosque.detectia.entity.Analisis
 * @see co.edu.unbosque.detectia.repository.AnalisisRepository
 */
@Service
public class AnalisisService {
	
	@Autowired
	private AnalisisRepository analisisRepo;
	
	@Autowired
	private ArchivoRepository archivoRepo;
	
	@Autowired
	private UsuarioRepository usuarioRepo;
	
	@Autowired
	private ModelMapper mapper;
	
	/**
	 * Calcula el resumen del análisis a partir de los votos de porcentaje
	 * proporcionados por los servicios de IA, persiste la entidad
	 * {@link co.edu.unbosque.detectia.entity.Analisis} y retorna un mapa con los
	 * resultados, el promedio y el veredicto final.
	 *
	 * @param votosIAs        mapa donde cada clave es el nombre del servicio de IA y
	 *                        cada valor es su porcentaje de detección (0-100)
	 * @param archivoReciente entidad {@link co.edu.unbosque.detectia.entity.Archivo}
	 *                        recién guardada a la que se asocia el análisis
	 * @return mapa con tres entradas: {@code "resultados"} (los votos originales),
	 *         {@code "promedio"} (promedio redondeado a 2 decimales) y
	 *         {@code "veredicto"} ({@code "PROBABLE IA"} o {@code "PROBABLE HUMANO"})
	 */
	public Map<String,Object> calcularResumen(Map<String,Double> votosIAs, Archivo archivoReciente) {
	    double promedio = 0;
	    if(!votosIAs.isEmpty()) {
	        double suma = 0;
	        for(Double porcentaje : votosIAs.values()) {
	            suma += porcentaje;
	        }
	        promedio = suma/votosIAs.size();
	    }
	    
	    String veredicto;
	    if (promedio >= 50) {
	        veredicto = "PROBABLE IA";
	    } else {
	        veredicto = "PROBABLE HUMANO";
	    }
	    
	    
	    Analisis entity = new Analisis();
	    entity.setVeredicto(veredicto);
	    entity.setPorcentajeFinal(Math.round(promedio * 100.0) / 100.0);
	    
	    entity.setArchivo(archivoReciente);
	    analisisRepo.save(entity);
	    
	    Map<String, Object> resumen = new HashMap<>();
	    resumen.put("resultados", votosIAs);
	    resumen.put("promedio", Math.round(promedio * 100.0) / 100.0);
	    resumen.put("veredicto", veredicto);
	    
	    return resumen;
	}
	
	/**
	 * Recupera todos los análisis asociados a los archivos de un usuario dado.
	 *
	 * @param username nombre de usuario del propietario de los archivos
	 * @return lista de {@link AnalisisDTO} con los análisis encontrados, o lista
	 *         vacía si el usuario no existe o no tiene archivos analizados
	 */
	public List<AnalisisDTO> getResultadosByUsuario(String username) {
	    Optional<Usuario> usuario = usuarioRepo.findByNombreUsuario(username);
	    if (usuario.isEmpty()) return new ArrayList<>();

	    List<Archivo> archivos = archivoRepo.findByUsuario(usuario.get());

	    List<AnalisisDTO> dtoList = new ArrayList<>();
	    archivos.forEach(archivo -> {
	        List<Analisis> resultados = analisisRepo.findByArchivo(archivo);
	        resultados.forEach(entity -> {
	        	AnalisisDTO dto = mapper.map(entity, AnalisisDTO.class);
	            dtoList.add(dto);
	        });
	    });
	    return dtoList;
	}
	
	/**
	 * Recupera todos los análisis asociados al archivo identificado por {@code id}.
	 *
	 * @param id identificador del archivo cuyos análisis se desean consultar
	 * @return lista de {@link AnalisisDTO} con los análisis encontrados, o lista
	 *         vacía si el archivo no existe o no tiene análisis registrados
	 */
	public List<AnalisisDTO> getAnalisisById(long id){
		List<AnalisisDTO> dtoList = new ArrayList<>();
		Optional<Archivo> archivoOpt = archivoRepo.findById(id);
		
		if(archivoOpt.isPresent()) {
			Archivo archivosEncontrados = archivoOpt.get();
			List<Analisis> analisis = analisisRepo.findByArchivo(archivosEncontrados);
			analisis.forEach(entity -> {
				AnalisisDTO dto = mapper.map(entity, AnalisisDTO.class);
				dtoList.add(dto);
			});
		}
		return dtoList;
	}

}
