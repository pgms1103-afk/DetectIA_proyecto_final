package co.edu.unbosque.detectia.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.detectia.dto.ResultadoIADTO;
import co.edu.unbosque.detectia.entity.Archivo;
import co.edu.unbosque.detectia.entity.ResultadoIA;
import co.edu.unbosque.detectia.repository.ArchivoRepository;
import co.edu.unbosque.detectia.repository.ResultadoIARepository;

@Service
public class ResultadoIAService implements CRUDoperation<ResultadoIADTO> {

	@Autowired
	private ResultadoIARepository resultadoIARepo;

	@Autowired
	private ArchivoRepository archivoRepo;

	@Autowired
	private ModelMapper mapper;

	public ResultadoIAService() {
	}

	@Override
	public int create(ResultadoIADTO data) {
		ResultadoIA entity = mapper.map(data, ResultadoIA.class);
		entity.setId(null);
		entity.setFechaAnalisis(LocalDateTime.now());
		resultadoIARepo.save(entity);
		return 0;
	}

	@Override
	public List<ResultadoIADTO> getAll() {
		List<ResultadoIA> entityList = resultadoIARepo.findAll();
		List<ResultadoIADTO> dtoList = new ArrayList<>();
		entityList.forEach((entity) -> {
			ResultadoIADTO dto = mapper.map(entity, ResultadoIADTO.class);
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

	public List<ResultadoIADTO> getResultadosByArchivoId(Long archivoId) {
		Optional<Archivo> archivoEncontrado = archivoRepo.findById(archivoId);
		if (archivoEncontrado.isEmpty()) {
			return new ArrayList<>();
		}
		List<ResultadoIA> entityList = resultadoIARepo.findByArchivo(archivoEncontrado.get());
		List<ResultadoIADTO> dtoList = new ArrayList<>();
		entityList.forEach((entity) -> {
			ResultadoIADTO dto = mapper.map(entity, ResultadoIADTO.class);
			dtoList.add(dto);
		});
		return dtoList;
	}

}