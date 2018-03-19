package chasqui.service.rest.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chasqui.model.Vendedor;
import chasqui.service.rest.response.ChasquiError;
import chasqui.service.rest.response.VendedorResponse;
import chasqui.services.interfaces.CatalogoService;

@Service
@Path("/catalogo")
public class CatalogoListener {
	@Autowired
	CatalogoService service;
	@Deprecated
	@GET
	@Produces("application/json")
	public Response obtenerCatalogoPorURL(){
		try{
			String url="pds.proyectochasqui.com";
			return Response.ok(toResponse(service.obtenerVendedor(url)),MediaType.APPLICATION_JSON).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError (e.getMessage())).build();
		}
	}

	private List<VendedorResponse> toResponse(List<Vendedor> vendedores) {
		List<VendedorResponse> response = new ArrayList<VendedorResponse>();
		for(Vendedor v : vendedores){
			response.add(new VendedorResponse(v));
		}
		return response;
	}
}
