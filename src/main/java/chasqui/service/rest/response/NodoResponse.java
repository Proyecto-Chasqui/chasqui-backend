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
import chasqui.model.Zona;
import chasqui.model_lite.ClienteLite;
import chasqui.model_lite.NodoLite;
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
	private ZonaResponse zona;

	private ClienteLite administrador = null;
	

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

	public NodoResponse(NodoLite nodo, String emailUserLogged) {
		this.id = nodo.getId();
		this.alias = nodo.getAlias();
		this.emailAdministrador = nodo.getEmailAdministrador();
		this.esAdministrador = this.emailAdministrador.equals(emailUserLogged);
		this.administrador = nodo.getAdministrador();
		this.idPedidoIndividual = 0;
		this.descripcion = nodo.getDescripcion();
		this.tipo = nodo.getTipo();
		this.direccionDelNodo = nodo.getDireccionDelNodo();
		this.barrio = nodo.getBarrio();
		Zona zona = nodo.getZona();
		if(zona != null) {
			this.zona = new ZonaResponse(zona);
		}
		
				
		nodo.getZona();
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
		this.setZona(null);
		if(nodo.getZona() != null) {
			this.setZona(new ZonaResponse(nodo.getZona()));
		}
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

	public ZonaResponse getZona() {
		return zona;
	}

	public void setZona(ZonaResponse zona) {
		this.zona = zona;
	}


	public ClienteLite getAdministrador() {
		return this.administrador;
	}

	public void setAdministrador(ClienteLite administrador) {
		this.administrador = administrador;
	}


	

}
