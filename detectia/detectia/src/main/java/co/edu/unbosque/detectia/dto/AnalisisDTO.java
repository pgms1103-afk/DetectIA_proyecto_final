package co.edu.unbosque.detectia.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import co.edu.unbosque.detectia.entity.Archivo;
import co.edu.unbosque.detectia.entity.Usuario;

public class AnalisisDTO {	
	
	private Long id;
	private double porcentajeFinal;
	private String veredicto;
	private String iaRecomendada;
	private LocalDateTime fechaAnalisis;
	private Long archivoId;
	private String nombreArchivo;

	
	public AnalisisDTO() {
		
	}


	public AnalisisDTO(Long id, double porcentajeFinal, String veredicto, String iaRecomendada,
			LocalDateTime fechaAnalisis, Long archivoId, String nombreArchivo) {
		super();
		this.id = id;
		this.porcentajeFinal = porcentajeFinal;
		this.veredicto = veredicto;
		this.iaRecomendada = iaRecomendada;
		this.fechaAnalisis = fechaAnalisis;
		this.archivoId = archivoId;
		this.nombreArchivo = nombreArchivo;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public double getPorcentajeFinal() {
		return porcentajeFinal;
	}


	public void setPorcentajeFinal(double porcentajeFinal) {
		this.porcentajeFinal = porcentajeFinal;
	}


	public String getVeredicto() {
		return veredicto;
	}


	public void setVeredicto(String veredicto) {
		this.veredicto = veredicto;
	}


	public String getIaRecomendada() {
		return iaRecomendada;
	}


	public void setIaRecomendada(String iaRecomendada) {
		this.iaRecomendada = iaRecomendada;
	}


	public LocalDateTime getFechaAnalisis() {
		return fechaAnalisis;
	}


	public void setFechaAnalisis(LocalDateTime fechaAnalisis) {
		this.fechaAnalisis = fechaAnalisis;
	}


	public Long getArchivoId() {
		return archivoId;
	}


	public void setArchivoId(Long archivoId) {
		this.archivoId = archivoId;
	}


	public String getNombreArchivo() {
		return nombreArchivo;
	}


	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}


	@Override
	public String toString() {
		return "AnalisisDTO [id=" + id + ", porcentajeFinal=" + porcentajeFinal + ", veredicto=" + veredicto
				+ ", iaRecomendada=" + iaRecomendada + ", fechaAnalisis=" + fechaAnalisis + ", archivoId=" + archivoId
				+ ", nombreArchivo=" + nombreArchivo + "]";
	}


	@Override
	public int hashCode() {
		return Objects.hash(archivoId, fechaAnalisis, iaRecomendada, id, nombreArchivo, porcentajeFinal, veredicto);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnalisisDTO other = (AnalisisDTO) obj;
		return Objects.equals(archivoId, other.archivoId) && Objects.equals(fechaAnalisis, other.fechaAnalisis)
				&& Objects.equals(iaRecomendada, other.iaRecomendada) && Objects.equals(id, other.id)
				&& Objects.equals(nombreArchivo, other.nombreArchivo)
				&& Double.doubleToLongBits(porcentajeFinal) == Double.doubleToLongBits(other.porcentajeFinal)
				&& Objects.equals(veredicto, other.veredicto);
	}

	
	
	

}
