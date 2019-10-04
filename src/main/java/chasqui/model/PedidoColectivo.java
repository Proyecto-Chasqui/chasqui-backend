package chasqui.model;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.DateTime;

import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.exceptions.PedidoDuplicadoEnGCC;
import chasqui.view.composer.Constantes;

/**
 * @author huenu
 * TODO deberia contar con un metodo que le permita chequear que cada usuario 
 * tiene un solo pedido en el pedido colectivo
 */
public class PedidoColectivo implements IPedido{

	private Map<String,Pedido> pedidosIndividuales;
	private Integer id;
	private String estado;
	private Zona zona;
	private Direccion domicilioEntrega;
	private PuntoDeRetiro puntoDeRetiro;
	@Temporal(TemporalType.TIMESTAMP)
	private DateTime fechaCreacion;
	@Temporal(TemporalType.TIMESTAMP)
	private DateTime fechaModificacion;
	private String comentario;
	private Map<String,String> respuestasAPreguntas;
	private GrupoCC colectivo;
	DecimalFormat df = new DecimalFormat("#.##");
	
	public PedidoColectivo() {
		pedidosIndividuales = new HashMap<String,Pedido>();
		this.fechaCreacion = new DateTime();
		this.fechaModificacion = new DateTime();
		this.estado = Constantes.ESTADO_PEDIDO_ABIERTO;
	}
	
	public Map<String, Pedido> getPedidosIndividuales() {
		return pedidosIndividuales;
	}

	public void setPedidosIndividuales(Map<String, Pedido> pedidosIndividuales) {
		this.pedidosIndividuales = pedidosIndividuales;
	}
	
	public void agregarPedidoIndividual(Pedido nuevoPedido) {		
		if (this.tienePedidoParaCliente(nuevoPedido.getCliente().getEmail())){
			throw new PedidoDuplicadoEnGCC("El usuario: "+ nuevoPedido.getCliente().getEmail() +" ya posee un pedido vigente para este grupo de compras colectivas");
		}
		else{
			nuevoPedido.setPedidoColectivo(this); //Agregado 2017.07.14
			
			this.pedidosIndividuales.put(nuevoPedido.getCliente().getEmail(), nuevoPedido);
		}
	}

	public boolean tienePedidoParaCliente(String usuarioBuscado) {
		return this.pedidosIndividuales.containsKey(usuarioBuscado);
	}	

	public Pedido buscarPedidoParaCliente(String usuarioBuscado) {
		return this.pedidosIndividuales.get(usuarioBuscado);
	}
	
	private Double trim2decimals(Double d) {
		String trim = df.format(d); 
		Double value = Double.parseDouble(trim.replace(",","."));
		return value;
	}

	@Override
	public Double getMontoTotal() {
		Double total=0.0;
		for(Pedido pedido: pedidosIndividuales.values()){
			if(estaConfirmado(pedido.getEstado())){
				total=total+pedido.getMontoActual();
			}
		}			
		return trim2decimals(total);
	}
	
	public Double getMontoTotalDeIncentivos() {
		Double total=0.0;
		for(Pedido pedido: pedidosIndividuales.values()){
			if(estaConfirmado(pedido.getEstado())){
				total=total+pedido.getMontoTotalIncentivo();
			}
		}			
		return trim2decimals(total);
	}
	
