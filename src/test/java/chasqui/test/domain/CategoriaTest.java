package chasqui.test.domain;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import chasqui.model.Categoria;
import chasqui.model.Producto;

public class CategoriaTest {

	Categoria categoria;
	Categoria categoriaEliminar;
	Producto p;
	
	
	@Before
	public void setUp() throws Exception {
		categoria = new Categoria();
		categoriaEliminar = new Categoria();
		categoria.setProductos(new ArrayList<Producto>());
		categoriaEliminar.setProductos(new ArrayList<Producto>());
		p = new Producto();
		categoriaEliminar.agregarProducto(p);
	}

	@Test
	public void testAgregarProducto() {
		categoria.agregarProducto(p);
		assertEquals(categoria.getProductos().size(),1);
	}
	
	@Test
	public void testEliminarProducto(){
		assertEquals(categoriaEliminar.getProductos().size(),1);
		categoriaEliminar.eliminarProducto(p);		
		assertEquals(categoriaEliminar.getProductos().size(),0);
	}
	

}
