package chasqui.service.rest.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import chasqui.exceptions.DomicilioInexistenteException;
import chasqui.exceptions.PedidoInexistenteException;
import chasqui.exceptions.PedidoVigenteException;
import chasqui.exceptions.ProductoInexistenteException;
import chasqui.exceptions.RequestIncorrectoException;
import chasqui.model.Pedido;
import chasqui.service.rest.request.AgregarQuitarProductoAPedidoRequest;
import chasqui.service.rest.request.ConfirmarPedidoRequest;
import chasqui.service.rest.request.ConfirmarPedidoSinDireccionRequest;
import chasqui.service.rest.request.CrearPedidoRequest;
import chasqui.service.rest.request.ObtenerPedidosConEstadoRequest;
import chasqui.service.rest.response.ChasquiError;
import chasqui.service.rest.response.PedidoResponse;
import chasqui.services.interfaces.GrupoService;
import chasqui.services.interfaces.PedidoService;
import chasqui.services.interfaces.ProductoService;
import chasqui.services.interfaces.UsuarioService;

@Service
@Path("/pedido")
public class PedidoListener {
	
	@Autowired
	UsuarioService usuarioService;
	@Autowired
	ProductoService productoService;
	
	@Autowired
	PedidoService pedidoService;
	
	@Autowired
	private GrupoService grupoService;
	

