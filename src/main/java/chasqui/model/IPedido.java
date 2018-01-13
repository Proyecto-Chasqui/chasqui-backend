package chasqui.model;

import chasqui.exceptions.EstadoPedidoIncorrectoException;

public interface IPedido {

	Double getMontoTotal();

	public String getEstado();

	void setZona(Zona zona);
	
	public Zona getZona();

	public void entregarte() throws EstadoPedidoIncorrectoException;

	public void cancelar() throws EstadoPedidoIncorrectoException;

	void confirmarte() throws EstadoPedidoIncorrectoException;

	void setDireccionEntrega(Direccion direccion);
	
	public Direccion getDireccionEntrega();

	void preparado() throws EstadoPedidoIncorrectoException;

}
