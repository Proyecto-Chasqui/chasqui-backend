package chasqui.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;

import chasqui.dao.SolicitudCreacionNodoDAO;
import chasqui.dao.SolicitudPertenenciaNodoDAO;
import chasqui.dao.impl.NodoDAOHbm;
import chasqui.dao.impl.SolicitudCreacionNodoDAOHbm;
import chasqui.dao.impl.SolicitudPertenenciaNodoDAOHbm;
import chasqui.exceptions.ClienteNoPerteneceAGCCException;
import chasqui.exceptions.ConfiguracionDeVendedorException;
import chasqui.exceptions.DireccionesInexistentes;
import chasqui.exceptions.EncrypterException;
import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.exceptions.GrupoCCInexistenteException;
import chasqui.exceptions.InvitacionExistenteException;
import chasqui.exceptions.NodoCerradoException;
import chasqui.exceptions.NodoInexistenteException;
import chasqui.exceptions.NodoYaExistenteException;
import chasqui.exceptions.PedidoInexistenteException;
import chasqui.exceptions.PedidoSinProductosException;
import chasqui.exceptions.PedidoVigenteException;
import chasqui.exceptions.RequestIncorrectoException;
import chasqui.exceptions.SolicitudCreacionNodoException;
import chasqui.exceptions.SolicitudCreacionNodoEnGestionExistenteException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.UsuarioNoPerteneceAlGrupoDeCompras;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.GrupoCC;
import chasqui.model.MiembroDeGCC;
import chasqui.model.Nodo;
import chasqui.model.Pedido;
import chasqui.model.PedidoColectivo;
import chasqui.model.PuntoDeRetiro;
import chasqui.model.SolicitudCreacionNodo;
import chasqui.model.SolicitudPertenenciaNodo;
import chasqui.model.Usuario;
import chasqui.model.Vendedor;
import chasqui.model.Zona;
import chasqui.service.rest.impl.OpcionSeleccionadaRequest;
import chasqui.service.rest.request.ConfirmarPedidoSinDireccionRequest;
import chasqui.services.interfaces.InvitacionService;
import chasqui.services.interfaces.NodoService;
import chasqui.services.interfaces.NotificacionService;
import chasqui.services.interfaces.PedidoService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.services.interfaces.VendedorService;
import chasqui.view.composer.Constantes;
import freemarker.template.TemplateException;

public class NodoServiceImpl implements NodoService {
	private static final String IDDISP = "";
	@Autowired
	NodoDAOHbm nodoDAO;
	@Autowired
	UsuarioService usuarioService;
	@Autowired
	VendedorService vendedorService;
	@Autowired
	SolicitudCreacionNodoDAO solicitudCreacionNodoDAO;
	@Autowired
	SolicitudPertenenciaNodoDAO solicitudPertenenciaNodoDAO;
	@Autowired
	private NotificacionService notificacionService;
	@Autowired
	private PedidoService pedidoService;
	@Autowired
	private InvitacionService invitacionService;

	@Override
	public void crearSolicitudDeCreacionNodo(Integer idVendedor, Cliente usuario, String nombre, Direccion direccion, String tipo, String barrio, String descripcion) throws DireccionesInexistentes, VendedorInexistenteException, ConfiguracionDeVendedorException, SolicitudCreacionNodoEnGestionExistenteException, NodoYaExistenteException{
		validar(usuario,direccion,idVendedor);
		validarNombreNodo(nombre,idVendedor);
		solicitudCreacionNodoDAO.guardar(new SolicitudCreacionNodo(idVendedor,usuario, nombre, direccion, tipo, barrio, descripcion));
	}

	@Override
	public void crearSolicitudDePertenenciaANodo(Nodo nodo, Cliente usuario) throws NodoCerradoException{
		validarNodo(nodo);
		solicitudPertenenciaNodoDAO.guardar(new SolicitudPertenenciaNodo(nodo, usuario));
	}


	@Override
	public List<Nodo> obtenerNodosDelVendedor(Integer idVendedor) throws VendedorInexistenteException {
		usuarioService.obtenerVendedorPorID(idVendedor); // Para verificar si el vendedor con el id pasado como parametro es válido

		return nodoDAO.obtenerNodosDelVendedor(idVendedor);
		
	}
	
