package chasqui.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import chasqui.model.CaracteristicaProductor;
import chasqui.model.Fabricante;
import chasqui.model.Vendedor;
import chasqui.services.interfaces.ProductorService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.services.interfaces.VendedorService;

public class FabricanteStartup {
	@Autowired
	VendedorService vendedorService;
	@Autowired
	UsuarioService usuarioService;
	@Autowired
	ProductorService productorService;
	
	
	
	private void syncro() {
		List<Vendedor> vendedores = vendedorService.obtenerVendedores();
		for(Vendedor v: vendedores) {
			mirgrarCampos(productorService.obtenerProductores(v.getId()));
		}
	}
	

	private void mirgrarCampos(List<Fabricante> fb) {
		for(Fabricante fab: fb) {
			if(fab.getCaracteristica() != null) {
				List<CaracteristicaProductor> caracts; 
				if(fab.getCaracteristicas() == null) {
					caracts = new ArrayList<CaracteristicaProductor>();
				}else {
					caracts = (List<CaracteristicaProductor>) fab.getCaracteristicas();
				}
				if(caracts.size() == 0) {
					caracts.add(fab.getCaracteristica());
					fab.setCaracteristicas(caracts);
					productorService.guardar(fab);
				}
			}else {
				fab.setCaracteristicas(new ArrayList<CaracteristicaProductor>());
				productorService.guardar(fab);
			}
		}
	}

}
