package co.edu.unbosque.detectia.dto;

public class GrokDTO {
	
	private double porcentajeIA;
	private String veredicto;
	
	public GrokDTO() {
	}

	public GrokDTO(double porcentajeIA, String veredicto) {
		super();
		this.porcentajeIA = porcentajeIA;
		this.veredicto = veredicto;
	}

	public double getPorcentajeIA() {
		return porcentajeIA;
	}

	public void setPorcentajeIA(double porcentajeIA) {
		this.porcentajeIA = porcentajeIA;
	}

	public String getVeredicto() {
		return veredicto;
	}

	public void setVeredicto(String veredicto) {
		this.veredicto = veredicto;
	}
	
	

}
