package co.edu.unbosque.detectia.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import co.edu.unbosque.detectia.entity.Archivo;


public class ResultadoIADTO {
	
	private Long id;
	private String nombreIA;
	private double porcentajeIA;
	private LocalDateTime fechaAnalisis;
	private Archivo archivo;
	
	public ResultadoIADTO() {
			
	}

	public ResultadoIADTO(String nombreIA, double porcentajeIA, LocalDateTime fechaAnalisis, Archivo archivo) {
	
		this.nombreIA = nombreIA;
		this.porcentajeIA = porcentajeIA;
		this.fechaAnalisis = fechaAnalisis;
		this.archivo = archivo;
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

	public Archivo getArchivo() {
		return archivo;
	}

	public void setArchivo(Archivo archivo) {
		this.archivo = archivo;
	}

	@Override
	public String toString() {
		return "ResultadoIADTO [id=" + id + ", nombreIA=" + nombreIA + ", porcentajeIA=" + porcentajeIA
				+ ", fechaAnalisis=" + fechaAnalisis + ", archivo=" + archivo + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(archivo, fechaAnalisis, id, nombreIA, porcentajeIA);
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
		return Objects.equals(archivo, other.archivo) && Objects.equals(fechaAnalisis, other.fechaAnalisis)
				&& Objects.equals(id, other.id) && Objects.equals(nombreIA, other.nombreIA)
				&& Double.doubleToLongBits(porcentajeIA) == Double.doubleToLongBits(other.porcentajeIA);
	}
	
	
	
	

}
