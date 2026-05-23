package co.edu.unbosque.detectia.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import co.edu.unbosque.detectia.entity.Archivo;


public class ResultadoIADTO {
	
	private Long id;
	private String nombreIA;
	private double porcentajeIA;
	private LocalDateTime fechaAnalisis;
	private String nombreArchivo;
	
	public ResultadoIADTO() {
			
	}

	public ResultadoIADTO(Long id, String nombreIA, double porcentajeIA, LocalDateTime fechaAnalisis,
			String nombreArchivo) {
		super();
		this.id = id;
		this.nombreIA = nombreIA;
		this.porcentajeIA = porcentajeIA;
		this.fechaAnalisis = fechaAnalisis;
		this.nombreArchivo = nombreArchivo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombreIA() {
		return nombreIA;
	}

	public void setNombreIA(String nombreIA) {
		this.nombreIA = nombreIA;
	}

	public double getPorcentajeIA() {
		return porcentajeIA;
	}

	public void setPorcentajeIA(double porcentajeIA) {
		this.porcentajeIA = porcentajeIA;
	}

	public LocalDateTime getFechaAnalisis() {
		return fechaAnalisis;
	}

	public void setFechaAnalisis(LocalDateTime fechaAnalisis) {
		this.fechaAnalisis = fechaAnalisis;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	@Override
	public String toString() {
		return "ResultadoIADTO [id=" + id + ", nombreIA=" + nombreIA + ", porcentajeIA=" + porcentajeIA
				+ ", fechaAnalisis=" + fechaAnalisis + ", nombreArchivo=" + nombreArchivo + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(fechaAnalisis, id, nombreArchivo, nombreIA, porcentajeIA);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResultadoIADTO other = (ResultadoIADTO) obj;
		return Objects.equals(fechaAnalisis, other.fechaAnalisis) && Objects.equals(id, other.id)
				&& Objects.equals(nombreArchivo, other.nombreArchivo) && Objects.equals(nombreIA, other.nombreIA)
				&& Double.doubleToLongBits(porcentajeIA) == Double.doubleToLongBits(other.porcentajeIA);
	}

	
	
	

}
