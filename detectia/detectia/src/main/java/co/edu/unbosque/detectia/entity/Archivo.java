package co.edu.unbosque.detectia.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "archivo")
public class Archivo {
	
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
	private String nombre;
	private String tipo;
	private String rutaAlmacenamiento;
	private LocalDateTime fechaSubida;
	
	@ManyToOne 
	
	/*
	 * Le dice a JPA cuál es la naturaleza de 
	 * la relación entre las dos entidades. En este caso:
	 * 
	 * "Muchos Archivo pertenecen a un solo Usuario"
	 * 
	 * Un usuario puede tener muchos archivos, 
	 * pero cada archivo solo tiene un dueño. 
	 * Esa es la cardinalidad Many(archivos) to One(usuario).
	 */
	
	@JoinColumn(name ="usuario_id")
	
	/*
	 * Le dice a JPA cómo representar esa relación físicamente en la base de datos. 
	 * Específicamente, le indica que en la tabla archivo va a existir una columna 
	 * llamada usuario_id que es la llave foránea que apunta al id de la tabla usuario.
	 */
	
	private Usuario usuario;
	
	public Archivo() {

	}

	public Archivo(String nombre, String tipo, String rutaAlmacenamiento, LocalDateTime fechaSubida,
			Usuario usuario) {
		
		this.nombre = nombre;
		this.tipo = tipo;
		this.rutaAlmacenamiento = rutaAlmacenamiento;
		this.fechaSubida = fechaSubida;
		this.usuario = usuario;
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

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
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
		return "Archivo [id=" + id + ", nombre=" + nombre + ", tipo=" + tipo + ", rutaAlmacenamiento="
				+ rutaAlmacenamiento + ", fechaSubida=" + fechaSubida + ", usuario=" + usuario + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(fechaSubida, id, nombre, rutaAlmacenamiento, tipo, usuario);
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
				&& Objects.equals(nombre, other.nombre) && Objects.equals(rutaAlmacenamiento, other.rutaAlmacenamiento)
				&& Objects.equals(tipo, other.tipo) && Objects.equals(usuario, other.usuario);
	}
	
	
	
	

}
