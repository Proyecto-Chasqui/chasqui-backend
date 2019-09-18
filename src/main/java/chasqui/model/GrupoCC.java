package chasqui.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chasqui.exceptions.ClienteNoPerteneceAGCCException;
import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.exceptions.InvitacionExistenteException;
import chasqui.exceptions.InvitacionInexistenteException;
import chasqui.exceptions.NoAlcanzaMontoMinimoException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.service.rest.impl.OpcionSeleccionadaRequest;
import chasqui.view.composer.Constantes;

/*
 * TODO: implementar historico de pedidos grupales
 */
public class GrupoCC {

	private Integer id;
	private Cliente administrador;
	private String alias;
	private String descripcion;

	@Deprecated
	//private Direccion domicilioEntrega; //TODO la relación con Dirección se deja en Nodo 2017.09.21
	private Boolean pedidosHabilitados;

	private PedidoColectivo pedidoActual;
	private HistorialGCC historial;

	// private List<PedidoColectivo> pedidosDelGrupo; //TODO historico de
	// pedidos?
	private Vendedor vendedor;

	private List<MiembroDeGCC> cache;

	// Constructor
	public GrupoCC() {
	}

	public GrupoCC(Cliente administrador, String alias, String descripcion) {
		this.administrador = administrador;
		this.setAlias(alias);
		this.setDescripcion(descripcion);
		this.pedidosHabilitados = true;
		this.cache = new ArrayList<MiembroDeGCC>();
		this.invitarAlGrupo(administrador);
		this.registrarInvitacionAceptada(administrador);
		this.pedidoActual = new PedidoColectivo();
		this.pedidoActual.setColectivo(this);
		this.historial = new HistorialGCC(this.id);
	}

	// Gets & Sets
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public PedidoColectivo getPedidoActual() {
		return pedidoActual;
	}

	public void setPedidoActual(PedidoColectivo pedidoActual) {
		this.pedidoActual = pedidoActual;
	}

	public Cliente getAdministrador() {
		return administrador;
	}

