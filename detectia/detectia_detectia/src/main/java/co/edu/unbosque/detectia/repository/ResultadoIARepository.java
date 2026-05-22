package co.edu.unbosque.detectia.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import co.edu.unbosque.detectia.entity.Archivo;
import co.edu.unbosque.detectia.entity.ResultadoIA;
import co.edu.unbosque.detectia.entity.Usuario;

public interface ResultadoIARepository extends JpaRepository<ResultadoIA, Long>{

	public List<ResultadoIA> findByArchivo(Archivo archivo);
	List<ResultadoIA> findByArchivo_Usuario_Correo(String correo);
	//public List<ResultadoIA> findByNombreUsuario(String nombreArchivo);
	//public List<ResultadoIA> findByUsuario(Usuario usuario);
	
}
