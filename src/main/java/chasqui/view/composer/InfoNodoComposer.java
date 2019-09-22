package chasqui.view.composer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox.ClickEvent;

import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Cliente;
import chasqui.model.MiembroDeGCC;
import chasqui.model.Nodo;
import chasqui.model.PedidoColectivo;
import chasqui.model.SolicitudCreacionNodo;
import chasqui.services.interfaces.NodoService;
import chasqui.view.renders.MiembrosGCCRenderer;
import chasqui.view.renders.SolicitudCreacionNodosRenderer;

public class InfoNodoComposer extends GenericForwardComposer<Component>{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5704145897414677360L;

	private AnnotateDataBinder binder;
	
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
	private Listbox listboxUsuarios;
	private List<MiembroDeGCC> clientes;
	private String totalUltimoConsumo;
	private String fechaUltimoConsumo;
	private String totalHastaLaFecha;
	private String totalUltimos3Consumos;
	private String fechaCreacion;
	private Nodo nodo;
	private NodoService nodoService;
	
	private Component component;
	
	public void doAfterCompose(Component c) throws Exception{
		super.doAfterCompose(c);
		component = c;
		nodoService = (NodoService) SpringUtil.getBean("nodoService");
		binder = new AnnotateDataBinder(c);
		nodo = (Nodo) Executions.getCurrent().getArg().get("nodo");
		datacliente = nodo.getAdministrador(); 
		clientes = obtenerUsuariosActivos(nodo.getCache());
		listboxUsuarios.setItemRenderer(new MiembrosGCCRenderer((Window) c));
		this.fillData(nodo);
		this.fillEstadisticas(nodo);
		this.binder.loadAll();
	}

	private List<MiembroDeGCC> obtenerUsuariosActivos(List<MiembroDeGCC> cache) {
		List<MiembroDeGCC> miembrosActivos = new ArrayList<MiembroDeGCC>();
		for(MiembroDeGCC miembro: cache) {
			if(miembro.getEstadoInvitacion().equals(Constantes.ESTADO_NOTIFICACION_LEIDA_ACEPTADA)) {
				miembrosActivos.add(miembro);
			}
		}
		return miembrosActivos;
	}

	private void fillEstadisticas(Nodo nodo2) {
		this.fillFechaUltimoConsumo(nodo);
		this.fillTotalUltimoConsumo(nodo);
		this.fillTotalHastaLaFecha(nodo);
		this.totalUltimos3Consumos(nodo);
	}

