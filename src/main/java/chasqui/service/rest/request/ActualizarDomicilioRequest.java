package chasqui.service.rest.request;

import java.io.Serializable;


public class ActualizarDomicilioRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8379806709346028536L;

	Integer idGrupo;
	DireccionRequest direccionRequest;
		
	public ActualizarDomicilioRequest(){
		
	}

	public ActualizarDomicilioRequest(Integer idGrupo, DireccionRequest direccionRequest){
		this.idGrupo = idGrupo;
		this.direccionRequest = direccionRequest;
	}
	
	public Integer getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Integer idGrupo) {
		this.idGrupo = idGrupo;
	}

	public DireccionRequest getDireccionRequest() {
		return direccionRequest;
	}

	public void setDomicilio(DireccionRequest direccionRequest) {
		this.direccionRequest = direccionRequest;
	}
	
	
}
