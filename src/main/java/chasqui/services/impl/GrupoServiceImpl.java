package chasqui.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import chasqui.dao.GrupoDAO;
import chasqui.exceptions.ClienteNoPerteneceAGCCException;
import chasqui.exceptions.ConfiguracionDeVendedorException;
import chasqui.exceptions.DireccionesInexistentes;
import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.exceptions.GrupoCCInexistenteException;
import chasqui.exceptions.NoAlcanzaMontoMinimoException;
import chasqui.exceptions.PedidoInexistenteException;
import chasqui.exceptions.PedidoSinProductosException;
import chasqui.exceptions.PedidoVigenteException;
import chasqui.exceptions.RequestIncorrectoException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.UsuarioNoPerteneceAlGrupoDeCompras;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.GrupoCC;
import chasqui.model.InvitacionAGCC;
import chasqui.model.MiembroDeGCC;
import chasqui.model.Pedido;
import chasqui.model.Usuario;
import chasqui.model.Vendedor;
import chasqui.service.rest.request.ConfirmarPedidoSinDireccionRequest;
import chasqui.service.rest.request.DireccionRequest;
import chasqui.services.interfaces.GrupoService;
import chasqui.services.interfaces.InvitacionService;
import chasqui.services.interfaces.NotificacionService;
import chasqui.services.interfaces.PedidoService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.view.composer.Constantes;
import freemarker.template.TemplateException;

public class GrupoServiceImpl implements GrupoService {

	private static final String IDDISP = ""; //TODO Borrar

	@Autowired
	GrupoDAO grupoDao;
	
	@Autowired
	UsuarioService usuarioService;

	
	@Autowired
	private InvitacionService invitacionService;

	@Autowired
	private PedidoService pedidoService;

	@Autowired
	private NotificacionService notificacionService;

	@Override
	public void altaGrupo(Integer idVendedor, String aliasGrupo, String descripcion, String emailClienteAdministrador)
			throws UsuarioInexistenteException, VendedorInexistenteException {
		
		Cliente administrador = (Cliente) usuarioService.obtenerUsuarioPorEmail(emailClienteAdministrador);

		usuarioService.inicializarDirecciones(administrador);
		
		Vendedor vendedor = usuarioService.obtenerVendedorPorID(idVendedor);

		GrupoCC grupo = new GrupoCC(administrador, aliasGrupo, descripcion); 
		grupo.setVendedor(vendedor);

		grupoDao.altaGrupo(grupo);
	}

	/*
	 * (non-Javadoc)
	 * @see chasqui.services.interfaces.GrupoService#obtenerGrupoDe(int)
	 */
	@Override
	public List<GrupoCC> obtenerGruposDe(int idVendedor) throws VendedorInexistenteException {

		usuarioService.obtenerVendedorPorID(idVendedor);
		return grupoDao.obtenerGruposDeVendedor(idVendedor);
	}
	
	@Override
	public Collection<? extends GrupoCC> obtenerGruposDe(Integer idVendedor, Date d, Date h, String estadoSeleccionado) throws VendedorInexistenteException {
		usuarioService.obtenerVendedorPorID(idVendedor);
		return grupoDao.obtenerGruposDeVendedorCon(idVendedor,d,h, estadoSeleccionado);
	}
	
	@Override
	public void eliminarGrupoCC(Integer idGrupoCC) throws GrupoCCInexistenteException {
		GrupoCC grupo = grupoDao.obtenerGrupoPorId(idGrupoCC);
		if (grupo == null) {
			throw new GrupoCCInexistenteException(idGrupoCC);
		}
		grupoDao.eliminarGrupoCC(grupo);
	}


	/**
	 * 1. Cambiar estado del objeto InvitacionAGG
	 * 2. Envia un mail para notificar al administrador
	 * 3. registrar en el grupo que el miembro acepto la invitacion
	 */
	@Override
	public void confirmarInvitacionGCC(Integer idInvitacion, String emailCliente) throws UsuarioInexistenteException {
		Cliente cliente = (Cliente) usuarioService.obtenerUsuarioPorEmail(emailCliente);		
		InvitacionAGCC invitacion = invitacionService.obtenerInvitacionAGCCporID(idInvitacion);		
		GrupoCC grupo = grupoDao.obtenerGrupoPorId(invitacion.getIdGrupo());
		
		invitacionService.aceptarInvitacionAGCC(invitacion);
		
		grupo.registrarInvitacionAceptada(cliente);
		grupoDao.guardarGrupo(grupo);
		notificacionService.notificarInvitacionAGCCAceptada(grupo, cliente);
	}
	
