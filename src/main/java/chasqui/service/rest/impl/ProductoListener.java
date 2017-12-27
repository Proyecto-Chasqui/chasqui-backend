package chasqui.service.rest.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
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

import chasqui.exceptions.CaracteristicaInexistenteException;
import chasqui.exceptions.ProductoInexistenteException;
import chasqui.exceptions.RequestIncorrectoException;
import chasqui.model.Caracteristica;
import chasqui.model.Imagen;
import chasqui.model.Variante;
import chasqui.service.rest.request.ByCategoriaRequest;
import chasqui.service.rest.request.ByMedallaRequest;
import chasqui.service.rest.request.ByMultiplesFiltros;
import chasqui.service.rest.request.ByProductorRequest;
import chasqui.service.rest.request.ByQueryRequest;
import chasqui.service.rest.request.MedallaRequest;
import chasqui.service.rest.request.SinFiltroRequest;
import chasqui.service.rest.response.CaracteristicaResponse;
import chasqui.service.rest.response.ChasquiError;
import chasqui.service.rest.response.ImagenResponse;
import chasqui.service.rest.response.ProductoResponse;
import chasqui.services.interfaces.ProductoService;

@Service
@Path("/producto")
public class ProductoListener {

	@Autowired
	ProductoService productoService;

	@Deprecated
	@POST
	@Path("/byCategoria")
	@Produces("application/json")
	public Response obtenerTodosLosProductosByCategoria(@Multipart(value="productoRequest", type="application/json")final String productoRequest){
		try{
			ByCategoriaRequest request = toRequest(productoRequest);	
			return toResponse(productoService.obtenerVariantesPorCategoria(request)
					,request.getPagina(),request.getCantItems(),request.getPrecio(),productoService.totalVariantesPorCategoria(request));
		}catch(IOException | RequestIncorrectoException e){
			return Response.status(406).entity(new ChasquiError("Parametros incorrectos")).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}		
	}

