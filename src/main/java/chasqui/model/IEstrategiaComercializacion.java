package chasqui.model;

public interface IEstrategiaComercializacion {

	public abstract String getNombreEstrategia();

	public abstract boolean permiteAltaGCC();
	
	public abstract boolean permiteBajaGCC();

	public abstract boolean permiteNuevoPedidoIndividual();
	
	public abstract boolean permiteNuevoPedidoGrupal();
	
	public Integer getId() ;
	
	public void setId(Integer id);
}