	@Override
	public List<Nodo> obtenerNodosDelCliente(Integer idVendedor, String email) throws VendedorInexistenteException {
		usuarioService.obtenerVendedorPorID(idVendedor); // Para verificar si el vendedor con el id pasado como parametro es válido

		return nodoDAO.obtenerNodosDelCliente(idVendedor,email);
		
	}
	
	@Override
	public Nodo obtenerNodoPorId(Integer idNodo) {
		return nodoDAO.obtenerNodoPorId(idNodo);
	}
	
	
	@Override
	public void guardarNodo(Nodo nodo) {
		nodoDAO.guardarNodo(nodo);
	}
	@Deprecated
	@Override
	public void altaNodoSinUsuario(String alias, String emailClienteAdministrador, String localidad, String calle, int altura, String telefono, int idVendedor, String descripcion) throws NodoYaExistenteException, VendedorInexistenteException{
		Nodo nodo;
			try {
				nodo = obtenerNodoPorAlias(alias);
				throw new NodoYaExistenteException(alias);
			} catch (NodoInexistenteException e) {

				nodo = new Nodo();
				nodo.setAlias(alias);
				nodo.setEmailAdministradorNodo(emailClienteAdministrador);

				Direccion direccionDelNodo = new Direccion();
				direccionDelNodo.setAltura(altura);
				direccionDelNodo.setCalle(calle);
				direccionDelNodo.setLocalidad(localidad);

				nodo.setDireccionDelNodo(direccionDelNodo);

				Vendedor vendedor = (Vendedor) usuarioService.obtenerVendedorPorID(idVendedor);
				nodo.setVendedor(vendedor);
				nodo.setTipo(Constantes.NODO_ABIERTO);
				nodoDAO.guardarNodo(nodo);
			}
	}
	@Deprecated
	@Override
	public void altaNodo(String alias, String emailClienteAdministrador, String localidad, String calle, int altura,
			String telefono, int idVendedor, String descripcion) throws UsuarioInexistenteException, NodoYaExistenteException, VendedorInexistenteException {

		Cliente administrador = (Cliente) usuarioService.obtenerUsuarioPorEmail(emailClienteAdministrador);
		
		// TODO mandar mail notificando la creacion del nodo.
		Nodo nodo;
		try {
			nodo = obtenerNodoPorAlias(alias);
			throw new NodoYaExistenteException(alias);
		} catch (NodoInexistenteException e) {

			nodo = new Nodo(administrador, alias, descripcion);

			Direccion direccionDelNodo = new Direccion();
			direccionDelNodo.setAltura(altura);
			direccionDelNodo.setCalle(calle);
			direccionDelNodo.setLocalidad(localidad);
			
			nodo.setDireccionDelNodo(direccionDelNodo);
			nodo.setEmailAdministradorNodo(emailClienteAdministrador);
			Vendedor vendedor = (Vendedor) usuarioService.obtenerVendedorPorID(idVendedor);
			nodo.setVendedor(vendedor);

			nodoDAO.guardarNodo(nodo);
		}
	}

	@Override
	public void eliminarNodo(Integer id) {
		nodoDAO.eliminarNodo(id);
	}

	@Override
	public Nodo obtenerNodoPorAlias(String alias) throws NodoInexistenteException {
		Nodo nodo = nodoDAO.obtenerNodoPorAlias(alias);
		if (nodo == null)
			throw new NodoInexistenteException(alias);
		return nodo;
	}

	@Override
	public List<SolicitudCreacionNodo> obtenerSolicitudesDeCreacionDe(String email, Integer idVendedor) throws UsuarioInexistenteException {
		return solicitudCreacionNodoDAO.obtenerSolicitudesDeCreacionDe(usuarioService.obtenerClientePorEmail(email).getId(), idVendedor);
	}

	@Override
	public void editarSolicitudDeCreacionNodo(Integer idVendedor, Cliente cliente, Integer idSolicitud,
			String nombreNodo, Direccion obtenerDireccionConId, String tipoNodo, String barrio, String descripcion) throws SolicitudCreacionNodoException, NodoYaExistenteException {
		SolicitudCreacionNodo solicitud = solicitudCreacionNodoDAO.obtenerSolitudCreacionNodo(idSolicitud, cliente.getId(), idVendedor);
		validarSolicitud(solicitud);
		validarNombreNodoAlEditar(nombreNodo, idVendedor, solicitud.getId());
		solicitud.setBarrio(barrio);
		solicitud.setDescripcion(descripcion);
		solicitud.setDomicilio(obtenerDireccionConId);
		solicitud.setNombreNodo(nombreNodo);
		solicitud.setTipoNodo(tipoNodo);
		solicitudCreacionNodoDAO.guardar(solicitud);
	}


