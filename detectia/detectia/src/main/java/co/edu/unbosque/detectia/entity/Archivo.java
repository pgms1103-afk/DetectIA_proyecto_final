package co.edu.unbosque.detectia.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "archivo")
public class Archivo {
	
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
	private String nombre;
	private String rutaAlmacenamiento;
	private LocalDateTime fechaSubida;
	
	@OneToMany(mappedBy = "archivo", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ResultadoIA> resultados = new ArrayList<>();
	
	@OneToMany(mappedBy = "archivo", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Analisis> analisis = new ArrayList<>();
	
	@ManyToOne
	@JoinColumn(name ="usuario_id")
	private Usuario usuario;
	
	public Archivo() {
		this.fechaSubida = LocalDateTime.now();
	}

	public Archivo(String nombre, String rutaAlmacenamiento, LocalDateTime fechaSubida, Usuario usuario) {
		super();
		this.nombre = nombre;
		this.rutaAlmacenamiento = rutaAlmacenamiento;
		this.fechaSubida = fechaSubida;
		this.usuario = usuario;
	}

	
	public List<ResultadoIA> getResultados() {
		return resultados;
	}

	public void setResultados(List<ResultadoIA> resultados) {
		this.resultados = resultados;
	}

	public List<Analisis> getAnalisis() {
		return analisis;
	}

	public void setAnalisis(List<Analisis> analisis) {
		this.analisis = analisis;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getRutaAlmacenamiento() {
		return rutaAlmacenamiento;
	}

	public void setRutaAlmacenamiento(String rutaAlmacenamiento) {
		this.rutaAlmacenamiento = rutaAlmacenamiento;
	}

	public LocalDateTime getFechaSubida() {
		return fechaSubida;
	}

	public void setFechaSubida(LocalDateTime fechaSubida) {
		this.fechaSubida = fechaSubida;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Override
	public String toString() {
		return "Archivo [id=" + id + ", nombre=" + nombre + ", tipo="  + ", rutaAlmacenamiento="
				+ rutaAlmacenamiento + ", fechaSubida=" + fechaSubida + ", is_human_written="
				+ ", is_gpt_generated="  + ", usuario=" + usuario + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(fechaSubida, id, nombre, resultados, rutaAlmacenamiento, usuario);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Archivo other = (Archivo) obj;
		return Objects.equals(fechaSubida, other.fechaSubida) && Objects.equals(id, other.id)
				&& Objects.equals(nombre, other.nombre) && Objects.equals(resultados, other.resultados)
				&& Objects.equals(rutaAlmacenamiento, other.rutaAlmacenamiento)
				&& Objects.equals(usuario, other.usuario);
	}


	

	

		
	

	
	
	
	
	

}
