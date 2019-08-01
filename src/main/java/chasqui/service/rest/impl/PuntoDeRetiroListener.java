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
import chasqui.exceptions.EstrategiaInvalidaException;
import chasqui.exceptions.PuntoDeRetiroInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.EstrategiasDeComercializacion;
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
import chasqui.services.interfaces.VendedorService;
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
	@Autowired
	VendedorService vendedorService;
	

	@POST
	@Path("/altaPuntoDeRetiro")
	@Produces("application/json")
	public Response guardarPuntoDeRetiro(
			@Multipart(value = "zonaRequest", type = "application/json") final String prRequest) {
		PuntoDeRetiroRequest request;
		ChasquiZonaStatus statusResponse = new ChasquiZonaStatus();
		try {			
			request = this.toPRRequest(prRequest);
			validarEstrategiaActiva("PR",request.getIdVendedor());
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
		} catch (EstrategiaInvalidaException e) {
			System.out.print(e);
			statusResponse.setCode("epr001");
			statusResponse.setStatus("ERROR");
			return Response.ok(statusResponse).build();
		} catch (Exception e) {
			System.out.print(e);
			statusResponse.setCode("");
			statusResponse.setStatus("ERROR");
			return Response.ok(statusResponse).build();
		}
	}
	
	private void validarEstrategiaActiva(String codigo_estrategia, Integer idVendedor) throws VendedorInexistenteException, EstrategiaInvalidaException {
		EstrategiasDeComercializacion estrategias = vendedorService.obtenerVendedorPorId(idVendedor).getEstrategiasUtilizadas();
		switch(codigo_estrategia) {
			case "PR": if(!estrategias.isPuntoDeEntrega()) {
							throw new EstrategiaInvalidaException();
						};
					break;
			}
	}
	
	@POST
	@Path("/eliminarPuntoDeRetiro")
	@Produces("application/json")
	public Response eliminarPuntoDeRetiro(
			@Multipart(value = "grupoRequest", type = "application/json") final String eliminarZonaRequest) {
		EliminarZonaRequest request;
		ChasquiZonaStatus statusResponse = new ChasquiZonaStatus();
		try {
			request = this.toEliminarZonaRequest(eliminarZonaRequest);
			validarEstrategiaActiva("PR", request.getIdVendedor());
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
		}catch (EstrategiaInvalidaException e){
			statusResponse.setStatus("ERROR");
			statusResponse.setCode("epr001");
			return Response.ok(statusResponse).build();
		} catch (IOException | VendedorInexistenteException e) {
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
	
	private PuntoDeRetiroRequest toPRRequest(String req) throws IOException {
		PuntoDeRetiroRequest request = new PuntoDeRetiroRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(req, PuntoDeRetiroRequest.class);
		return request;
	}

}
