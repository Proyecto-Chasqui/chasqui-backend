package chasqui.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import chasqui.dao.impl.NodoDAOHbm;
import chasqui.exceptions.NodoInexistenteException;
import chasqui.exceptions.NodoYaExistenteException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.Nodo;
import chasqui.model.Vendedor;
import chasqui.services.interfaces.NodoService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.view.composer.Constantes;

public class NodoServiceImpl implements NodoService {

	@Autowired
	NodoDAOHbm nodoDAO;
	@Autowired
	UsuarioService usuarioService;

	@Override
	public void aprobarNodoPorId(Integer id) throws NodoInexistenteException {
		Nodo nodo = nodoDAO.obtenerNodoPorId(id);
		if (nodo == null)
			throw new NodoInexistenteException(id);
		nodo.aprobarNodo();
		nodoDAO.guardarNodo(nodo);
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

				Direccion domicilioEntrega = new Direccion();
				domicilioEntrega.setAltura(altura);
				domicilioEntrega.setCalle(calle);
				domicilioEntrega.setLocalidad(localidad);

				nodo.setDireccionEntrega(domicilioEntrega);

				Vendedor vendedor = (Vendedor) usuarioService.obtenerVendedorPorID(idVendedor);
				nodo.setVendedor(vendedor);
				nodo.setTipo(Constantes.NODO_ABIERTO);
				nodo.setEstado(Constantes.ESTADO_NODO_SOLICITADO_SIN_ADMIN);
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

			Direccion domicilioEntrega = new Direccion();
			domicilioEntrega.setAltura(altura);
			domicilioEntrega.setCalle(calle);
			domicilioEntrega.setLocalidad(localidad);

			nodo.setDireccionEntrega(domicilioEntrega);
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
	public void aprobarNodoPorAlias(String alias) {
		Nodo nodo = nodoDAO.obtenerNodoPorAlias(alias);
		nodo.aprobarNodo();
		nodoDAO.guardarNodo(nodo);
	}

	@Override
	public Nodo obtenerNodoPorAlias(String alias) throws NodoInexistenteException {
		Nodo nodo = nodoDAO.obtenerNodoPorAlias(alias);
		if (nodo == null)
			throw new NodoInexistenteException(alias);
		return nodo;
	}

}