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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vividsolutions.jts.geom.Point;

import chasqui.exceptions.DomicilioInexistenteException;
import chasqui.exceptions.EstrategiaInvalidaException;
import chasqui.exceptions.PuntoDeRetiroInexistenteException;
import chasqui.exceptions.TokenInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Nodo;
import chasqui.model.Direccion;
import chasqui.model.EstrategiasDeComercializacion;
import chasqui.model.PreguntaDeConsumo;
import chasqui.model.Vendedor;
import chasqui.model.Zona;
import chasqui.service.rest.request.ConfirmarPedidoRequest;
import chasqui.service.rest.request.DireccionZonaRequest;
import chasqui.service.rest.request.ObtenerPedidosConEstadoRequest;
import chasqui.service.rest.response.ChasquiError;
import chasqui.service.rest.response.DataPortadaResponse;
import chasqui.service.rest.response.NodoAbiertoResponse;
import chasqui.service.rest.response.PreguntaDeConsumoResponse;
import chasqui.service.rest.response.PuntosDeRetiroResponse;
import chasqui.service.rest.response.VendedorResponse;
import chasqui.service.rest.response.ZonaGeoJsonResponse;
import chasqui.service.rest.response.ZonaResponse;
import chasqui.services.interfaces.DireccionService;
import chasqui.services.interfaces.NodoService;
import chasqui.services.interfaces.VendedorService;
import chasqui.services.interfaces.ZonaService;
import chasqui.utils.TokenGenerator;

@Service
@Path("/vendedor")
public class VendedorListener {

	
	@Autowired
	VendedorService vendedorService;
	
	@Autowired
	ZonaService zonaService;
	
	@Autowired
	NodoService nodoService;
	
	@Autowired
	DireccionService direccionService;
	
	@Autowired
	TokenGenerator tokenGenerator;
	
