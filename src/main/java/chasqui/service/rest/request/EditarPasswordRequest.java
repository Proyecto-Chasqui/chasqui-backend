package chasqui.service.rest.request;

public class EditarPasswordRequest {
	private String password;
	private String oldPassword;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	@Override
	public String toString(){
		return "EditarPasswordRequest: [password:"+password+" ]";
	}
}
