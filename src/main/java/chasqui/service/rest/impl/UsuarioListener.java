package chasqui.service.rest.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import chasqui.exceptions.DireccionesInexistentes;
import chasqui.exceptions.RequestIncorrectoException;
import chasqui.exceptions.UsuarioExistenteException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.Notificacion;
import chasqui.service.rest.request.DireccionEditRequest;
import chasqui.service.rest.request.DireccionRequest;
import chasqui.service.rest.request.EditarPasswordRequest;
import chasqui.service.rest.request.EditarPerfilRequest;
import chasqui.service.rest.request.ImagenRequest;
import chasqui.service.rest.response.ChasquiError;
import chasqui.service.rest.response.DireccionResponse;
import chasqui.service.rest.response.ImagenDePerfilResponse;
import chasqui.service.rest.response.LoginResponse;
import chasqui.service.rest.response.NotificacionResponse;
import chasqui.service.rest.response.PerfilResponse;
import chasqui.services.interfaces.UsuarioService;

@Service
@Path("/adm")
public class UsuarioListener {

	@Autowired
	UsuarioService usuarioService;

	@GET
	@Path("/read")
	@Produces("application/json")
	public Response obtenerDatosPerfilUsuario(@Context HttpHeaders header) {
		try {
			String email = obtenerEmailDeContextoDeSeguridad();
			Cliente c = (Cliente) usuarioService.obtenerUsuarioPorEmail(email);
			usuarioService.inicializarDirecciones(c);
			return Response.ok(toResponse(c), MediaType.APPLICATION_JSON).build();
		} catch (IndexOutOfBoundsException e) {
			return Response.status(406).entity(new ChasquiError("El email es invalido o el usuario no existe")).build();
		} catch (Exception e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	

	@PUT
	@Path("/edit")
	@Produces("application/json")
	public Response editarPerfilUsuario(
			@Multipart(value = "editRequest", type = "application/json") final String editRequest) {
		try {
			String email = obtenerEmailDeContextoDeSeguridad();
			EditarPerfilRequest request = toRequest(editRequest);
			Cliente c = (Cliente) usuarioService.obtenerUsuarioPorEmail(email);
			usuarioService.modificarUsuario(request, email);
			return toLoginResponse(c);
		} catch (IOException | UsuarioExistenteException e) {
			return Response.status(406).entity(new ChasquiError("Parametros incorrectos")).build();
		} catch (Exception e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	
	@PUT
	@Path("/editpassword")
	@Produces("application/json")
	public Response editarPasswordUsuario(
			@Multipart(value = "editRequest", type = "application/json") final String editRequest) {
		try {
			String email = obtenerEmailDeContextoDeSeguridad();
			EditarPasswordRequest request = toPasswordRequest(editRequest);
			Cliente c = (Cliente) usuarioService.obtenerUsuarioPorEmail(email);
			usuarioService.modificarPassowrd(request, email);
			return toLoginResponse(c);
		} catch (IOException | UsuarioExistenteException e) {
			return Response.status(406).entity(new ChasquiError("Parametros incorrectos")).build();
		} catch (Exception e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}
	

	@GET
	@Path("/check")
	@Produces("application/json")
	public Response esTokenValido() {
		return Response.ok().build();
	}

	@PUT
	@Path("/registrar/{dispositivo}")
	@Produces("application/json")
	public Response registrarDispositivo(@PathParam("dispositivo") String dispositivo) {
		String mail = obtenerEmailDeContextoDeSeguridad();
		try {
			usuarioService.agregarIDDeDispositivo(mail, dispositivo);
		} catch (UsuarioInexistenteException e) {
			return Response.status(406).entity(new ChasquiError("El email es invalido o el usuario no existe")).build();
		}
		return Response.ok().build();
	}


	@GET
	@Path("/dir")
	@Produces("application/json")
	public Response obtenerDirecciones(@Context HttpHeaders header) {
		try {
			String email = obtenerEmailDeContextoDeSeguridad();

			Cliente c = (Cliente) usuarioService.obtenerUsuarioPorEmail(email);
			usuarioService.inicializarDirecciones(c);

			return Response.ok(toDireccionResponse(c.getDireccionesAlternativas()), MediaType.APPLICATION_JSON).build();
		} catch (IndexOutOfBoundsException | NullPointerException e) {
			return Response.status(406).entity(new ChasquiError("Parametros Incorrectos")).build();
		} catch (Exception e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}

	@POST
	@Path("/dir")
	@Produces("application/json")
	public Response agregarNuevaDireccion(
			@Multipart(value = "direccionRequest", type = "application/json") final String direccionRequest) {
		try {
			DireccionRequest request = toDireccionRequest(direccionRequest);
			String mail = obtenerEmailDeContextoDeSeguridad();
			return Response.ok(new DireccionResponse(usuarioService.agregarDireccionAUsuarioCon(mail, request)),
					MediaType.APPLICATION_JSON).build();
		} catch (IOException | RequestIncorrectoException e) {
			return Response.status(406).entity(new ChasquiError("Parametros Incorrectos")).build();
		} catch (Exception e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}

	@PUT
	@Path("/dir")
	@Produces("application/json")
	public Response editarDireccionDe(
			@Multipart(value = "direccionRequest", type = "application/json") String request) {
		try {
			String mail = obtenerEmailDeContextoDeSeguridad();
			DireccionEditRequest dirRequest = toDireccionEditRequest(request);
			usuarioService.editarDireccionDe(mail, dirRequest, dirRequest.getIdDireccion());
			return Response.ok().build();
		} catch (RequestIncorrectoException e) {
			return Response.status(406).entity(new ChasquiError("Parametros Incorrectos")).build();
		} catch (DireccionesInexistentes e) {
			return Response.status(404).entity(new ChasquiError("No se han encontrado direcciones")).build();
		} catch (Exception e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}

	private DireccionEditRequest toDireccionEditRequest(String req) throws IOException {
		DireccionEditRequest request = new DireccionEditRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(req, DireccionEditRequest.class);
		return request;
	}

	@DELETE
	@Path("/dir/{idDireccion : \\d+}")
	@Produces("application/json")
	public Response elimiarDireccionDe(@PathParam("idDireccion") Integer idDireccion) {
		try {
			String mail = obtenerEmailDeContextoDeSeguridad();
			usuarioService.eliminarDireccionDe(mail, idDireccion);
			return Response.ok().build();
		} catch (DireccionesInexistentes e) {
			return Response.status(404).entity(new ChasquiError("No se han encontrado direcciones")).build();
		} catch (Exception e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}

	}

	@GET
	@Path("/notificacion/{pagina : \\d+}")
	@Produces("application/json")
	public Response obtenerNotificacionesDe(@PathParam("pagina") Integer pagina) {
		try {
			String mail = obtenerEmailDeContextoDeSeguridad();
			return Response.ok(toNotificacionResponse(usuarioService.obtenerNotificacionesDe(mail, pagina)),
					MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}

	@GET
	@Path("/notificacion/noLeidas")
	@Produces("application/json")
	public Response obtenerNotificacionesNoLeidas() {
		try {
			String mail = obtenerEmailDeContextoDeSeguridad();
			return Response.ok(toNotificacionResponse(usuarioService.obtenerNotificacionesNoLeidas(mail)),
					MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}

	@POST
	@Path("/notificacion/{id : \\d+}")
	@Produces("application/json")
	public Response leerNotificacion(@PathParam("id") Integer id) {
		try {
			usuarioService.leerNotificacion(id);
			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}

	@GET
	@Path("/notificacion/total")
	@Produces("application/json")
	public Response obtenerTotalNotificaciones() {
		try {
			String mail = obtenerEmailDeContextoDeSeguridad();
			return Response.ok(usuarioService.obtenerTotalNotificacionesDe(mail)).build();
		} catch (Exception e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}

	@GET
	@Path("/avatar")
	@Produces("application/json")
	public Response obtenerAvatar() {
		try {
			String mail = obtenerEmailDeContextoDeSeguridad();
			return Response.ok(toImagenDePerfilResponse(usuarioService.obtenerAvatar(mail))).build();
		} catch (IOException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}
	}

	/*
	 * Desde el FEW se usan los metodos de /user/adm/singUp y /user/adm/edit para modificar el avatar
	 */

	@POST
	@Path("/avatar")
	@Produces("application/json")
	public Response actualizarAvatar(
			@Multipart(value = "imagenRequest", type = "application/json") String imagenRequest) throws IOException {

		ImagenRequest imagenReq = toImagenRequest(imagenRequest);
		String mail = obtenerEmailDeContextoDeSeguridad();
		try {
			Cliente cliente = (Cliente) usuarioService.obtenerUsuarioPorEmail(mail);
			usuarioService.editarAvatarDe(cliente, imagenReq.getAvatar(), imagenReq.getExtension());
			return Response.ok().build();
		} catch (IOException e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		} catch (Exception e) {
			return Response.status(500).entity(new ChasquiError(e.getMessage())).build();
		}

	}

	private ImagenRequest toImagenRequest(String req) throws IOException {
		ImagenRequest request = new ImagenRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(req, ImagenRequest.class);
		return request;
	}

	private List<NotificacionResponse> toNotificacionResponse(List<Notificacion> notificaciones) {
		List<NotificacionResponse> resultado = new ArrayList<NotificacionResponse>();
		for (Notificacion n : notificaciones) {
			resultado.add(new NotificacionResponse(n));
		}
		return resultado;
	}

	private String obtenerEmailDeContextoDeSeguridad() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	private List<DireccionResponse> toDireccionResponse(List<Direccion> dirs) {
		List<DireccionResponse> rs = new ArrayList<DireccionResponse>();
		for (Direccion d : dirs) {
			rs.add(new DireccionResponse(d));
		}
		return rs;
	}

	private EditarPerfilRequest toRequest(String editRequest) throws IOException {
		EditarPerfilRequest request = new EditarPerfilRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(editRequest, EditarPerfilRequest.class);
		return request;
	}
	
	private EditarPasswordRequest toPasswordRequest(String editRequest) throws IOException {
		EditarPasswordRequest request = new EditarPasswordRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(editRequest, EditarPasswordRequest.class);
		return request;
	}

	private PerfilResponse toResponse(Cliente c) {
		return new PerfilResponse(c);
	}

	private DireccionRequest toDireccionRequest(String req) throws IOException {
		DireccionRequest request = new DireccionRequest();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		request = mapper.readValue(req, DireccionRequest.class);
		return request;
	}

	private ImagenDePerfilResponse toImagenDePerfilResponse(String avatar) {
		return new ImagenDePerfilResponse(avatar);
	}
	

	private Response toLoginResponse(Cliente c) {
		LoginResponse response = new LoginResponse(c); 
		return Response.ok(response, MediaType.APPLICATION_JSON).build();
	}

}
