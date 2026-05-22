package co.edu.unbosque.detectia.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import co.edu.unbosque.detectia.entity.Archivo;
import co.edu.unbosque.detectia.entity.ResultadoIA;
import co.edu.unbosque.detectia.entity.Usuario;

public interface ArchivoRepository extends CrudRepository<Archivo, Long>{
	
	public List<Archivo> findByUsuario(Usuario usuario);
	public List<Archivo> findByNombre(String nombre);



}
