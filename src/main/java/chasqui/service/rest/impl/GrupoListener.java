package chasqui.service.rest.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
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

import chasqui.exceptions.ClienteNoPerteneceAGCCException;
import chasqui.exceptions.ConfiguracionDeVendedorException;
import chasqui.exceptions.DireccionesInexistentes;
import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.exceptions.GrupoCCInexistenteException;
import chasqui.exceptions.NoAlcanzaMontoMinimoException;
import chasqui.exceptions.PedidoInexistenteException;
import chasqui.exceptions.PedidoVigenteException;
import chasqui.exceptions.PuntoDeRetiroInexistenteException;
import chasqui.exceptions.RequestIncorrectoException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.UsuarioNoPerteneceAlGrupoDeCompras;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.GrupoCC;
import chasqui.model.InvitacionAGCC;
import chasqui.model.Pedido;
import chasqui.service.rest.request.AceptarRequest;
import chasqui.service.rest.request.ActualizarDomicilioRequest;
import chasqui.service.rest.request.CederAdministracionRequest;
import chasqui.service.rest.request.ConfirmarPedidoColectivoRequest;
import chasqui.service.rest.request.EditarGCCRequest;
import chasqui.service.rest.request.EliminarGrupoRequest;
import chasqui.service.rest.request.GrupoRequest;
import chasqui.service.rest.request.InvitacionRequest;
import chasqui.service.rest.request.NuevoPedidoIndividualRequest;
import chasqui.service.rest.request.QuitarMiembroRequest;
import chasqui.service.rest.response.ChasquiError;
import chasqui.service.rest.response.GrupoResponse;
import chasqui.service.rest.response.PedidoResponse;
import chasqui.services.interfaces.GrupoService;
import chasqui.services.interfaces.InvitacionService;
import chasqui.services.interfaces.PedidoService;
import freemarker.template.TemplateException;

@Service
@Path("/gcc")
public class GrupoListener {

	@Autowired
	GrupoService grupoService;
	@Autowired
	PedidoService pedidoService;
	@Autowired
	private InvitacionService invitacionService;

