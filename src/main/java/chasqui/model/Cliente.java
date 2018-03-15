package chasqui.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.cxf.common.util.StringUtils;
import org.joda.time.DateTime;
import org.zkoss.zkplus.spring.SpringUtil;

import chasqui.dao.PuntoDeRetiroDAO;
import chasqui.exceptions.DireccionesInexistentes;
import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.exceptions.PedidoInexistenteException;
import chasqui.service.rest.request.DireccionRequest;
import chasqui.service.rest.request.EditarPerfilRequest;
import chasqui.service.rest.request.SingUpRequest;
import chasqui.services.impl.UsuarioServiceImpl;
import chasqui.services.interfaces.UsuarioService;
import chasqui.view.composer.Constantes;

public class Cliente extends Usuario {

	private String nombre;
	private String apellido;
//	private String nickName;
	private String telefonoFijo;
	private String telefonoMovil;
	private List<Direccion> direccionesAlternativas;
	private List<Notificacion> notificaciones;
	private Historial historialPedidos;
	private String idDispositivo;
	private List<Pedido> pedidos;
	private String estado;
	// Atributos necesarios para la integracion con spring security
	private String rol = "ROLE_USER";
	private String token;

	// GETs & SETs

	public Cliente() {
		//this.pedidos = new ArrayList<Pedido>();
	}
	
	public Cliente(SingUpRequest request, String nuevoToken) throws Exception {
		nombre = request.getNombre();
		apellido = request.getApellido();
		username = request.getNickName();
		email = request.getEmail();
		telefonoFijo = request.getTelefonoFijo();
		telefonoMovil = request.getTelefonoMovil();
		direccionesAlternativas = new ArrayList<Direccion>();
		token = nuevoToken;
		password = request.getPassword();
		//TODO: Pasar a Sin confirmar cuando el front  lo soporte.
		estado = Constantes.MAIL_CONFIRMADO;
		//this.pedidos = new ArrayList<Pedido>();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public String getIdDispositivo() {
		return idDispositivo;
	}

