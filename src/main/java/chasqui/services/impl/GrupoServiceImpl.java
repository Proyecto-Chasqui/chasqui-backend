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

import chasqui.aspect.Dateable;
import chasqui.dao.GrupoDAO;
import chasqui.dao.MiembroDeGCCDAO;
import chasqui.exceptions.ClienteNoPerteneceAGCCException;
import chasqui.exceptions.ConfiguracionDeVendedorException;
import chasqui.exceptions.DireccionesInexistentes;
import chasqui.exceptions.EncrypterException;
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
import chasqui.model.PedidoColectivo;
import chasqui.model.PuntoDeRetiro;
import chasqui.model.Usuario;
import chasqui.model.Vendedor;
import chasqui.model.Zona;
import chasqui.service.rest.impl.OpcionSeleccionadaRequest;
import chasqui.service.rest.request.ConfirmarPedidoSinDireccionRequest;
import chasqui.service.rest.request.DireccionRequest;
import chasqui.services.interfaces.GrupoService;
import chasqui.services.interfaces.InvitacionService;
import chasqui.services.interfaces.NotificacionService;
import chasqui.services.interfaces.PedidoService;
import chasqui.services.interfaces.PuntoDeRetiroService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.services.interfaces.ZonaService;
import chasqui.view.composer.Constantes;
import freemarker.template.TemplateException;

public class GrupoServiceImpl implements GrupoService {

	private static final String IDDISP = ""; //TODO Borrar

	@Autowired
	GrupoDAO grupoDao;
	
	@Autowired
	UsuarioService usuarioService;

	@Autowired
	PuntoDeRetiroService puntoDeRetiroService;
	
	@Autowired
	private InvitacionService invitacionService;

	@Autowired
	private PedidoService pedidoService;

	@Autowired
	private NotificacionService notificacionService;
	
	@Autowired
	private ZonaService zonaService;
	
	@Autowired
	private MiembroDeGCCDAO miembroDeGCCDao;
	
	@Autowired
	private MailService mailService;

	@Override
	public void altaGrupo(Integer idVendedor, String aliasGrupo, String descripcion, String emailClienteAdministrador)
			throws UsuarioInexistenteException, VendedorInexistenteException, RequestIncorrectoException {
		
		Cliente administrador = (Cliente) usuarioService.obtenerUsuarioPorEmail(emailClienteAdministrador);

		usuarioService.inicializarDirecciones(administrador);
		
		Vendedor vendedor = usuarioService.obtenerVendedorPorID(idVendedor);
		this.validarAliasGrupo(aliasGrupo);
		GrupoCC grupo = new GrupoCC(administrador, aliasGrupo, descripcion); 
		grupo.setVendedor(vendedor);

		grupoDao.altaGrupo(grupo);
	}

