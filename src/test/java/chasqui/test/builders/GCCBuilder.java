package chasqui.test.builders;

import java.util.ArrayList;
import java.util.List;

import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.GrupoCC;
import chasqui.model.HistorialGCC;
import chasqui.model.MiembroDeGCC;
import chasqui.model.PedidoColectivo;
import chasqui.model.Vendedor;

public class GCCBuilder {
	
	
	//vendedor y administrador no deberian estar sin asignar
	private Cliente administrador;
	private String alias = "un alias";
	private String descripcion = "una descripcion"; 	
	private Direccion domicilioEntrega = DireccionBuilder.unaDireccion().build();
	private Boolean pedidosHabilitados = true;
	private PedidoColectivo pedidoActual = new PedidoColectivo();
	private HistorialGCC historial= new HistorialGCC();
	private Vendedor vendedor;
	private List<MiembroDeGCC> cache = new ArrayList<MiembroDeGCC>();
	
	public static GCCBuilder unGCC(){
		return new GCCBuilder();
	};
	
	public GrupoCC build(){
		GrupoCC gcc = new GrupoCC();
		gcc.setAdministrador(administrador);
		gcc.setAlias(alias);
		gcc.setCache(cache);
		gcc.setDescripcion(descripcion);
		//2017.09.21 gcc.setDomicilioEntrega(domicilioEntrega);
		gcc.setPedidoActual(pedidoActual);
		gcc.setPedidosHabilitados(pedidosHabilitados);
		gcc.setVendedor(vendedor);
		return gcc;
	}
	
	public GCCBuilder conCache(List<MiembroDeGCC> cache){
		this.cache = cache;
		return this;
	}
	
	public GCCBuilder conVendedor(Vendedor vendedor){
		this.vendedor = vendedor;
		return this;
				
	}
	
	public GCCBuilder conHistorialGCC(HistorialGCC historial){
		this.historial = historial;
		return this;
	}
	
	public GCCBuilder conPedidoColectivoActual(PedidoColectivo pcc){
		this.pedidoActual = pcc;
		return this;
	}
	
	public GCCBuilder conPedidosHabilitados(Boolean bool){
		this.pedidosHabilitados = bool;
		return this;
	}
	
	public GCCBuilder conDomicilioDeEntrega(Direccion unaDireccion){
		this.domicilioEntrega = unaDireccion;
		return this;
	}
	
	public GCCBuilder conDescripcion(String alias){
		this.descripcion = alias;
		return this;
	}
	
	public GCCBuilder conAlias(String alias){
		this.alias = alias;
		return this;
	}
	
	public GCCBuilder conAdmin(Cliente admin){
		this.administrador = admin;
		return this;
	}
	
	
}
