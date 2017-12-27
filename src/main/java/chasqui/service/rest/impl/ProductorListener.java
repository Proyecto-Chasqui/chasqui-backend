package chasqui.service.rest.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Fabricante;
import chasqui.service.rest.response.ChasquiError;
import chasqui.service.rest.response.FabricanteResponse;
import chasqui.services.interfaces.ProductorService;

@Service
@Path("/productor")
public class ProductorListener {
	
	
	@Autowired
	ProductorService productorService;
	
	
	
	@GET
	@Path("/all/{idVendedor : \\d+}")
	@Produces("application/json")
	public Response obtenerProductoresDe(@PathParam("idVendedor")final Integer idVendedor){
		try{
			return Response.ok(toResponse(productorService.obtenerProductoresDe(idVendedor)),MediaType.APPLICATION_JSON).build();
		}catch(VendedorInexistenteException e){
			return Response.status(404).entity(new ChasquiError("Vendedor inexistente o el mismo no posee productores definidos")).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}



	private List<FabricanteResponse> toResponse(List<Fabricante> fabricantes) {
		List<FabricanteResponse> fss = new ArrayList<FabricanteResponse>();
		for(Fabricante f : fabricantes){
			fss.add(new FabricanteResponse(f));
		}
		return fss;
	}
	
	

}
