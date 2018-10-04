package chasqui.services.interfaces;

import java.io.IOException;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import chasqui.exceptions.DireccionesInexistentes;
import chasqui.exceptions.RequestIncorrectoException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.Notificacion;
import chasqui.model.Usuario;
import chasqui.model.Vendedor;
import chasqui.service.rest.request.DireccionRequest;
import chasqui.service.rest.request.EditarPasswordRequest;
import chasqui.service.rest.request.EditarPerfilRequest;
import chasqui.service.rest.request.SingUpRequest;
import chasqui.service.rest.request.SingUpRequestWithInvitation;

public interface UsuarioService {

	public Usuario obtenerUsuarioPorID(final Integer id);

	public Vendedor obtenerVendedorPorID(final Integer id) throws VendedorInexistenteException;

	public Cliente obtenerClientePorEmail(String email) throws UsuarioInexistenteException;

	public Usuario obtenerUsuarioPorEmail(String email) throws UsuarioInexistenteException;

	public Usuario login(final String username, final String password) throws Exception;

	public Cliente loginCliente(final String email, final String password) throws Exception;

	public boolean existeUsuarioCon(String email);

	@Transactional
	public void modificarPasswordUsuario(String email, String password) throws UsuarioInexistenteException;

	@Transactional
	public void guardarUsuario(Usuario u);

	@Transactional
	public void merguear(Vendedor usuario);

	/**
	 * Se da de alta el nuevo Cliente y se buscan las invitaciones a GCC
	 * pendientes para actualizar los datos en la membresía.
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public Cliente crearCliente(SingUpRequest request) throws Exception;

	@Transactional
	public void modificarUsuario(EditarPerfilRequest editRequest, String email) throws Exception;

	@Transactional
	public Direccion agregarDireccionAUsuarioCon(String mail, DireccionRequest request)
			throws UsuarioInexistenteException, RequestIncorrectoException;

	@Transactional
	public void inicializarListasDe(Vendedor vendedor);

	@Transactional
	public void editarDireccionDe(String mail, DireccionRequest request, Integer idDireccion)
			throws DireccionesInexistentes, UsuarioInexistenteException, RequestIncorrectoException;

	@Transactional
	public void eliminarDireccionDe(String mail, Integer idDireccion)
			throws DireccionesInexistentes, UsuarioInexistenteException;

	@Transactional
	public void eliminarUsuario(Vendedor u);

	@Transactional
	public List<Notificacion> obtenerNotificacionesDe(String mail, Integer pagina);

	@Transactional
	public void enviarInvitacionRequest(String origen, String destino) throws Exception;

	@Transactional
	public void agregarIDDeDispositivo(String mail, String dispositivo) throws UsuarioInexistenteException;

	@Transactional
	public List<Notificacion> obtenerNotificacionesNoLeidas(String mail);

	@Transactional
	public Integer obtenerTotalNotificacionesDe(String mail);

	@Transactional
	public void leerNotificacion(Integer id);

	@Transactional
	public <T> void deleteObject(T object);

	@Transactional
	void inicializarDirecciones(Cliente cliente);

	@Transactional
	void inicializarPedidos(Cliente cliente);

	@Transactional
	public void inicializarHistorial(Cliente cliente);

	@Transactional
	void inicializarColecciones(Cliente cliente);

	/*
	 * Metodo para actualizar el path del avatar en el Cliente y ademas
	 * actualizar los objetos MiembroDeGCC del Cliente en cuestion
	 */
	@Transactional
	public void editarAvatarDe(Cliente cliente, String avatar, String extension) throws IOException, Exception;

	@Transactional
	public String obtenerAvatar(String mail) throws IOException;

	public Cliente crearCliente(SingUpRequestWithInvitation request) throws Exception;

	void modificarPassowrd(EditarPasswordRequest request, String email) throws Exception;

}
