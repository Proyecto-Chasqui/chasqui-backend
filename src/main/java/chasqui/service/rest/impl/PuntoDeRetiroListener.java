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
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chasqui.exceptions.ErrorZona;
import chasqui.model.PuntoDeRetiro;
import chasqui.model.Zona;
import chasqui.service.rest.request.EliminarZonaRequest;
import chasqui.service.rest.request.ErrorCodesRequest;
import chasqui.service.rest.request.PuntoDeRetiroRequest;
import chasqui.service.rest.request.ZonaRequest;
import chasqui.service.rest.response.ChasquiError;
import chasqui.service.rest.response.ChasquiZonaStatus;
import chasqui.service.rest.response.PuntoDeRetiroADMResponse;
import chasqui.service.rest.response.PuntoDeRetiroResponse;
import chasqui.service.rest.response.ZonaGeoJsonResponse;
import chasqui.services.interfaces.GeoService;
import chasqui.services.interfaces.PuntoDeRetiroService;
import chasqui.services.interfaces.ZonaService;
import chasqui.utils.ErrorCodes;
import chasqui.utils.TokenGenerator;

@Service
@Path("/puntosDeRetiroAdmin/")
public class PuntoDeRetiroListener {
	@Autowired
	PuntoDeRetiroService puntoDeRetiroService;
	@Autowired
	GeoService geoService;
	@Autowired
	TokenGenerator tokenGenerator;
	
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
	@Path("/altaPuntoDeRetiro")
	@Produces("application/json")
	public Response guardarZona(
			@Multipart(value = "zonaRequest", type = "application/json") final String prRequest) {
		PuntoDeRetiroRequest request;
		ChasquiZonaStatus statusResponse = new ChasquiZonaStatus();
		try {
			request = this.toPRRequest(prRequest);
			geoService.crearGuardarPR(request);
			PuntoDeRetiroADMResponse returnPR = new PuntoDeRetiroADMResponse(puntoDeRetiroService.obtenerPuntoDeRetiroConId(request.getId()),"OK");
			return Response.ok(returnPR).build();
		} catch (IOException e) {
			System.out.print(e);
			statusResponse.setStatus("ERROR");
			statusResponse.setCode("");
			return Response.ok(statusResponse).build();
		} catch (ErrorZona e) {
			System.out.print(e);
			statusResponse.setCode(e.getMessage());
			statusResponse.setStatus("ERROR");
			return Response.ok(statusResponse).build();
		} catch (Exception e) {
			System.out.print(e);
			statusResponse.setCode("");
			statusResponse.setStatus("ERROR");
			return Response.ok(statusResponse).build();
		}
	}
	
	@POST
	@Path("/eliminarPuntoDeRetiro")
	@Produces("application/json")
	public Response eliminarZona(
			@Multipart(value = "grupoRequest", type = "application/json") final String eliminarZonaRequest) {
		EliminarZonaRequest request;
		ChasquiZonaStatus statusResponse = new ChasquiZonaStatus();
		try {
			request = this.toEliminarZonaRequest(eliminarZonaRequest);
			if(tokenGenerator.tokenActivo(request.getToken())) {
				geoService.eliminarPuntoDeRetiro(request);
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
	
	private PuntoDeRetiroRequest toPRRequest(String req) throws IOException {
		PuntoDeRetiroRequest request = new PuntoDeRetiroRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(req, PuntoDeRetiroRequest.class);
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
