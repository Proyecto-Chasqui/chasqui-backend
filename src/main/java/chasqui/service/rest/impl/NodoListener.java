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
import chasqui.exceptions.EncrypterException;
import chasqui.exceptions.EstadoPedidoIncorrectoException;
import chasqui.exceptions.GrupoCCInexistenteException;
import chasqui.exceptions.NoAlcanzaMontoMinimoException;
import chasqui.exceptions.NodoInexistenteException;
import chasqui.exceptions.NodoYaExistenteException;
import chasqui.exceptions.PedidoInexistenteException;
import chasqui.exceptions.PedidoVigenteException;
import chasqui.exceptions.RequestIncorrectoException;
import chasqui.exceptions.SolicitudCreacionNodoException;
import chasqui.exceptions.SolicitudPernenciaNodoException;
import chasqui.exceptions.SolicitudCreacionNodoEnGestionExistenteException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.UsuarioNoPerteneceAlGrupoDeCompras;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.GrupoCC;
import chasqui.model.InvitacionAGCC;
import chasqui.model.Nodo;
import chasqui.model.Pedido;
import chasqui.model.SolicitudCreacionNodo;
import chasqui.model.SolicitudPertenenciaNodo;
import chasqui.model.Usuario;
import chasqui.service.rest.request.AceptarRequest;
import chasqui.service.rest.request.ActualizarDomicilioRequest;
import chasqui.service.rest.request.CancelarSolicitudCreacionNodoRequest;
import chasqui.service.rest.request.CederAdministracionRequest;
import chasqui.service.rest.request.ConfirmarPedidoColectivoRequest;
import chasqui.service.rest.request.ConfirmarPedidoSinDireccionRequest;
import chasqui.service.rest.request.EditarGCCRequest;
import chasqui.service.rest.request.EditarNodoRequest;
import chasqui.service.rest.request.EditarSolicitudCreacionNodoRequest;
import chasqui.service.rest.request.EliminarGrupoRequest;
import chasqui.service.rest.request.GrupoRequest;
import chasqui.service.rest.request.InvitacionRequest;
import chasqui.service.rest.request.NodoSolicitudCreacionRequest;
import chasqui.service.rest.request.NuevoPedidoIndividualRequest;
import chasqui.service.rest.request.QuitarMiembroRequest;
import chasqui.service.rest.request.SolicitudDePertenenciaRequest;
import chasqui.service.rest.response.ChasquiError;
import chasqui.service.rest.response.GrupoResponse;
import chasqui.service.rest.response.NodoAbiertoResponse;
import chasqui.service.rest.response.NodoResponse;
import chasqui.service.rest.response.PedidoResponse;
import chasqui.service.rest.response.SolicitudCreacionNodoResponse;
import chasqui.service.rest.response.SolicitudDePertenenciaResponse;
import chasqui.services.interfaces.GrupoService;
import chasqui.services.interfaces.InvitacionService;
import chasqui.services.interfaces.NodoService;
import chasqui.services.interfaces.NotificacionService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.services.interfaces.VendedorService;
import chasqui.view.composer.Constantes;
import freemarker.template.TemplateException;

@Service
@Path("/nodo")
public class NodoListener {

