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
@Table(name = "resultadoia")
public class ResultadoIA {
	
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
	private String nombreIA;
	private double porcentajeIA;
	private LocalDateTime fechaAnalisis;
	
	@ManyToOne
	@JoinColumn(name = "archivo_id")
	private Archivo archivo;
	
	public ResultadoIA() {
		
	}

	public ResultadoIA(String nombreIA, double porcentajeIA, LocalDateTime fechaAnalisis, Archivo archivo) {
		
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
		return "ResultadoIA [id=" + id + ", nombreIA=" + nombreIA + ", porcentajeIA=" + porcentajeIA
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
		ResultadoIA other = (ResultadoIA) obj;
		return Objects.equals(archivo, other.archivo) && Objects.equals(fechaAnalisis, other.fechaAnalisis)
				&& Objects.equals(id, other.id) && Objects.equals(nombreIA, other.nombreIA)
				&& Double.doubleToLongBits(porcentajeIA) == Double.doubleToLongBits(other.porcentajeIA);
	}
	
	
	
	

}
