package co.edu.unbosque.detectia.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.detectia.dto.AnalisisDTO;
import co.edu.unbosque.detectia.entity.Analisis;
import co.edu.unbosque.detectia.entity.Usuario;
import co.edu.unbosque.detectia.repository.AnalisisRepository;
import co.edu.unbosque.detectia.repository.UsuarioRepository;

@Service
public class AnalisisService implements CRUDoperation<AnalisisDTO> {

	@Autowired
	private AnalisisRepository analisisRepo;

	@Autowired
	private UsuarioRepository usuarioRepo;

	@Autowired
	private ModelMapper mapper;

	public AnalisisService() {
	}

	@Override
	public int create(AnalisisDTO data) {
		Analisis entity = mapper.map(data, Analisis.class);
		entity.setId(null);
		entity.setFechaAnalisis(LocalDateTime.now());
		analisisRepo.save(entity);
		return 0;
	}

	@Override
	public List<AnalisisDTO> getAll() {
		List<Analisis> entityList = analisisRepo.findAll();
		List<AnalisisDTO> dtoList = new ArrayList<>();
		entityList.forEach((entity) -> {
			AnalisisDTO dto = mapper.map(entity, AnalisisDTO.class);
			dtoList.add(dto);
		});
		return dtoList;
	}

	@Override
	public int delateById(Long id) {
		Optional<Analisis> encontrado = analisisRepo.findById(id);
		if (encontrado.isPresent()) {
			AnalisisDTO dto = mapper.map(encontrado.get(), AnalisisDTO.class);
			Analisis entity = mapper.map(dto, Analisis.class);
			analisisRepo.delete(entity);
			return 0;
		}
		return 1;
	}

	@Override
	public int updateById(Long id, AnalisisDTO data) {
		Optional<Analisis> encontrado = analisisRepo.findById(id);
		if (encontrado.isPresent()) {
			Analisis temp = encontrado.get();
			temp.setPorcentajeFinal(data.getPorcentajeFinal());
			temp.setVeredicto(data.getVeredicto());
			temp.setIaRecomendada(data.getIaRecomendada());
			analisisRepo.save(temp);
			return 0;
		}
		return 1;
	}

	public List<AnalisisDTO> getAnalisisByCorreo(String correo) {
		Optional<Usuario> usuarioEncontrado = usuarioRepo.findByCorreo(correo);
		if (usuarioEncontrado.isEmpty()) {
			return new ArrayList<>();
		}
		List<Analisis> entityList = analisisRepo.findByUsuario(usuarioEncontrado.get());
		List<AnalisisDTO> dtoList = new ArrayList<>();
		entityList.forEach((entity) -> {
			AnalisisDTO dto = mapper.map(entity, AnalisisDTO.class);
			dtoList.add(dto);
		});
		return dtoList;
	}

}