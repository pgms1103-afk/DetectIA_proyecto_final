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
	private Archivo archivo;
	private Usuario usuario;
	
	public AnalisisDTO() {
		
	}

	public AnalisisDTO(double porcentajeFinal, String veredicto, String iaRecomendada, LocalDateTime fechaAnalisis,
			Archivo archivo, Usuario usuario) {
	
		this.porcentajeFinal = porcentajeFinal;
		this.veredicto = veredicto;
		this.iaRecomendada = iaRecomendada;
		this.fechaAnalisis = fechaAnalisis;
		this.archivo = archivo;
		this.usuario = usuario;
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
		return "AnalisisDTO [id=" + id + ", porcentajeFinal=" + porcentajeFinal + ", veredicto=" + veredicto
				+ ", iaRecomendada=" + iaRecomendada + ", fechaAnalisis=" + fechaAnalisis + ", archivo=" + archivo
				+ ", usuario=" + usuario + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(archivo, fechaAnalisis, iaRecomendada, id, porcentajeFinal, usuario, veredicto);
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
		return Objects.equals(archivo, other.archivo) && Objects.equals(fechaAnalisis, other.fechaAnalisis)
				&& Objects.equals(iaRecomendada, other.iaRecomendada) && Objects.equals(id, other.id)
				&& Double.doubleToLongBits(porcentajeFinal) == Double.doubleToLongBits(other.porcentajeFinal)
				&& Objects.equals(usuario, other.usuario) && Objects.equals(veredicto, other.veredicto);
	}
	
	
	
	

}