	/**
	 * 1. Cambiar estado del objeto InvitacionAGG
	 * 2. Envia un mail para notificar al administrador
	 * 3. registrar en el grupo que el miembro acepto la invitacion
	 * @throws UsuarioInexistenteException 
	 */
	@Override
	public void rechazarInvitacionGCC(Integer idInvitacion, String emailCliente) throws UsuarioInexistenteException {
		Cliente cliente = (Cliente) usuarioService.obtenerUsuarioPorEmail(emailCliente);		
		InvitacionAGCC invitacion = invitacionService.obtenerInvitacionAGCCporID(idInvitacion);		
		GrupoCC grupo = grupoDao.obtenerGrupoPorId(invitacion.getIdGrupo());
		invitacionService.rechazarInvitacionAGCC(invitacion);
		
		grupo.registrarInvitacionRechazada(cliente);
		grupoDao.guardarGrupo(grupo);
	}

	public List<GrupoCC> obtenerGruposDeCliente(String email, Integer idVendedor) throws UsuarioInexistenteException {
		usuarioService.obtenerUsuarioPorEmail(email);// Valida que el cliente exista
		List<GrupoCC> grupos = grupoDao.obtenerGruposDelClienteParaVendedor(email, idVendedor);
		return grupos;
	}

	
	/**
	 * 1. Genera un objeto InvitacionAGCC
	 * 2. Envia un mail para notificar (e invitar a Chasqui si corresponde)
	 * 3. Agrega un miembro al grupo
	 * @throws Exception 
	 */
	@Override
	public void invitarAGrupo(Integer idGrupo, String emailInvitado, String emailAdministrador)
			throws Exception {

		GrupoCC grupo = grupoDao.obtenerGrupoPorId(idGrupo);
		if (grupo == null) {
			throw new GrupoCCInexistenteException(idGrupo);
		}
		Cliente administrador = grupo.getAdministrador();
		//comprobar que el solicitante sea el administrador del grupo
		if (!administrador.getEmail().equals(emailAdministrador)) {
			throw new GrupoCCInexistenteException("El solicitante no es administrador del grupo: "+emailAdministrador);
		}
		
		//comprobar que el administrador no se invite a si mismo
		
		if (administrador.getEmail().equals(emailInvitado)) {
			throw new GrupoCCInexistenteException("El administrador está intentando invitarse a si mismo");
		}
		
		if (grupo.fueInvitado(emailInvitado)) {
			throw new GrupoCCInexistenteException("El usuario que intenta invitar ya fue invitado al grupo o ya pertence al grupo");
		}
		
		try {
			Cliente cliente = (Cliente) usuarioService.obtenerUsuarioPorEmail(emailInvitado);	
			
			notificacionService.notificarInvitacionAGCCClienteRegistrado(administrador, emailInvitado, grupo, IDDISP);
			grupo.invitarAlGrupo(cliente);			
			
		} catch (UsuarioInexistenteException e) {
			notificacionService.notificarInvitacionAGCCClienteNoRegistrado(administrador, emailInvitado, grupo, IDDISP);
			grupo.invitarAlGrupo(emailInvitado);
		}
		
		grupoDao.guardarGrupo(grupo);
	}

	/*
	 * 1. Se elimina el miembro de la caché (esté o no registrado en chasqui)
	 * 2. Se elimina la notificación correspondiente 
	 */
	@Override
	public void quitarMiembroDelGrupo(Integer idGrupo, String emailCliente) throws UsuarioInexistenteException  {

		GrupoCC grupo = grupoDao.obtenerGrupoPorId(idGrupo);
		
		Cliente cliente;
		try {
			cliente = (Cliente) usuarioService.obtenerUsuarioPorEmail(emailCliente);
			grupo.quitarMiembro(cliente);
			
		} catch (UsuarioInexistenteException e) {
			// Si el usuario no existe es porque fue invitado pero no está registrado en chasqui
			grupo.eliminarInvitacion(emailCliente);
		}
		grupoDao.guardarGrupo(grupo);
		invitacionService.eliminarInvitacion(idGrupo,emailCliente); //TODO 2017.07.14 ver porqué falla este paso
		
	}

