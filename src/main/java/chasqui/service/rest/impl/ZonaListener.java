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
import chasqui.exceptions.EstrategiaInvalidaException;
import chasqui.exceptions.PuntoDeRetiroInexistenteException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.EstrategiasDeComercializacion;
import chasqui.model.Zona;
import chasqui.service.rest.request.EliminarZonaRequest;
import chasqui.service.rest.request.ErrorCodesRequest;
import chasqui.service.rest.request.GrupoRequest;
import chasqui.service.rest.request.ZonaRequest;
import chasqui.service.rest.response.ChasquiError;
import chasqui.service.rest.response.ChasquiZonaStatus;
import chasqui.service.rest.response.ZonaGeoJsonResponse;
import chasqui.services.interfaces.GeoService;
import chasqui.services.interfaces.VendedorService;
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
	@Autowired
	VendedorService vendedorService;
	
	@GET
	@Path("/all/{idVendedor}")
	@Produces("application/json")
	public Response obtenerVendedores(@PathParam("idVendedor") Integer idVendedor){
		try{
			validarEstrategiaActiva("ZN",idVendedor);
			return Response.ok(toResponseZona(zonaService.buscarZonasBy(idVendedor)),MediaType.APPLICATION_JSON).build();
		}catch(VendedorInexistenteException e){
			return Response.status(500).entity(new ChasquiError (e.getMessage())).build();
		}catch(EstrategiaInvalidaException e){
			return Response.status(401).build();
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
		ChasquiZonaStatus statusResponse = new ChasquiZonaStatus();
		try {
			request = this.ErrorCodesRequest(errorCodesRequest);
			if(tokenGenerator.tokenActivo(request.getToken())){
				return Response.ok(new ErrorCodes()).build();
			}else {
				statusResponse.setStatus("ERROR");
				statusResponse.setCode("ez001");
				return Response.ok(statusResponse).build();
			}

		} catch (Exception e) {
			statusResponse.setStatus("ERROR");
			statusResponse.setCode("");
			return Response.ok(statusResponse).build();
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
		ChasquiZonaStatus statusResponse = new ChasquiZonaStatus();
		try {
			request = this.toZonaRequest(zonaRequest);
			validarEstrategiaActiva("ZN",request.getIdVendedor());
			DateTime d = request.getFechaCierre().plusDays(1);
			request.setFechaCierre(d);
			geoService.crearGuardarZona(request);
			ZonaGeoJsonResponse returnZona = toEchoZona(zonaService.obtenerZonaPorId(request.getId()));
			return Response.ok(returnZona).build();
		} catch (IOException e) {
			statusResponse.setStatus("ERROR");
			statusResponse.setCode("");
			return Response.ok(statusResponse).build();
		} catch (EstrategiaInvalidaException e) {
			statusResponse.setCode("ez009");
			statusResponse.setStatus("ERROR");
			return Response.ok(statusResponse).build();
		}catch (ErrorZona e) {
			statusResponse.setCode(e.getMessage());
			statusResponse.setStatus("ERROR");
			return Response.ok(statusResponse).build();
		} catch (Exception e) {
			statusResponse.setCode("");
			statusResponse.setStatus("ERROR");
			return Response.ok(statusResponse).build();
		}
	}
	
	@POST
	@Path("/eliminarZona")
	@Produces("application/json")
	public Response eliminarZona(
			@Multipart(value = "grupoRequest", type = "application/json") final String eliminarZonaRequest) {
		EliminarZonaRequest request;
		ChasquiZonaStatus statusResponse = new ChasquiZonaStatus();
		try {
			request = this.toEliminarZonaRequest(eliminarZonaRequest);
			validarEstrategiaActiva("ZN",request.getIdVendedor());
			if(tokenGenerator.tokenActivo(request.getToken())) {
				geoService.eliminarZona(request);
				statusResponse.setStatus("OK");
				statusResponse.setCode("");
				return Response.ok(statusResponse).build();
			}else {				
				statusResponse.setStatus("ERROR");
				statusResponse.setCode("ez001");
				return Response.ok(statusResponse).build();
			}
		} catch (IOException e) {
			statusResponse.setStatus("ERROR");
			statusResponse.setCode("");
			return Response.ok(statusResponse).build();
		} catch (VendedorInexistenteException e) {
			statusResponse.setStatus("ERROR");
			statusResponse.setCode("ez004");
			return Response.ok(statusResponse).build();
		} catch (EstrategiaInvalidaException e) {
			statusResponse.setStatus("ERROR");
			statusResponse.setCode("ez009");
			return Response.ok(statusResponse).build();
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
	
	private void validarEstrategiaActiva(String codigo_estrategia, Integer idVendedor) throws VendedorInexistenteException, EstrategiaInvalidaException {
		EstrategiasDeComercializacion estrategias = vendedorService.obtenerVendedorPorId(idVendedor).getEstrategiasUtilizadas();
		switch(codigo_estrategia) {
			case "ZN": if(!estrategias.isSeleccionDeDireccionDelUsuario()) {
							throw new EstrategiaInvalidaException();
						};
					break;
			}
	}
}
