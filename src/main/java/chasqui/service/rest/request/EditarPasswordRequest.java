package chasqui.service.rest.request;

public class EditarPasswordRequest {
	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString(){
		return "EditarPasswordRequest: [password:"+password+" ]";
	}
}
