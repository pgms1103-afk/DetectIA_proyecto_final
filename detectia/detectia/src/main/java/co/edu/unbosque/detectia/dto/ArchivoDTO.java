package co.edu.unbosque.detectia.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import co.edu.unbosque.detectia.entity.Usuario;

public class ArchivoDTO {
	
	private Long id;
	private String nombre;
	private String rutaAlmacenamiento;
	private LocalDateTime fechaSubida;
	private Long usuarioId;
	private String username;
	
	public ArchivoDTO() {
		this.fechaSubida = LocalDateTime.now();
	}

	public ArchivoDTO(Long id, String nombre, String rutaAlmacenamiento, LocalDateTime fechaSubida, Long usuarioId,
			String username) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.rutaAlmacenamiento = rutaAlmacenamiento;
		this.fechaSubida = fechaSubida;
		this.usuarioId = usuarioId;
		this.username = username;
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

	public Long getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Long usuarioId) {
		this.usuarioId = usuarioId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "ArchivoDTO [id=" + id + ", nombre=" + nombre + ", rutaAlmacenamiento=" + rutaAlmacenamiento
				+ ", fechaSubida=" + fechaSubida + ", usuarioId=" + usuarioId + ", username=" + username + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(fechaSubida, id, nombre, rutaAlmacenamiento, username, usuarioId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArchivoDTO other = (ArchivoDTO) obj;
		return Objects.equals(fechaSubida, other.fechaSubida) && Objects.equals(id, other.id)
				&& Objects.equals(nombre, other.nombre) && Objects.equals(rutaAlmacenamiento, other.rutaAlmacenamiento)
				&& Objects.equals(username, other.username) && Objects.equals(usuarioId, other.usuarioId);
	}


	

	


	

	
	
		
	
	
	
	

}
