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
import chasqui.model.GrupoCC;
import chasqui.model.MiembroDeGCC;
import chasqui.model.Pedido;
import chasqui.service.rest.impl.OpcionSeleccionadaRequest;
import chasqui.service.rest.request.ConfirmarPedidoSinDireccionRequest;
import chasqui.service.rest.request.DireccionRequest;
import freemarker.template.TemplateException;

public interface GrupoService {

	public void altaGrupo(Integer idVendedor, String aliasGrupo, String descripcion, String emailClienteAdministrador)
			throws UsuarioInexistenteException, VendedorInexistenteException, RequestIncorrectoException;

	/**
	 * Obtiene los grupos del Vendedor.
	 * 
	 * @param idVendedor
	 * @return
	 * @throws VendedorInexistenteException
	 */
	List<GrupoCC> obtenerGruposDe(int idVendedor) throws VendedorInexistenteException;

	public Pedido obtenerPedidoIndividualEnGrupo(Integer idGrupo, String emailCliente);

	public void eliminarGrupoCC(Integer id) throws GrupoCCInexistenteException;

	public void confirmarInvitacionGCC(Integer idInvitacion, String emailClienteInvitado)
			throws UsuarioInexistenteException;

	public void invitarAGrupo(Integer idGrupo, String emailInvitado, String emailAdministrador)
			throws GrupoCCInexistenteException, IOException, MessagingException, TemplateException, Exception;

	public List<GrupoCC> obtenerGruposDeCliente(String email, Integer idVendedor) throws UsuarioInexistenteException;

	public void quitarMiembroDelGrupo(Integer idGrupo, String emailCliente) throws UsuarioInexistenteException;

	Map<Integer, Pedido> obtenerPedidosEnGruposCC(List<GrupoCC> grupos, String email)
			throws UsuarioInexistenteException, ClienteNoPerteneceAGCCException;

	GrupoCC obtenerGrupo(int idGrupo) throws GrupoCCInexistenteException;

	public void nuevoPedidoIndividualPara(Integer idGrupo, String email, Integer idVendedor)
			throws UsuarioInexistenteException, ClienteNoPerteneceAGCCException, ConfiguracionDeVendedorException,
			PedidoVigenteException, PedidoInexistenteException, VendedorInexistenteException,
			GrupoCCInexistenteException;

	public void cederAdministracion(Integer idGrupo, String emailCliente) throws UsuarioInexistenteException, UsuarioNoPerteneceAlGrupoDeCompras;

	/**
	 * Confirma el pedido colectivo vigente en el grupo. Chequea que el
	 * solicitante sea administrador y que el idDomicilio corresponda a un
	 * domicilio que le pertenece. Utiliza ese domicilio como domicilio e
	 * entrega del pedido
	 * 
	 * @param idGrupo
	 * @param emailSolicitante
	 * @param idDomicilio
	 * @param idPuntoDeRetiro 
	 * @throws EstadoPedidoIncorrectoException
	 * @throws NoAlcanzaMontoMinimoException
	 * @throws RequestIncorrectoException
	 * @throws DireccionesInexistentes
	 *             Si el usuario solicitante no posee el domicilio con el ID
	 *             indicado
	 * @throws UsuarioInexistenteException
	 */
	public void confirmarPedidoColectivo(Integer idGrupo, String emailSolicitante, Integer idDomicilio, Integer idPuntoDeRetiro, String comentario, List<OpcionSeleccionadaRequest> opcionesSeleccionadas, Integer idZona)
			throws EstadoPedidoIncorrectoException, NoAlcanzaMontoMinimoException, RequestIncorrectoException,
			DireccionesInexistentes, UsuarioInexistenteException;

	public void actualizarDomicilio(Integer idGrupo, DireccionRequest domicilio) throws RequestIncorrectoException;

	public void editarGrupo(Integer idGrupo, String email, String alias, String descripcion)
			throws RequestIncorrectoException;

	public void rechazarInvitacionGCC(Integer idInvitacion, String emailClienteLogueado)
			throws UsuarioInexistenteException;

	@Transactional
	public List<MiembroDeGCC> obtenerOtrosMiembrosDelGCC(String mail, Integer idGrupo)
			throws GrupoCCInexistenteException;

	@Transactional
	public GrupoCC obtenerGrupoPorIdPedidoColectivo(Integer idPedidoColectivo, int idVendedor)
			throws VendedorInexistenteException, PedidoInexistenteException;

	void confirmarPedidoIndividualEnGCC(String email, ConfirmarPedidoSinDireccionRequest request)
			throws RequestIncorrectoException, EstadoPedidoIncorrectoException, UsuarioInexistenteException,
			VendedorInexistenteException, PedidoInexistenteException, ClienteNoPerteneceAGCCException,
			GrupoCCInexistenteException, PedidoSinProductosException;

	public Collection<? extends GrupoCC> obtenerGruposDe(Integer id, Date d, Date h, String estadoSeleccionado)
			throws VendedorInexistenteException;

	public void guardarGrupo(GrupoCC grupo);
	@Transactional
	public void vaciarGrupoCC(Integer idGrupo) throws EstadoPedidoIncorrectoException;
	@Transactional
	public void eliminarGrupos(List<GrupoCC> obtenerGruposDe);
}
