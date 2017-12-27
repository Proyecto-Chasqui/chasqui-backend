package chasqui.dao;

import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

import chasqui.model.Cliente;
import chasqui.model.Notificacion;
import chasqui.model.Usuario;
import chasqui.model.Vendedor;

public interface UsuarioDAO {

	
	public Usuario obtenerUsuarioPorID(Integer id);
	
	public Vendedor obtenerVendedorPorID(Integer id);
	
	public void guardarUsuario(Usuario u);
	
	public Usuario obtenerUsuarioPorNombre (String username);
	
	public void merge(Vendedor usuario);
	
	public Usuario obtenerUsuarioPorEmail(String email);
	
	public boolean existeUsuarioCon(String email);
	
	public void inicializarListasDe(Vendedor usuarioLogueado);
	
	//public Cliente obtenerClienteConDireccionPorEmail(final String email);
	
	//public Cliente obtenerClienteConPedido(String mail);
	
	public void eliminarUsuario(Vendedor u);
	
	public List<Notificacion> obtenerNotificacionesDe(String mail, Integer pagina);
	
	//public Cliente obtenerClienteConPedidoEHistorial(String mail);
	
	public List<Notificacion> obtenerNotificacionNoLeidas(String mail);
	
	public Integer obtenerTotalNotificacionesDe(String mail);
	
	public Notificacion obtenerNotificacion(Integer id);
	
	public void guardar(Notificacion n);
	
	public <T> void deleteObject(T obj);
	
	public List<Cliente> obtenerClientesCercanos(Geometry area, String email);

	//public Cliente obtenerClientePorEmailConDirecciones(String email);

	Cliente inicializarDirecciones(Cliente cliente);

	Cliente inicializarPedidos(Cliente cliente);

	Cliente inicializarHistorial(Cliente cliente);

	Cliente inicializarColecciones(Cliente cliente);

}
