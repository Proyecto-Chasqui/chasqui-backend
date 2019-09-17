package chasqui.view.composer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;

import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.SolicitudCreacionNodo;

public class GestionSolicitudCreacionNodoComposer extends GenericForwardComposer<Component>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4470985263767200283L;
	
	private AnnotateDataBinder binder;
	
	private String estado;
	private String nombreNodo;
	private String cliente;
	private String mail;
	private String telfijo;
	private String celular;
	private String barrio;
	private String tipoNodo;
	private String direccion;
	private String descripcion;
	private Cliente datacliente;

	public void doAfterCompose(Component c) throws Exception{
		super.doAfterCompose(c);
		binder = new AnnotateDataBinder(c);
		SolicitudCreacionNodo s = (SolicitudCreacionNodo) Executions.getCurrent().getArg().get("solicitud");
		datacliente = ((Cliente) s.getUsuarioSolicitante()); 
		this.fillData(s);
		this.binder.loadAll();
	}

	private void fillData(SolicitudCreacionNodo solicitud) {
		estado = this.renderizarConstante(solicitud.getEstado());
		cliente = datacliente.getNombre() + " " + datacliente.getApellido();
		mail = datacliente.getEmail();
		telfijo = (datacliente.getTelefonoFijo().equals(""))? "N/D" : datacliente.getTelefonoFijo();
		celular = (datacliente.getTelefonoMovil().equals(""))? "N/D" : datacliente.getTelefonoMovil();
		barrio = solicitud.getBarrio();
		setDireccion(solicitud.getDomicilio().toString());
		tipoNodo = this.renderizarConstante(solicitud.getTipoNodo());
		descripcion = solicitud.getDescripcion();
		nombreNodo = solicitud.getNombreNodo();
	}

	private String renderizarConstante(String estado) {
		if(estado.equals(Constantes.SOLICITUD_NODO_EN_GESTION)) {
			return "EN GESTIÓN";
		}
		if(estado.equals(Constantes.SOLICITUD_NODO_APROBADO)) {
			return "APROBADO";
		}
		if(estado.equals(Constantes.SOLICITUD_NODO_RECHAZADO)) {
			return "RECHAZADO";
		}
		if(estado.equals(Constantes.SOLICITUD_NODO_CANCELADO)) {
			return "CANCELADO";
		}
		
		if(estado.equals(Constantes.NODO_ABIERTO)) {
			return "NODO ABIERTO";
		}
		if(estado.equals(Constantes.NODO_CERRADO)) {
			return "NODO CERRADO";
		}
		
		return "N/D";
	}

	public AnnotateDataBinder getBinder() {
		return binder;
	}

	public void setBinder(AnnotateDataBinder binder) {
		this.binder = binder;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getTelfijo() {
		return telfijo;
	}

	public void setTelfijo(String telfijo) {
		this.telfijo = telfijo;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public String getBarrio() {
		return barrio;
	}

	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}

	public String getTipoNodo() {
		return tipoNodo;
	}

	public void setTipoNodo(String tipoNodo) {
		this.tipoNodo = tipoNodo;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getNombreNodo() {
		return nombreNodo;
	}

	public void setNombreNodo(String nombreNodo) {
		this.nombreNodo = nombreNodo;
	}
	
	
}
