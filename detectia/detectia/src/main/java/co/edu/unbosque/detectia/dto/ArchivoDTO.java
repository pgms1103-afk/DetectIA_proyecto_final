package co.edu.unbosque.detectia.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import co.edu.unbosque.detectia.entity.Usuario;

public class ArchivoDTO {
	
	private Long id;
	private String nombre;
	private String tipo;
	private String rutaAlmacenamiento;
	private LocalDateTime fechaSubida;
	private Usuario usuario;
	
	public ArchivoDTO() {
		
	}

	public ArchivoDTO(String nombre, String tipo, String rutaAlmacenamiento, LocalDateTime fechaSubida,
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
		return "ArchivoDTO [id=" + id + ", nombre=" + nombre + ", tipo=" + tipo + ", rutaAlmacenamiento="
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
		ArchivoDTO other = (ArchivoDTO) obj;
		return Objects.equals(fechaSubida, other.fechaSubida) && Objects.equals(id, other.id)
				&& Objects.equals(nombre, other.nombre) && Objects.equals(rutaAlmacenamiento, other.rutaAlmacenamiento)
				&& Objects.equals(tipo, other.tipo) && Objects.equals(usuario, other.usuario);
	}
	
		
	
	
	
	

}
