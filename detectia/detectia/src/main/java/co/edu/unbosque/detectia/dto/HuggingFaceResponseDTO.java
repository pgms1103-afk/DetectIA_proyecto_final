package co.edu.unbosque.detectia.dto;

public class HuggingFaceResponseDTO {

	   private double score;
	   private String label;
	   
	   public HuggingFaceResponseDTO() {
		// TODO Auto-generated constructor stub
	}

	   public HuggingFaceResponseDTO(double score, String label) {
		super();
		this.score = score;
		this.label = label;
	   }

	   public double getScore() {
		   return score;
	   }

	   public void setScore(double score) {
		   this.score = score;
	   }

	   public String getLabel() {
		   return label;
	   }

	   public void setLabel(String label) {
		   this.label = label;
	   }

	  
}
