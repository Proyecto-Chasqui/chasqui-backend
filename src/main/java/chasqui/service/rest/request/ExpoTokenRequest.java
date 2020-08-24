package chasqui.service.rest.request;

import java.io.Serializable;

public class ExpoTokenRequest implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6230466641037574531L;
	
	public String expoToken;
	
	public ExpoTokenRequest() {}

	public String getExpoToken() {
		return expoToken;
	}

	public void setExpoToken(String expoToken) {
		this.expoToken = expoToken;
	}
	

}
