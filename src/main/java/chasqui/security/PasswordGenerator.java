package chasqui.security;

public class PasswordGenerator {

	private String ALPHA_NUMERIC_STRING;
	  
	  
	public String generateRandomToken() {
		  StringBuilder builder = new StringBuilder();
		  int lenght = 10;
	  		while (lenght-- != 0) {
	  			int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
	  			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
	  		}
	  		return builder.toString();
	  }


	public String getALPHA_NUMERIC_STRING() {
		return ALPHA_NUMERIC_STRING;
	}


	public void setALPHA_NUMERIC_STRING(String aLPHA_NUMERIC_STRING) {
		ALPHA_NUMERIC_STRING = aLPHA_NUMERIC_STRING;
	}
	
	
	
}