	@Deprecated
	@POST
	@Path("/byProductor")
	@Produces("application/json")
	public Response obtenerTodosLosProductosByProductor(@Multipart(value="productoRequest", type="application/json")final String productoRequest){
		try{
			ByProductorRequest request = toByProductorRequest(productoRequest);	
			return toResponse(productoService.obtenerVariantesPorProductor(request),request.getPagina(),request.getCantItems(),request.getPrecio(),
							 productoService.totalVariantesPorProductor(request));
		}catch(IOException | RequestIncorrectoException e){
			return Response.status(406).entity(new ChasquiError("Parametros incorrectos")).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	@Deprecated
	@POST
	@Path("/byMedalla")
	@Produces("application/json")
	public Response obtenerTodosLosProductosByMedalla(@Multipart(value="productoRequest", type="application/json")final String productoRequest){
		try{
			ByMedallaRequest request = toByMedallaRequest(productoRequest);	
			return toResponse(productoService.obtenerVariantesPorMedalla(request),request.getPagina(),request.getCantItems(),request.getPrecio(),
					productoService.totalVariantesPorMedalla(request));
		}catch(IOException | RequestIncorrectoException e){
			return Response.status(406).entity(new ChasquiError("Parametros incorrectos "+e.getMessage())).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	
	@POST
	@Path("/byMedallaProductor")
	@Produces("application/json")
	public Response obtenerTodosLosProductosPorMedalla(@Multipart(value="medallaRequest", type="application/json")final String medallaRequest){
		try{
			ByMedallaRequest request = toByMedallaRequest(medallaRequest);	
			return toResponse(productoService.obtenerProductosConMedallaEnProductor(request.getIdMedalla()));
		}catch(IOException e){
			return Response.status(406).entity(new ChasquiError("Parametros incorrectos "+e.getMessage())).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	@POST
	@Path("/productosByMultiplesFiltros")
	@Produces("application/json")
	public Response obtenerProductosConMultiplesFiltros(@Multipart(value="productoRequest", type="application/json")final String productoRequest){
		try{
			ByMultiplesFiltros request = toByMultiplesFiltros(productoRequest);	
			return toResponse(productoService.obtenerVariantesPorMultiplesFiltros(request.getIdVendedor(),
					request.getIdCategoria(),request.getIdMedalla(), request.getIdProductor(), request.getIdMedallaProductor(), request.getQuery(), request.getPagina(), 
					request.getCantItems()),request.getPagina(),request.getCantItems(),request.getPrecio(),
					productoService.totalVariantesBajoMultiplesFiltros(request.getIdCategoria(), request.getIdMedalla(), request.getIdProductor())
					);
		}catch(IOException e){
			return Response.status(406).entity(new ChasquiError("Parametros incorrectos "+e.getMessage())).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	@Deprecated
	@POST
	@Path("/sinFiltro")
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response obtenerTodosLosProductosSinFiltroParticular(SinFiltroRequest request){
		try {
			return toResponse(productoService.obtenerVariantesSinFiltro(request),request.getPagina(),request.getCantItems(),request.getPrecio(),productoService.totalVariantesSinFiltro(request));
		} catch (RequestIncorrectoException e) {
			return Response.status(406).entity(new ChasquiError("Parametros incorrectos "+e.getMessage())).build();
		}
	}
	
	@GET
	@Path("/medalla/{idMedalla : \\d+}")
	@Produces("application/json")
	public Response obtenerMedalla(@PathParam(value="idMedalla")final Integer idMedalla){
		try{
			return toResponseMedalla(productoService.obtenerMedalla(idMedalla));
		}catch(CaracteristicaInexistenteException e){
			return Response.status(404).entity(new ChasquiError(e.getMessage())).build();
		}
		catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	private Response toResponseMedalla(Caracteristica medalla) {
		return Response.ok(new CaracteristicaResponse(medalla) , MediaType.APPLICATION_JSON).build();
	}


	@GET
	@Path("/images/{idVariedad}")
	@Produces("application/json")
	public Response obtenerImagenesDe(@PathParam(value="idVariedad")Integer idVariedad){
		try{
			return Response.ok(toResponseImagenes(productoService.obtenerImagenesDe(idVariedad)),MediaType.APPLICATION_JSON).build();
		}catch (ProductoInexistenteException e){
			return Response.status(404).entity(new ChasquiError("El producto no existe")).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	@POST
	@Path("/byQuery")
	@Produces("application/json")
	public Response obtenerProductosPorDescripcionONombre(@Multipart(value="productoRequest", type="application/json") String productoRequest){
		try{
			ByQueryRequest request =toByQueryRequest(productoRequest);
			return toResponse(productoService.obtenerVariantesPorNombreODescripcion(request),request.getPagina(),request.getCantItems(),request.getPrecio(),
					productoService.totalVariantesPorNombreODescripcion(request));			
		}catch(RequestIncorrectoException | IOException e){
			return Response.status(406).entity(new ChasquiError("Parametros Incorrectos")).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}

	
	@GET
	@Path("/destacados/{idVendedor}")
	@Produces("application/json")
	public Response obtenerDestacados(@PathParam(value="idVendedor")final Integer idVendedor ){
		try{
			return toResponse(productoService.obtenerVariantesDestacadas(idVendedor));
		}catch(CaracteristicaInexistenteException e){
			return Response.status(404).entity(new ChasquiError(e.getMessage())).build();
		}
		catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}


	private List<ImagenResponse> toResponseImagenes(List<Imagen> obtenerImagenesDe) {
		List<ImagenResponse> resultado = new ArrayList<ImagenResponse>();
		for(Imagen i : obtenerImagenesDe){
			resultado.add(new ImagenResponse(i));
		}
		return resultado;
	}
	
	
	private ByQueryRequest toByQueryRequest(String productoRequest) throws IOException{
		ByQueryRequest prodRequest = new ByQueryRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		prodRequest = mapper.readValue(productoRequest, ByQueryRequest.class);
		return prodRequest;
	}

	private ByMultiplesFiltros toByMultiplesFiltros(String productoRequest) throws IOException {
		ByMultiplesFiltros prodRequest = new ByMultiplesFiltros();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		prodRequest = mapper.readValue(productoRequest, ByMultiplesFiltros.class);
		return prodRequest;
	}
	
	private ByProductorRequest toByProductorRequest(String productoRequest) throws IOException {
		ByProductorRequest prodRequest = new ByProductorRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		prodRequest = mapper.readValue(productoRequest, ByProductorRequest.class);
		return prodRequest;
	}
	
	private ByMedallaRequest toByMedallaRequest(String productoRequest) throws IOException {
		ByMedallaRequest prodRequest = new ByMedallaRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		prodRequest = mapper.readValue(productoRequest, ByMedallaRequest.class);
		return prodRequest;
	}
	
	
	private MedallaRequest toMedallaRequest(String medallaRequest) throws IOException {
		MedallaRequest request = new MedallaRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(medallaRequest, MedallaRequest.class);
		return request;
	}

	private Response toResponse(List<Variante> variantes, Integer pagina, Integer items,String precio, Long total) {
		return Response.ok(new ProductoResponse(variantes,pagina,items, precio,total), MediaType.APPLICATION_JSON).build();
	}

	private Response toResponse(List<Variante> variantes) {
		return Response.ok(new ProductoResponse(variantes), MediaType.APPLICATION_JSON).build();
	}
	
	private ByCategoriaRequest toRequest(String request) throws IOException{
		ByCategoriaRequest prodRequest = new ByCategoriaRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		prodRequest = mapper.readValue(request, ByCategoriaRequest.class);
		return prodRequest;
	}
	
	
	
	
}