	@Override
	public void cancelarSolicitudDeCreacionNodo(Integer idSolicitud, Integer idVendedor, Integer idCliente) throws SolicitudCreacionNodoException, VendedorInexistenteException, ConfiguracionDeVendedorException {
		SolicitudCreacionNodo solicitud = solicitudCreacionNodoDAO.obtenerSolitudCreacionNodo(idSolicitud, idCliente, idVendedor);
		validarEstrategiaNodoActiva(idVendedor);
		validarSolicitud(solicitud);
		solicitud.setEstado(Constantes.SOLICITUD_NODO_CANCELADO);
		solicitudCreacionNodoDAO.guardar(solicitud);
	}
	
	/**
	 * Sección de validaciones internas
	 */
	
	private void validarNombreNodo(String nombre, Integer idVendedor) throws NodoYaExistenteException {
		List<SolicitudCreacionNodo> solicitudes = solicitudCreacionNodoDAO.obtenerSolicitudesDeCreacionEnGestionDe(idVendedor);
		List<Nodo> nodos = nodoDAO.obtenerNodosDelVendedor(idVendedor);
		validarNombreDeSolicitudes(solicitudes, nombre);
		validarNombreDeNodos(nodos, nombre);
	}
	
	

	private void validarNombreDeNodos(List<Nodo> nodos, String nombre) throws NodoYaExistenteException {
		for(Nodo nodo: nodos) {
			if(nodo.getAlias().equals(nombre)) {
				throw new NodoYaExistenteException("El nombre ya existe, por favor elija otro");
			}
		}
	}

	private void validarNombreDeSolicitudes(List<SolicitudCreacionNodo> solicitudes, String nombre) throws NodoYaExistenteException {
		for(SolicitudCreacionNodo solicitud: solicitudes) {
			if(solicitud.getNombreNodo().equals(nombre)) {
				throw new NodoYaExistenteException("El nombre ya fue solicitado, por favor elija otro");
			}
		}
	}

	private void validarSolicitud(SolicitudCreacionNodo solicitud) throws SolicitudCreacionNodoException {
		if(!solicitud.getEstado().equals(Constantes.SOLICITUD_NODO_EN_GESTION)) {
			throw new SolicitudCreacionNodoException();
		}
	}
	
	private void validarNombreNodoAlEditar(String nombreNodo, Integer idVendedor, Integer idSolicitud) throws NodoYaExistenteException {
		List<SolicitudCreacionNodo> solicitudes = solicitudCreacionNodoDAO.obtenerSolicitudesDeCreacionEnGestionDe(idVendedor);
		List<Nodo> nodos = nodoDAO.obtenerNodosDelVendedor(idVendedor);
		validarNombreDeSolicitudesAlEditar(solicitudes, nombreNodo,idSolicitud);
		validarNombreDeNodos(nodos, nombreNodo);
		
	}

	private void validarNombreDeSolicitudesAlEditar(List<SolicitudCreacionNodo> solicitudes, String nombreNodo, Integer IdSolicitud) throws NodoYaExistenteException {
		for(SolicitudCreacionNodo solicitud: solicitudes) {
			if(!solicitud.getId().equals(IdSolicitud)) {
				if(solicitud.getNombreNodo().equals(nombreNodo)) {
					throw new NodoYaExistenteException("El nombre ya fue solicitado, por favor elija otro");
				}
			}
		}
		
	}
	

	private void validarNodo(Nodo nodo) throws NodoCerradoException{
		if(nodo.getTipo().equals(Constantes.NODO_CERRADO)){
			throw new NodoCerradoException();
		}
	}
	
	private void validarEstrategiaNodoActiva(Integer idVendedor) throws VendedorInexistenteException, ConfiguracionDeVendedorException {
		if(!vendedorService.obtenerVendedorPorId(idVendedor).getEstrategiasUtilizadas().isNodos()) {
			throw new ConfiguracionDeVendedorException("Acción denegada");
		}
	}
	

