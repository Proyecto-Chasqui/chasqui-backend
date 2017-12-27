package chasqui.service.rest.request;

public class MedallaRequest {
	
	Integer medallaId;
	
	public MedallaRequest(){}
	
	public MedallaRequest(Integer idMedalla){
		this.setMedallaId(idMedalla);
	}

	public Integer getMedallaId() {
		return medallaId;
	}

	public void setMedallaId(Integer medallaId) {
		this.medallaId = medallaId;
	}

}
