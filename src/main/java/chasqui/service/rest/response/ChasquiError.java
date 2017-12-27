package chasqui.service.rest.response;

import java.io.Serializable;

public class ChasquiError implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4122522086069529224L;
	private String error;
	
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	public ChasquiError(String error){
		this.error = error;
	}
	
	
	
	
	

}