	private boolean estaConfirmado(String estado) {
		return estado.equals(Constantes.ESTADO_PEDIDO_CONFIRMADO) || estado.equals(Constantes.ESTADO_PEDIDO_PREPARADO) || estado.equals(Constantes.ESTADO_PEDIDO_ENTREGADO);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public GrupoCC getColectivo() {
		return colectivo;
	}

	public void setColectivo(GrupoCC colectivo) {
		this.colectivo = colectivo;
	}

	@Override
	public String getEstado() {
		return estado;
	}
	
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	public boolean estaAbierto(){
		return this.estado.equals(Constantes.ESTADO_PEDIDO_ABIERTO);
	}
	
	/*
	 * Workflow
	 *   *--> ABIERTO --> CONFIRMADO --> ENTREGADO
	 *   *--> ABIERTO --> CANCELADO    
	 */
	@Override
	public void confirmarte() throws EstadoPedidoIncorrectoException{
		if (this.estado.equals(Constantes.ESTADO_PEDIDO_ABIERTO)) {
			this.estado = Constantes.ESTADO_PEDIDO_CONFIRMADO;			
		}
		else{
			throw new EstadoPedidoIncorrectoException("El pedido no estaba abierto");
		}
			
	}
	@Override
	public void cancelar() throws EstadoPedidoIncorrectoException{
		if (this.estado.equals(Constantes.ESTADO_PEDIDO_ABIERTO)) {
			this.estado = Constantes.ESTADO_PEDIDO_CANCELADO;			
		}
		else{
			throw new EstadoPedidoIncorrectoException("El pedido no estaba abierto");
		}
			
	}

	@Override
	public void entregarte() throws EstadoPedidoIncorrectoException {
		if (this.estado.equals(Constantes.ESTADO_PEDIDO_PREPARADO)) {
			this.estado = Constantes.ESTADO_PEDIDO_ENTREGADO;
			this.cambiarEstadoDePedidosIndividuales(Constantes.ESTADO_PEDIDO_PREPARADO, Constantes.ESTADO_PEDIDO_ENTREGADO);
		}
		else{
			throw new EstadoPedidoIncorrectoException("El pedido no estaba confirmado");
		}
	}
	
	public boolean tieneAlgunPedidoConfirmado() {
		for (Pedido pedido : pedidosIndividuales.values()) {
			if (pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_CONFIRMADO)||pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_CANCELADO)) {
				return true;
			}
		}
		return false;
	}

	public boolean tienePedidosAbiertos() {
		for (Pedido pedido : pedidosIndividuales.values()) {
			if (pedido.estaAbierto()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean tienePedidos(){
		return !pedidosIndividuales.isEmpty();
	}


	public boolean todosLosPedidosCancelados() {
		for (Pedido pedido : pedidosIndividuales.values()) {
			if (!pedido.estaCancelado()) {
				return false;
			}
		}
		return true;
	}
	
	

	@Override
	public void setZona(Zona zona) {
		this.zona = zona;
	}

	@Override
	public Zona getZona() {
		return this.zona;
	}

	@Override
	public void setDireccionEntrega(Direccion direccion) {
		this.domicilioEntrega = direccion;
	}
	
	@Override
	public Direccion getDireccionEntrega() {
		return domicilioEntrega;
	}

	public DateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(DateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	@Override
	public void preparado() throws EstadoPedidoIncorrectoException {
		if (this.estado.equals(Constantes.ESTADO_PEDIDO_CONFIRMADO)) {
			this.estado = Constantes.ESTADO_PEDIDO_PREPARADO;	
			this.grabarPuntoDeRetiro();
			this.cambiarEstadoDePedidosIndividuales(Constantes.ESTADO_PEDIDO_CONFIRMADO, Constantes.ESTADO_PEDIDO_PREPARADO);
		}
		else{
			throw new EstadoPedidoIncorrectoException("El pedido no estaba confirmado");
		}
	}
	
	public void grabarPuntoDeRetiro(){
		if(this.puntoDeRetiro != null) {
			this.puntoDeRetiro = new PuntoDeRetiro(this.puntoDeRetiro);		
		}
	}

	/**
	 * Cambia el estado de los pedidos individuales dentro del
	 * pedido colectivo.
	 * 
	 * @param estadoRequerido Estado de los pedidos a cambiar. 
	 * @param estado Estado por el que se cambiara.
	 */
	private void cambiarEstadoDePedidosIndividuales(String estadoRequerido, String estado) {
		for(Pedido pedido: pedidosIndividuales.values()){
			if(pedido.getEstado().equals(estadoRequerido)){
				pedido.setEstado(estado);
			}
		}
	}

	public PuntoDeRetiro getPuntoDeRetiro() {
		return puntoDeRetiro;
	}

	public void setPuntoDeRetiro(PuntoDeRetiro puntoDeRetiro) {
		this.puntoDeRetiro = puntoDeRetiro;
	}

	public Map<String,String> getRespuestasAPreguntas() {
		return respuestasAPreguntas;
	}

	public void setRespuestasAPreguntas(Map<String,String> respuestasAPreguntas) {
		this.respuestasAPreguntas = respuestasAPreguntas;
	}

	public DateTime getFechaModificacion() {
		return fechaModificacion;
	}

	public void setFechaModificacion(DateTime fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}

	@Override
	public void confirmarteSinMontoMinimo() throws EstadoPedidoIncorrectoException {
		if (this.estado.equals(Constantes.ESTADO_PEDIDO_ABIERTO)) {
			this.estado = Constantes.ESTADO_PEDIDO_CONFIRMADO;			
		}
		else{
			throw new EstadoPedidoIncorrectoException("El pedido no estaba abierto");
		}
		
	}

	@Override
	public boolean esParaRetirar() {
		return (this.puntoDeRetiro != null);
	}

	@Override
	public boolean esParaDomicilio() {
		return (this.domicilioEntrega != null);
	}

}
