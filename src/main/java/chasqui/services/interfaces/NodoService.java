package chasqui.services.interfaces;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.transaction.annotation.Transactional;

import chasqui.exceptions.ClienteNoPerteneceAGCCException;
import chasqui.exceptions.ConfiguracionDeVendedorException;
import chasqui.exceptions.DireccionesInexistentes;
import chasqui.exceptions.EncrypterException;
import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.exceptions.GrupoCCInexistenteException;
import chasqui.exceptions.InvitacionExistenteException;
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
import chasqui.model.Nodo;
import chasqui.model.Pedido;
import chasqui.model.SolicitudCreacionNodo;
import chasqui.model.SolicitudPertenenciaNodo;
import chasqui.model.Usuario;
import chasqui.service.rest.impl.OpcionSeleccionadaRequest;
import chasqui.service.rest.request.ConfirmarPedidoSinDireccionRequest;
import freemarker.template.TemplateException;

public interface NodoService {

	@Transactional
	public void guardarNodo(Nodo nodo);

	@Transactional
	List<Nodo> obtenerNodosDelVendedor(Integer idVendedor)throws VendedorInexistenteException;

	@Transactional
	void altaNodo(String alias, String emailClienteAdministrador, String Localidad, String calle, int Altura, String telefono, int idVendedor, String descripcion) throws UsuarioInexistenteException, NodoYaExistenteException, VendedorInexistenteException;

	Nodo obtenerNodoPorId(Integer id);

	@Transactional
	void eliminarNodo(Integer id);

	Nodo obtenerNodoPorAlias(String alias) throws NodoInexistenteException;

	void altaNodoSinUsuario(String alias, String emailClienteAdministrador, String localidad, String calle, int altura,
			String telefono, int idVendedor, String descripcion) throws NodoYaExistenteException, VendedorInexistenteException;
	/**
	 * Crea una solicitud de creación de nodo en estado "En_gestion"
	 * Valida que el vendedor tenga la estrategia de venta "nodos" y verifica que la direccion corresponda al usuario
	 * @param usuario
	 * @param nombre
	 * @param direccion
	 * @param tipo
	 * @param barrio
	 * @param descripcion
	 * @throws DireccionesInexistentes 
	 * @throws SolicitudCreacionNodoEnGestionExistenteException 
	 * @throws NodoYaExistenteException 
	 */
	void crearSolicitudDeCreacionNodo(Integer idVendedor, Cliente usuario, String nombre, Direccion direccion,
			String tipo, String barrio, String descripcion)
			throws DireccionesInexistentes, VendedorInexistenteException, ConfiguracionDeVendedorException, SolicitudCreacionNodoEnGestionExistenteException, NodoYaExistenteException;
	/**
	 * Crea una solicitud para pertenecer a un nodo en estado "enviado"
	 * Valida que el nodo enviado sea de tipo "abierto"
	 * @param nodo
	 * @param usuario
	 */
	@Transactional
	void crearSolicitudDePertenenciaANodo(Nodo nodo, Cliente usuario);
	
	/**
	 * Retorna todas las solicitudes de creacion de nodo para el usuario
	 * @param email
	 * @param idVendedor
	 * @return
	 * @throws UsuarioInexistenteException 
	 */
	public List<SolicitudCreacionNodo> obtenerSolicitudesDeCreacionDe(String email, Integer idVendedor) throws UsuarioInexistenteException;
	/**
	 * Edita la solicitud con los datos enviados
	 * Solo permite editar solicitudes en estado de "gestion".
	 * @param idVendedor
	 * @param cliente
	 * @param idSolicitud
	 * @param nombreNodo
	 * @param obtenerDireccionConId
	 * @param tipoNodo
	 * @param barrio
	 * @param descripcion
	 * @return
	 * @throws SolicitudCreacionNodoException
	 * @throws NodoYaExistenteException 
	 * @throws DireccionesInexistentes 
	 */
	@Transactional
	public void editarSolicitudDeCreacionNodo(Integer idVendedor, Cliente cliente, Integer idSolicitud,
			String nombreNodo, Direccion obtenerDireccionConId, String tipoNodo, String barrio, String descripcion) throws SolicitudCreacionNodoException, NodoYaExistenteException;
	/**
	 * Cancela la solicitud, valida que el vendedor tenga la estrategia nodos activa para emplear la accion.
	 * @param idSolicitud
	 * @param idVendedor
	 * @param id
	 * @throws SolicitudCreacionNodoException
	 * @throws ConfiguracionDeVendedorException 
	 * @throws VendedorInexistenteException 
	 */
	public void cancelarSolicitudDeCreacionNodo(Integer idSolicitud, Integer idVendedor, Integer id) throws SolicitudCreacionNodoException, VendedorInexistenteException, ConfiguracionDeVendedorException;
	/**
	 * Obtiene todos las solicitudes de creacion de nodos para un vendedor determinado, independientemente de su estado.
	 * @param id
	 * @return
	 */
	public List<SolicitudCreacionNodo> obtenerSolicitudesDeCreacionDeVendedor(Integer id);
	
