package chasqui.dao;

import java.util.List;

import chasqui.dtos.queries.NodoQueryDTO;
import chasqui.model.Nodo;
import chasqui.model_lite.NodoLite;

public interface NodoDAO {
	
    public void guardarNodo(Nodo nodo);

    public List<Nodo> obtenerNodosDelVendedor(Integer idVendedor);

    public List<Nodo> obtenerNodos(NodoQueryDTO query);
    public Long countNodos(NodoQueryDTO query);

    public List<NodoLite> obtenerNodosLite(NodoQueryDTO query);
    
    public List<Nodo> obtenerNodosDelCliente(Integer idVendedor, String email);
    
    public Nodo obtenerNodoPorAlias(final String alias) ;
 
    public Nodo obtenerNodoPorId(final Integer idNodo);
    
    public void eliminarNodo(Integer idNodo) ;
    
    public void aprobarNodo(Integer id) ;

	void guardarNodos(List<Nodo> nodos);
}
