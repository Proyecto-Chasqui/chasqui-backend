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

	void confirmarteSinMontoMinimo() throws EstadoPedidoIncorrectoException;
	
	/**
	 * @return true si es un pedido para pasar a retirar sino false.
	 */
	boolean esParaRetirar();

	/**
	 * @return true si es un pedido a domicilio sino false.
	 */
	boolean esParaDomicilio();

}
