package chasqui.services.interfaces;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import chasqui.exceptions.NodoInexistenteException;
import chasqui.exceptions.NodoYaExistenteException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Nodo;

public interface NodoService {

	@Transactional
	void aprobarNodoPorId(Integer id) throws NodoInexistenteException;

	@Transactional
	public void guardarNodo(Nodo nodo);

	@Transactional
	List<Nodo> obtenerNodosDelVendedor(Integer idVendedor) throws VendedorInexistenteException;

	@Transactional
	void altaNodo(String alias, String emailClienteAdministrador, String Localidad, String calle, int Altura, String telefono, int idVendedor, String descripcion) throws UsuarioInexistenteException, NodoYaExistenteException, VendedorInexistenteException;

	Nodo obtenerNodoPorId(Integer id);

	@Transactional
	void eliminarNodo(Integer id);
	
	@Transactional
	void aprobarNodoPorAlias(String alias);

	Nodo obtenerNodoPorAlias(String alias) throws NodoInexistenteException;

	void altaNodoSinUsuario(String alias, String emailClienteAdministrador, String localidad, String calle, int altura,
			String telefono, int idVendedor, String descripcion) throws NodoYaExistenteException, VendedorInexistenteException;
}