	public void setIdDispositivo(String idDIspositivo) {
		this.idDispositivo = idDIspositivo;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getTelefonoFijo() {
		return telefonoFijo;
	}

	public void setTelefonoFijo(String telefonoFijo) {
		this.telefonoFijo = telefonoFijo;
	}

	public String getTelefonoMovil() {
		return telefonoMovil;
	}

	public void setTelefonoMovil(String telefonoMovil) {
		this.telefonoMovil = telefonoMovil;
	}

	public List<Direccion> getDireccionesAlternativas() {
		return direccionesAlternativas;
	}

	public void setDireccionesAlternativas(List<Direccion> direccionesAlternativas) {
		this.direccionesAlternativas = direccionesAlternativas;
	}

	public List<Notificacion> getNotificaciones() {
		return notificaciones;
	}

	public void setNotificaciones(List<Notificacion> notificaciones) {
		this.notificaciones = notificaciones;
	}

	public Historial getHistorialPedidos() {
		return historialPedidos;
	}

	public void setHistorialPedidos(Historial historialPedidos) {
		this.historialPedidos = historialPedidos;
	}


	public List<Pedido> getPedidos() {
		return pedidos;
	}

	public void setPedidos(List<Pedido> pedidos) {
		this.pedidos = pedidos;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void modificarCon(EditarPerfilRequest editRequest) throws Exception {
		if (!StringUtils.isEmpty(editRequest.getApellido())) {
			this.apellido = editRequest.getApellido();
		}

		if (!StringUtils.isEmpty(editRequest.getNickName())) {
			this.username = editRequest.getNickName();
		}
		if (!StringUtils.isEmpty(editRequest.getNombre())) {
			this.nombre = editRequest.getNombre();
		}
		if (!StringUtils.isEmpty(editRequest.getPassword())) {
			this.password = editRequest.getPassword();
		}
		if (editRequest.getTelefonoFijo() != null) {
			this.telefonoFijo = editRequest.getTelefonoFijo();
		}
		if (editRequest.getTelefonoMovil() != null) {
			this.telefonoMovil = editRequest.getTelefonoMovil();
		}
		// Direccion d = this.obtenerDireccionPredeterminada();
		// d.modificarCon(editRequest.getDireccion());

	}

	public Direccion obtenerDireccionPredeterminada() {
		for (Direccion d : this.getDireccionesAlternativas()) {
			if (d.getPredeterminada()) {
				return d;
			}
		}
		return (this.getDireccionesAlternativas() != null && this.getDireccionesAlternativas().size() > 0)
				? this.getDireccionesAlternativas().get(0) : null;
	}

	public Direccion agregarDireccion(DireccionRequest request) {
		Direccion d = new Direccion(request);

		if (this.direccionesAlternativas.size() == 0) {
			// Primera dirección
			d.setPredeterminada(true);
		} else {
			Direccion predeterminada = this.obtenerDireccionPredeterminada();
			if (request.getPredeterminada() && predeterminada != null) {
				predeterminada.setPredeterminada(false);
				d.setPredeterminada(true);
			}
		}

		this.direccionesAlternativas.add(d);
		return d;

	}

	public void editarDireccionCon(DireccionRequest request, Integer idDireccion) throws DireccionesInexistentes {
		boolean predeterminada = request.getPredeterminada();

		if (predeterminada) {
			Direccion pre = this.obtenerDireccionPredeterminada();
			if (pre != null) {
				pre.setPredeterminada(false);
			}
		}

		if (contieneDireccionConId(idDireccion, direccionesAlternativas)) {
			for (Direccion d : direccionesAlternativas) {
				if (idDireccion.equals(d.getId())) {
					// d.editate(altura,calle,alias,localidad,departamento,codigoPostal,latitud,longitud,predeterminada);
					d.modificarCon(request);
				}
			}
		} else {
			throw new DireccionesInexistentes();
		}
	}

	private boolean contieneDireccionConId(Integer idDireccion, List<Direccion> direccionesAlternativas) {
		for (Direccion d : direccionesAlternativas) {
			if (idDireccion.equals(d.getId())) {
				return true;
			}
		}
		return false;
	}

	public void eliminarDireccion(Integer idDireccion) throws DireccionesInexistentes {
		if (contieneDireccionConId(idDireccion, direccionesAlternativas)) {
			Iterator<Direccion> it = direccionesAlternativas.iterator();
			while (it.hasNext()) {
				Direccion d = it.next();
				if (d.getId().equals(idDireccion)) {
					it.remove();
				}
			}
		} else {
			throw new DireccionesInexistentes();
		}

	}

	/*
	 * Obtiene el pedido INDIVIDUAL vigente en un determinado catálogo (vendedor) .
	 * Dispara una excepción si aún no existe
	 */
	public Pedido obtenerPedidoActualDe(Integer idVendedor) throws PedidoInexistenteException {
		for (Pedido p : pedidos) {
			if (p.getIdVendedor().equals(idVendedor) && p.estaVigente() && !p.getPerteneceAPedidoGrupal()) {
				return p;
			}
		}
		throw new PedidoInexistenteException(
				"El usuario: " + this.getUsername() + " ("+this.getEmail()+ ") no posee ningun pedido vigente para el vendedor solicitado");
	}

	public boolean contienePedidoVigenteParaVendedor(Integer idVendedor) {

		for (Pedido p : pedidos) {
			if (p.getIdVendedor().equals(idVendedor) && p.estaVigente()&& !p.getPerteneceAPedidoGrupal()) {
				return true;
			}
		}

		return false;
	}
	
	public boolean contienePedidoAbiertoOCanceladoParaVendedor(Integer idVendedor) {
		//Tiene al menos un pedido para el vendedor, vigente o cancelado
		for (Pedido p : pedidos) {
			if (p.getIdVendedor().equals(idVendedor) && (p.estaVigente() || p.estaCancelado()||p.getEstado().equals(Constantes.ESTADO_PEDIDO_VENCIDO))) {
				return true;
			}
		}

		return false;
	}

	public void agregarPedido(Pedido p) {
		if(p!= null) this.pedidos.add(p);
	}

	public boolean contienePedido(Integer idPedido) {
		Pedido p = encontrarPedidoConId(idPedido);
		return p != null && p.estaVigente();
	}

	public boolean tienePedidoDeVendedor(Integer idVendedor, Integer idPedido) {
		Pedido p = encontrarPedidoConId(idPedido);
		return p != null && p.getIdVendedor().equals(idVendedor);
	}

	public void agregarProductoAPedido(Variante v, Integer idPedido, Integer cantidad, DateTime nuevoVencimiento) throws EstadoPedidoIncorrectoException {
		Pedido p = encontrarPedidoConId(idPedido);
		ProductoPedido pp = new ProductoPedido(v, cantidad);
		p.agregarProductoPedido(pp, nuevoVencimiento);
		p.sumarAlMontoActual(v.getPrecio(), cantidad);
	}

	public Pedido encontrarPedidoConId(Integer idPedido) {
		for (Pedido pe : pedidos) {
			if (pe.getId().equals(idPedido)) {
				return pe;
			}
		}
		return null;
	}

	public boolean contieneProductoEnPedido(Variante v, Integer idPedido) {
		Pedido p = encontrarPedidoConId(idPedido);
		ProductoPedido pp = p.encontrarProductoPedido(v.getId());
		if (pp != null && pp.getIdVariante().equals(v.getId())) {
			return true;
		}
		return false;
	}

	public boolean contieneCantidadDeProductoEnPedido(Variante v, Integer idPedido, Integer cantidad) {
		Pedido p = encontrarPedidoConId(idPedido);
		ProductoPedido pp = p.encontrarProductoPedido(v.getId());
		if (pp.getIdVariante().equals(v.getId()) && cantidad <= pp.getCantidad()) {
			return true;
		}
		return false;
	}

	public void eliminarProductoEnPedido(Integer idVariante, Double precio, Integer idPedido, Integer cantidad) {
		Pedido p = encontrarPedidoConId(idPedido);
		ProductoPedido pp = p.encontrarProductoPedido(idVariante);
		if (cantidad < pp.getCantidad()) {
			pp.restar(cantidad);
		} else {
			p.eliminar(pp);
		}
		p.restarAlMontoActual(precio, cantidad);
	}

	public Pedido vencerPedido(Integer idPedido) throws EstadoPedidoIncorrectoException {
		Pedido p = encontrarPedidoConId(idPedido);
		p.vencerte();
		//pedidos.remove(p); //TODO; ver cuando corresponde remover de la colección
		return p;
	}

	public void confirmarPedido(Integer idPedido, Integer idDireccion, Integer idPuntoDeRetiro) throws EstadoPedidoIncorrectoException {
		Pedido p = encontrarPedidoConId(idPedido);
		p.confirmarte();
		if(idDireccion ==null ^ idPuntoDeRetiro ==null){
			throw new EstadoPedidoIncorrectoException("El pedido no puede poseer un id de punto de retiro y id direccion, o faltante de los 2");
		}
		
		if (idDireccion != null) {
			p.setDireccionEntrega(this.obtenerDireccionConId(idDireccion));
		}
		if (idPuntoDeRetiro != null) {
			PuntoDeRetiroDAO PuntoDeRetiroDAO = (PuntoDeRetiroDAO) SpringUtil.getBean("puntoDeRetiroDAO");
			p.setPuntoDeRetiro(PuntoDeRetiroDAO.obtenerPuntoDeRetiro(idPuntoDeRetiro));
		}
		//TODO; ver cuando corresponde remover de la colección
//		pedidos.remove(p);
//		if (historialPedidos == null) {
//			historialPedidos = new Historial(this.getEmail());
//		}
//		historialPedidos.agregarAHistorial(p);
	}
	
	public void confirmarPedidoSinDireccion(Integer idPedido) throws EstadoPedidoIncorrectoException {
		Pedido p = encontrarPedidoConId(idPedido);
		p.confirmarte();
		//TODO; ver cuando corresponde remover de la colección
//		pedidos.remove(p);
//		if (historialPedidos == null) {
//			historialPedidos = new Historial(this.getEmail());
//		}
//		historialPedidos.agregarAHistorial(p);
	}

	public Direccion obtenerDireccionConId(Integer id) {
		for (Direccion d : direccionesAlternativas) {
			if (d.getId().equals(id)) {
				return d;
			}
		}
		return null;
	}

	public boolean contieneDireccion(Integer idDireccion) {
		for (Direccion d : direccionesAlternativas) {
			if (d.getId().equals(idDireccion)) {
				return true;
			}
		}
		return false;
	}

	public List<Pedido> obtenerPedidosVigentes() {
		List<Pedido> resultado = new ArrayList<Pedido>();
		for (Pedido p : pedidos) {
			if (p.estaVigente()) {
				resultado.add(p);
			}
		}
		return resultado;
	}

	public Pedido cancelarPedido(Integer idPedido) throws EstadoPedidoIncorrectoException {
			Pedido p = encontrarPedidoConId(idPedido);
			p.cancelar();
			return p;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	public void confirmarMail(){
		this.estado = Constantes.MAIL_CONFIRMADO;
	}

}
