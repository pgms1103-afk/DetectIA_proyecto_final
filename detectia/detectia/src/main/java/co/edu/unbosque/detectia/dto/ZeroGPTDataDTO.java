package co.edu.unbosque.detectia.dto;

public class ZeroGPTDataDTO {
	
	 private double is_human_written;
	 private double is_gpt_generated;
	 private String feedback_message;
	 private int words_count;
	 
	 public ZeroGPTDataDTO() {
		// TODO Auto-generated constructor stub
	}

	 public ZeroGPTDataDTO(double is_human_written, double is_gpt_generated, String feedback_message, int words_count) {
		super();
		this.is_human_written = is_human_written;
		this.is_gpt_generated = is_gpt_generated;
		this.feedback_message = feedback_message;
		this.words_count = words_count;
	 }

	 public double getIs_human_written() {
		 return is_human_written;
	 }

	 public void setIs_human_written(double is_human_written) {
		 this.is_human_written = is_human_written;
	 }

	 public double getIs_gpt_generated() {
		 return is_gpt_generated;
	 }

	 public void setIs_gpt_generated(double is_gpt_generated) {
		 this.is_gpt_generated = is_gpt_generated;
	 }

	 public String getFeedback_message() {
		 return feedback_message;
	 }

	 public void setFeedback_message(String feedback_message) {
		 this.feedback_message = feedback_message;
	 }

	 public int getWords_count() {
		 return words_count;
	 }

	 public void setWords_count(int words_count) {
		 this.words_count = words_count;
	 }
	 
	 

}