	@POST
	@Produces("application/json")
	@Path("/individual/confirmar")
	public Response confirmarPedido(@Multipart(value="crearRequest", type="application/json") final String  request){
		try{
			String email = obtenerEmailDeContextoDeSeguridad();
			pedidoService.confirmarPedido(email,toConfirmarPedidoRequest(request));
			return Response.ok().build();
		}catch(DomicilioInexistenteException e){
			return Response.status(406).entity(new ChasquiError("Parametros Incorrectos")).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}

	@POST
	@Produces("application/json")
	@Path("/individualEnGrupo/confirmar")//TODO este servicio debe moverse a GRUPOLISTENER
	public Response confirmarPedidoEnGrupo(@Multipart(value="crearRequest", type="application/json") final String  request){
		try{
			String email = obtenerEmailDeContextoDeSeguridad();
			grupoService.confirmarPedidoIndividualEnGCC(email,toConfirmarPedidoSinDireccionRequest(request));
			return Response.ok().build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}

	@POST
	@Produces("application/json")
	@Path("/individual")
	public Response crearPedidoIndividualParaUsuario(@Multipart(value="crearRequest", type="application/json") final String crearRequest){
		String mail = obtenerEmailDeContextoDeSeguridad();
		try{
			Integer idVendedor = toCrearPedidoRequest(crearRequest).getIdVendedor();
			pedidoService.crearPedidoIndividualPara(mail,idVendedor);
			return Response.status(201).build();
		}catch(PedidoVigenteException e){
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	@GET
	@Produces("application/json")
	@Path("/individual/{idVendedor : \\d+}")
	public Response obtenerPedidoActual(@PathParam("idVendedor")Integer idVendedor){
		String mail =  obtenerEmailDeContextoDeSeguridad();
		try{
			return Response.ok(toResponse(pedidoService.obtenerPedidoActualDe(mail,idVendedor)),MediaType.APPLICATION_JSON).build();
		}catch(PedidoInexistenteException e){
			return Response.status(404).entity(new ChasquiError(e.getMessage())).build();
		}
		catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}	
	
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/individual/vigentes")
	public Response obtenerPedidosVigentes(){
		String mail =  obtenerEmailDeContextoDeSeguridad();
		try{
			return Response.ok(toListResponse(pedidoService.obtenerPedidosVigentesEnTodosLosCatalogosPara(mail)),MediaType.APPLICATION_JSON).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}

	@PUT
	@Produces("application/json")
	@Path("/individual/agregar-producto")
	public Response agregarProductoAPedido(@Multipart(value="agregarRequest", type="application/json")final String agregarRequest){
		try{
			AgregarQuitarProductoAPedidoRequest request = toAgregarPedidoRequest(agregarRequest);
			String email = obtenerEmailDeContextoDeSeguridad();
			pedidoService.agregarProductosAPedido(request,email);
			return Response.ok().build();
		}catch(IOException | RequestIncorrectoException e ){
			return Response.status(406).entity(new ChasquiError("Parametros Incorrectos")).build();
		}catch(PedidoVigenteException | ProductoInexistenteException e){
			return Response.status(404).entity(new ChasquiError(e.getMessage())).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	
	@PUT
	@Produces("application/json")
	@Path("/individual/eliminar-producto")
	public Response eliminarProductoDelPedido(@Multipart(value="eliminarRequest", type="application/json") final String eliminarRequest){
		try{
			AgregarQuitarProductoAPedidoRequest request = toAgregarPedidoRequest(eliminarRequest);
			String email = obtenerEmailDeContextoDeSeguridad();
			pedidoService.eliminarProductoDePedido(request,email);
			return Response.ok().build();
		}catch(IOException | RequestIncorrectoException e){
			return Response.status(406).entity(new ChasquiError("Parametros Incorrectos")).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}		
	}
	
	
	@DELETE
	@Produces("application/json")
	@Path("/individual/{idPedido : \\d+}")
	public Response eliminarPedido(@PathParam("idPedido")Integer idPedido){
		try{
			String email = obtenerEmailDeContextoDeSeguridad();
			pedidoService.cancelarPedidoPara(email,idPedido);
			return Response.ok().build();			
		}catch(RequestIncorrectoException e){
			return Response.status(406).entity(new ChasquiError("Parametros Incorrectos")).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}	
	}
	
	@POST
	@Produces("application/json")
	@Path("/conEstados")
	public Response obtenerPedidosConEstado(@Multipart(value="obtenerPedidosConEstadoRequest", type="application/json") final String obtenerPedidosConEstadoRequest) throws JsonParseException, JsonMappingException, IOException{
		ObtenerPedidosConEstadoRequest request = toObtenerPedidosConEstadoRequest(obtenerPedidosConEstadoRequest);
		String mail =  obtenerEmailDeContextoDeSeguridad();
		try{
			return Response.ok(toListResponse(pedidoService.obtenerPedidosConEstados(mail,request.getIdVendedor(), request.getEstados())),MediaType.APPLICATION_JSON).build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}



	private AgregarQuitarProductoAPedidoRequest toAgregarPedidoRequest(String agregarRequest) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		return mapper.readValue(agregarRequest, AgregarQuitarProductoAPedidoRequest.class);
	}

	private PedidoResponse toResponse(Pedido pedido) {
		return new PedidoResponse( pedido);
	}
	private String obtenerEmailDeContextoDeSeguridad(){
		return 	SecurityContextHolder.getContext().getAuthentication().getName();
	}
	
	
	private List<PedidoResponse> toListResponse(List<Pedido> obtenerPedidosVigentesDe) {
		List<PedidoResponse> resultado = new ArrayList<PedidoResponse>();
		for(Pedido p : obtenerPedidosVigentesDe){
			resultado.add(new PedidoResponse(p));
		}
		return resultado;
	}

	private ObtenerPedidosConEstadoRequest toObtenerPedidosConEstadoRequest(String obtenerPedidosConEstadoRequest) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		return mapper.readValue(obtenerPedidosConEstadoRequest, ObtenerPedidosConEstadoRequest.class);
	}
	
	private CrearPedidoRequest toCrearPedidoRequest (String crearRequest) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		return mapper.readValue(crearRequest, CrearPedidoRequest.class);
	}
	
	
	private ConfirmarPedidoRequest toConfirmarPedidoRequest(String request) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		return mapper.readValue(request, ConfirmarPedidoRequest.class);
	}
	
	private ConfirmarPedidoSinDireccionRequest toConfirmarPedidoSinDireccionRequest(String request) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		return mapper.readValue(request, ConfirmarPedidoSinDireccionRequest.class);
	}

}