	/**
	 * Cambia el estado de la solicitud a Aceptada, y ejecuta la creación del nodo con los datos contenidos en la misma
	 * @param solicitud
	 * @throws VendedorInexistenteException 
	 */
	public void aceptarSolicitud(SolicitudCreacionNodo solicitud) throws VendedorInexistenteException;
	
	/**
	 * Cambia el estado de la solicitud a Rechazado
	 * @param solicitud
	 */
	public void rechazarSolicitud(SolicitudCreacionNodo solicitud);
	/**
	 * Obtiene los nodos del cliente con el email solicitado
	 * @param idVendedor
	 * @param email
	 * @return
	 * @throws VendedorInexistenteException
	 */
	List<Nodo> obtenerNodosDelCliente(Integer idVendedor, String email) throws VendedorInexistenteException;
	
	/**
	 * Permite eliminar el nodo, vaciando sus pedidos e integrantes, solo se puede eliminar si no hay pedidos confirmados o abiertos.
	 * @param idGrupo
	 * @throws EstadoPedidoIncorrectoException
	 */
	@Transactional
	public void vaciarNodo(Integer idGrupo) throws EstadoPedidoIncorrectoException;
	/**
	 * Cede la administración del nodo y notifica de la acción por email.
	 * @param idNodo
	 * @param emailCliente
	 * @throws UsuarioNoPerteneceAlGrupoDeCompras
	 * @throws UsuarioInexistenteException 
	 */
	@Transactional
	public void cederAdministracion(Integer idNodo, String emailCliente) throws UsuarioNoPerteneceAlGrupoDeCompras, UsuarioInexistenteException;
	/**
	 * Elimina el miembro del nodo
	 * @param idGrupo
	 * @param emailCliente
	 * @throws UsuarioInexistenteException
	 */
	@Transactional
	public void quitarMiembroDelNodo(Integer idGrupo, String emailCliente) throws UsuarioInexistenteException;
	/**
	 * Permite enviar invitación a usuarios al nodo
	 * @param idGrupo
	 * @param emailInvitado
	 * @param emailAdministrador
	 * @throws IOException
	 * @throws MessagingException
	 * @throws TemplateException
	 * @throws EncrypterException
	 * @throws GrupoCCInexistenteException 
	 */
	@Transactional
	public void invitarANodo(Integer idGrupo, String emailInvitado, String emailAdministrador) throws IOException, MessagingException, TemplateException, EncrypterException, GrupoCCInexistenteException;
	/**
	 * Permite editar el nodo.
	 * @param idNodo
	 * @param email
	 * @param alias
	 * @param descripcion
	 * @param idDireccion
	 * @param tipoNodo
	 * @param barrio
	 * @throws RequestIncorrectoException
	 */
	@Transactional
	public void editarNodo(Integer idNodo, String email, String alias, String descripcion, Integer idDireccion,
			String tipoNodo, String barrio) throws RequestIncorrectoException;
	/**
	 * Acepta la solicitud de pertencia de ese usuario al nodo solicitado, lo agrega como miembro de mismo.
	 * Si ya existe en el nodo retorna una excepcion de su existencia.
	 * @param nodo
	 * @param usuario
	 * @throws InvitacionExistenteException
	 */
	@Transactional
	public void aceptarSolicitudDePertenencia(SolicitudPertenenciaNodo solicitudpertenencia);

	public SolicitudPertenenciaNodo obtenerSolicitudDePertenenciaById(Integer idSolicitud);
	/**
	 * Cancela la solicitud de pertenencia, debe ser hecho por el usuario solicitante
	 * @param solicitudpertenencia
	 */
	@Transactional
	public void cancelarSolicitudDePertenencia(SolicitudPertenenciaNodo solicitudpertenencia);
	/**
	 * Rechaza la solicitud de pertenencia, debe ser hecho por el administrador del Nodo
	 * @param solicitudpertenencia
	 */
	@Transactional
	public void rechazarSolicitudDePertenencia(SolicitudPertenenciaNodo solicitudpertenencia);

