package chasqui.service.rest.request;

public class EditarSolicitudCreacionNodoRequest extends NodoSolicitudCreacionRequest{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5916848845291795645L;
	
	private Integer idSolicitud;
	
	public EditarSolicitudCreacionNodoRequest() {
		
	}

	public Integer getIdSolicitud() {
		return idSolicitud;
	}

	public void setIdSolicitud(Integer idSolicitud) {
		this.idSolicitud = idSolicitud;
	}

}
