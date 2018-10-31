package chasqui.service.rest.impl;

import java.io.IOException;
import java.lang.reflect.Field;
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
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chasqui.exceptions.ErrorZona;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Zona;
import chasqui.service.rest.request.EliminarZonaRequest;
import chasqui.service.rest.request.ErrorCodesRequest;
import chasqui.service.rest.request.GrupoRequest;
import chasqui.service.rest.request.ZonaRequest;
import chasqui.service.rest.response.ChasquiError;
import chasqui.service.rest.response.ZonaGeoJsonResponse;
import chasqui.services.interfaces.GeoService;
import chasqui.services.interfaces.ZonaService;
import chasqui.utils.ErrorCodes;
import chasqui.utils.TokenGenerator;
import chasqui.view.composer.Constantes;

@Service
@Path("/zona")
public class ZonaListener {
	@Autowired
	ZonaService zonaService;
	@Autowired
	GeoService geoService;
	@Autowired
	TokenGenerator tokenGenerator;
	
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
	@Path("/getErrorCodes")
	@Produces("application/json")
	public Response codigosDeError(
			@Multipart(value = "errorCodesRequest", type = "application/json") final String errorCodesRequest) {
		ErrorCodesRequest request;
		try {
			request = this.ErrorCodesRequest(errorCodesRequest);
			if(tokenGenerator.tokenActivo(request.getToken())){
				return Response.ok(new ErrorCodes()).build();
			}else {
				return Response.ok("{\"STATUS\":\"ERROR\"}").build();
			}

		} catch (IOException e) {
			return Response.ok(new ZonaGeoJsonResponse()).build();
		} catch (ErrorZona e) {
			return Response.ok(new ZonaGeoJsonResponse()).build();
		} catch (Exception e) {
			return Response.ok(new ZonaGeoJsonResponse()).build();
		}
	}
	
	private ErrorCodesRequest ErrorCodesRequest(String errorCodesRequest) throws JsonParseException, JsonMappingException, IOException {
		ErrorCodesRequest request = new ErrorCodesRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(errorCodesRequest, ErrorCodesRequest.class);
		return request;
	}

	@POST
	@Path("/altaZona")
	@Produces("application/json")
	public Response guardarZona(
			@Multipart(value = "zonaRequest", type = "application/json") final String zonaRequest) {
		ZonaRequest request;
		try {
			request = this.toZonaRequest(zonaRequest);
			DateTime d = request.getFechaCierre().plusDays(1);
			request.setFechaCierre(d);
			geoService.crearGuardarZona(request);
			ZonaGeoJsonResponse returnZona = toEchoZona(zonaService.obtenerZonaPorId(request.getId()));
			return Response.ok(returnZona).build();
		} catch (IOException e) {
			return Response.ok(new ZonaGeoJsonResponse()).build();
		} catch (ErrorZona e) {
			return Response.ok(new ZonaGeoJsonResponse()).build();
		} catch (Exception e) {
			return Response.ok(new ZonaGeoJsonResponse()).build();
		}
	}
	
	@POST
	@Path("/eliminarZona")
	@Produces("application/json")
	public Response eliminarZona(
			@Multipart(value = "grupoRequest", type = "application/json") final String eliminarZonaRequest) {
		EliminarZonaRequest request;
		try {
			request = this.toEliminarZonaRequest(eliminarZonaRequest);
			//sacar el ! cuando se terminen las pruebas de codigos de error
			if(!tokenGenerator.tokenActivo(request.getToken())) {
				geoService.eliminarZona(request);
				return Response.ok("{\"STATUS\":\"OK\"}").build();
			}else {
				return Response.ok("{\"STATUS\":\"ERROR\", \"CODE\":\"ez001\"}").build();
			}
		} catch (IOException e) {
			return Response.ok("{\"STATUS\":\"ERROR\", \"CODE\":\""+e+"\"}").build();
		} 
	}
	
	private EliminarZonaRequest toEliminarZonaRequest(String eliminarZonaRequest) throws JsonParseException, JsonMappingException, IOException {
		EliminarZonaRequest request = new EliminarZonaRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(eliminarZonaRequest, EliminarZonaRequest.class);
		return request;
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
	
	private ZonaGeoJsonResponse toEchoZona(Zona zona) {
		return new ZonaGeoJsonResponse(zona);
	}
}
