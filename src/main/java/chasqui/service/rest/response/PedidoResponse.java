package chasqui.service.rest.response;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import chasqui.model.Pedido;
import chasqui.model.PedidoColectivo;
import chasqui.model.ProductoPedido;
import chasqui.model.Usuario;
import chasqui.services.interfaces.GrupoService;

public class PedidoResponse implements Serializable {

	/**
	 * 
	 */
	@Autowired
	GrupoService grupoService;
	
	
	private static final long serialVersionUID = -3034619190993190512L;
	
	
	private Integer id;
	private Integer idGrupo;
	private Integer idVendedor;
	private ClienteResponse cliente;
	private String estado;
	private String aliasGrupo;
	private String fechaCreacion;
	private String fechaVencimiento;
	private Double montoMinimo;
	private Double montoActual;
	private Double incentivoActual;
	private String nombreVendedor;
	private ZonaResponse zona;
	private DireccionResponse direccion;
	private PuntoDeRetiroResponse puntoDeRetiro;
	private List<ProductoPedidoResponse> productosResponse;	
	
	
	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getEstado() {
		return estado;
	}

	public Integer getIdVendedor() {
		return idVendedor;
	}


	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
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


	public String getFechaVencimiento() {
		return fechaVencimiento;
	}


	public void setFechaVencimiento(String fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}


	public Double getMontoMinimo() {
		return montoMinimo;
	}


	public void setMontoMinimo(Double montoMinimo) {
		this.montoMinimo = montoMinimo;
	}


	public Double getMontoActual() {
		return montoActual;
	}


	public void setMontoActual(Double montoActual) {
		this.montoActual = montoActual;
	}
	
	
	



	public String getNombreVendedor() {
		return nombreVendedor;
	}


	public void setNombreVendedor(String nombreVendedor) {
		this.nombreVendedor = nombreVendedor;
	}


	public List<ProductoPedidoResponse> getProductosResponse() {
		return productosResponse;
	}


	public void setProductosResponse(List<ProductoPedidoResponse> productosResponse) {
		this.productosResponse = productosResponse;
	}

	
	public PedidoResponse(Integer idGrupo,String aliasGrupo, Pedido p){
		this.setIdGrupo(idGrupo);
		this.setAliasGrupo(aliasGrupo);
		id = p.getId();
		idVendedor = p.getIdVendedor();
		estado = p.getEstado();
		nombreVendedor=p.getNombreVendedor();
		DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
		fechaCreacion = f.format(p.getFechaCreacion().toDate());
		if(p.getFechaDeVencimiento()!=null){
			fechaVencimiento = f.format(p.getFechaDeVencimiento().toDate());
		}
		montoMinimo = p.getMontoMinimo();
		montoActual = p.getMontoActual();
		productosResponse = new ArrayList<ProductoPedidoResponse>();
		if (p.getProductosEnPedido() != null) {
			for(ProductoPedido pp : p.getProductosEnPedido()){
				productosResponse.add(new ProductoPedidoResponse(pp));
			}
		}

		
		
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


	private void cargarDireccionYZonaSeleccionada(Pedido p) {
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


	public PedidoResponse(Pedido p) {
		if(p.getPerteneceAPedidoGrupal()) {
			if(p.getPedidoColectivo() != null) {
				if(p.getPedidoColectivo().getColectivo() != null) {
					this.setIdGrupo(p.getPedidoColectivo().getColectivo().getId());
					this.setAliasGrupo(p.getPedidoColectivo().getColectivo().getAlias());
				}else {
					this.setAliasGrupo("Pedido Grupal");
				}
			}else {
				this.setAliasGrupo("Pedido Grupal");
			}
		}
		id = p.getId();
		idVendedor = p.getIdVendedor();
		estado = p.getEstado();
		nombreVendedor=p.getNombreVendedor();
		setCliente(new ClienteResponse(p.getCliente()));
		DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
		fechaCreacion = f.format(p.getFechaCreacion().toDate());
		fechaVencimiento = (p.getFechaDeVencimiento()!=null)?f.format(p.getFechaDeVencimiento().toDate()):null;
		montoMinimo = p.getMontoMinimo();
		montoActual = p.getMontoActual();
		incentivoActual = p.getMontoTotalIncentivo();
		productosResponse = new ArrayList<ProductoPedidoResponse>();
		if (p.getProductosEnPedido() != null) {
			for(ProductoPedido pp : p.getProductosEnPedido()){
				productosResponse.add(new ProductoPedidoResponse(pp));
			}
		}
		cargarDireccionYZonaSeleccionada(p);
		if(p.getPedidoColectivo() != null) {
			cargarDireccionYZonaSeleccionadaDePedidoColectivo(p.getPedidoColectivo());
		}
	}


	public Integer getIdGrupo() {
		return idGrupo;
	}


	public void setIdGrupo(Integer idGrupo) {
		this.idGrupo = idGrupo;
	}


	public String getAliasGrupo() {
		return aliasGrupo;
	}


	public void setAliasGrupo(String aliasGrupo) {
		this.aliasGrupo = aliasGrupo;
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


	public ClienteResponse getCliente() {
		return cliente;
	}


	public void setCliente(ClienteResponse cliente) {
		this.cliente = cliente;
	}


	public Double getIncentivoActual() {
		return incentivoActual;
	}


	public void setIncentivoActual(Double incentivoActual) {
		this.incentivoActual = incentivoActual;
	}
	

}
