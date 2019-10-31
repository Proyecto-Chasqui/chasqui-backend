package chasqui.service.rest.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import chasqui.exceptions.ClienteNoPerteneceAGCCException;
import chasqui.model.Direccion;
import chasqui.model.GrupoCC;
import chasqui.model.MiembroDeGCC;
import chasqui.model.Nodo;
import chasqui.model.Pedido;

public class NodoResponse implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8954765425373435830L;

	private String alias;
	private String emailAdministrador;
	private boolean esAdministrador;
	private Integer idPedidoIndividual;
	private List<MiembroDeGCCResponse> miembros;

	private String descripcion;

	private Integer id;
	
	private String tipo;
	private Direccion direccionDelNodo;
	private String barrio;
	

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public NodoResponse() {}

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
	
	public NodoResponse(Nodo nodo) {
		this.setId(nodo.getId());
	}

	public NodoResponse(Nodo nodo,String email) throws ClienteNoPerteneceAGCCException {
		alias=nodo.getAlias();
		emailAdministrador = nodo.getAdministrador().getEmail();
		esAdministrador = emailAdministrador.equals(email);
		this.miembros = new ArrayList<MiembroDeGCCResponse>();
		for (MiembroDeGCC miembro : nodo.getCache()) {
			Pedido pedido = nodo.obtenerPedidoIndividual(miembro.getEmail());
			if (pedido!=null && pedido.getCliente().getEmail().equals(email)){
				this.idPedidoIndividual = pedido.getId();
			}
			this.miembros.add(new MiembroDeGCCResponse(miembro, pedido));
		}
		this.descripcion = nodo.getDescripcion();
		this.setId(nodo.getId());
		this.setTipo(nodo.getTipo());
		Direccion direccion = nodo.getDireccionDelNodo();
		direccion.setGeoUbicacion(null);
		this.setDireccionDelNodo(direccion);
		this.setBarrio(nodo.getBarrio());
	}
	


	public List<MiembroDeGCCResponse> getMiembros() {
		return miembros;
	}

	public void setMiembros(List<MiembroDeGCCResponse> miembros) {
		this.miembros = miembros;
	}

	public String getTipo() {
		return tipo;
	}


	public Direccion getDireccionDelNodo() {
		return direccionDelNodo;
	}

	public String getBarrio() {
		return barrio;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}


	public void setDireccionDelNodo(Direccion direccionDelNodo) {
		this.direccionDelNodo = direccionDelNodo;
	}

	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	

}