	private void validar(Cliente usuario, Direccion direccion,Integer idVendedor) throws DireccionesInexistentes, VendedorInexistenteException, ConfiguracionDeVendedorException, SolicitudCreacionNodoEnGestionExistenteException {
		if(direccion == null) {
			throw new DireccionesInexistentes();
		}
		
		if(!usuario.contieneDireccion(direccion.getId())) {
			throw new DireccionesInexistentes();
		}
		
		validarEstrategiaNodoActiva(idVendedor);
		
		if(solicitudCreacionNodoDAO.obtenerSolitudCreacionNodoEnGestion(usuario.getId(), idVendedor)!= null) {
			throw new SolicitudCreacionNodoEnGestionExistenteException();
		}
	}

	@Override
	public List<SolicitudCreacionNodo> obtenerSolicitudesDeCreacionDeVendedor(Integer idVendedor) {
		return solicitudCreacionNodoDAO.obtenerSolicitudesDeCreacionDe(idVendedor);
	}

	@Override
	public void aceptarSolicitud(SolicitudCreacionNodo solicitud) throws VendedorInexistenteException {
		Nodo nodo = new Nodo(solicitud, vendedorService.obtenerVendedorPorId(solicitud.getIdVendedor()));
		solicitud.setEstado(Constantes.SOLICITUD_NODO_APROBADO);
		solicitudCreacionNodoDAO.guardar(solicitud);
		nodoDAO.guardarNodo(nodo);		
	}
	
	@Override
	public void rechazarSolicitud(SolicitudCreacionNodo solicitud){
		solicitud.setEstado(Constantes.SOLICITUD_NODO_RECHAZADO);
		solicitudCreacionNodoDAO.guardar(solicitud);	
	}

	@Override
	public void vaciarNodo(Integer idNodo) throws EstadoPedidoIncorrectoException {
		Nodo nodo = nodoDAO.obtenerNodoPorId(idNodo);
		if(nodo.sePuedeEliminar()){
			nodo.vaciarGrupo();
			nodoDAO.guardarNodo(nodo);
		}else {
			throw new EstadoPedidoIncorrectoException("El grupo no puede ser eliminado, por que hay pedidos abiertos o confirmados");
		}
		
	}
	
	private MiembroDeGCC obtenerMiembroGCC(GrupoCC grupo, String email) {
		for(MiembroDeGCC miembro : grupo.getCache()){
			if(miembro.getEmail().equals(email)){
				return miembro;
			}
		}
		return null;
	}


	@Override
	public void cederAdministracion(Integer idNodo, String emailCliente) throws UsuarioNoPerteneceAlGrupoDeCompras, UsuarioInexistenteException {
		Nodo nodo = nodoDAO.obtenerNodoPorId(idNodo);
		Cliente administradorAnterior = nodo.getAdministrador(); //Es necesario guardar la referencia para notificarlo luego que cedio la administracion.
		Cliente nuevoAdministrador = (Cliente) usuarioService.obtenerUsuarioPorEmail(emailCliente);
		
		MiembroDeGCC miembro = obtenerMiembroGCC(nodo, administradorAnterior.getEmail());
		if(!miembro.getEstadoInvitacion().equals(Constantes.ESTADO_NOTIFICACION_LEIDA_ACEPTADA)){
			throw new UsuarioNoPerteneceAlGrupoDeCompras(Constantes.ERROR_INVITACION_NO_ACEPTADA);
		}
		
		if(nodo.pertenece(nuevoAdministrador.getEmail())){
			nodo.cederAdministracion(nuevoAdministrador);
			nodo.setEmailAdministradorNodo(nuevoAdministrador.getEmail());
			//redefinir esta notificacion para nodos.
			notificacionService.notificarNuevoAdministrador(administradorAnterior, nuevoAdministrador, nodo);
			
			nodoDAO.guardarNodo(nodo);
		}else{
			throw new UsuarioNoPerteneceAlGrupoDeCompras(Constantes.ERROR_CREDENCIALES_INVALIDAS);
		}
		
	}

