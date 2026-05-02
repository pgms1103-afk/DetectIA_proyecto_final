package co.edu.unbosque.detectia.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import co.edu.unbosque.detectia.dto.ArchivoDTO;
import co.edu.unbosque.detectia.entity.Archivo;
import co.edu.unbosque.detectia.entity.Usuario;
import co.edu.unbosque.detectia.repository.ArchivoRepository;
import co.edu.unbosque.detectia.repository.UsuarioRepository;

public class ArchivoService implements CRUDoperation<ArchivoDTO>{
	
	@Autowired
	private ArchivoRepository archivoRepo;
	
	@Autowired
	private UsuarioRepository usuarioRepo;
	
	@Autowired
	private ModelMapper mapper;
	
	public ArchivoService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int create(ArchivoDTO data) {
		Archivo entity = mapper.map(data, Archivo.class);
		entity.setId(null);
		entity.setFechaSubida(LocalDateTime.now());
		archivoRepo.save(entity);
		return 0;
	}

	@Override
	public List<ArchivoDTO> getAll() {
		List<Archivo> entityList = (List<Archivo>) archivoRepo.findAll();
		List<ArchivoDTO> dtoList = new ArrayList<>();
		entityList.forEach((entity) -> {
			ArchivoDTO dto = mapper.map(entity, ArchivoDTO.class);
			dtoList.add(dto);
		});
		
		return dtoList;
		
	}

	@Override
	public int delateById(Long id) {
		Optional<Archivo> encontrado = archivoRepo.findById(id);
		if(encontrado.isPresent()) {
			ArchivoDTO dto =mapper.map(encontrado.get(), ArchivoDTO.class);
			Archivo entity = mapper.map(dto, Archivo.class);
			archivoRepo.delete(entity);
			return 0;
		}
		
		return 1;
	}

	@Override
	public int updateById(Long id, ArchivoDTO data) {
		Optional<Archivo> encontrado = (Optional<Archivo>) archivoRepo.findById(id);
		
		if(encontrado.isPresent()) {
			Archivo temp = encontrado.get();
			temp.setNombre(data.getNombre());
			temp.setTipo(data.getTipo());
			temp.setFechaSubida(data.getFechaSubida());
			temp.setRutaAlmacenamiento(data.getRutaAlmacenamiento());
			archivoRepo.save(temp);
			return 0;
		}
		
		return 1;
		
	}
	
	public List<ArchivoDTO> getArchivosByCorreo(String correo){
		Optional<Usuario> usuarioEncontrado = usuarioRepo.findByCorreo(correo);
		if(usuarioEncontrado.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<Archivo> entityList = archivoRepo.findByUsuario(usuarioEncontrado.get());
		List<ArchivoDTO> dtoList = new ArrayList<>();
		entityList.forEach((entity) -> {
			ArchivoDTO dto = mapper.map(entity, ArchivoDTO.class);
			dtoList.add(dto);
		});
		
		return dtoList;
	}
	
	
	
}
