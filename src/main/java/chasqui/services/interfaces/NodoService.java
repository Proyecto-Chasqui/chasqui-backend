package chasqui.services.interfaces;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import chasqui.exceptions.ConfiguracionDeVendedorException;
import chasqui.exceptions.DireccionesInexistentes;
import chasqui.exceptions.NodoInexistenteException;
import chasqui.exceptions.NodoYaExistenteException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.Nodo;
import chasqui.model.SolicitudCreacionNodo;
import chasqui.model.Usuario;

public interface NodoService {

	@Transactional
	public void guardarNodo(Nodo nodo);

	@Transactional
	List<Nodo> obtenerNodosDelVendedor(Integer idVendedor) throws VendedorInexistenteException;

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
	 */
	void crearSolicitudDeCreacionNodo(Integer idVendedor, Cliente usuario, String nombre, Direccion direccion,
			String tipo, String barrio, String descripcion)
			throws DireccionesInexistentes, VendedorInexistenteException, ConfiguracionDeVendedorException;
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


}