	@Override
	public void quitarMiembroDelNodo(Integer idNodo, String emailCliente) throws UsuarioInexistenteException {
		Nodo nodo = nodoDAO.obtenerNodoPorId(idNodo);
		
		Cliente cliente;
		try {
			cliente = (Cliente) usuarioService.obtenerUsuarioPorEmail(emailCliente);
			nodo.quitarMiembro(cliente);
			SolicitudPertenenciaNodo solicitud = solicitudPertenenciaNodoDAO.obtenerSolicitudDe(nodo.getId(), cliente.getId());
			if(solicitud != null) {
				solicitud.setEstado(Constantes.SOLICITUD_PERTENENCIA_NODO_RECHAZADO);
				if(solicitud.getReintentos()>0) {
					solicitud.setReintentos(solicitud.getReintentos() - 1);
				}
				solicitudPertenenciaNodoDAO.guardar(solicitud);
			}
		} catch (UsuarioInexistenteException e) {
			// Si el usuario no existe es porque fue invitado pero no está registrado en chasqui
			nodo.eliminarInvitacion(emailCliente);
		}
		nodoDAO.guardarNodo(nodo);
		invitacionService.eliminarInvitacion(idNodo,emailCliente);
		
	}
	


	@Override
	public void invitarANodo(Integer idNodo, String emailInvitado, String emailAdministrador) throws IOException, MessagingException, TemplateException, EncrypterException, GrupoCCInexistenteException {
		Nodo nodo = nodoDAO.obtenerNodoPorId(idNodo);
		if (nodo == null) {
			throw new GrupoCCInexistenteException(idNodo);
		}
		Cliente administrador = nodo.getAdministrador();
		//comprobar que el solicitante sea el administrador del grupo
		if (!administrador.getEmail().equals(emailAdministrador)) {
			throw new GrupoCCInexistenteException("El solicitante no es administrador del grupo: "+emailAdministrador);
		}
		
		//comprobar que el administrador no se invite a si mismo
		
		if (administrador.getEmail().equals(emailInvitado)) {
			throw new GrupoCCInexistenteException("El administrador está intentando invitarse a si mismo");
		}
		
		if (nodo.fueInvitado(emailInvitado)) {
			throw new GrupoCCInexistenteException("El usuario que intenta invitar ya fue invitado al grupo o ya pertence al grupo");
		}
		
		try {
			Cliente cliente = (Cliente) usuarioService.obtenerUsuarioPorEmail(emailInvitado);	
			
			notificacionService.notificarInvitacionAGCCClienteRegistrado(administrador, emailInvitado, nodo, IDDISP);
			nodo.invitarAlGrupo(cliente);			
			
		} catch (UsuarioInexistenteException e) {
			notificacionService.notificarInvitacionAGCCClienteNoRegistrado(administrador, emailInvitado, nodo, IDDISP);
			nodo.invitarAlGrupo(emailInvitado);
		}
		
		nodoDAO.guardarNodo(nodo);
		
	}
	
	

	@Override
	public void editarNodo(Integer idNodo, String email, String alias, String descripcion, Integer idDireccion,
			String tipoNodo, String barrio) throws RequestIncorrectoException {
		Nodo nodo = nodoDAO.obtenerNodoPorId(idNodo);
		if (nodo.getAdministrador().getEmail().equals(email)) {
			nodo.setAlias(alias);
			nodo.setDescripcion(descripcion);
			nodo.setEmailAdministradorNodo(email);
			nodo.setBarrio(barrio);
			nodo.setDescripcion(descripcion);
			if(tipoNodo.equals(Constantes.NODO_ABIERTO)||tipoNodo.equals(Constantes.NODO_CERRADO)) {
				nodo.setTipo(tipoNodo);
			}else {
				throw new RequestIncorrectoException("El tipo de nodo es incorrecto o fue mal especificado");
			}
			Direccion direccion = nodo.getAdministrador().obtenerDireccionConId(idDireccion);
			if(direccion == null) {
				throw new RequestIncorrectoException("El usuario "+nodo.getAdministrador().getNombre() + " no posee la dirección asignada");
			}
			nodo.setDireccionDelNodo(direccion);
			nodoDAO.guardarNodo(nodo);
		}
		else{
			throw new RequestIncorrectoException("El usuario "+ email + " no es el administrador del grupo:"+ nodo.getAlias());
		}
		
	}

