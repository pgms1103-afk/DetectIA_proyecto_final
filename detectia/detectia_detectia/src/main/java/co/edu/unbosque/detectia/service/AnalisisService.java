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
	
	// 1. Cambiamos String nombreArchivo por Archivo archivoReciente
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
	    
	    // 2. 🟢 Borramos la búsqueda traicionera (List<Archivo> archivo = archivoRepo.findByNombre...)
	    
	    Analisis entity = new Analisis();
	    entity.setVeredicto(veredicto);
	    entity.setPorcentajeFinal(Math.round(promedio * 100.0) / 100.0);
	    
	    // 3. 🟢 Le asignamos el archivo directamente 
	    entity.setArchivo(archivoReciente);
	    analisisRepo.save(entity);
	    
	    // 4. Tu código intacto, devolviendo exactamente el mismo JSON de siempre
	    Map<String, Object> resumen = new HashMap<>();
	    resumen.put("resultados", votosIAs);
	    resumen.put("promedio", Math.round(promedio * 100.0) / 100.0);
	    resumen.put("veredicto", veredicto);
	    
	    return resumen;
	}
	
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