	public SolicitudPertenenciaNodo obtenerSolicitudDe(Integer idNodo, Integer idCliente);
	
	@Transactional
	public void reabrirSolicitudDePertenenciaNodo(SolicitudPertenenciaNodo solicitud);
	/**
	 * Crea el pedido individual dentro de nodo, y notifica a los demas miembros que se creo.
	 * @param idGrupo
	 * @param email
	 * @param idVendedor
	 * @throws ClienteNoPerteneceAGCCException
	 * @throws VendedorInexistenteException
	 * @throws ConfiguracionDeVendedorException
	 * @throws PedidoVigenteException
	 * @throws UsuarioInexistenteException
	 * @throws GrupoCCInexistenteException
	 * @throws PedidoInexistenteException
	 * @throws UsuarioNoPerteneceAlGrupoDeCompras 
	 */
	@Transactional
	public void nuevoPedidoIndividualPara(Integer idNodo, String email, Integer idVendedor) throws ClienteNoPerteneceAGCCException, VendedorInexistenteException, ConfiguracionDeVendedorException, PedidoVigenteException, UsuarioInexistenteException, GrupoCCInexistenteException, PedidoInexistenteException, UsuarioNoPerteneceAlGrupoDeCompras;
	/**
	 * Retorna el pedido actual del usuario de ese nodo.
	 * @param idNodo
	 * @param email
	 * @return
	 */
	public Pedido obtenerPedidoIndividualEnNodo(Integer idNodo, String email);
	/**
	 * 
	 * @param email
	 * @param confirmarPedidoSinDireccionRequest
	 * @throws RequestIncorrectoException 
	 * @throws UsuarioInexistenteException 
	 * @throws VendedorInexistenteException 
	 * @throws PedidoInexistenteException 
	 * @throws PedidoSinProductosException 
	 * @throws EstadoPedidoIncorrectoException 
	 * @throws GrupoCCInexistenteException 
	 * @throws ClienteNoPerteneceAGCCException 
	 */
	@Transactional
	public void confirmarPedidoIndividualEnNodo(String email,
			ConfirmarPedidoSinDireccionRequest confirmarPedidoSinDireccionRequest) throws RequestIncorrectoException, UsuarioInexistenteException, VendedorInexistenteException, PedidoInexistenteException, PedidoSinProductosException, EstadoPedidoIncorrectoException, ClienteNoPerteneceAGCCException, GrupoCCInexistenteException;
	/**
	 * Retorna todos los nodos abiertos que posea ese vendedor.
	 * @param idVendedor
	 * @return
	 * @throws VendedorInexistenteException
	 */
	public List<Nodo> obtenerNodosAbiertosDelVendedor(Integer idVendedor) throws VendedorInexistenteException;

	public List<SolicitudPertenenciaNodo> obtenerSolicitudesDePertenencia(Integer idNodo);

	public List<SolicitudPertenenciaNodo> obtenerSolicitudesDePertenenciaDeUsuarioDeVendededor(Integer id,
			Integer idVendedor) throws VendedorInexistenteException;
	/**
	 * Retorna un nodo con los datos solicitados, si los mismos son null o "", son ignorados.
	 * @param id
	 * @param d
	 * @param h
	 * @param estadoNodoBool
	 * @param nombreNodo
	 * @param emailcoordinador
	 * @param barrio
	 * @return
	 */

	List<Nodo> obtenerNodosDelVendedorCon(Integer idvendedor, Date d, Date h, String estadoNodoBool, String nombreNodo,
			String emailcoordinador, String barrio, String tipo);

	public Collection<? extends SolicitudCreacionNodo> obtenerSolicitudesDeCreacionNodosDelVendedorCon(Integer id,
			Date d, Date h, String estado, String nombreCoordinador, String email, String barrio);

	public Map<Integer, Pedido> obtenerPedidosEnNodos(List<Nodo> nodos, String email) throws ClienteNoPerteneceAGCCException;
	
	/**
	 * Recalcula las zonas para los nodos del vendedor especificado
	 * @param idVendedor
	 */
	@Transactional
	public void recalcularZonasParaNodos(Integer idVendedor);


}
