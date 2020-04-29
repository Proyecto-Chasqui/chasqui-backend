package chasqui.service.rest.request;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import chasqui.model.Pedido;
import chasqui.model.PedidoColectivo;
import chasqui.service.rest.response.DireccionResponse;
import chasqui.service.rest.response.PedidoResponse;
import chasqui.service.rest.response.PuntoDeRetiroResponse;
import chasqui.service.rest.response.ZonaResponse;

public class PedidoColectivoResponse {
	private static final long serialVersionUID = -3034619190993190512L;
	
	private String estado;
	private String fechaCreacion;
	private String fechaModificacion;
	private Double montoTotal;
	private Double incentivoTotal;
	private ZonaResponse zona;
	private DireccionResponse direccion;
	private PuntoDeRetiroResponse puntoDeRetiro;
	private List<PedidoResponse> pedidos;


	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}


	public String getFechaCreacion() {
		return fechaCreacion;
	}


	public void setFechaCreacion(String fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}


	public String getFechaModificacion() {
		return fechaModificacion;
	}


	public void setFechaModificacion(String fechaVencimiento) {
		this.fechaModificacion = fechaVencimiento;
	}


	private void cargarDireccionYZonaSeleccionadaDePedidoColectivo(PedidoColectivo p) {
		if(p.getDireccionEntrega() != null) {
			direccion = new DireccionResponse(p.getDireccionEntrega());
		}
		
		if(p.getPuntoDeRetiro() != null) {	
			setPuntoDeRetiro(new PuntoDeRetiroResponse(p.getPuntoDeRetiro()));
		}
		
		if(p.getZona() != null) {
			zona = new ZonaResponse(p.getZona());
		}
		
	}


	public PedidoColectivoResponse(PedidoColectivo p) {

		estado = p.getEstado();
		DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
		fechaCreacion = f.format(p.getFechaCreacion().toDate());
		fechaModificacion = (p.getFechaModificacion()!=null)?f.format(p.getFechaModificacion().toDate()):null;
		montoTotal = p.getMontoTotal();
		incentivoTotal = p.getMontoTotalDeIncentivos();
		cargarDireccionYZonaSeleccionadaDePedidoColectivo(p);
		setPedidos(crearListaDePedidos(p));
	}

	private List<PedidoResponse> crearListaDePedidos(PedidoColectivo p) {
		List<PedidoResponse> pedidos= new ArrayList<PedidoResponse>();
		for(Pedido pd: p.getPedidosIndividuales().values()) {
			pedidos.add(new PedidoResponse(pd));
		}
		return pedidos;
	}

	public ZonaResponse getZona() {
		return zona;
	}


	public void setZona(ZonaResponse zona) {
		this.zona = zona;
	}


	public DireccionResponse getDireccion() {
		return direccion;
	}


	public void setDireccion(DireccionResponse direccion) {
		this.direccion = direccion;
	}


	public PuntoDeRetiroResponse getPuntoDeRetiro() {
		return puntoDeRetiro;
	}


	public void setPuntoDeRetiro(PuntoDeRetiroResponse puntoDeRetiro) {
		this.puntoDeRetiro = puntoDeRetiro;
	}


	public Double getMontoTotal() {
		return montoTotal;
	}

	public void setMontoTotal(Double montoTotal) {
		this.montoTotal = montoTotal;
	}

	public List<PedidoResponse> getPedidos() {
		return pedidos;
	}

	public void setPedidos(List<PedidoResponse> pedidos) {
		this.pedidos = pedidos;
	}

	public Double getIncentivoTotal() {
		return incentivoTotal;
	}

	public void setIncentivoTotal(Double incentivoTotal) {
		this.incentivoTotal = incentivoTotal;
	}
}