	@Override
	public void aceptarSolicitudDePertenencia(SolicitudPertenenciaNodo solicitudpertenencia){
		Nodo nodo = solicitudpertenencia.getNodo();
		Cliente usuario = (Cliente) solicitudpertenencia.getUsuarioSolicitante();
		solicitudpertenencia.setEstado(Constantes.SOLICITUD_PERTENENCIA_NODO_ACEPTADO);
		nodo.invitarAlNodo(usuario);
		nodoDAO.guardarNodo(nodo);
		solicitudPertenenciaNodoDAO.guardar(solicitudpertenencia);
	}

	@Override
	public SolicitudPertenenciaNodo obtenerSolicitudDePertenenciaById(Integer idSolicitud) {
		return solicitudPertenenciaNodoDAO.obtenerSolicitudPertenenciaById(idSolicitud);
	}

	@Override
	public void cancelarSolicitudDePertenencia(SolicitudPertenenciaNodo solicitudpertenencia) {
		solicitudpertenencia.setEstado(Constantes.SOLICITUD_PERTENENCIA_NODO_CANCELADO);
		solicitudPertenenciaNodoDAO.guardar(solicitudpertenencia);		
	}

	@Override
	public void rechazarSolicitudDePertenencia(SolicitudPertenenciaNodo solicitudpertenencia) {
		solicitudpertenencia.setEstado(Constantes.SOLICITUD_PERTENENCIA_NODO_RECHAZADO);
		solicitudPertenenciaNodoDAO.guardar(solicitudpertenencia);				
	}

	@Override
	public SolicitudPertenenciaNodo obtenerSolicitudDe(Integer idNodo, Integer idCliente) {
		return solicitudPertenenciaNodoDAO.obtenerSolicitudDe(idNodo,idCliente);
		
	}

	@Override
	public void reabrirSolicitudDePertenenciaNodo(SolicitudPertenenciaNodo solicitud) {
		solicitud.setEstado(Constantes.SOLICITUD_PERTENENCIA_NODO_ENVIADO);
		solicitud.setReintentos(solicitud.getReintentos() + 1);
		solicitudPertenenciaNodoDAO.guardar(solicitud);
		
	}
	

	private List<MiembroDeGCC> obtenerOtrosMiembrosDelNodo(String mailCliente, Integer idNodo) throws GrupoCCInexistenteException {		
		Nodo nodo = this.obtenerNodoPorId(idNodo);
		List<MiembroDeGCC> nuevaLista = new ArrayList<MiembroDeGCC>();
		for (MiembroDeGCC miembroDeGCC : nodo.getCache()) {
			if (!miembroDeGCC.getEmail().equals(mailCliente)) {
				nuevaLista.add(miembroDeGCC);
			}
		}
		return nuevaLista;
				
	}
	