	@Override
	public void cederAdministracion(Integer idGrupo, String emailCliente) throws UsuarioInexistenteException, UsuarioNoPerteneceAlGrupoDeCompras {
		GrupoCC grupo = grupoDao.obtenerGrupoPorId(idGrupo);
		Cliente administradorAnterior = grupo.getAdministrador(); //Es necesario guardar la referencia para notificarlo luego que cedio la administracion.
		Cliente nuevoAdministrador = (Cliente) usuarioService.obtenerUsuarioPorEmail(emailCliente);
		
		if(grupo.pertenece(nuevoAdministrador.getEmail()))
		{
			grupo.cederAdministracion(nuevoAdministrador);
			//Notificarlos
			// administradorAnterior
			// nuevoAdministrador
			
			grupoDao.guardarGrupo(grupo);
		}else{
			throw new UsuarioNoPerteneceAlGrupoDeCompras(Constantes.ERROR_CREDENCIALES_INVALIDAS);
		}
		

	}

	@Override
	public Map<Integer,Pedido> obtenerPedidosEnGruposCC(List<GrupoCC> grupos, String email)
			throws ClienteNoPerteneceAGCCException {

		Map<Integer,Pedido> pedidos = new HashMap<Integer,Pedido>();

		for (GrupoCC grupo : grupos) {

			Pedido pedido = grupo.obtenerPedidoIndividual(email);

			if (pedido != null) {
				pedidos.put(grupo.getId(),grupo.obtenerPedidoIndividual(email));
			}

		}

		return pedidos;

	}

	@Override
	public GrupoCC obtenerGrupo(int idGrupo) throws GrupoCCInexistenteException {
		return grupoDao.obtenerGrupoPorId(idGrupo);
	}

	@Override
	public void nuevoPedidoIndividualPara(Integer idGrupo, String email, Integer idVendedor) throws UsuarioInexistenteException, ClienteNoPerteneceAGCCException, ConfiguracionDeVendedorException, PedidoVigenteException, PedidoInexistenteException, VendedorInexistenteException, GrupoCCInexistenteException {
		GrupoCC grupo = grupoDao.obtenerGrupoPorId(idGrupo);
		Pedido pedidoVigente = grupo.obtenerPedidoIndividual(email);
		if (pedidoVigente == null ) {
			Pedido pedidoNuevo = pedidoService.crearPedidoIndividualEnGrupo(grupo, email, idVendedor);
			grupo.nuevoPedidoIndividualPara(email, pedidoNuevo);
			grupoDao.guardarGrupo(grupo);

			Vendedor vendedor = usuarioService.obtenerVendedorPorID(idVendedor);
			String nombreVendedor =vendedor.getNombre(); 
			
			Cliente cliente = (Cliente) usuarioService.obtenerUsuarioPorEmail(email);
			String nombreCliente= cliente.getUsername(); 
			this.notificarNuevoPedidoIndividualAOtrosMiembros(grupo,email, nombreCliente, nombreVendedor);
			
		}
		else{
			throw new PedidoVigenteException(email);
		}
	}
	
	
	/*
	 * Chequear que el cliente sea el administrador, que el pedido esté abierto
	 * (non-Javadoc)
	@Override
	 * @see chasqui.services.interfaces.GrupoService#confirmarPedidoColectivo(java.lang.Integer)
	 */
	public void confirmarPedidoColectivo(Integer idGrupo, String emailSolicitante, Integer idDomicilio, String comentario) throws EstadoPedidoIncorrectoException, NoAlcanzaMontoMinimoException, RequestIncorrectoException, DireccionesInexistentes, UsuarioInexistenteException {
		GrupoCC grupo = grupoDao.obtenerGrupoPorId(idGrupo);
		
		//Confirmar que el idDomicilio le pertenezca al solicitante
		Cliente solicitante = (Cliente) usuarioService.obtenerClientePorEmail(emailSolicitante);
		Direccion direccion = solicitante.obtenerDireccionConId(idDomicilio);
		if(direccion== null)
			throw new DireccionesInexistentes("El cliente con email:" + emailSolicitante + "no tiene un domicilio con id:"+ idDomicilio); 
		
		
		if (grupo.getAdministrador().getEmail().equals(emailSolicitante)) {
			grupo.confirmarPedidoColectivo(direccion, comentario);
			List<MiembroDeGCC> miembros = grupo.getCache();
			for (MiembroDeGCC miembroDeGCC : miembros) {
				notificacionService.notificarConfirmacionPedidoColectivo(idGrupo, emailSolicitante,grupo.getAlias(),miembroDeGCC.getEmail(), miembroDeGCC.getNickname(), grupo.getVendedor().getNombre());
			}
			grupoDao.guardarGrupo(grupo);
			
		}
		else{
			throw new RequestIncorrectoException("El usuario "+emailSolicitante + " no es el administrador del grupo:"+ grupo.getAlias());
		}
		
	}
	
