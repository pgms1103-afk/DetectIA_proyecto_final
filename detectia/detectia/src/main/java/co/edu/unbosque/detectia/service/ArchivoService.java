package co.edu.unbosque.detectia.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.unbosque.detectia.dto.ArchivoDTO;
import co.edu.unbosque.detectia.entity.Archivo;
import co.edu.unbosque.detectia.entity.ResultadoIA;
import co.edu.unbosque.detectia.entity.Usuario;
import co.edu.unbosque.detectia.repository.ArchivoRepository;
import co.edu.unbosque.detectia.repository.ResultadoIARepository;
import co.edu.unbosque.detectia.repository.UsuarioRepository;

@Service
public class ArchivoService implements CRUDoperation<ArchivoDTO>{
	
	@Autowired
	private ArchivoRepository archivoRepo;
	
	@Autowired
	private UsuarioRepository usuarioRepo;

	
	@Autowired
	private ModelMapper mapper;
	


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
	        return archivoRepo.save(entity);
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
			dto.setUsername(entity.getUsuario() != null ? entity.getUsuario().getNombreUsuario(): null);
			dto.setUsuarioId(entity.getUsuario() != null ? entity.getUsuario().getId(): null);
			dtoList.add(dto);
		});
		return dtoList;
	}

	@Override
	public int delateById(Long id) {
		Optional<Archivo> encontrado = archivoRepo.findById(id);
		if(encontrado.isPresent()) {
			archivoRepo.delete(encontrado.get());
			return 0;
		}else {
			return 1;
		}
	}

	@Override
	public int updateById(Long id, ArchivoDTO data) {
		Optional<Archivo> encontrado = (Optional<Archivo>) archivoRepo.findById(id);
		if(encontrado.isPresent()) {
			Archivo temp = encontrado.get();
			temp.setNombre(data.getNombre());
			temp.setFechaSubida(data.getFechaSubida());
			temp.setRutaAlmacenamiento(data.getRutaAlmacenamiento());
			archivoRepo.save(temp);
			return 0;
		}	
		return 1;
	}
	
	public List<ArchivoDTO> getArchivosByuser(String username){
		Optional<Usuario> usuarioEncontrado = usuarioRepo.findByNombreUsuario(username);
		if(usuarioEncontrado.isEmpty()) {
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
			dto.setUsername(entity.getUsuario() != null ? entity.getUsuario().getNombreUsuario(): null);
			dto.setUsuarioId(entity.getUsuario() != null ? entity.getUsuario().getId(): null);
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
	
	
	}
	
	
	

