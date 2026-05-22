package co.edu.unbosque.detectia.dto;

public class HuggingFaceDTO {
    private double porcentajeIA;
    private String veredicto;

    public HuggingFaceDTO() {
    	
    }

    public HuggingFaceDTO(double porcentajeIA, String veredicto) {
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
	  

