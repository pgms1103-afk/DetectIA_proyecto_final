package co.edu.unbosque.detectia.dto;

public class MistralDTO {
	
	private double fakePercentage;
	private boolean isAiGenerated;
	
	public MistralDTO() {
		// TODO Auto-generated constructor stub
	}

	public MistralDTO(double fakePercentage, boolean isAiGenerated) {
		this.fakePercentage = fakePercentage;
		this.isAiGenerated = isAiGenerated;
	}

	public double getFakePercentage() {
		return fakePercentage;
	}

	public void setFakePercentage(double fakePercentage) {
		this.fakePercentage = fakePercentage;
	}

	public boolean isAiGenerated() {
		return isAiGenerated;
	}

	public void setAiGenerated(boolean isAiGenerated) {
		this.isAiGenerated = isAiGenerated;
	}
	

}
