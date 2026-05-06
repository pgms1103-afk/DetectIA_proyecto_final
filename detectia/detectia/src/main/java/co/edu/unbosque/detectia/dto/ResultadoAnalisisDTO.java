package co.edu.unbosque.detectia.dto;

public class ResultadoAnalisisDTO {

	private Double porcentajeIA;
	private String fuente;

	public ResultadoAnalisisDTO() {
		// TODO Auto-generated constructor stub
	}

	public ResultadoAnalisisDTO(Double porcentajeIA, String fuente) {
		super();
		this.porcentajeIA = porcentajeIA;
		this.fuente = fuente;
	}

	public Double getPorcentajeIA() {
		return porcentajeIA;
	}

	public void setPorcentajeIA(Double porcentajeIA) {
		this.porcentajeIA = porcentajeIA;
	}

	public String getFuente() {
		return fuente;
	}

	public void setFuente(String fuente) {
		this.fuente = fuente;
	}

}