	/**
	 * Valida que no sea null ni espacios en blanco
	 * @param aliasGrupo nombre del grupo
	 * @throws RequestIncorrectoException 
	 */
	private void validarAliasGrupo(String aliasGrupo) throws RequestIncorrectoException {
		if(aliasGrupo == null || aliasGrupo.trim().length() == 0){
			throw new RequestIncorrectoException("El alias no puede estar vacio");
		}
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
		grupo.quitarMiembro(cliente);
		notificacionService.notificar(cliente.getEmail(), grupo.getAdministrador().getEmail(), "El usuario con el email " + cliente.getEmail() + " que invitaste, rechazo tu invitacion al grupo "+ grupo.getAlias() , null);
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
	 * @throws EncrypterException
	 * @throws GrupoCCInexistenteException 
	 * @throws TemplateException 
	 * @throws MessagingException 
	 * @throws IOException 
	 */
	@Override
	public void invitarAGrupo(Integer idGrupo, String emailInvitado, String emailAdministrador)
			throws GrupoCCInexistenteException, IOException, MessagingException, TemplateException, EncrypterException {
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
		
		MiembroDeGCC miembro = obtenerMiembroGCC(grupo, administradorAnterior.getEmail());
		if(!miembro.getEstadoInvitacion().equals(Constantes.ESTADO_NOTIFICACION_LEIDA_ACEPTADA)){
			throw new UsuarioNoPerteneceAlGrupoDeCompras(Constantes.ERROR_INVITACION_NO_ACEPTADA);
		}
		
		if(grupo.pertenece(nuevoAdministrador.getEmail())){
			grupo.cederAdministracion(nuevoAdministrador);
			notificacionService.notificarNuevoAdministrador(administradorAnterior, nuevoAdministrador, grupo);
			
			grupoDao.guardarGrupo(grupo);
		}else{
			throw new UsuarioNoPerteneceAlGrupoDeCompras(Constantes.ERROR_CREDENCIALES_INVALIDAS);
		}
		

	}

	/**
	 * Busca el MiembroDeGCC dentro del grupo con el email igual al pedido
	 * Precondicion: Asume que esta en el grupo.
	 * @param grupo
	 * @param email
	 * @return MiembroDeGCC. Si no existe null.
	 */
	private MiembroDeGCC obtenerMiembroGCC(GrupoCC grupo, String email) {
		for(MiembroDeGCC miembro : grupo.getCache()){
			if(miembro.getEmail().equals(email)){
				return miembro;
			}
		}
		return null;
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
	@Override
	@Dateable
	public void confirmarPedidoColectivo(Integer idGrupo, String emailSolicitante, Integer idDomicilio, Integer idPuntoDeRetiro, String comentario, List<OpcionSeleccionadaRequest>opcionesSeleccionadas, Integer idZona) throws EstadoPedidoIncorrectoException, NoAlcanzaMontoMinimoException, RequestIncorrectoException, DireccionesInexistentes, UsuarioInexistenteException {
		GrupoCC grupo = grupoDao.obtenerGrupoPorId(idGrupo);
		
		//Confirmar que el idDomicilio le pertenezca al solicitante
		Cliente solicitante = (Cliente) usuarioService.obtenerClientePorEmail(emailSolicitante);
		Direccion direccion = solicitante.obtenerDireccionConId(idDomicilio);
		PuntoDeRetiro puntoderetiro = buscarpuntoderetiro(grupo.getVendedor(), idPuntoDeRetiro);
		Zona zona=null;
		if(idZona != null){
			zona = buscarZona(grupo.getVendedor(),idZona);
		}
		if(!(direccion == null ^ puntoderetiro == null)){
			throw new DireccionesInexistentes("El punto de retiro o direccion seleccionada no existe"); 
		}
		
		
		if (grupo.getAdministrador().getEmail().equals(emailSolicitante)) {
			PedidoColectivo pc = grupo.getPedidoActual();
			grupo.confirmarPedidoColectivo(puntoderetiro, direccion, comentario,opcionesSeleccionadas,zona);
			List<MiembroDeGCC> miembros = grupo.getCache();
			for (MiembroDeGCC miembroDeGCC : miembros) {
				actualizarMiembroGCC(miembroDeGCC);
			}
			grupoDao.guardarGrupo(grupo);
			for (MiembroDeGCC miembroDeGCC : miembros) {
				if(miembroDeGCC.getEstadoInvitacion().equals(Constantes.ESTADO_NOTIFICACION_LEIDA_ACEPTADA)) {
					Pedido p = pc.buscarPedidoParaCliente(miembroDeGCC.getEmail());
					if(p != null) {
						if(p.getEstado().equals(Constantes.ESTADO_PEDIDO_CONFIRMADO) || p.getCliente().getEmail().equals(grupo.getAdministrador().getEmail())) {
							notificacionService.notificarConfirmacionPedidoColectivo(idGrupo, emailSolicitante,grupo.getAlias(),miembroDeGCC.getEmail(), miembroDeGCC.getNickname(), grupo.getVendedor().getNombre());
							mailService.enviarEmailCierreDePedidoColectivo(pc);
						}
					}
				}
			}
			
		}
		else{
			throw new RequestIncorrectoException("El usuario "+emailSolicitante + " no es el administrador del grupo:"+ grupo.getAlias());
		}
		
	}
	
	private void actualizarMiembroGCC(MiembroDeGCC miembroDeGCC) {
		try {
			Usuario usuario = null;
			if(miembroDeGCC.getIdCliente() != null) {
				usuario = usuarioService.obtenerUsuarioPorID(miembroDeGCC.getIdCliente());
			}else {
				usuario = usuarioService.obtenerUsuarioPorEmail(miembroDeGCC.getEmail());
			}
			miembroDeGCC.setNickname(usuario.getUsername());
			miembroDeGCC.setAvatar(usuario.getImagenPerfil());
			miembroDeGCC.setEmail(usuario.getEmail());
			miembroDeGCC.setIdCliente(usuario.getId());
		}catch(UsuarioInexistenteException e){
			
		}
		
	}
	
	private PuntoDeRetiro buscarpuntoderetiro(Vendedor vendedor, Integer idPr) {
		PuntoDeRetiro ret = null;
		for(PuntoDeRetiro pr: vendedor.getPuntosDeRetiro()) {
			if(pr.getId()==idPr) {
				ret = pr;
			}
		}
		return ret;
	}
	
	private Zona buscarZona(Vendedor vendedor, Integer idZona) {
		Zona ret = null;
		for(Zona pr: vendedor.getZonas()) {
			if(pr.getId()==idZona) {
				ret = pr;
			}
		}
		return ret;
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
		Pedido pedido = pedidoService.obtenerPedidosporId(request.getIdPedido());
		validarConfirmacionDePedidoSinDireccionPara(pedido, request); 
		
		validarQueContengaProductos(pedido);
		
		Vendedor vendedor = (Vendedor) usuarioService.obtenerVendedorPorID(pedido.getIdVendedor());
		usuarioService.inicializarListasDe(vendedor);
		if(pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_ABIERTO)) {
		pedido.confirmarte();
		}else {
			throw new EstadoPedidoIncorrectoException("El pedido no esta abierto");
		}
		vendedor.descontarStockYReserva(pedido);

		pedidoService.guardar(pedido);
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
	

	private void validarConfirmacionDePedidoSinDireccionPara(Pedido p, ConfirmarPedidoSinDireccionRequest request)
			throws PedidoInexistenteException {
		if (p == null) {
			throw new PedidoInexistenteException(
					"El usuario no posee un pedido vigente con el ID otorgado");
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
			PedidoColectivo p = grupo.getPedidoActual();
			if(!p.tienePedidoParaCliente(compa.getEmail())) {
				if(pedidoEnEstadoInactivo(p.getPedidosIndividuales().get(compa.getEmail()))) {
					if(compa.getEstadoInvitacion().equals(Constantes.ESTADO_NOTIFICACION_LEIDA_ACEPTADA)) {
						notificacionService.notificarNuevoPedidoEnGCC(grupo.getId(),grupo.getAlias(), emailCliente, compa.getEmail(), nombreCliente, nombreVendedor);
					}
				}
			}else {
				if(compa.getEstadoInvitacion().equals(Constantes.ESTADO_NOTIFICACION_LEIDA_ACEPTADA)) {
					notificacionService.notificarNuevoPedidoEnGCC(grupo.getId(),grupo.getAlias(), emailCliente, compa.getEmail(), nombreCliente, nombreVendedor);
				}
			}
		}
	}
	

	private boolean pedidoEnEstadoInactivo(Pedido pedido) {		
		return pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_CANCELADO) || pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_VENCIDO) || pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_INEXISTENTE);
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

	@Override
	public void vaciarGrupoCC(Integer idGrupo) throws EstadoPedidoIncorrectoException {
		GrupoCC grupo = grupoDao.obtenerGrupoPorId(idGrupo);
		if(grupo.sePuedeEliminar()){
			grupo.vaciarGrupo();
			grupoDao.guardarGrupo(grupo);
		}else {
			throw new EstadoPedidoIncorrectoException("El grupo no puede ser eliminado, por que hay pedidos abiertos o confirmados");
		}
		
	}
	
	



}
