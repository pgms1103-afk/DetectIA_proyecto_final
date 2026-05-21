package co.edu.unbosque.detectia.dto;

public class SightengineDTO {
	
	private double ai_generated;
	private String status;
	
	public SightengineDTO() {
		// TODO Auto-generated constructor stub
	}

	public SightengineDTO(double ai_generated, String status) {
		super();
		this.ai_generated = ai_generated;
		this.status = status;
	}

	public double getAi_generated() {
		return ai_generated;
	}

	public void setAi_generated(double ai_generated) {
		this.ai_generated = ai_generated;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
	

}
