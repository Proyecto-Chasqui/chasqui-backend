package chasqui.dao;

import java.util.List;

import chasqui.model.Nodo;

public interface NodoDAO {
	
    public void guardarNodo(Nodo nodo);

    public List<Nodo> obtenerNodosDelVendedor(Integer idVendedor);
    
    public List<Nodo> obtenerNodosDelCliente(Integer idVendedor, String email);
    
    public Nodo obtenerNodoPorAlias(final String alias) ;
 
    public Nodo obtenerNodoPorId(final Integer idNodo);
    
    public void eliminarNodo(Integer idNodo) ;
    
    public void aprobarNodo(Integer id) ;
}