	public void setAdministrador(Cliente administrador) {
		this.administrador = administrador;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	//TODO la relación con Dirección se deja en Nodo 2017.09.21
//	public Direccion getDomicilioEntrega() {
//		if (domicilioEntrega == null) {
//			Direccion dirAdministrador = this.getAdministrador().obtenerDireccionPredeterminada();
//			if (dirAdministrador != null) {
//				this.setDomicilioEntrega(new Direccion(dirAdministrador));
//			}
//		}
//		return domicilioEntrega;
//	}

//	public void setDomicilioEntrega(Direccion domicilioEntrega) {
//		this.domicilioEntrega = domicilioEntrega;
//	}

	public Boolean getPedidosHabilitados() {
		return pedidosHabilitados;
	}

	public void setPedidosHabilitados(Boolean pedidosHabilitados) {
		this.pedidosHabilitados = pedidosHabilitados;
	}

	public Vendedor getVendedor() {
		return vendedor;
	}

	public void setVendedor(Vendedor vendedor) {
		this.vendedor = vendedor;
	}

	// Methods
	public void quitarMiembro(Cliente c) {
		this.cache.remove(this.findMiembro(c.getEmail()));
	}

	public List<MiembroDeGCC> getCache() {
		return cache;
	}

	public void setCache(List<MiembroDeGCC> cache) {
		this.cache = cache;
	}

	// ----------------Pedidos individuales

	public HistorialGCC getHistorial() {
		return historial;
	}

	public void setHistorial(HistorialGCC historial) {
		this.historial = historial;
	}

	public Pedido nuevoPedidoIndividual(Cliente cte) {
		Pedido nuevoPedido = new Pedido(this.vendedor, cte, true, null);
		this.pedidoActual.agregarPedidoIndividual(nuevoPedido);
		return nuevoPedido;
	}

	public Pedido obtenerPedidoIndividual(String email) throws ClienteNoPerteneceAGCCException {
		Pedido pedidoDelCliente = this.pedidoActual.buscarPedidoParaCliente(email);
		return pedidoDelCliente;
	}

	// ----------------Administracion de invitaciones

	/*
	 * Este método debe usarse cuando el cliente NO está registrado en Chasqui
	 */
	public void invitarAlGrupo(String emailCliente) {
		MiembroDeGCC miembro = this.validarNuevoMiembro(emailCliente);
		this.cache.add(miembro);
	}

	/*
	 * Este método debe usarse cuando el cliente SI está registrado en Chasqui
	 */
	public void invitarAlGrupo(Cliente cliente) {
		MiembroDeGCC miembro = this.validarNuevoMiembro(cliente.getEmail());
		miembro.setAvatar(cliente.getImagenPerfil());
		miembro.setNickname(cliente.getUsername());
		miembro.setIdCliente(cliente.getId());
		this.cache.add(miembro);
	}

	private MiembroDeGCC validarNuevoMiembro(String emailCliente) {
		MiembroDeGCC miembro = this.findMiembro(emailCliente);

		if (miembro == null) {
			miembro = new MiembroDeGCC(emailCliente);
		} else {
			if (miembro.tieneInvitacionRechazada()) { // Esta validacion se
														// lleva a cabo por si
														// el cliente rechazo la
														// invitacion y se lo
														// volvió a invitar
				miembro.setEstadoInvitacion(Constantes.ESTADO_NOTIFICACION_NO_LEIDA);
			} else {
				throw new InvitacionExistenteException(
						"El email " + emailCliente + " ya se encuenta pendiente de aceptacion");
			}
		}
		return miembro;

	}

	public void registrarInvitacionAceptada(Cliente c) {
		MiembroDeGCC miembro = this.findMiembro(c.getEmail());
		if (miembro == null || !miembro.tieneInvitacionPendiente()) {
			// se chequea si el miembro ya ha sido invitado
			throw new InvitacionInexistenteException("El cliente ( " + c.getEmail()
					+ ") no había sido invitado o la invitacion ya había sido confirmada");
		}
		miembro.aceptarInvitacion(c.getId());
	}

	public void registrarInvitacionRechazada(Cliente cliente) {
		MiembroDeGCC miembro = this.findMiembro(cliente.getEmail());
		if (miembro == null || !miembro.tieneInvitacionPendiente()) {
			throw new InvitacionInexistenteException(
					"El email " + cliente.getEmail() + " no tiene invitación pendiente de aprobación");
		}
		miembro.rechazarInvitacion();
	}

	private MiembroDeGCC findMiembro(String emailCliente) {
		for (MiembroDeGCC miembro : cache) {
			if (miembro.getEmail().equals(emailCliente)) {
				return miembro;
			}
		}

		return null;
	}

	/*
	 * Workflow de Pedidos
	 */
	public void nuevoPedidoIndividualPara(String email, Pedido nuevoPedido) throws ClienteNoPerteneceAGCCException {

		MiembroDeGCC miembro = this.findMiembro(email);

		if (miembro == null) {
			throw new ClienteNoPerteneceAGCCException(email);
		}
		miembro.abrirPedido();
		this.pedidoActual.agregarPedidoIndividual(nuevoPedido);
	}

	public void confirmarPedidoColectivo(PuntoDeRetiro puntoDeRetiro, Direccion direccion, String comentario,List<OpcionSeleccionadaRequest> opcionesSeleccionadas, Zona zona) throws EstadoPedidoIncorrectoException, NoAlcanzaMontoMinimoException {

		if (!pedidoActual.tienePedidos()) {
			throw new EstadoPedidoIncorrectoException(
					"El grupo no posee pedidos, para confirmar el pedido colectivo se debe almenos tener un pedido y estar confirmado.");
		}

		if (pedidoActual.todosLosPedidosCancelados()) {
			throw new EstadoPedidoIncorrectoException(
					"No puede confirmar un pedido colectivo que posea todos sus pedidos cancelados");
		}

		if (pedidoActual.tienePedidosAbiertos()) {
			throw new EstadoPedidoIncorrectoException(
					"Alguno de los pedidos del grupo " + alias + " no está confirmado");
		}
		
		if(direccion != null) {
			if (this.pedidoActual.getMontoTotal() >= this.vendedor.getMontoMinimoPedido()) {
				cerrarPedidoColectivo(puntoDeRetiro,direccion,comentario,opcionesSeleccionadas,zona);
			} else {
				throw new NoAlcanzaMontoMinimoException();
			}
		}else {
			cerrarPedidoColectivo(puntoDeRetiro,direccion,comentario,opcionesSeleccionadas,zona);
		}


	}
	
	private void cerrarPedidoColectivo(PuntoDeRetiro puntoDeRetiro, Direccion direccion, String comentario,List<OpcionSeleccionadaRequest> opcionesSeleccionadas, Zona zona) throws EstadoPedidoIncorrectoException {
		this.setearDireccionEnPedido(puntoDeRetiro,direccion);
		this.setearOpcionesSeleccionadas(opcionesSeleccionadas);
		this.pedidoActual.setZona(zona);
		this.pedidoActual.setComentario(comentario);
		this.pedidoActual.confirmarte();
		this.historial.agregarAHistorial(this.pedidoActual);
		this.historial.setId(this.id);
		this.pedidoActual = new PedidoColectivo();
		this.pedidoActual.setColectivo(this);
	}
	
	private void setearOpcionesSeleccionadas(List<OpcionSeleccionadaRequest> opcionesSeleccionadas) {
		Map<String,String> opciones = new HashMap<String,String>();
		if(opcionesSeleccionadas!=null){
			for(OpcionSeleccionadaRequest o : opcionesSeleccionadas){
				opciones.put(o.getNombre(), o.getOpcionSeleccionada());
			}
		}
		this.pedidoActual.setRespuestasAPreguntas(opciones);
	}

	private void setearDireccionEnPedido(PuntoDeRetiro puntoDeRetiro, Direccion direccion){
		if(direccion != null){
			this.pedidoActual.setDireccionEntrega(new Direccion(direccion));
		}else{
			this.pedidoActual.setPuntoDeRetiro(puntoDeRetiro);
		}
	}

	/*
	 * Debe recuperar el último pedido que entró al historial (que debería ser
	 * el único en estado confirmado, pero esto depende de que el usuario
	 * vendedor no deje varios pedidos confirmados sin entregar), y cambiar su
	 * estado a ENTREGADO
	 */
	public void entregarPedidoColectivo() throws EstadoPedidoIncorrectoException {
		this.pedidoActual.entregarte();
	}
	//TODO: No se aplica hasta que se releve la idea.
	public void cancelarPedidoColectivo() throws EstadoPedidoIncorrectoException {
//		this.pedidoActual.cancelar();
//		this.historial.agregarAHistorial(this.pedidoActual);
//		this.pedidoActual = new PedidoColectivo();
	}

	public void eliminarInvitacion(String emailCliente) throws UsuarioInexistenteException {
		MiembroDeGCC miembro = this.findMiembro(emailCliente);
		if (miembro != null) {
			this.cache.remove(miembro);
		} else
			throw new UsuarioInexistenteException(
					"El usuario con mail: " + emailCliente + " no había sido invitado al grupo de compras colectivas");
	}

	public boolean fueInvitado(String emailInvitado) {		
		return this.findMiembro(emailInvitado)!=null;
	}

	public void cederAdministracion(Cliente cliente) {
		this.setAdministrador(cliente);
	}

	public boolean pertenece(String emailCliente) {
		return (null != this.findMiembro(emailCliente));
	}

	public boolean sePuedeEliminar() {
		boolean sePuedeEliminar = true;
		for(Pedido p : pedidoActual.getPedidosIndividuales().values()) {
			if(sePuedeEliminar) {
				sePuedeEliminar = !(p.getEstado().equals(Constantes.ESTADO_PEDIDO_ABIERTO) || p.getEstado().equals(Constantes.ESTADO_PEDIDO_CONFIRMADO));
			}
		}
		return sePuedeEliminar;
	}

	public void vaciarGrupo() throws EstadoPedidoIncorrectoException {
		this.cache.clear();
		this.pedidoActual.cancelar();
	}

}