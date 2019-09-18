package chasqui.view.composer;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Button;

import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Cliente;
import chasqui.model.SolicitudCreacionNodo;
import chasqui.services.interfaces.NodoService;

public class GestionSolicitudCreacionNodoComposer extends GenericForwardComposer<Component>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4470985263767200283L;
	
	private AnnotateDataBinder binder;
	
	private Button aceptarSolicitudBtn;
	private Button rechazarSolicitudBtn;
	private Button salirBtn;
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
	private SolicitudCreacionNodo solicitud;
	private NodoService nodoService;
	
	private Component component;
	
	public void doAfterCompose(Component c) throws Exception{
		super.doAfterCompose(c);
		component = c;
		nodoService = (NodoService) SpringUtil.getBean("nodoService");
		binder = new AnnotateDataBinder(c);
		solicitud = (SolicitudCreacionNodo) Executions.getCurrent().getArg().get("solicitud");
		datacliente = ((Cliente) solicitud.getUsuarioSolicitante()); 
		this.fillData(solicitud);
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
		this.setButtons(solicitud);
	}

	private void setButtons(SolicitudCreacionNodo solicitud2) {
		if(!solicitud.getEstado().equals(Constantes.SOLICITUD_NODO_EN_GESTION)) {
			rechazarSolicitudBtn.setVisible(false);
			aceptarSolicitudBtn.setVisible(false);
		}else {
			rechazarSolicitudBtn.setVisible(true);
			aceptarSolicitudBtn.setVisible(true);
		}
		
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
	
	public void onClick$aceptarSolicitudBtn() {
		Map<String,Object>params1 = new HashMap<String,Object>();
		params1.put("accion", "actualizardata");
		params1.put("solicitud", solicitud);
		try {
			nodoService.aceptarSolicitud(solicitud);
			Events.sendEvent(Events.ON_NOTIFY,this.self.getParent(),params1);
			this.self.detach();
		} catch (VendedorInexistenteException e) {
			Clients.showNotification("ocurrio un error");
			e.printStackTrace();
		}
	}
	
	public void onClick$rechazarSolicitudBtn() {
		Map<String,Object>params1 = new HashMap<String,Object>();
		params1.put("accion", "actualizardata");
		params1.put("solicitud", solicitud);
		try {
			nodoService.rechazarSolicitud(solicitud);
			Events.sendEvent(Events.ON_NOTIFY,this.self.getParent(),params1);
			this.self.detach();
		} catch (Exception e) {
			Clients.showNotification("ocurrio un error");
			e.printStackTrace();
		}
	}
	
	public void onClick$salirBtn() {
		this.self.detach();
		this.binder.loadAll();
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

	public Button getAceptarSolicitudBtn() {
		return aceptarSolicitudBtn;
	}

	public void setAceptarSolicitudBtn(Button aceptarSolicitudBtn) {
		this.aceptarSolicitudBtn = aceptarSolicitudBtn;
	}

	public Button getRechazarSolicitudBtn() {
		return rechazarSolicitudBtn;
	}

	public void setRechazarSolicitudBtn(Button rechazarSolicitudBtn) {
		this.rechazarSolicitudBtn = rechazarSolicitudBtn;
	}

	public Button getSalirBtn() {
		return salirBtn;
	}

	public void setSalirBtn(Button salirBtn) {
		this.salirBtn = salirBtn;
	}
	
	
}