	private boolean pedidoEnEstadoInactivo(Pedido pedido) {
		if(pedido != null) {
			return pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_CANCELADO) || pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_VENCIDO) || pedido.getEstado().equals(Constantes.ESTADO_PEDIDO_INEXISTENTE);
		}else {
			return true;
		}
	}
	
	//modoficar a futuro para que notifique en contexto Nodo, por ahora es una copia de grupos
	private void notificarNuevoPedidoIndividualAOtrosMiembros(GrupoCC grupo, String emailCliente, String nombreCliente, String nombreVendedor) throws UsuarioInexistenteException, ClienteNoPerteneceAGCCException, GrupoCCInexistenteException, VendedorInexistenteException, PedidoInexistenteException {
		
		List<MiembroDeGCC> compas = this.obtenerOtrosMiembrosDelNodo(emailCliente,grupo.getId());
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
	
	@Override
	public void nuevoPedidoIndividualPara(Integer idNodo, String email, Integer idVendedor) throws ClienteNoPerteneceAGCCException, VendedorInexistenteException, ConfiguracionDeVendedorException, PedidoVigenteException, UsuarioInexistenteException, GrupoCCInexistenteException, PedidoInexistenteException {
		Nodo nodo = nodoDAO.obtenerNodoPorId(idNodo);
		Pedido pedidoVigente = nodo.obtenerPedidoIndividual(email);
		if (pedidoVigente == null ) {
			Pedido pedidoNuevo = pedidoService.crearPedidoIndividualEnGrupo(nodo, email, idVendedor);
			nodo.nuevoPedidoIndividualPara(email, pedidoNuevo);
			nodoDAO.guardarNodo(nodo);

			Vendedor vendedor = usuarioService.obtenerVendedorPorID(idVendedor);
			String nombreVendedor =vendedor.getNombre(); 
			
			Cliente cliente = (Cliente) usuarioService.obtenerUsuarioPorEmail(email);
			String nombreCliente= cliente.getUsername(); 
			this.notificarNuevoPedidoIndividualAOtrosMiembros(nodo,email, nombreCliente, nombreVendedor);
			
		}
		else{
			throw new PedidoVigenteException(email);
		}
		
	}

	@Override
	public Pedido obtenerPedidoIndividualEnNodo(Integer idNodo, String email) {
		GrupoCC nodo = nodoDAO.obtenerNodoPorId(idNodo);
		return nodo.getPedidoActual().buscarPedidoParaCliente(email);
	}
	

	private void validarRequest(Integer idPedido) throws RequestIncorrectoException {
		if (idPedido == null || idPedido < 0) {
			throw new RequestIncorrectoException("el id del pedido debe ser mayor a 0");
		}
	}
	
	private void validarQueContengaProductos(Pedido pedido) throws PedidoSinProductosException {
		if(pedido.getProductosEnPedido().isEmpty()){
			throw new PedidoSinProductosException("El usuario: " + pedido.getCliente().getUsername() + " no posee productos en su pedido" );
		}
	}

	
	/*
	 * Este método notifica a los miembros del nodo que un cliente ha confirmado el pedido individual
	 * 
	 */
	//TODO: crear la notificación para nodo, actualmente usa la de grupos. 
	private void notificarConfirmacionAOtrosMiembros(String emailCliente, String nombreCliente, Pedido pedido, Integer idVendedor, String emailVendedor) throws UsuarioInexistenteException, ClienteNoPerteneceAGCCException, GrupoCCInexistenteException, VendedorInexistenteException, PedidoInexistenteException {
		
		Nodo nodo = this.obtenerNodoPorId(pedido.getPedidoColectivo().getColectivo().getId());
		
		List<MiembroDeGCC> compas = this.obtenerOtrosMiembrosDelNodo(emailCliente,nodo.getId());
		for (MiembroDeGCC compa : compas) {
		     notificacionService.notificarConfirmacionCompraOtroMiembro(emailVendedor, compa.getEmail(),nombreCliente , nodo.getAlias());
			
		}
	}
	private void validarConfirmacionDePedidoSinDireccionPara(Pedido p, ConfirmarPedidoSinDireccionRequest request)
			throws PedidoInexistenteException {
		if (p == null) {
			throw new PedidoInexistenteException(
					"El usuario no posee un pedido vigente con el ID otorgado");
		}
	}
	
	@Override
	public void confirmarPedidoIndividualEnNodo(String email,
			ConfirmarPedidoSinDireccionRequest request) throws RequestIncorrectoException, UsuarioInexistenteException, VendedorInexistenteException, PedidoInexistenteException, PedidoSinProductosException, EstadoPedidoIncorrectoException, ClienteNoPerteneceAGCCException, GrupoCCInexistenteException {
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
		
		//Notificar al cliente y a sus compañeros, redefinirlos para que sea para Nodos
		notificacionService.enviarAClienteSuPedidoConfirmado(vendedor.getEmail(), email, pedido);		
		this.notificarConfirmacionAOtrosMiembros(email, cliente.getUsername(), pedido, vendedor.getId(), vendedor.getEmail());
		
	}

	@Override
	public List<Nodo> obtenerNodosAbiertosDelVendedor(Integer idVendedor) throws VendedorInexistenteException {
		usuarioService.obtenerVendedorPorID(idVendedor);
		return nodoDAO.obtenerNodosAbiertosDelVendedor(idVendedor);
	}

	@Override
	public List<SolicitudPertenenciaNodo> obtenerSolicitudesDePertenencia(Integer idNodo) {
		return solicitudPertenenciaNodoDAO.obtenerSolicitudesDePertenenciaDeNodo(idNodo);
	}

	@Override
	public List<SolicitudPertenenciaNodo> obtenerSolicitudesDePertenenciaDeUsuarioDeVendededor(Integer idUsuario,
			Integer idVendedor) throws VendedorInexistenteException {
		usuarioService.obtenerVendedorPorID(idVendedor);
		return solicitudPertenenciaNodoDAO.obtenerSolicitudesDePertenenciaDeUsuarioDeVendededor(idUsuario, idVendedor);
	}
	

}