	@Autowired
	NodoService nodoService;
	@Autowired
	VendedorService vendedorService;
	@Autowired
	UsuarioService usuarioService;
	@Autowired
	GrupoService grupoService;
	@Autowired
	InvitacionService invitacionService;
	@GET
	@Path("/all/{idVendedor : \\d+ }")
	@Produces("application/json")
	public Response obtenerNodosDelVendedor(@PathParam("idVendedor")final Integer idVendedor){
		try{
			String emailAdministrador = obtenerEmailDeContextoDeSeguridad();
			return Response.ok(toResponse(nodoService.obtenerNodosDelCliente(idVendedor,emailAdministrador),emailAdministrador),MediaType.APPLICATION_JSON).build();
		}catch(VendedorInexistenteException e){
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build(); 
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	@POST
	@Path("/alta")
	@Produces("application/json")
	public Response solicitudDeCreacionNodo(@Multipart(value = "solicitudCreacionNodoRequest", type = "application/json") final String solicitudCreacionNodoRequest) {
		NodoSolicitudCreacionRequest request;
		try {
			//validar estrategia
			String emailAdministrador = obtenerEmailDeContextoDeSeguridad();
			request = this.toNodoCreacionRequest(solicitudCreacionNodoRequest);
			this.crearSolicitudDeCreacionDeNodo(request,emailAdministrador);
			return Response.ok().build();
		} catch (IOException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} catch (UsuarioInexistenteException e) {
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		} catch (VendedorInexistenteException e) {
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		} catch (DireccionesInexistentes e) {
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();			
		} catch (ConfiguracionDeVendedorException e) {
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();	
		} catch (SolicitudCreacionNodoEnGestionExistenteException e) {
			return Response.status(406).entity(new ChasquiError("Ya posee una solicitud de creación de nodo, solo se permite gestionar una por vez")).build();
		} catch (NodoYaExistenteException e) {
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		} catch (Exception e) {
			return Response.status(406).entity(new ChasquiError("error interno")).build();
		}
	}

	private void crearSolicitudDeCreacionDeNodo(NodoSolicitudCreacionRequest request, String emailAdministrador) throws DireccionesInexistentes, VendedorInexistenteException, ConfiguracionDeVendedorException, UsuarioInexistenteException, SolicitudCreacionNodoEnGestionExistenteException, NodoYaExistenteException {
		Cliente cliente = usuarioService.obtenerClientePorEmail(emailAdministrador);
		nodoService.crearSolicitudDeCreacionNodo(request.getIdVendedor(),
				cliente,
				request.getNombreNodo(),
				cliente.obtenerDireccionConId(request.getIdDomicilio()),
				request.getTipoNodo(),
				request.getBarrio(),
				request.getDescripcion());
		
	}

	private NodoSolicitudCreacionRequest toNodoCreacionRequest(String solicitudCreacionNodoRequest) throws JsonParseException, JsonMappingException, IOException {
		NodoSolicitudCreacionRequest request = new NodoSolicitudCreacionRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(solicitudCreacionNodoRequest, NodoSolicitudCreacionRequest.class);
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
			nodoService.cederAdministracion(request.getIdGrupo(), request.getEmailCliente());
			return Response.ok().build();
		} catch (IOException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} catch (UsuarioInexistenteException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} catch (UsuarioNoPerteneceAlGrupoDeCompras e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	@POST
	@Path("/eliminarNodo")
	@Produces("application/json")
	public Response eliminarGrupo(
			@Multipart(value = "eliminarGrupoRequest", type = "application/json") final String eliminarGrupoRequest) {
		EliminarGrupoRequest request;
		
		try {
			String emailAdministrador = obtenerEmailDeContextoDeSeguridad();
			request = this.toEliminarGrupoRequest(eliminarGrupoRequest);
			Nodo nodo = nodoService.obtenerNodoPorId(request.getIdGrupo());
			validarNodoParaEliminar(nodo, emailAdministrador);
			nodoService.vaciarNodo(request.getIdGrupo());
			
			return Response.ok().build();
		} catch (IOException e) {
			return Response.status(500).entity(new ChasquiError("Error de parseo en JSON")).build();
		} catch (EstadoPedidoIncorrectoException e) {
			return Response.status(500).entity(new ChasquiError("No se puede eliminar el grupo debido a que algunos pedidos estan confirmados o abiertos")).build();
		} catch (NodoInexistenteException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} catch (UsuarioNoPerteneceAlGrupoDeCompras e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	@GET
	@Path("/pedidos/{idVendedor : \\d+ }")
	@Produces("application/json")
	public Response obtenerPedidosEnNodo(@PathParam("idVendedor") final Integer idVendedor) {

		String email = obtenerEmailDeContextoDeSeguridad();

		Map<Integer, Pedido> pedidos;

		try {

			List<Nodo> nodos = nodoService.obtenerNodosDelCliente(idVendedor, email);

			pedidos = nodoService.obtenerPedidosEnNodos(nodos, email);

			return Response.ok(this.toResponse(pedidos, null), MediaType.APPLICATION_JSON)
						.build();

		} catch (ClienteNoPerteneceAGCCException e) {
			return Response.status(RestConstants.CLIENTE_NO_ESTA_EN_GRUPO).entity(new ChasquiError(e.getMessage()))
					.build();
		} catch (GrupoCCInexistenteException e) {
			return Response.status(RestConstants.GRUPOCC_INEXISTENTE).entity(new ChasquiError(e.getMessage())).build();
		} catch (VendedorInexistenteException e1) {
			return Response.status(RestConstants.VENDEDOR_INEXISTENTE).entity(new ChasquiError(e1.getMessage())).build();
		}
		

	}

	
	private void validarNodoParaEliminar(Nodo nodo, String emailAdministrador) throws UsuarioNoPerteneceAlGrupoDeCompras, NodoInexistenteException {
		if(nodo == null) {
			throw new NodoInexistenteException("El nodo no existe");
		}
		if(!nodo.getAdministrador().getEmail().equals(emailAdministrador)) {
			throw new UsuarioNoPerteneceAlGrupoDeCompras("No tiene permisos para eliminar el nodo");
		}
	}
	
	@GET
	@Path("/solicitudesDeCreacion/{idVendedor : \\d+ }")
	@Produces("application/json")
	public Response obtenerSolicitudes(@PathParam("idVendedor") final Integer idVendedor) {

		String email = obtenerEmailDeContextoDeSeguridad();
		
			try {
				return Response.ok(toResponseSolicitudes(nodoService.obtenerSolicitudesDeCreacionDe(email,idVendedor)),
						MediaType.APPLICATION_JSON).build();
			} catch (UsuarioInexistenteException e) {
				e.printStackTrace();
				return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
			}

	}
	
	@POST
	@Path("/editarSolicitudDeCreacion")
	@Produces("application/json")
	public Response editarSolicitudDeCreacion(@Multipart(value = "editarSolicitudDeCreacion", type = "application/json") final String editarSolicitudDeCreacion) {
		EditarSolicitudCreacionNodoRequest request;
		try {
			String emailAdministrador = obtenerEmailDeContextoDeSeguridad();
			request = this.toEditNodoCreacionRequest(editarSolicitudDeCreacion);
			this.editarSolicitudDeCreacionDeNodo(request,emailAdministrador);
			return Response.ok().build();
		} catch (IOException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} catch (UsuarioInexistenteException e) {
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		} catch (SolicitudCreacionNodoException e) {
			return Response.status(406).entity(new ChasquiError("La solicitud no esta en etapa de gestión")).build();
		} catch (DireccionesInexistentes e) {
			return Response.status(406).entity(new ChasquiError("La dirección seleccionada no pertenece al usuario")).build();
		} catch (NodoYaExistenteException e) {
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		} catch (Exception e) {
			return Response.status(500).entity(new ChasquiError("Error desconocido")).build();
		}
	}
	
	@POST
	@Path("/cancelarSolicitudDeCreacion")
	@Produces("application/json")
	public Response cancelarSolicitudDeCreacion(@Multipart(value = "cancelarSolicitudDeCreacion", type = "application/json") final String cancelarSolicitudDeCreacion) {
		CancelarSolicitudCreacionNodoRequest request;
		try {
			String emailAdministrador = obtenerEmailDeContextoDeSeguridad();
			request = this.toCancelarNodoCreacionRequest(cancelarSolicitudDeCreacion);
			this.cancelarSolicitudDeCreacionDeNodo(request,emailAdministrador);
			return Response.ok().build();
		} catch (IOException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} catch (UsuarioInexistenteException e) {
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		} catch (SolicitudCreacionNodoException e) {
			return Response.status(406).entity(new ChasquiError("La solicitud no esta en etapa de gestión")).build();
		} catch (Exception e) {
			return Response.status(500).entity(new ChasquiError("Error desconocido")).build();
		}
	}
	
	@POST
	@Path("/enviarSolicitudDePertenencia")
	@Produces("application/json")
	public Response crearSolicitudDePertenencia(@Multipart(value = "crearSolicitudDePertenencia", type = "application/json") final String crearSolicitudDePertenencia) {
		SolicitudDePertenenciaRequest request;
		try {
			String emailAdministrador = obtenerEmailDeContextoDeSeguridad();
			Usuario usuario = usuarioService.obtenerClientePorEmail(emailAdministrador);
			request = this.toSolicitarPertenencia(crearSolicitudDePertenencia);
			SolicitudPertenenciaNodo solicitud = nodoService.obtenerSolicitudDe(request.getIdNodo(), usuario.getId());
			if(solicitud != null) {
				validarSolicitudDeEnvio(solicitud);
				this.editarSolicitudDePertentenciaNodo(solicitud);
			}else {
				this.crearSolicitudDePertenencia(request,emailAdministrador);
			}
			return Response.ok().build();
		} catch (IOException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} catch (UsuarioInexistenteException e) {
			return Response.status(406).entity(new ChasquiError(e.getMessage())).build();
		} catch (SolicitudPernenciaNodoException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} catch (NodoInexistenteException e) {
			return Response.status(500).entity(new ChasquiError("Nodo inexistente")).build();
		}catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity(new ChasquiError("Error desconocido")).build();
		}
	}


	private void editarSolicitudDePertentenciaNodo(SolicitudPertenenciaNodo solicitud) {
		nodoService.reabrirSolicitudDePertenenciaNodo(solicitud);
	}

	private void validarSolicitudDeEnvio(SolicitudPertenenciaNodo solicitud) throws SolicitudPernenciaNodoException {
		
		if(solicitud.getReintentos() > 2) {
			throw new SolicitudPernenciaNodoException("La solicitud tiene muchos reintentos, solo puede ser invitado por el administrador de nodo");
		}
		if(!solicitud.getEstado().equals(Constantes.SOLICITUD_PERTENENCIA_NODO_RECHAZADO) && !solicitud.getEstado().equals(Constantes.SOLICITUD_PERTENENCIA_NODO_ACEPTADO) && !solicitud.getEstado().equals(Constantes.SOLICITUD_PERTENENCIA_NODO_CANCELADO)){
			throw new SolicitudPernenciaNodoException("La solicitud esta en gestión");
		}
		if(solicitud.getEstado().equals(Constantes.SOLICITUD_PERTENENCIA_NODO_ACEPTADO)){
			throw new SolicitudPernenciaNodoException("Ya tiene una solicitud aceptada para ese nodo");
		}

	}

	private void crearSolicitudDePertenencia(SolicitudDePertenenciaRequest request, String email) throws UsuarioInexistenteException, NodoInexistenteException, SolicitudPernenciaNodoException {
		Usuario usuario = usuarioService.obtenerUsuarioPorEmail(email);
		Nodo nodo = nodoService.obtenerNodoPorId(request.getIdNodo());
		if(nodo == null) {
			throw new NodoInexistenteException();
		}
		if(nodo.getTipo().equals(Constantes.NODO_CERRADO)) {
			throw new SolicitudPernenciaNodoException("La solicitud no puede ser enviada a un nodo cerrado");
		}
		nodoService.crearSolicitudDePertenenciaANodo(nodo, (Cliente)usuario);
	}
	
	@POST
	@Path("/rechazarSolicitudDePertenencia/{idSolicitud : \\d+ }")
	@Produces("application/json")
	public Response rechazarSolicitudDePertenencia(@PathParam("idSolicitud") final Integer idSolicitud) {
		try {
			String emailSolicitante = obtenerEmailDeContextoDeSeguridad();
			SolicitudPertenenciaNodo solicitudpertenencia = nodoService.obtenerSolicitudDePertenenciaById(idSolicitud);
			this.validarGestionDeSolicitudPertenencia(emailSolicitante,solicitudpertenencia);
			nodoService.rechazarSolicitudDePertenencia(solicitudpertenencia);
			return Response.ok().build();
		} catch (SolicitudPernenciaNodoException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} 
	}
	
	@POST
	@Path("/aceptarSolicitudDePertenencia/{idSolicitud : \\d+ }")
	@Produces("application/json")
	public Response aceptarSolicitudDePertenencia(@PathParam("idSolicitud") final Integer idSolicitud) {
		try {
			String emailSolicitante = obtenerEmailDeContextoDeSeguridad();
			SolicitudPertenenciaNodo solicitudpertenencia = nodoService.obtenerSolicitudDePertenenciaById(idSolicitud);
			this.validarGestionDeSolicitudPertenencia(emailSolicitante,solicitudpertenencia);
			nodoService.aceptarSolicitudDePertenencia(solicitudpertenencia);
			return Response.ok().build();
		} catch (SolicitudPernenciaNodoException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} 
	}
	
	@GET
	@Path("/obtenerSolicitudesDePertenenciaDeUsuario/{idVendedor : \\d+ }")
	@Produces("application/json")
	public Response obtenerSolicitudesDePertenenciaDeUsuario(@PathParam("idVendedor") final Integer idVendedor) {
		try {
			String emailSolicitante = obtenerEmailDeContextoDeSeguridad();
			Usuario usuario = usuarioService.obtenerUsuarioPorEmail(emailSolicitante);
			List<SolicitudPertenenciaNodo> solicitudesDepertenencia = nodoService.obtenerSolicitudesDePertenenciaDeUsuarioDeVendededor(usuario.getId(), idVendedor);
			return Response.ok(toListSolicitudPertenenciaResponse(solicitudesDepertenencia),MediaType.APPLICATION_JSON).build();
		} catch (UsuarioInexistenteException e) {
			e.printStackTrace();
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} catch (VendedorInexistenteException e) {
			e.printStackTrace();
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} 
	}
	
	@GET
	@Path("/obtenerSolicitudesDePertenenciaANodo/{idNodo : \\d+ }")
	@Produces("application/json")
	public Response obtenerSolicitudesDePertenencia(@PathParam("idNodo") final Integer idNodo) {
		try {
			String emailSolicitante = obtenerEmailDeContextoDeSeguridad();
			Nodo nodo = nodoService.obtenerNodoPorId(idNodo);
			this.validarAdministrador(emailSolicitante,nodo.getAdministrador().getEmail());
			List<SolicitudPertenenciaNodo> solicitudesDepertenencia = nodoService.obtenerSolicitudesDePertenencia(idNodo);
			return Response.ok(toListSolicitudPertenenciaResponse(solicitudesDepertenencia),MediaType.APPLICATION_JSON).build();
		} catch (RequestIncorrectoException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} 
	}
	
	private List<SolicitudDePertenenciaResponse> toListSolicitudPertenenciaResponse(List<SolicitudPertenenciaNodo> solicitudesDepertenencia) {
		List<SolicitudDePertenenciaResponse> resultado = new ArrayList<SolicitudDePertenenciaResponse>();
		for(SolicitudPertenenciaNodo solicitud : solicitudesDepertenencia){
			resultado.add(new SolicitudDePertenenciaResponse(solicitud));
		}
		return resultado;
	}

	private void validarAdministrador(String emailSolicitante, String email) throws RequestIncorrectoException {
		if(!email.equals(emailSolicitante)){
			throw new RequestIncorrectoException("no tiene permisos");
		}
		
	}

	private void validarGestionDeSolicitudPertenencia(String emailSolicitante, SolicitudPertenenciaNodo solicitudpertenencia) throws SolicitudPernenciaNodoException {
		if(solicitudpertenencia == null) {
			throw new SolicitudPernenciaNodoException("La solicitud no existe");
		}
		if(!solicitudpertenencia.getEstado().equals(Constantes.SOLICITUD_PERTENENCIA_NODO_ENVIADO)) {
			throw new SolicitudPernenciaNodoException("La solicitud ya fue gestionada");
		}
		if(!solicitudpertenencia.getNodo().getEmailAdministradorNodo().equals(emailSolicitante)) {
			throw new SolicitudPernenciaNodoException("No tiene permisos para gestionar la solicitud");
		}
	}
	
	@POST
	@Path("/cancelarSolicitudDePertenencia/{idSolicitud : \\d+ }")
	@Produces("application/json")
	public Response cancelarSolicitudDePertenencia(@PathParam("idSolicitud") final Integer idSolicitud) {
		try {
			String emailSolicitante = obtenerEmailDeContextoDeSeguridad();
			SolicitudPertenenciaNodo solicitudpertenencia = nodoService.obtenerSolicitudDePertenenciaById(idSolicitud);
			this.validarCancelarSolicitud(emailSolicitante,solicitudpertenencia);
			nodoService.cancelarSolicitudDePertenencia(solicitudpertenencia);
			return Response.ok().build();
		} catch (SolicitudPernenciaNodoException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} 
	}

	private void validarCancelarSolicitud(String emailSolicitante, SolicitudPertenenciaNodo solicitud) throws SolicitudPernenciaNodoException {
		if(solicitud == null) {
			throw new SolicitudPernenciaNodoException("La solicitud no existe");
		}
		if(!solicitud.getEstado().equals(Constantes.SOLICITUD_PERTENENCIA_NODO_ENVIADO)) {
			throw new SolicitudPernenciaNodoException("La solicitud no se puede cancelar, ya fue gestionada");
		}
		if(!solicitud.getUsuarioSolicitante().getEmail().equals(emailSolicitante)) {
			throw new SolicitudPernenciaNodoException("La solicitud no pertence a quien la envío");
		}

	}

	//testeado, falta flujo de notificacion cuando cambia de ABIERTO <-> CERRADO
	@PUT
	@Path("/editarNodo")
	@Produces("application/json")
	public Response editarGCC(
			@Multipart(value = "editarNodoRequest", type = "application/json") final String editarNodoRequest) {

		String email = obtenerEmailDeContextoDeSeguridad();
		EditarNodoRequest request;
		try {
			request = this.toEditarNodo(editarNodoRequest);
			nodoService.editarNodo(request.getIdNodo(), email, request.getNombreNodo(), request.getDescripcion(), request.getIdDireccion(),
					request.getTipoNodo(), request.getBarrio());
			
		} catch (JsonParseException e) {
			return Response.status(RestConstants.REQ_INCORRECTO).entity(new ChasquiError(e.getMessage())).build();
		} catch (JsonMappingException e) {
			return Response.status(RestConstants.REQ_INCORRECTO).entity(new ChasquiError(e.getMessage())).build();
		} catch (IOException e) {
			return Response.status(RestConstants.REQ_INCORRECTO).entity(new ChasquiError(e.getMessage())).build();
		} catch (RequestIncorrectoException e) {
			e.printStackTrace();
			return Response.status(RestConstants.REQ_INCORRECTO).entity(new ChasquiError(e.getMessage())).build();
		}

		return Response.ok().build();
	}
	
	private EditarNodoRequest toEditarNodo(String editarNodoRequest) throws JsonParseException, JsonMappingException, IOException {
		EditarNodoRequest request = new EditarNodoRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(editarNodoRequest, EditarNodoRequest.class);
		return request;

	}
	
	@POST
	@Path("/enviarInvitacion")
	@Produces("application/json")
	public Response invitarAGrupo(
			@Multipart(value = "invitacionRequest", type = "application/json") final String invitacionRequest) {

		InvitacionRequest request;
		try {
			request = this.toInvitacionRequest(invitacionRequest);
			String emailAdministrador = obtenerEmailDeContextoDeSeguridad();

			nodoService.invitarANodo(request.getIdGrupo(), request.getEmailInvitado(), emailAdministrador);
			return Response.ok().build();
		}catch (IOException e) {
			return Response.status(500).entity(new ChasquiError("Error al enviar mail" + e.getMessage())).build();
		} catch (GrupoCCInexistenteException e) {
			e.printStackTrace();
			return Response.status(500).entity(new ChasquiError("Nodo inexistente" + e.getMessage())).build();

		} catch (MessagingException e) {
			return Response.status(500).entity(new ChasquiError("Error al enviar mail" + e.getMessage())).build();
		} catch (TemplateException e) {
			return Response.status(500).entity(new ChasquiError("Error al enviar mail" + e.getMessage())).build();
		} catch (ClassCastException e) {
			return Response.status(500)
					.entity(new ChasquiError("El mail invitado ya pertence a un vendedor" + e.getMessage())).build();
		} catch (EncrypterException e) {
			e.printStackTrace();
			return Response.status(RestConstants.ERROR_INTERNO).entity("Error interno de encriptación").build();
			
		}  catch (Exception e) {
			e.printStackTrace();
			return Response.status(RestConstants.ERROR_INTERNO).entity("error interno").build();
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
			nodoService.quitarMiembroDelNodo(request.getIdGrupo(), request.getEmailCliente());

			return Response.ok().build();
		} catch (IOException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} catch (UsuarioInexistenteException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	@POST
	@Path("/aceptarInvitacion")
	@Produces("application/json")
	// @Consumes(MediaType.APPLICATION_JSON)
	public Response confirmarInvitacionGCC(
			@Multipart(value = "invitacionRequest", type = "application/json") final String aceptarReqString) throws ClienteNoPerteneceAGCCException, GrupoCCInexistenteException{
		try {
			String emailClienteLogueado = obtenerEmailDeContextoDeSeguridad();
			AceptarRequest aceptarReq = this.toAceptarRequest(aceptarReqString);
			grupoService.confirmarInvitacionGCC(aceptarReq.getIdInvitacion(), emailClienteLogueado);
			InvitacionAGCC invitacion = invitacionService.obtenerInvitacionAGCCporID(aceptarReq.getIdInvitacion());
			return Response.ok(toNodoSimpleResponse(nodoService.obtenerNodoPorId(invitacion.getIdGrupo()))).build();
		} catch (UsuarioInexistenteException e) {
			return Response.status(RestConstants.CLIENTE_INEXISTENTE).entity(new ChasquiError(e.getMessage())).build();
		} catch (IOException e) {
			return Response.status(RestConstants.IO_EXCEPTION).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	private NodoResponse toNodoSimpleResponse(Nodo nodo) throws ClienteNoPerteneceAGCCException{
		return new NodoResponse(nodo);
	}
	
	@POST
	@Path("/rechazarInvitacion")
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
	@Path("/individual")
	@Produces("application/json")
	public Response nuevoPedidoIndividual(
			@Multipart(value = "nuevoPedidoIndividualRequest", type = "application/json") final String nuevoPedidoIndividualRequest) {

		NuevoPedidoIndividualRequest request;
		String email = obtenerEmailDeContextoDeSeguridad();
		Pedido nuevoPedido = null;
		try {
			request = this.tonuevoPedidoIndividualRequest(nuevoPedidoIndividualRequest);
			nodoService.nuevoPedidoIndividualPara(request.getIdGrupo(), email, request.getIdVendedor());
			nuevoPedido = nodoService.obtenerPedidoIndividualEnNodo(request.getIdGrupo(), email);
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
		} catch (UsuarioNoPerteneceAlGrupoDeCompras e) {
			return Response.status(RestConstants.CLIENTE_NO_ESTA_EN_GRUPO).entity(new ChasquiError("el usuario no pertenece al nodo")).build();
		}

			return Response.ok(toResponse(nuevoPedido),MediaType.APPLICATION_JSON).build();
		
	}
	
	@POST
	@Produces("application/json")
	@Path("/confirmarIndividualEnNodo")
	public Response confirmarPedidoEnNodo(@Multipart(value="crearRequest", type="application/json") final String  request){
		try{
			String email = obtenerEmailDeContextoDeSeguridad();
			nodoService.confirmarPedidoIndividualEnNodo(email,toConfirmarPedidoSinDireccionRequest(request));
			return Response.ok().build();
		}catch(Exception e){
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
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


	private void cancelarSolicitudDeCreacionDeNodo(CancelarSolicitudCreacionNodoRequest request,
			String emailAdministrador) throws UsuarioInexistenteException, SolicitudCreacionNodoException, VendedorInexistenteException, ConfiguracionDeVendedorException {
		Cliente cliente = usuarioService.obtenerClientePorEmail(emailAdministrador);
		nodoService.cancelarSolicitudDeCreacionNodo(request.getIdSolicitud(), request.getIdVendedor(), cliente.getId());	
	}


	private void editarSolicitudDeCreacionDeNodo(EditarSolicitudCreacionNodoRequest request, String emailAdministrador) throws UsuarioInexistenteException, SolicitudCreacionNodoException, DireccionesInexistentes, NodoYaExistenteException {
		Cliente cliente = usuarioService.obtenerClientePorEmail(emailAdministrador);
		validarDireccion(cliente, request.getIdDomicilio());
		nodoService.editarSolicitudDeCreacionNodo(request.getIdVendedor(),
				cliente,
				request.getIdSolicitud(),
				request.getNombreNodo(),
				cliente.obtenerDireccionConId(request.getIdDomicilio()),
				request.getTipoNodo(),
				request.getBarrio(),
				request.getDescripcion());
		
	}
	
	
	private void validarDireccion(Cliente cliente, Integer idDomicilio) throws DireccionesInexistentes {
		if(!cliente.contieneDireccion(idDomicilio)) {
			throw new DireccionesInexistentes();
		}		
		
	}

	private List<SolicitudCreacionNodoResponse> toResponseSolicitudes(List<SolicitudCreacionNodo> solicitudes) {
		List<SolicitudCreacionNodoResponse> response = new ArrayList<SolicitudCreacionNodoResponse>();
		//TODO seguir desde aca la prox. Solucionar Error serializing the response, please check the server logs, response class : ArrayList.
		for(SolicitudCreacionNodo solicitud : solicitudes){
			response.add(new SolicitudCreacionNodoResponse(solicitud));
		}
		return response;
	}
	
	private List<NodoResponse> toResponse(List<Nodo> nodos, String email) throws ClienteNoPerteneceAGCCException {
		List<NodoResponse> response = new ArrayList<NodoResponse>();
		for(Nodo nodo : nodos){
			response.add(new NodoResponse(nodo,email));
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
	
	protected String obtenerEmailDeContextoDeSeguridad() {
		return SecurityContextHolder.getContext().getAuthentication().getName();

	}
	
	private CancelarSolicitudCreacionNodoRequest toCancelarNodoCreacionRequest(String cancelarSolicitudDeCreacion) throws JsonParseException, JsonMappingException, IOException {
		CancelarSolicitudCreacionNodoRequest request = new CancelarSolicitudCreacionNodoRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(cancelarSolicitudDeCreacion, CancelarSolicitudCreacionNodoRequest.class);
		return request;
	}

	private EditarSolicitudCreacionNodoRequest toEditNodoCreacionRequest(String editarSolicitudDeCreacion) throws JsonParseException, JsonMappingException, IOException {
		EditarSolicitudCreacionNodoRequest request = new EditarSolicitudCreacionNodoRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(editarSolicitudDeCreacion, EditarSolicitudCreacionNodoRequest.class);
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
	
	private PedidoResponse toResponse(Pedido nuevoPedido){
		return new PedidoResponse(nuevoPedido);
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
	

	private EliminarGrupoRequest toEliminarGrupoRequest(String grupoRequest) throws JsonParseException, JsonMappingException, IOException {
		EliminarGrupoRequest request = new EliminarGrupoRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(grupoRequest, EliminarGrupoRequest.class);
		return request;
	}
	
	private InvitacionRequest toInvitacionRequest(String req) throws IOException {
		InvitacionRequest request = new InvitacionRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(req, InvitacionRequest.class);
		return request;
	}
	
	private SolicitudDePertenenciaRequest toSolicitarPertenencia(String crearSolicitudDePertenencia) throws JsonParseException, JsonMappingException, IOException {
		SolicitudDePertenenciaRequest request = new SolicitudDePertenenciaRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(crearSolicitudDePertenencia, SolicitudDePertenenciaRequest.class);
		return request;
	}
	
	private ConfirmarPedidoSinDireccionRequest toConfirmarPedidoSinDireccionRequest(String request) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		return mapper.readValue(request, ConfirmarPedidoSinDireccionRequest.class);
	}
	

	private AceptarRequest toAceptarRequest(String aceptarRequest) throws IOException {
		AceptarRequest request = new AceptarRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(aceptarRequest, AceptarRequest.class);
		return request;
	}
	
	private List<PedidoResponse> toResponse(Map<Integer, Pedido> pedidos, Pedido pedidoIndividual)
			throws GrupoCCInexistenteException {
		List<PedidoResponse> pedidosResponse = new ArrayList<PedidoResponse>();

		Iterator<Integer> it = pedidos.keySet().iterator();
		Integer idNodo;
		while (it.hasNext()) {
			//Integer idGrupo = (Integer) pedidos.keySet().iterator().next();
			idNodo = it.next();
			String alias = nodoService.obtenerNodoPorId(idNodo).getAlias(); // TODO
																			// optimizar
			pedidosResponse.add(new PedidoResponse(idNodo, alias, pedidos.get(idNodo)));
			//it.next();
		}
		if (pedidoIndividual != null) {
			pedidosResponse.add(new PedidoResponse(pedidoIndividual));
		}

		return pedidosResponse;
	}
}
