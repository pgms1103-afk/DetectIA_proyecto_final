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

/**
 * Entidad JPA que almacena el resumen consolidado del análisis de IA para un
 * archivo concreto.
 * <p>
 * Contiene el porcentaje final promedio obtenido de los votos de todos los
 * servicios de IA y el veredicto resultante ({@code "PROBABLE IA"} o
 * {@code "PROBABLE HUMANO"}). Se relaciona Many-to-One con
 * {@link Archivo} y con {@link Usuario}.
 * </p>
 *
 * @author Martín Peña
 * @version 1.0
 * @since 1.0
 * @see co.edu.unbosque.detectia.service.AnalisisService
 * @see co.edu.unbosque.detectia.repository.AnalisisRepository
 */
@Entity
@Table(name = "analisis")
public class Analisis {
	
	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
	private double porcentajeFinal;
	private double porcentajeIA;
	private String veredicto;
	private String nombreIA;
	private LocalDateTime fechaAnalisis;
	
	@ManyToOne
	@JoinColumn(name = "archivo_id")
	private Archivo archivo;
	
	@ManyToOne
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;
	
	public Analisis() {
		
	}

	

	public Analisis(double porcentajeFinal, double porcentajeIA, String veredicto, String nombreIA,
			LocalDateTime fechaAnalisis, Archivo archivo, Usuario usuario) {
		super();
		this.porcentajeFinal = porcentajeFinal;
		this.porcentajeIA = porcentajeIA;
		this.veredicto = veredicto;
		this.nombreIA = nombreIA;
		this.fechaAnalisis = fechaAnalisis;
		this.archivo = archivo;
		this.usuario = usuario;
	}



	public double getPorcentajeIA() {
		return porcentajeIA;
	}



	public void setPorcentajeIA(double porcentajeIA) {
		this.porcentajeIA = porcentajeIA;
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

	public String getNombreIA() {
		return nombreIA;
	}

	public void setNombreIA(String nombreIA) {
		this.nombreIA = nombreIA;
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

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}



	@Override
	public String toString() {
		return "Analisis [id=" + id + ", porcentajeFinal=" + porcentajeFinal + ", porcentajeIA=" + porcentajeIA
				+ ", veredicto=" + veredicto + ", nombreIA=" + nombreIA + ", fechaAnalisis=" + fechaAnalisis
				+ ", archivo=" + archivo + ", usuario=" + usuario + "]";
	}



	@Override
	public int hashCode() {
		return Objects.hash(archivo, fechaAnalisis, id, nombreIA, porcentajeFinal, porcentajeIA, usuario, veredicto);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Analisis other = (Analisis) obj;
		return Objects.equals(archivo, other.archivo) && Objects.equals(fechaAnalisis, other.fechaAnalisis)
				&& Objects.equals(id, other.id) && Objects.equals(nombreIA, other.nombreIA)
				&& Double.doubleToLongBits(porcentajeFinal) == Double.doubleToLongBits(other.porcentajeFinal)
				&& Double.doubleToLongBits(porcentajeIA) == Double.doubleToLongBits(other.porcentajeIA)
				&& Objects.equals(usuario, other.usuario) && Objects.equals(veredicto, other.veredicto);
	}

	

	
	
	
	
	

}