	@POST
	@Path("/alta")
	@Produces("application/json")
	public Response altaDeGrupo(
			@Multipart(value = "grupoRequest", type = "application/json") final String grupoRequest) {
		GrupoRequest request;
		try {
			request = this.toGrupoRequest(grupoRequest);
			String emailAdministrador = obtenerEmailDeContextoDeSeguridad();
			grupoService.altaGrupo(request.getIdVendedor(), request.getAlias(), request.getDescripcion(),
					emailAdministrador);
			return Response.ok().build();
		} catch (IOException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} catch (UsuarioInexistenteException e) {
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		} catch (VendedorInexistenteException e) {
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		} catch (RequestIncorrectoException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}

	@POST
	@Path("/aceptar")
	@Produces("application/json")
	// @Consumes(MediaType.APPLICATION_JSON)
	public Response confirmarInvitacionGCC(
			@Multipart(value = "invitacionRequest", type = "application/json") final String aceptarReqString) throws ClienteNoPerteneceAGCCException, GrupoCCInexistenteException{
		try {
			String emailClienteLogueado = obtenerEmailDeContextoDeSeguridad();
			AceptarRequest aceptarReq = this.toAceptarRequest(aceptarReqString);
			grupoService.confirmarInvitacionGCC(aceptarReq.getIdInvitacion(), emailClienteLogueado);
			InvitacionAGCC invitacion = invitacionService.obtenerInvitacionAGCCporID(aceptarReq.getIdInvitacion());
			return Response.ok(toResponseSimpleGroup(grupoService.obtenerGrupo(invitacion.getIdGrupo()))).build();
		} catch (UsuarioInexistenteException e) {
			return Response.status(RestConstants.CLIENTE_INEXISTENTE).entity(new ChasquiError(e.getMessage())).build();
		} catch (IOException e) {
			return Response.status(RestConstants.IO_EXCEPTION).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	@POST
	@Path("/rechazar")
	@Produces("application/json")
	// @Consumes(MediaType.APPLICATION_JSON)
	public Response rechazarInvitacionGCC(
			@Multipart(value = "invitacionRequest", type = "application/json") final String aceptarReqString){
		try {
			String emailClienteLogueado = obtenerEmailDeContextoDeSeguridad();
			AceptarRequest aceptarReq = this.toAceptarRequest(aceptarReqString);
			grupoService.rechazarInvitacionGCC(aceptarReq.getIdInvitacion(), emailClienteLogueado);
			
			return Response.ok().build();
		} catch (UsuarioInexistenteException e) {
			return Response.status(RestConstants.CLIENTE_INEXISTENTE).entity(new ChasquiError(e.getMessage())).build();
		} catch (IOException e) {
			return Response.status(RestConstants.IO_EXCEPTION).entity(new ChasquiError(e.getMessage())).build();
		}
	}

	@POST
	@Path("/invitacion")
	@Produces("application/json")
	// idGrupo mailInvitado token(obtenerEmailDeContextoDeSeguridad)
	public Response invitarAGrupo(
			@Multipart(value = "invitacionRequest", type = "application/json") final String invitacionRequest) {

		InvitacionRequest request;
		try {
			request = this.toInvitacionRequest(invitacionRequest);
			String emailAdministrador = obtenerEmailDeContextoDeSeguridad();

			grupoService.invitarAGrupo(request.getIdGrupo(), request.getEmailInvitado(), emailAdministrador);
			return Response.ok().build();
		} catch (IOException e) {
			return Response.status(500).entity(new ChasquiError("Error al enviar mail" + e.getMessage())).build();
		} catch (GrupoCCInexistenteException e) {
			e.printStackTrace();
			return Response.status(500).entity(new ChasquiError("Grupo inexistente" + e.getMessage())).build();

		} catch (MessagingException e) {
			return Response.status(500).entity(new ChasquiError("Error al enviar mail" + e.getMessage())).build();
		} catch (TemplateException e) {
			return Response.status(500).entity(new ChasquiError("Error al enviar mail" + e.getMessage())).build();
		} catch (ClassCastException e) {
			return Response.status(500)
					.entity(new ChasquiError("El mail invitado ya pertence a un vendedor" + e.getMessage())).build();
		} catch (Exception e) {
			return Response.status(RestConstants.ERROR_INTERNO).entity(new ChasquiError(e.getMessage())).build();
		}
	}

	@POST
	@Path("/quitarMiembro")
	@Produces("application/json")
	public Response quitarMiembroDelGrupo(
			@Multipart(value = "quitarMiembroRequest", type = "application/json") final String quitarMiembroRequest) {
		QuitarMiembroRequest request;
		
		try {
			request = this.toQuitarMiembroRequest(quitarMiembroRequest);
			grupoService.quitarMiembroDelGrupo(request.getIdGrupo(), request.getEmailCliente());

			return Response.ok().build();
		} catch (IOException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} catch (UsuarioInexistenteException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} catch (PedidoVigenteException e) {
			e.printStackTrace();
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	@POST
	@Path("/eliminarGrupo")
	@Produces("application/json")
	public Response eliminarGrupo(
			@Multipart(value = "eliminarGrupoRequest", type = "application/json") final String eliminarGrupoRequest) {
		EliminarGrupoRequest request;
		
		try {
			String emailAdministrador = obtenerEmailDeContextoDeSeguridad();
			request = this.toEliminarGrupoRequest(eliminarGrupoRequest);
			grupoService.vaciarGrupoCC(request.getIdGrupo());
			
			return Response.ok().build();
		} catch (IOException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} catch (EstadoPedidoIncorrectoException e) {
			return Response.status(500).entity(new ChasquiError("No se puede eliminar el grupo debido a que algunos pedidos estan confirmados o abiertos")).build();
		}
	}

	private EliminarGrupoRequest toEliminarGrupoRequest(String grupoRequest) throws JsonParseException, JsonMappingException, IOException {
		EliminarGrupoRequest request = new EliminarGrupoRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(grupoRequest, EliminarGrupoRequest.class);
		return request;
	}

	@POST
	@Path("/cederAdministracion")
	@Produces("application/json")
	public Response CederAdministracionDelGrupo(
			@Multipart(value = "cederAdministracionRequest", type = "application/json") final String cederAdministracionRequest) {
		CederAdministracionRequest request;

		try {
			request = this.toCederAdministracionRequest(cederAdministracionRequest);
			grupoService.cederAdministracion(request.getIdGrupo(), request.getEmailCliente());
			return Response.ok().build();
		} catch (IOException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} catch (UsuarioInexistenteException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} catch (UsuarioNoPerteneceAlGrupoDeCompras e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}

	private QuitarMiembroRequest toQuitarMiembroRequest(String req) throws IOException {
		QuitarMiembroRequest request = new QuitarMiembroRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(req, QuitarMiembroRequest.class);
		return request;
	}

	private CederAdministracionRequest toCederAdministracionRequest(String req) throws IOException {
		CederAdministracionRequest request = new CederAdministracionRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(req, CederAdministracionRequest.class);
		return request;
	}

	private InvitacionRequest toInvitacionRequest(String req) throws IOException {
		InvitacionRequest request = new InvitacionRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(req, InvitacionRequest.class);
		return request;
	}

	private GrupoRequest toGrupoRequest(String req) throws IOException {
		GrupoRequest request = new GrupoRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(req, GrupoRequest.class);
		return request;
	}

	private AceptarRequest toAceptarRequest(String aceptarRequest) throws IOException {
		AceptarRequest request = new AceptarRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(aceptarRequest, AceptarRequest.class);
		return request;
	}

	@GET
	@Path("/all/{idVendedor : \\d+ }")
	@Produces("application/json")
	public Response obtenerGruposDeCliente(@PathParam("idVendedor") final Integer idVendedor) {

		String email = obtenerEmailDeContextoDeSeguridad();

		try {
			return Response.ok(toResponse(grupoService.obtenerGruposDeCliente(email, idVendedor), email),
					MediaType.APPLICATION_JSON).build();
		} catch (UsuarioInexistenteException e) {
			return Response.status(RestConstants.CLIENTE_INEXISTENTE).entity(new ChasquiError("Cliente inexistente"))
					.build();
		} catch (ClienteNoPerteneceAGCCException e) {
			return Response.status(RestConstants.CLIENTE_NO_ESTA_EN_GRUPO).entity(new ChasquiError(e.getMessage()))
					.build();
		}

	}

	@GET
	@Path("/grupo/{idGrupo : \\d+ }")
	@Produces("application/json")
	public Response obtenerGrupoDeCliente(@PathParam("idGrupo") final Integer idGrupo){
		String email = obtenerEmailDeContextoDeSeguridad();
		try {
			return Response.ok(toResponse(grupoService.obtenerGrupo(idGrupo), email),
					MediaType.APPLICATION_JSON).build();
		} catch (ClienteNoPerteneceAGCCException e) {
			return Response.status(RestConstants.CLIENTE_NO_ESTA_EN_GRUPO).entity(new ChasquiError(e.getMessage()))
					.build();
		} catch (GrupoCCInexistenteException e){
			return Response.status(RestConstants.GRUPOCC_INEXISTENTE).entity(new ChasquiError(e.getMessage()))
					.build();
		}
	}

	

	@POST
	@Path("/individual")
	@Produces("application/json")
	// idGrupo mailInvitado token(obtenerEmailDeContextoDeSeguridad)
	public Response nuevoPedidoIndividual(
			@Multipart(value = "nuevoPedidoIndividualRequest", type = "application/json") final String nuevoPedidoIndividualRequest) {

		NuevoPedidoIndividualRequest request;
		String email = obtenerEmailDeContextoDeSeguridad();
		Pedido nuevoPedido = null;
		try {
			request = this.tonuevoPedidoIndividualRequest(nuevoPedidoIndividualRequest);
			grupoService.nuevoPedidoIndividualPara(request.getIdGrupo(), email, request.getIdVendedor());
			nuevoPedido = grupoService.obtenerPedidoIndividualEnGrupo(request.getIdGrupo(), email);
		} catch (JsonParseException e) {
			return Response.status(RestConstants.REQ_INCORRECTO).entity(new ChasquiError(e.getMessage())).build();
		} catch (JsonMappingException e) {
			return Response.status(RestConstants.REQ_INCORRECTO).entity(new ChasquiError(e.getMessage())).build();
		} catch (IOException e) {
			return Response.status(RestConstants.REQ_INCORRECTO).entity(new ChasquiError(e.getMessage())).build();
		} catch (UsuarioInexistenteException e) {
			return Response.status(RestConstants.CLIENTE_INEXISTENTE).entity(new ChasquiError(e.getMessage())).build();
		} catch (ClienteNoPerteneceAGCCException e) {
			return Response.status(RestConstants.CLIENTE_NO_ESTA_EN_GRUPO).entity(new ChasquiError(e.getMessage()))
					.build();
		} catch (ConfiguracionDeVendedorException e) {
			return Response.status(RestConstants.VENDEDOR_INEXISTENTE).entity(new ChasquiError(e.getMessage())).build();
		} catch (PedidoVigenteException e) {

			//Si esto no se informa como un error entonces este método es idempotente
			return Response.status(RestConstants.PEDIDO_EXISTENTE).entity(new ChasquiError(e.getMessage())).build();
		} catch (PedidoInexistenteException e) {
			return Response.status(RestConstants.PEDIDO_INEXISTENTE).entity(new ChasquiError(e.getMessage())).build();
		} catch (VendedorInexistenteException e) {
			return Response.status(RestConstants.VENDEDOR_INEXISTENTE).entity(new ChasquiError(e.getMessage())).build();
		} catch (GrupoCCInexistenteException e) {
			return Response.status(RestConstants.GRUPOCC_INEXISTENTE).entity(new ChasquiError(e.getMessage())).build();
		} catch (EstadoPedidoIncorrectoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.status(RestConstants.PEDIDO_INEXISTENTE).entity(new ChasquiError(e.getMessage())).build();
		}

			return Response.ok(toResponse(nuevoPedido),MediaType.APPLICATION_JSON).build();
		
	}
	@POST
	@Path("/confirmar")
	@Produces("application/json")
	public Response confirmar(
			@Multipart(value = "confirmarPedidoColectivoRequest", type = "application/json") final String confirmarPedidoColectivoRequest) {

		String email = obtenerEmailDeContextoDeSeguridad();
		ConfirmarPedidoColectivoRequest request;
		try {
			request = this.toConfirmarPedidoColectivoRequest(confirmarPedidoColectivoRequest);

			grupoService.confirmarPedidoColectivo(request.getIdGrupo(), email,request.getIdDireccion(), request.getIdPuntoDeRetiro(), request.getComentario(), request.getOpcionesSeleccionadas(), request.getIdZona());

		} catch (JsonParseException e) {
			return Response.status(RestConstants.REQ_INCORRECTO).entity(new ChasquiError(e.getMessage())).build();
		} catch (JsonMappingException e) {
			return Response.status(RestConstants.REQ_INCORRECTO).entity(new ChasquiError(e.getMessage())).build();
		} catch (IOException e) {
			return Response.status(RestConstants.REQ_INCORRECTO).entity(new ChasquiError(e.getMessage())).build();
		} catch (EstadoPedidoIncorrectoException e) {
			return Response.status(RestConstants.PEDIDO_INEXISTENTE).entity(new ChasquiError(e.getMessage())).build();
		} catch (NoAlcanzaMontoMinimoException e) {
			return Response.status(RestConstants.MONTO_INSUFICIENTE).entity(new ChasquiError(e.getMessage())).build();
		} catch (RequestIncorrectoException e) {
			return Response.status(RestConstants.CLIENTE_INEXISTENTE).entity(new ChasquiError(e.getMessage())).build();
		} catch (DireccionesInexistentes e) {
			return Response.status(RestConstants.DIRECCION_INEXISTENTE).entity(new ChasquiError(e.getMessage())).build();
		} catch (UsuarioInexistenteException e) {
			return Response.status(RestConstants.CLIENTE_INEXISTENTE).entity(new ChasquiError(e.getMessage())).build();
		}
		return Response.ok().build();

	}

	private ConfirmarPedidoColectivoRequest toConfirmarPedidoColectivoRequest(String req)
			throws JsonParseException, JsonMappingException, IOException {
		ConfirmarPedidoColectivoRequest request = new ConfirmarPedidoColectivoRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(req, ConfirmarPedidoColectivoRequest.class);
		return request;
	}

	@PUT
	@Path("/editarGCC/{idGrupo : \\d+ }")
	@Produces("application/json")
	public Response editarGCC(
			@Multipart(value = "editarGCCRequest", type = "application/json") final String editarGCCRequest,
			@PathParam("idGrupo") final Integer idGrupo) {

		String email = obtenerEmailDeContextoDeSeguridad();
		EditarGCCRequest request;
		// toRequest editarGCC
		//TODO TERMINAR
		try {
			request = this.toEditarGCCRequest(editarGCCRequest);
			grupoService.editarGrupo(idGrupo, email, request.getAlias(), request.getDescripcion());
			
		} catch (JsonParseException e) {
			return Response.status(RestConstants.REQ_INCORRECTO).entity(new ChasquiError(e.getMessage())).build();
		} catch (JsonMappingException e) {
			return Response.status(RestConstants.REQ_INCORRECTO).entity(new ChasquiError(e.getMessage())).build();
		} catch (IOException e) {
			return Response.status(RestConstants.REQ_INCORRECTO).entity(new ChasquiError(e.getMessage())).build();
		} catch (RequestIncorrectoException e) {
			// TODO Ver que genera esto y convertirlo en un error mas descriptivo.
			e.printStackTrace();
		}

		return Response.ok().build();
	}

	private EditarGCCRequest toEditarGCCRequest(String editarGCCRequest)
			throws JsonParseException, JsonMappingException, IOException {
		EditarGCCRequest request = new EditarGCCRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(editarGCCRequest, EditarGCCRequest.class);
		return request;

	}

	@GET
	@Path("/pedidos/{idVendedor : \\d+ }")
	@Produces("application/json")
	public Response obtenerPedidosEnGruposCC(@PathParam("idVendedor") final Integer idVendedor) {

		String email = obtenerEmailDeContextoDeSeguridad();

		Map<Integer, Pedido> pedidos;

		try {

			List<GrupoCC> grupos = grupoService.obtenerGruposDeCliente(email, idVendedor);

			pedidos = grupoService.obtenerPedidosEnGruposCC(grupos, email);

			try {
				Pedido pedidoActualIndividual = pedidoService.obtenerPedidoActualDe(email, idVendedor);
				return Response.ok(this.toResponse(pedidos, pedidoActualIndividual), MediaType.APPLICATION_JSON)
						.build();
			} catch (PedidoInexistenteException e) {
				return Response.ok(this.toResponse(pedidos, null), MediaType.APPLICATION_JSON).build();
			}

			// TODO falla en la respuesta , pero me trae los pedidos bien.
			// REVISAR
		} catch (UsuarioInexistenteException e) {
			return Response.status(RestConstants.CLIENTE_INEXISTENTE).entity(new ChasquiError("Cliente inexistente"))
					.build();
		} catch (ClienteNoPerteneceAGCCException e) {
			return Response.status(RestConstants.CLIENTE_NO_ESTA_EN_GRUPO).entity(new ChasquiError(e.getMessage()))
					.build();
		} catch (GrupoCCInexistenteException e) {
			return Response.status(RestConstants.GRUPOCC_INEXISTENTE).entity(new ChasquiError(e.getMessage())).build();
		}

	}

	@POST
	@Path("/actualizarDomicilio")
	@Produces("application/json")
	public Response actualizarDomicilio(
			@Multipart(value = "actualizarDomicilioRequest", type = "application/json") final String actualizarDomicilioRequest) {

		ActualizarDomicilioRequest request;
		try {
			request = this.toActualizarDomicilioRequest(actualizarDomicilioRequest);

			grupoService.actualizarDomicilio(request.getIdGrupo(), request.getDireccionRequest());

		} catch (JsonParseException e) {
			return Response.status(RestConstants.REQ_INCORRECTO).entity(new ChasquiError(e.getMessage())).build();
		} catch (JsonMappingException e) {
			return Response.status(RestConstants.REQ_INCORRECTO).entity(new ChasquiError(e.getMessage())).build();
		} catch (IOException e) {
			return Response.status(RestConstants.REQ_INCORRECTO).entity(new ChasquiError(e.getMessage())).build();
		} catch (RequestIncorrectoException e) {
			// TODO La direccion no tiene todos los datos, generar un error para
			// este caso.
			e.printStackTrace();
		}
		return Response.ok().build();

	}

	private ActualizarDomicilioRequest toActualizarDomicilioRequest(String actualizarDomicilioRequest)
			throws JsonParseException, JsonMappingException, IOException {
		ActualizarDomicilioRequest request = new ActualizarDomicilioRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(actualizarDomicilioRequest, ActualizarDomicilioRequest.class);
		return request;
	}

	private NuevoPedidoIndividualRequest tonuevoPedidoIndividualRequest(String nuevoPedidoIndividualRequest)
			throws JsonParseException, JsonMappingException, IOException {
		NuevoPedidoIndividualRequest request = new NuevoPedidoIndividualRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(nuevoPedidoIndividualRequest, NuevoPedidoIndividualRequest.class);
		return request;
	}

	private List<GrupoResponse> toResponse(List<GrupoCC> grupos, String email) throws ClienteNoPerteneceAGCCException {
		List<GrupoResponse> listaResponses = new ArrayList<GrupoResponse>();
		for (GrupoCC grupo : grupos) {
			listaResponses.add(new GrupoResponse(grupo, email));
		}
		return listaResponses;
	}
	
	private GrupoResponse toResponse(GrupoCC grupo, String email) throws ClienteNoPerteneceAGCCException{
		return new GrupoResponse(grupo, email);
	}
	
	private GrupoResponse toResponseSimpleGroup(GrupoCC grupo) throws ClienteNoPerteneceAGCCException{
		return new GrupoResponse(grupo);
	}

	private List<PedidoResponse> toResponse(Map<Integer, Pedido> pedidos, Pedido pedidoIndividual)
			throws GrupoCCInexistenteException {
		List<PedidoResponse> pedidosResponse = new ArrayList<PedidoResponse>();

		Iterator<Integer> it = pedidos.keySet().iterator();
		Integer idGrupo;
		while (it.hasNext()) {
			//Integer idGrupo = (Integer) pedidos.keySet().iterator().next();
			idGrupo = it.next();
			String alias = grupoService.obtenerGrupo(idGrupo).getAlias(); // TODO
																			// optimizar
			pedidosResponse.add(new PedidoResponse(idGrupo, alias, pedidos.get(idGrupo)));
			//it.next();
		}
		if (pedidoIndividual != null) {
			pedidosResponse.add(new PedidoResponse(pedidoIndividual));
		}

		return pedidosResponse;
	}

	private PedidoResponse toResponse(Pedido nuevoPedido){
		return new PedidoResponse(nuevoPedido);
	}
	
	protected String obtenerEmailDeContextoDeSeguridad() {
		return SecurityContextHolder.getContext().getAuthentication().getName();

	}
}
