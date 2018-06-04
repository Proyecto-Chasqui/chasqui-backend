package chasqui.service.rest.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Zona;
import chasqui.service.rest.request.GrupoRequest;
import chasqui.service.rest.request.ZonaRequest;
import chasqui.service.rest.response.ChasquiError;
import chasqui.service.rest.response.ZonaGeoJsonResponse;
import chasqui.services.interfaces.ZonaService;

@Service
@Path("/zona")
public class ZonaListener {
	@Autowired
	ZonaService zonaService;
	
	@GET
	@Path("/all/{idVendedor}")
	@Produces("application/json")
	public Response obtenerVendedores(@PathParam("idVendedor") Integer idVendedor){
		try{
			return Response.ok(toResponseZona(zonaService.buscarZonasBy(idVendedor)),MediaType.APPLICATION_JSON).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError (e.getMessage())).build();
		}
	}
	
	@POST
	@Path("/altaZona")
	@Produces("application/json")
	public Response guardarZona(
			@Multipart(value = "grupoRequest", type = "application/json") final String zonaRequest) {
		ZonaRequest request;
		try {
			request = this.toZonaRequest(zonaRequest);
			//crear el objeto zona segun los casos necesarios.
			return Response.ok(request).build();
		} catch (IOException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} 
	}
	
	private ZonaRequest toZonaRequest(String req) throws IOException {
		ZonaRequest request = new ZonaRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(req, ZonaRequest.class);
		return request;
	}

	
	
	private List<ZonaGeoJsonResponse> toResponseZona(List<Zona> obtenerZonas) {
		List<ZonaGeoJsonResponse> response = new ArrayList<ZonaGeoJsonResponse>();
		for(Zona z : obtenerZonas){
			response.add(new ZonaGeoJsonResponse(z));
		}
		return response;
	}
}
