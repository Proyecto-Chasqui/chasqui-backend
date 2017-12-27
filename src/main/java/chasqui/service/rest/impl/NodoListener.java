package chasqui.service.rest.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chasqui.exceptions.RequestIncorrectoException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Nodo;
import chasqui.service.rest.response.ChasquiError;
import chasqui.service.rest.response.NodoResponse;
import chasqui.services.interfaces.NodoService;
import chasqui.services.interfaces.VendedorService;

@Service
@Path("/nodo")
public class NodoListener {

	@Autowired
	NodoService nodoService;
	@Autowired
	VendedorService vendedorService;
	
	@GET
	@Path("/all/{idVendedor : \\d+ }")
	@Produces("application/json")
	//TODO Deberian usarse el emailUsuario y el token en alguna parte, donde?
	public Response obtenerNodosDelVendedor(@PathParam("idVendedor")final Integer idVendedor){
		try{
			return Response.ok(toResponse(nodoService.obtenerNodosDelVendedor(idVendedor)),MediaType.APPLICATION_JSON).build();
		}catch(VendedorInexistenteException e){
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build(); 
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}

	private List<NodoResponse> toResponse(List<Nodo> nodos) {
		List<NodoResponse> response = new ArrayList<NodoResponse>();
		//TODO seguir desde aca la prox. Solucionar Error serializing the response, please check the server logs, response class : ArrayList.
		for(Nodo nodo : nodos){
			response.add(new NodoResponse(nodo));
		}
		return response;
	}
}
