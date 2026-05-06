package co.edu.unbosque.detectia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.detectia.entity.Analisis;
import co.edu.unbosque.detectia.entity.Archivo;
import co.edu.unbosque.detectia.entity.Usuario;

public interface AnalisisRepository extends JpaRepository<Analisis, Long>{
	
	public List<Analisis> findByArchivo(Archivo archivo);
	public List<Analisis> findByUsuario(Usuario usuario);

}
