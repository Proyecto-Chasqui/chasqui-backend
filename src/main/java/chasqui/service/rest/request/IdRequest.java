package chasqui.service.rest.request;

import java.io.Serializable;

public class IdRequest implements Serializable {

	private static final long serialVersionUID = -7374410199756577019L;
	public Integer id;
	
	public IdRequest(){
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
}
