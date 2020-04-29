package chasqui.service.rest.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import chasqui.exceptions.ClienteNoPerteneceAGCCException;
import chasqui.model.GrupoCC;
import chasqui.model.MiembroDeGCC;
import chasqui.model.Pedido;

public class GrupoResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3222038702711964078L;

	private String alias;
	private String emailAdministrador;
	private boolean esAdministrador;
	private Integer idPedidoIndividual;
	private List<MiembroDeGCCResponse> miembros;

	private String descripcion;

	private Integer id;
	

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public GrupoResponse() {}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getEmailAdministrador() {
		return emailAdministrador;
	}

	public void setEmailAdministrador(String emailAdministrador) {
		this.emailAdministrador = emailAdministrador;
	}

	public boolean isEsAdministrador() {
		return esAdministrador;
	}

	public void setEsAdministrador(boolean esAdministrador) {
		this.esAdministrador = esAdministrador;
	}


	public Integer getIdPedidoIndividual() {
		return idPedidoIndividual;
	}

	public void setIdPedidoIndividual(Integer idPedidoIndividual) {
		this.idPedidoIndividual = idPedidoIndividual;
	}
	
	public GrupoResponse(GrupoCC grupo) {
		this.setId(grupo.getId());
	}

	public GrupoResponse(GrupoCC grupo,String email) throws ClienteNoPerteneceAGCCException {
		alias=grupo.getAlias();
		emailAdministrador = grupo.getAdministrador().getEmail();
		esAdministrador = emailAdministrador.equals(email);
		this.miembros = new ArrayList<MiembroDeGCCResponse>();
		for (MiembroDeGCC miembro : grupo.getCache()) {
			Pedido pedido = grupo.obtenerPedidoIndividual(miembro.getEmail());
			if (pedido!=null && pedido.getCliente().getEmail().equals(email)){
				this.idPedidoIndividual = pedido.getId();
			}
			this.miembros.add(new MiembroDeGCCResponse(miembro, pedido));
		}
		this.descripcion = grupo.getDescripcion();
		this.setId(grupo.getId());
	}
	


	public List<MiembroDeGCCResponse> getMiembros() {
		return miembros;
	}

	public void setMiembros(List<MiembroDeGCCResponse> miembros) {
		this.miembros = miembros;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
