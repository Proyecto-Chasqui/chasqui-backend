package chasqui.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import chasqui.dao.impl.NodoDAOHbm;
import chasqui.dao.impl.SolicitudCreacionNodoDAOHbm;
import chasqui.dao.impl.SolicitudPertenenciaNodoDAOHbm;
import chasqui.exceptions.ConfiguracionDeVendedorException;
import chasqui.exceptions.DireccionesInexistentes;
import chasqui.exceptions.NodoCerradoException;
import chasqui.exceptions.NodoInexistenteException;
import chasqui.exceptions.NodoYaExistenteException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.Nodo;
import chasqui.model.SolicitudCreacionNodo;
import chasqui.model.SolicitudPertenenciaNodo;
import chasqui.model.Usuario;
import chasqui.model.Vendedor;
import chasqui.services.interfaces.NodoService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.services.interfaces.VendedorService;
import chasqui.view.composer.Constantes;

public class NodoServiceImpl implements NodoService {

	@Autowired
	NodoDAOHbm nodoDAO;
	@Autowired
	UsuarioService usuarioService;
	@Autowired
	VendedorService vendedorService;
	@Autowired
	SolicitudCreacionNodoDAOHbm solicitudCreacionNodoDAO;
	@Autowired
	SolicitudPertenenciaNodoDAOHbm solicitudPertenenciaNodoDAO;

	@Override
	public void crearSolicitudDeCreacionNodo(Integer idVendedor, Cliente usuario, String nombre, Direccion direccion, String tipo, String barrio, String descripcion) throws DireccionesInexistentes, VendedorInexistenteException, ConfiguracionDeVendedorException{
		validar(usuario,direccion,idVendedor);
		solicitudCreacionNodoDAO.guardar(new SolicitudCreacionNodo(idVendedor,usuario, nombre, direccion, tipo, barrio, descripcion));
	}
	
	private void validar(Cliente usuario, Direccion direccion,Integer idVendedor) throws DireccionesInexistentes, VendedorInexistenteException, ConfiguracionDeVendedorException {
		if(!usuario.contieneDireccion(direccion.getId())) {
			throw new DireccionesInexistentes();
		}
		if(!vendedorService.obtenerVendedorPorId(idVendedor).getEstrategiasUtilizadas().isNodos()) {
			throw new ConfiguracionDeVendedorException("");
		}
	}

	@Override
	public void crearSolicitudDePertenenciaANodo(Nodo nodo, Cliente usuario) throws NodoCerradoException{
		validarNodo(nodo);
		solicitudPertenenciaNodoDAO.guardar(new SolicitudPertenenciaNodo(nodo, usuario));
	}

	private void validarNodo(Nodo nodo) throws NodoCerradoException{
		if(nodo.getTipo().equals(Constantes.NODO_CERRADO)){
			throw new NodoCerradoException();
		}
	}

	@Override
	public List<Nodo> obtenerNodosDelVendedor(Integer idVendedor) throws VendedorInexistenteException {
		usuarioService.obtenerVendedorPorID(idVendedor); // Para verificar si el vendedor con el id pasado como parametro es válido

		return nodoDAO.obtenerNodosDelVendedor(idVendedor);
		
	}
	
	@Override
	public Nodo obtenerNodoPorId(Integer idNodo) {
		return nodoDAO.obtenerNodoPorId(idNodo);
	}
	
	
	@Override
	public void guardarNodo(Nodo nodo) {
		nodoDAO.guardarNodo(nodo);
	}
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

}