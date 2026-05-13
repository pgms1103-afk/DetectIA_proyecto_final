package co.edu.unbosque.detectia.dto;

public class ZeroGPTResponseDTO {
	
	private boolean success;
    private ZeroGPTDataDTO data;
    
    public ZeroGPTResponseDTO() {
		// TODO Auto-generated constructor stub
	}

	public ZeroGPTResponseDTO(boolean success, ZeroGPTDataDTO data) {
		super();
		this.success = success;
		this.data = data;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public ZeroGPTDataDTO getData() {
		return data;
	}

	public void setData(ZeroGPTDataDTO data) {
		this.data = data;
	}
    
    

}