	private void totalUltimos3Consumos(Nodo nodo2) {
		Double total = 0.0;
		List<PedidoColectivo> pedidos = nodo.getHistorial().getPedidosGrupales();
		pedidos.sort(new Comparator<PedidoColectivo>() {
			@Override
			public int compare(PedidoColectivo o1, PedidoColectivo o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});
		Integer startIndex = startIndex(pedidos,3);
		Integer endIndex = pedidos.size();
		pedidos = pedidos.subList(startIndex, endIndex);
		for(PedidoColectivo pedidoColectivo: pedidos) {
			total = total + pedidoColectivo.getMontoTotal();
		}
		this.totalUltimos3Consumos = Double.toString(total);
		
	}

	private Integer startIndex(List<PedidoColectivo> pedidos, Integer gap) {
		if(pedidos.size() > gap){
			return pedidos.size() - gap;
		}else {
			return 0;
		}
	}

	private void fillTotalHastaLaFecha(Nodo nodo2) {
		Double consumo = 0.0;
		for (PedidoColectivo pedidoColectivo: nodo.getHistorial().getPedidosGrupales()) {
			if(pedidoColectivo.getFechaModificacion()!=null) {
				consumo = consumo + pedidoColectivo.getMontoTotal();
			}
		}
		this.totalHastaLaFecha = Double.toString(consumo);
	}

	private void fillTotalUltimoConsumo(Nodo nodo2) {
		Double consumo = 0.0;
		Integer idMasAlto = 0;
		PedidoColectivo ultimoPedido = null;
		for (PedidoColectivo pedidoColectivo: nodo.getHistorial().getPedidosGrupales()) {
			if(pedidoColectivo.getFechaModificacion()!=null) {
				if(pedidoColectivo.getId() > idMasAlto) {
					idMasAlto = pedidoColectivo.getId();
					ultimoPedido = pedidoColectivo;
				}
			}
		}
		if(ultimoPedido != null) {
			consumo = ultimoPedido.getMontoTotal();
		}
		totalUltimoConsumo = Double.toString(consumo);
	}

	private void fillFechaUltimoConsumo(Nodo nodo2) {
		DateTime fecha = null;
		for (PedidoColectivo pedidoColectivo: nodo.getHistorial().getPedidosGrupales()) {
			if(pedidoColectivo.getFechaModificacion()!=null) {
				if(fecha == null) {
					fecha = pedidoColectivo.getFechaModificacion();
				}else {
					fecha = (pedidoColectivo.getFechaModificacion().isAfter(fecha))? pedidoColectivo.getFechaModificacion() : fecha;
				}
			}
		}
		if(fecha == null) {
			fechaUltimoConsumo = "No hay consumos";
		}else {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			Date d = new Date(fecha.getMillis());
			fechaUltimoConsumo = format.format(d);
		}
	}

	private void fillData(Nodo nodo) {
		estado = this.renderizarConstante(nodo.getTipo());
		cliente = datacliente.getNombre() + " " + datacliente.getApellido();
		mail = datacliente.getEmail();
		telfijo = (datacliente.getTelefonoFijo().equals(""))? "N/D" : datacliente.getTelefonoFijo();
		celular = (datacliente.getTelefonoMovil().equals(""))? "N/D" : datacliente.getTelefonoMovil();
		barrio = nodo.getBarrio();
		setDireccion(nodo.getDireccionDelNodo().toString());
		tipoNodo = this.renderizarConstante(nodo.getTipo());
		descripcion = nodo.getDescripcion();
		nombreNodo = nodo.getAlias();
		if(nodo.getFechaCreacion() != null) {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			Date d = new Date( nodo.getFechaCreacion().getMillis());
			fechaCreacion = format.format(d);
		}else {
			fechaCreacion = "N/D";
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
			return "ABIERTO";
		}
		if(estado.equals(Constantes.NODO_CERRADO)) {
			return "CERRADO";
		}
		
		return "N/D";
	}
	
	public void onClick$salirBtn() {
		this.self.detach();
		this.binder.loadAll();
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

	public Button getSalirBtn() {
		return salirBtn;
	}

	public void setSalirBtn(Button salirBtn) {
		this.salirBtn = salirBtn;
	}

	public Listbox getListboxUsuarios() {
		return listboxUsuarios;
	}

	public List<MiembroDeGCC> getClientes() {
		return clientes;
	}

	public void setListboxUsuarios(Listbox listboxUsuarios) {
		this.listboxUsuarios = listboxUsuarios;
	}

	public void setClientes(List<MiembroDeGCC> clientes) {
		this.clientes = clientes;
	}

	public String getTotalUltimoConsumo() {
		return totalUltimoConsumo;
	}

	public String getFechaUltimoConsumo() {
		return fechaUltimoConsumo;
	}

	public String getTotalHastaLaFecha() {
		return totalHastaLaFecha;
	}

	public String getTotalUltimos3Consumos() {
		return totalUltimos3Consumos;
	}

	public void setTotalUltimoConsumo(String totalUltimoConsumo) {
		this.totalUltimoConsumo = totalUltimoConsumo;
	}

	public void setFechaUltimoConsumo(String fechaUltimoConsumo) {
		this.fechaUltimoConsumo = fechaUltimoConsumo;
	}

	public void setTotalHastaLaFecha(String totalHastaLaFecha) {
		this.totalHastaLaFecha = totalHastaLaFecha;
	}

	public void setTotalUltimos3Consumos(String totalUltimos3Consumos) {
		this.totalUltimos3Consumos = totalUltimos3Consumos;
	}

	public String getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(String fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
}