	@GET
	@Path("/all")
	@Produces("application/json")
	public Response obtenerVendedores(){
		try{
			return Response.ok(toResponse(vendedorService.obtenerVendedores()),MediaType.APPLICATION_JSON).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError (e.getMessage())).build();
		}
	}
	
	@GET
	@Path("/nodosAbiertos/{idVendedor : \\d+ }")
	@Produces("application/json")
	public Response obtenerNodosAbiertosDelVendedor(@PathParam("idVendedor")final Integer idVendedor){
		try{
			return Response.ok(toResponseNodoAbierto(nodoService.obtenerNodosAbiertosDelVendedor(idVendedor)),MediaType.APPLICATION_JSON).build();
		}catch(VendedorInexistenteException e){
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build(); 
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}

	@Deprecated
	@GET
	@Path("/zonas/{idVendedor}")
	@Produces("application/json")
	public Response obtenerZonas(@PathParam("idVendedor") Integer idVendedor){
		try{
			return Response.ok(toResponseZona(zonaService.obtenerZonas(idVendedor)),MediaType.APPLICATION_JSON).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError (e.getMessage())).build();
		}
	}
	
	@GET
	@Path("/zonasGeo/{idVendedor}")
	@Produces("application/json")
	public Response obtenerZonasGeo(@PathParam("idVendedor") Integer idVendedor){
		try{
			this.validarEstrategiaActiva("ZN", idVendedor);
			return Response.ok(toResponseZonaGeo(zonaService.obtenerZonas(idVendedor)),MediaType.APPLICATION_JSON).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError (e.getMessage())).build();
		}
	}
	
	@POST
	@Produces("application/json")
	@Path("/obtenerZonaDeDireccion")
	public Response obtenerZonaDeDireccion(@Multipart(value="crearRequest", type="application/json") final String  request){
		try{
			DireccionZonaRequest dirRequest = toDireccionZonaRequest(request);
			Direccion d = direccionService.obtenerDireccionPorId(dirRequest.getIdDireccion());
			if(d==null) {
				throw new DomicilioInexistenteException();
			}
			if(d.getGeoUbicacion() == null) {
				d.crearGeoDireccion(d.getLatitud(), d.getLongitud());
			}
			Zona zona = zonaService.obtenerZonaDePertenenciaDeDireccion(d.getGeoUbicacion(), dirRequest.getIdVendedor());
			return Response.ok(toResponseZona(zona),MediaType.APPLICATION_JSON).build();
		}catch(DomicilioInexistenteException e){
			return Response.status(406).entity(new ChasquiError("Parametros Incorrectos")).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	@GET
	@Path("/zonas/proxima/{idVendedor}")
	@Produces("application/json")
	public Response obtenerProximaZonaDeEntrega(@PathParam("idVendedor") Integer idVendedor){
		try {
		return Response.ok(toResponseZona(zonaService.buscarZonaProxima(idVendedor)),MediaType.APPLICATION_JSON).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError ("No hay zonas proximas disponibles")).build();
		}
	}
	
	private ZonaResponse toResponseZona(Zona z) {
		return new ZonaResponse(z);
	}
	
	private List<ZonaResponse> toResponseZona(List<Zona> obtenerZonas) {
		List<ZonaResponse> response = new ArrayList<ZonaResponse>();
		for(Zona z : obtenerZonas){
			response.add(new ZonaResponse(z));
		}
		return response;
	}
	
	
	@GET
	@Path("/{nombreVendedor}")
	@Produces("application/json")
	public Response obtenerVendedorPor(@PathParam("nombreVendedor")String nombreVendedor){
		try{
			return Response.ok(new VendedorResponse(vendedorService.obtenerVendedorPorNombreCorto(nombreVendedor))).build();
		}catch(VendedorInexistenteException e){
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		}catch(Exception e){			
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	@GET
	@Path("/byToken/{token}")
	@Produces("application/json")
	public Response obtenerVendedorPorToken(@PathParam("token")String token){
		try {
			Integer idVendedor = tokenGenerator.getIdDeVendedorConToken(token);
			return Response.ok(new VendedorResponse(vendedorService.obtenerVendedorPorId(idVendedor))).build();
		} catch (TokenInexistenteException e) {
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		}catch (VendedorInexistenteException e) {
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	@GET
	@Path("/puntosDeRetiro/{nombreVendedor}")
	@Produces("application/json")
	public Response obtenerPuntosDeRetiroDeVendedor(@PathParam("nombreVendedor")String nombreVendedor){
		try{
			return Response.ok(new PuntosDeRetiroResponse(vendedorService.obtenerVendedorPorNombreCorto(nombreVendedor).getPuntosDeRetiroHabilitados())).build();
		}catch(VendedorInexistenteException e){
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		}catch(Exception e){			
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	@GET
	@Path("/puntosDeRetiro/id/{idVendedor}")
	@Produces("application/json")
	public Response obtenerPuntosDeRetiroPorIdDeVendedor(@PathParam("idVendedor")Integer idVendedor){
		try{
			this.validarEstrategiaActiva("PR", idVendedor);
			return Response.ok(new PuntosDeRetiroResponse(vendedorService.obtenerVendedorPorId(idVendedor).getPuntosDeRetiro())).build();
		}catch(VendedorInexistenteException e){
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		}catch(PuntoDeRetiroInexistenteException e){			
			return Response.status(401).build();
		}
		catch(Exception e){			
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	private void validarEstrategiaActiva(String codigo_estrategia, Integer idVendedor) throws Exception {
		EstrategiasDeComercializacion estrategias = vendedorService.obtenerVendedorPorId(idVendedor).getEstrategiasUtilizadas();
		switch(codigo_estrategia) {
			case "PR": if(!estrategias.isPuntoDeEntrega()) {
							throw new PuntoDeRetiroInexistenteException();
						};
					break;
			case "ZN": if(!estrategias.isSeleccionDeDireccionDelUsuario()) {
				throw new EstrategiaInvalidaException();
			};
			break;
			}
	}



	@GET
	@Path("/preguntasDeConsumoIndividual/{nombreVendedor}")
	@Produces("application/json")
	public Response obtenerPreguntasDeConsumoIndividual(@PathParam("nombreVendedor")String nombreVendedor){
		try{
			return Response.ok(this.toResponsePreguntaConsumo(vendedorService.obtenerVendedorPorNombreCorto(nombreVendedor).getPreguntasDePedidosIndividualesHabilitadas())).build();
		}catch(VendedorInexistenteException e){
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		}catch(Exception e){			
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	@GET
	@Path("/preguntasDeConsumoColectivo/{nombreVendedor}")
	@Produces("application/json")
	public Response obtenerPreguntasDeConsumoColectivo(@PathParam("nombreVendedor")String nombreVendedor){
		try{
			return Response.ok(this.toResponsePreguntaConsumo(vendedorService.obtenerVendedorPorNombreCorto(nombreVendedor).getPreguntasDePedidosColectivosHabilitados())).build();
		}catch(VendedorInexistenteException e){
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		}catch(Exception e){			
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	


	@GET
	@Path("/datosPortada/{nombreVendedor}")
	@Produces("application/json")
	public Response obtenerInfoDePortada(@PathParam("nombreVendedor")String nombreVendedor){
		try{
			DataPortadaResponse dr = new DataPortadaResponse(vendedorService.obtenerVendedorPorNombreCorto(nombreVendedor).getDataMultimedia());
			return Response.ok(dr).build();
		}catch(VendedorInexistenteException e){
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		}catch(Exception e){			
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	private DireccionZonaRequest toDireccionZonaRequest(String request) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		return mapper.readValue(request, DireccionZonaRequest.class);
	}
	
	private List<PreguntaDeConsumoResponse> toResponsePreguntaConsumo(List<PreguntaDeConsumo> lista){
		List<PreguntaDeConsumoResponse> pr = new ArrayList<PreguntaDeConsumoResponse>();
		for(PreguntaDeConsumo p : lista){
			pr.add(new PreguntaDeConsumoResponse(p.getNombre(), p.getHabilitada(), p.getOpciones()));
		}
		return pr;
	}

	private List<VendedorResponse> toResponse(List<Vendedor> vendedores) {
		List<VendedorResponse> response = new ArrayList<VendedorResponse>();
		for(Vendedor v : vendedores){
			response.add(new VendedorResponse(v));
		}
		return response;
	}
	
	private List<NodoAbiertoResponse> toResponseNodoAbierto(List<Nodo> nodos) {
		List<NodoAbiertoResponse> response = new ArrayList<NodoAbiertoResponse>();
		for(Nodo nodo : nodos){
			response.add(new NodoAbiertoResponse(nodo));
		}
		return response;
	}
	
	private List<ZonaGeoJsonResponse> toResponseZonaGeo(List<Zona> obtenerZonas) {
		List<ZonaGeoJsonResponse> response = new ArrayList<ZonaGeoJsonResponse>();
		for(Zona z : obtenerZonas){
			response.add(new ZonaGeoJsonResponse(z));
		}
		return response;
	}
}
