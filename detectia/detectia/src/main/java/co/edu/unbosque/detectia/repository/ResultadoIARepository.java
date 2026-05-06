package co.edu.unbosque.detectia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import co.edu.unbosque.detectia.entity.Archivo;
import co.edu.unbosque.detectia.entity.ResultadoIA;

public interface ResultadoIARepository extends JpaRepository<ResultadoIA, Long>{

	public List<ResultadoIA> findByArchivo(Archivo archivo);
	
}