	@Override
	public void editarGrupo(Integer idGrupo, String emailAdministrador, String alias, String descripcion) throws RequestIncorrectoException {
		GrupoCC grupo = grupoDao.obtenerGrupoPorId(idGrupo);
		if (grupo.getAdministrador().getEmail().equals(emailAdministrador)) {
			grupo.setAlias(alias);
			grupo.setDescripcion(descripcion);
			grupoDao.guardarGrupo(grupo);
		}
		else{
			throw new RequestIncorrectoException("El usuario "+emailAdministrador + " no es el administrador del grupo:"+ grupo.getAlias());
		}
		
	}

	@Override
	public void confirmarPedidoIndividualEnGCC(String email,
			ConfirmarPedidoSinDireccionRequest request) throws RequestIncorrectoException, EstadoPedidoIncorrectoException, UsuarioInexistenteException, VendedorInexistenteException, PedidoInexistenteException, ClienteNoPerteneceAGCCException, GrupoCCInexistenteException, PedidoSinProductosException {
		
		validarRequest(request.getIdPedido());
		
		Cliente cliente = (Cliente) usuarioService.obtenerUsuarioPorEmail(email);
		usuarioService.inicializarPedidos(cliente);
		//usuarioService.inicializarHistorial(cliente);
		//suarioService.inicializarColecciones(cliente);

		validarConfirmacionDePedidoSinDireccionPara(cliente, request); //TODO fusionar este metodo con cliente.encontrarPedidoConId
		
		Pedido pedido = cliente.encontrarPedidoConId(request.getIdPedido());
		
		validarQueContengaProductos(pedido);
		
		Vendedor vendedor = (Vendedor) usuarioService.obtenerVendedorPorID(pedido.getIdVendedor());
		usuarioService.inicializarListasDe(vendedor);
		
		vendedor.descontarStockYReserva(pedido);
		cliente.confirmarPedidoSinDireccion(pedido.getId());
		
		usuarioService.guardarUsuario(cliente);
		usuarioService.guardarUsuario(vendedor);
		
		//Notificar al cliente y a sus compañeros
		notificacionService.enviarAClienteSuPedidoConfirmado(vendedor.getEmail(), email, pedido);		
		this.notificarConfirmacionAOtrosMiembros(email, cliente.getUsername(), pedido, vendedor.getId(), vendedor.getEmail());
		
	}


	private void validarQueContengaProductos(Pedido pedido) throws PedidoSinProductosException {
		if(pedido.getProductosEnPedido().isEmpty()){
			throw new PedidoSinProductosException("El usuario: " + pedido.getCliente().getUsername() + " no posee productos en su pedido" );
		}
	}

	@Override
	@Deprecated
	public void actualizarDomicilio(Integer idGrupo, DireccionRequest request) throws RequestIncorrectoException {
		GrupoCC grupo = grupoDao.obtenerGrupoPorId(idGrupo);
		
		this.validarDireccionRequest(request);
		
		//Direccion direccionGCC= grupo.getDomicilioEntrega();
//		if (direccionGCC!=null) {
//			direccionGCC.modificarCon(request);
//		}
//		else{
//			direccionGCC = new Direccion(request);
//		}
//			
//		grupo.setDomicilioEntrega(direccionGCC);
		grupoDao.guardarGrupo(grupo);
	}
	

