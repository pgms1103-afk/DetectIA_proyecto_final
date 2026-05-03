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
	private double is_human_written;
	private double is_gpt_generated;

	
	public ArchivoDTO() {
		
	}


	public ArchivoDTO(Long id, String nombre, String tipo, String rutaAlmacenamiento, LocalDateTime fechaSubida,
			Usuario usuario, double is_human_written, double is_gpt_generated) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.tipo = tipo;
		this.rutaAlmacenamiento = rutaAlmacenamiento;
		this.fechaSubida = fechaSubida;
		this.usuario = usuario;
		this.is_human_written = is_human_written;
		this.is_gpt_generated = is_gpt_generated;
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


	public double getIs_human_written() {
		return is_human_written;
	}


	public void setIs_human_written(double is_human_written) {
		this.is_human_written = is_human_written;
	}


	public double getIs_gpt_generated() {
		return is_gpt_generated;
	}


	public void setIs_gpt_generated(double is_gpt_generated) {
		this.is_gpt_generated = is_gpt_generated;
	}


	@Override
	public String toString() {
		return "ArchivoDTO [id=" + id + ", nombre=" + nombre + ", tipo=" + tipo + ", rutaAlmacenamiento="
				+ rutaAlmacenamiento + ", fechaSubida=" + fechaSubida + ", usuario=" + usuario + ", is_human_written="
				+ is_human_written + ", is_gpt_generated=" + is_gpt_generated + "]";
	}


	@Override
	public int hashCode() {
		return Objects.hash(fechaSubida, id, is_gpt_generated, is_human_written, nombre, rutaAlmacenamiento, tipo,
				usuario);
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
				&& Double.doubleToLongBits(is_gpt_generated) == Double.doubleToLongBits(other.is_gpt_generated)
				&& Double.doubleToLongBits(is_human_written) == Double.doubleToLongBits(other.is_human_written)
				&& Objects.equals(nombre, other.nombre) && Objects.equals(rutaAlmacenamiento, other.rutaAlmacenamiento)
				&& Objects.equals(tipo, other.tipo) && Objects.equals(usuario, other.usuario);
	}

	
	
		
	
	
	
	

}