	private void validarConfirmacionDePedidoSinDireccionPara(Cliente c, ConfirmarPedidoSinDireccionRequest request)
			throws PedidoInexistenteException {
		if (!c.contienePedido(request.getIdPedido())) {
			throw new PedidoInexistenteException(
					"El usuario: " + c.getUsername() + " no posee un pedido vigente con el ID otorgado");
		}
	}

	
	private void validarDireccionRequest(DireccionRequest request) throws RequestIncorrectoException {
		if (StringUtils.isEmpty(request.getAlias())) {
			throw new RequestIncorrectoException();
		}
		if (StringUtils.isEmpty(request.getCalle())) {
			throw new RequestIncorrectoException();
		}
		if (StringUtils.isEmpty(request.getCodigoPostal())) {
			throw new RequestIncorrectoException();
		}
		if (request.getPredeterminada() == null) {
			throw new RequestIncorrectoException();
		}
		if (StringUtils.isEmpty(request.getLocalidad())) {
			throw new RequestIncorrectoException();
		}
		if (request.getAltura() == null || request.getAltura() < 0) {
			throw new RequestIncorrectoException();
		}
	}

	public Pedido obtenerPedidoIndividualEnGrupo(Integer idGrupo, String emailCliente) {
		GrupoCC grupo = grupoDao.obtenerGrupoPorId(idGrupo);
		return grupo.getPedidoActual().buscarPedidoParaCliente(emailCliente);
	}


	@Override
	public List<MiembroDeGCC> obtenerOtrosMiembrosDelGCC(String mailCliente, Integer idGrupo) throws GrupoCCInexistenteException {		
		GrupoCC grupo = this.obtenerGrupo(idGrupo);
		List<MiembroDeGCC> nuevaLista = new ArrayList<MiembroDeGCC>();
		for (MiembroDeGCC miembroDeGCC : grupo.getCache()) {
			if (!miembroDeGCC.getEmail().equals(mailCliente)) {
				nuevaLista.add(miembroDeGCC);
			}
		}
		return nuevaLista;
				
	}

	@Override
	public GrupoCC obtenerGrupoPorIdPedidoColectivo(Integer idPedidoColectivo, int idVendedor) throws VendedorInexistenteException, PedidoInexistenteException {
		List<GrupoCC> grupos = this.obtenerGruposDe(idVendedor);
		for (GrupoCC grupoCC : grupos) {
			if (grupoCC.getPedidoActual().getId().equals(idPedidoColectivo)) {
				return grupoCC;
			}
		}
		throw new PedidoInexistenteException("El pedido colectivo con id: "+idPedidoColectivo+" no existe");
	}

	

	/*
	 * Este método notifica a los miembros del grupo que un cliente ha iniciado su pedido individual
	 * 
	 */
	private void notificarNuevoPedidoIndividualAOtrosMiembros(GrupoCC grupo, String emailCliente, String nombreCliente, String nombreVendedor) throws UsuarioInexistenteException, ClienteNoPerteneceAGCCException, GrupoCCInexistenteException, VendedorInexistenteException, PedidoInexistenteException {
		
		List<MiembroDeGCC> compas = this.obtenerOtrosMiembrosDelGCC(emailCliente,grupo.getId());
		for (MiembroDeGCC compa : compas) {
		     notificacionService.notificarNuevoPedidoEnGCC(grupo.getId(),grupo.getAlias(), emailCliente, compa.getEmail(), nombreCliente, nombreVendedor);
			
		}
	}
	

	/*
	 * Este método notifica a los miembros del grupo que un cliente ha confirmado el pedido individual
	 * 
	 */
	private void notificarConfirmacionAOtrosMiembros(String emailCliente, String nombreCliente, Pedido pedido, Integer idVendedor, String emailVendedor) throws UsuarioInexistenteException, ClienteNoPerteneceAGCCException, GrupoCCInexistenteException, VendedorInexistenteException, PedidoInexistenteException {
		
		GrupoCC grupo = this.obtenerGrupoPorIdPedidoColectivo(pedido.getPedidoColectivo().getId(),idVendedor);
		
		List<MiembroDeGCC> compas = this.obtenerOtrosMiembrosDelGCC(emailCliente,grupo.getId());
		for (MiembroDeGCC compa : compas) {
		     notificacionService.notificarConfirmacionCompraOtroMiembro(emailVendedor, compa.getEmail(),nombreCliente , grupo.getAlias());
			
		}
	}

	private void validarRequest(Integer idPedido) throws RequestIncorrectoException {
		if (idPedido == null || idPedido < 0) {
			throw new RequestIncorrectoException("el id del pedido debe ser mayor a 0");
		}
	}

	@Override
	public void guardarGrupo(GrupoCC grupo) {
		grupoDao.guardarGrupo(grupo);
		
	}


}
