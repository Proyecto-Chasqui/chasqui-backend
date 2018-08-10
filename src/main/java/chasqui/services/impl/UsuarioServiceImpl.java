package chasqui.services.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletContext;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.cxf.common.util.StringUtils;
import org.hsqldb.lib.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import chasqui.aspect.Auditada;
import chasqui.dao.MiembroDeGCCDAO;
import chasqui.dao.UsuarioDAO;
import chasqui.exceptions.DireccionesInexistentes;
import chasqui.exceptions.EncrypteException;
import chasqui.exceptions.PasswordIncorrectoException;
import chasqui.exceptions.RequestIncorrectoException;
import chasqui.exceptions.UsuarioExistenteException;
import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Cliente;
import chasqui.model.Direccion;
import chasqui.model.Imagen;
import chasqui.model.MiembroDeGCC;
import chasqui.model.Notificacion;
import chasqui.model.Usuario;
import chasqui.model.Vendedor;
import chasqui.security.Encrypter;
import chasqui.security.PasswordGenerator;
import chasqui.service.rest.request.DireccionRequest;
import chasqui.service.rest.request.EditarPasswordRequest;
import chasqui.service.rest.request.EditarPerfilRequest;
import chasqui.service.rest.request.SingUpRequest;
import chasqui.service.rest.request.SingUpRequestWithInvitation;
import chasqui.services.interfaces.GrupoService;
import chasqui.services.interfaces.NotificacionService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.view.composer.Constantes;

@Auditada
public class UsuarioServiceImpl implements UsuarioService {

	@Autowired
	private UsuarioDAO usuarioDAO;
	@Autowired
	private Encrypter encrypter;
	@Autowired
	private NotificacionService notificacionService;
	@Autowired
	private PasswordGenerator passwordGenerator;
	@Autowired
	private MailService mailService;
	@Autowired
	private FileSaver fileSaver;
	@Autowired
	private GrupoService grupoService;
	@Autowired
	private String usuarioAdmin;
	@Autowired
	private String passwordAdmin;
	@Autowired
	private String mailAdmin;
	@Autowired
	private String imagenAdmin;
	@Autowired
	private String serverAbsolutPath;
	@Autowired
	private MiembroDeGCCDAO miembroDeGCCDao;
	@Autowired
	private ServletContext servletContext;

	public Usuario obtenerUsuarioPorID(Integer id) {
		return usuarioDAO.obtenerUsuarioPorID(id);
	}

	public Vendedor obtenerVendedorPorID(Integer id) throws VendedorInexistenteException {
		Vendedor vendedor = usuarioDAO.obtenerVendedorPorID(id);
		if (vendedor == null) {
			throw new VendedorInexistenteException();
		}
		return vendedor;
	}

	public void guardarUsuario(Usuario u) {
		usuarioDAO.guardarUsuario(u);
		
	}

	public void modificarPasswordUsuario(String email, String password) throws UsuarioInexistenteException {
		Usuario u = usuarioDAO.obtenerUsuarioPorEmail(email);
		if (u == null) {
			throw new UsuarioInexistenteException(email);
		}
		u.setPassword(password);
		usuarioDAO.guardarUsuario(u);
	}

	/**
	 * Este metodo solo se debe usar para los tear down de los test
	 * 
	 * @param obj
	 */
	@Override
	public <T> void deleteObject(T obj) {
		usuarioDAO.deleteObject(obj);
	}

	public Usuario login(String username, String passwordHashed) throws Exception {
		Usuario usuario = usuarioDAO.obtenerUsuarioPorNombre(username);
		if (usuario != null) {
			String passwordUser = encrypter.decrypt(usuario.getPassword());
			if (passwordUser.equals(passwordHashed)) {
				return usuario;
			}
		}
		throw new Exception("Usuario o Password incorrectos!");
	}

	public void merguear(Vendedor usuario) {
		usuarioDAO.merge(usuario);

	}

	public Usuario obtenerUsuarioPorEmail(String email) throws UsuarioInexistenteException {
		Usuario usr = usuarioDAO.obtenerUsuarioPorEmail(email);
		if (usr == null) {
			throw new UsuarioInexistenteException(Constantes.ERROR_CREDENCIALES_INVALIDAS);
		}
		return usr;
	}

	public Cliente obtenerClientePorEmail(String email) throws UsuarioInexistenteException {
		Cliente usr = (Cliente) usuarioDAO.obtenerUsuarioPorEmail(email);
		usuarioDAO.inicializarDirecciones(usr);
		if (usr == null) {
			throw new UsuarioInexistenteException(email);
		}
		return usr;
	}

	public boolean existeUsuarioCon(String email) {
		return usuarioDAO.existeUsuarioCon(email);
	}

	public Cliente loginCliente(String email, String password) throws Exception {
		Cliente c = (Cliente) obtenerUsuarioPorEmail(email);
		if (c != null) {
			String passwordUser = encrypter.decrypt(c.getPassword());
			if (passwordUser.equals(password)) {
				if(c.getEstado() == null || c.getEstado().equals(Constantes.MAIL_CONFIRMADO)){
					String token = passwordGenerator.generateRandomToken();
					c.setToken(token);
					usuarioDAO.guardarUsuario(c);
					return c;
				}else{
					throw new RequestIncorrectoException(Constantes.ERROR_USUARIO_MAIL_SIN_CONFIRMAR);
				}
			}
		}
		throw new UsuarioInexistenteException(Constantes.ERROR_CREDENCIALES_INVALIDAS);
	}

	public Cliente crearCliente(SingUpRequest request) throws Exception {
		validarRequestCreacionDeUsuario(request);
		request.setPassword(encrypter.encrypt(request.getPassword()));
		Cliente c = new Cliente(request, passwordGenerator.generateRandomToken());
		usuarioDAO.guardarUsuario(c);
		editarAvatarDe((Cliente)usuarioDAO.obtenerUsuarioPorEmail(request.getEmail()), request.getAvatar(), request.getExtension());
		mailService.enviarEmailBienvenidaCliente(request.getEmail(), request.getNombre(), request.getApellido());
		return (Cliente) usuarioDAO.obtenerUsuarioPorEmail(request.getEmail()); //Cliente de la BD porque sino no tiene el avatar
	}
	
	@Override
	public Cliente crearCliente(SingUpRequestWithInvitation requestWithInvitation) throws Exception {

		Integer idInvitacion = Integer.valueOf(encrypter.decryptURL(requestWithInvitation.getInvitacion()));
		String email = notificacionService.obtenerNotificacionPorID(Integer.valueOf(idInvitacion)).getUsuarioDestino();
		
		Cliente nuevoCliente = this.crearCliente(this.generateRequest(requestWithInvitation, email));
		this.actualizarDatosDeNuevoCliente(nuevoCliente);
		grupoService.confirmarInvitacionGCC(idInvitacion, email);
		return nuevoCliente;
	}


	private SingUpRequest generateRequest(SingUpRequestWithInvitation requestWithInvitation, String email) throws NumberFormatException, Exception{
		
		SingUpRequest request = new SingUpRequest();
		request.setApellido(requestWithInvitation.getApellido());
		request.setAvatar(requestWithInvitation.getAvatar());
		request.setEmail(email);
		request.setExtension(requestWithInvitation.getExtension());
		request.setNickName(requestWithInvitation.getNickName());
		request.setNombre(requestWithInvitation.getNombre());
		request.setPassword(requestWithInvitation.getPassword());
		request.setTelefonoFijo(requestWithInvitation.getTelefonoFijo());
		request.setTelefonoMovil(requestWithInvitation.getTelefonoMovil());
		
		return request;
	}

	/**
	 * Buscar las invitaciones a GCC que tenga pendientes en TODOS LOS
	 * CATALOGOS, a través del mail y actualizar nickname
	 * 
	 * @param cliente
	 */
	private void actualizarDatosDeNuevoCliente(Cliente cliente) {
		List<MiembroDeGCC> membresias = miembroDeGCCDao.obtenerMiembrosDeGCCParaClientePorMail(cliente.getEmail());
		for (MiembroDeGCC miembroDeGCC : membresias) {
			miembroDeGCC.setNickname(cliente.getUsername());
			miembroDeGCC.setAvatar(cliente.getImagenPerfil());
			miembroDeGCC.setIdCliente(cliente.getId());
			miembroDeGCCDao.actualizarMiembroDeGCC(miembroDeGCC);
		}
	}

	@Override
	public void modificarUsuario(EditarPerfilRequest request, String email) throws Exception {
		Cliente c = (Cliente) usuarioDAO.obtenerUsuarioPorEmail(email);
		if (c == null) {
			throw new UsuarioExistenteException("No existe el usuario");
		}
		if (!StringUtil.isEmpty(request.getPassword())) {
			request.setPassword(encrypter.encrypt(request.getPassword()));
		}
		c.modificarCon(request);
		editarAvatarDe(c, request.getAvatar(), request.getExtension());
		usuarioDAO.guardarUsuario(c);
	}
	
	@Override
	public void modificarPassowrd(EditarPasswordRequest request, String email) throws Exception {
		Cliente cliente = (Cliente) usuarioDAO.obtenerUsuarioPorEmail(email);
		if (cliente == null) {
			throw new UsuarioExistenteException("No existe el usuario");
		}
		if (!StringUtil.isEmpty(request.getPassword())) {
			validarPasswordRequest(request , cliente);
			request.setPassword(encrypter.encrypt(request.getPassword()));
		}
		cliente.setPassword(request.getPassword());
		usuarioDAO.guardarUsuario(cliente);
	}
	
	private void validarPasswordRequest(EditarPasswordRequest request, Cliente cliente) throws RequestIncorrectoException, EncrypteException, PasswordIncorrectoException {

		try{
			if(!request.getOldPassword().equals(encrypter.decrypt(cliente.getPassword()))){
				//La password anterior no coincide con la que dice asi que no se le permite cambiarla
				throw new PasswordIncorrectoException(Constantes.ERROR_CREDENCIALES_INVALIDAS_EN_MODIFICACION);
			}
		}catch (PasswordIncorrectoException e){
			throw e;
		}catch (Exception e) {
			throw new EncrypteException(Constantes.ERROR_DE_DESCENCRIPTACION);
		}				
		if(request.getPassword().length() < 10 || request.getPassword().length() > 26){
			throw new RequestIncorrectoException(Constantes.PASSWORD_CORTO);
		}
	}

	@Override
	public Direccion agregarDireccionAUsuarioCon(String mail, DireccionRequest request)
			throws UsuarioInexistenteException, RequestIncorrectoException {
		validarDireccionRequest(request);
		Cliente cliente = (Cliente) usuarioDAO.obtenerUsuarioPorEmail(mail);
		if (cliente == null) {
			throw new UsuarioInexistenteException(mail);
		}
		this.inicializarDirecciones(cliente);
		Direccion d = cliente.agregarDireccion(request);
		usuarioDAO.guardarUsuario(cliente);
		return d;

	}

	@Override
	public void inicializarListasDe(Vendedor vendedor) {
		usuarioDAO.inicializarListasDe(vendedor);

	}

	@Override
	public void editarDireccionDe(String mail, DireccionRequest request, Integer idDireccion)
			throws DireccionesInexistentes, UsuarioInexistenteException, RequestIncorrectoException {
		validarDireccionRequest(request);
		Cliente cliente = (Cliente) usuarioDAO.obtenerUsuarioPorEmail(mail);
		if (cliente == null) {
			throw new UsuarioInexistenteException("No existe el usuario");
		}
		if (idDireccion == null) {
			throw new DireccionesInexistentes("No existe la direccion que se desea editar");
		}
		inicializarDirecciones(cliente);
		cliente.editarDireccionCon(request, idDireccion);
		usuarioDAO.guardarUsuario(cliente);
	}

	@Override
	public void eliminarDireccionDe(String mail, Integer idDireccion)
			throws DireccionesInexistentes, UsuarioInexistenteException {
		Cliente c = (Cliente) usuarioDAO.obtenerUsuarioPorEmail(mail);
		if (c == null) {
			throw new UsuarioInexistenteException("No existe el usuario");
		}
		c.eliminarDireccion(idDireccion);
		usuarioDAO.guardarUsuario(c);

	}
	
	public void onStartUp() throws Exception  {
		Usuario root = usuarioDAO.obtenerUsuarioPorNombre(usuarioAdmin);
		if (root == null) {
			Vendedor user = new Vendedor();
			user.setUsername(usuarioAdmin);
			user.setPassword(encrypter.encrypt(passwordAdmin));
			user.setEmail(mailAdmin);
			user.setIsRoot(true);
			Imagen img = new Imagen();
			img.setNombre("perfil.jpg");
			img.setPath(imagenAdmin);
			user.setImagenPerfil(img.getPath());
			this.guardarUsuario(user);
		}
		
	}

	public void eliminarUsuario(Vendedor u) {
		usuarioDAO.eliminarUsuario(u);

	}

	@Override
	public List<Notificacion> obtenerNotificacionesDe(String mail, Integer pagina) {
		return usuarioDAO.obtenerNotificacionesDe(mail, pagina);
	}

	@Override
	public void enviarInvitacionRequest(String origen, String destino) throws Exception {

		Cliente clienteOrigen = (Cliente) usuarioDAO.obtenerUsuarioPorEmail(origen);
		Cliente clienteDestino = (Cliente) usuarioDAO.obtenerUsuarioPorEmail(destino);
		if (clienteDestino != null) {
			// enviar Notificacion de creacion de grupo
		} else {
			mailService.enviarEmailDeInvitacionChasqui(clienteOrigen, destino);
		}

	}
	//TODO: generar validaciones mas interesante, ie: telefono mayor a 8 numeros.
	private void validarRequestCreacionDeUsuario(SingUpRequest request)
			throws RequestIncorrectoException, UsuarioExistenteException {
		if (StringUtil.isEmpty(request.getEmail())) {
			throw new RequestIncorrectoException("Debe completar el email");
		}

		if (!EmailValidator.getInstance().isValid(request.getEmail())) {
			throw new RequestIncorrectoException("Email invalido");
		}

		if (StringUtil.isEmpty(request.getApellido())) {
			throw new RequestIncorrectoException("Se requiere un apellido");
		}

		if (StringUtil.isEmpty(request.getNickName())) {
			throw new RequestIncorrectoException("Debe escribir un Nick para el usuario");
		}

		if (StringUtil.isEmpty(request.getNombre())) {
			throw new RequestIncorrectoException("Se requiere un nombre");
		}
		if (StringUtil.isEmpty(request.getPassword())) {
			throw new RequestIncorrectoException("Debe escribir un password");
		}
		// if(request.getDireccion() == null){
		// throw new RequestIncorrectoException("Debe completar todos los
		// campos");
		// }
		
		if ((request.getTelefonoFijo() == null || request.getTelefonoFijo().equals("")) && (request.getTelefonoMovil() == null || request.getTelefonoMovil().equals(""))) {
			throw new RequestIncorrectoException("Debe completar almenos un telefono");
		}

		// validarDireccion(request.getDireccion());

		if (this.existeUsuarioCon(request.getEmail())) {
			throw new UsuarioExistenteException("El email ya se encuentra en uso");
		}
		
		if(request.getPassword().length() < 10 || request.getPassword().length() > 26){
			throw new RequestIncorrectoException(Constantes.PASSWORD_CORTO);
		}
	}

	private void validarDireccionRequest(DireccionRequest request) throws RequestIncorrectoException {
		if (StringUtils.isEmpty(request.getAlias())) {
			throw new RequestIncorrectoException();
		}
		if (StringUtils.isEmpty(request.getCalle())) {
			throw new RequestIncorrectoException();
		}
		if (request.getPredeterminada() == null) {
			throw new RequestIncorrectoException();
		}
		if (StringUtils.isEmpty(request.getLocalidad())) {
			throw new RequestIncorrectoException();
		}
		if (request.getAltura() == null || request.getAltura() < 0) {
			throw new RequestIncorrectoException();
		}

	}

	@Override
	public void agregarIDDeDispositivo(String mail, String dispositivo) throws UsuarioInexistenteException {
		Cliente c = (Cliente) this.obtenerUsuarioPorEmail(mail);
		if (c != null) {
			c.setIdDispositivo(dispositivo);
			this.guardarUsuario(c);
		}

	}

	@Override
	public List<Notificacion> obtenerNotificacionesNoLeidas(String mail) {
		return usuarioDAO.obtenerNotificacionNoLeidas(mail);
	}

	@Override
	public Integer obtenerTotalNotificacionesDe(String mail) {
		return usuarioDAO.obtenerTotalNotificacionesDe(mail);
	}

	@Override
	public void leerNotificacion(Integer id) {
		Notificacion n = usuarioDAO.obtenerNotificacion(id);
		if (n != null) {
			n.setEstado("Leído");
			usuarioDAO.guardar(n);
		}

	}



	@Override
	public void inicializarColecciones(Cliente cliente){
		this.usuarioDAO.inicializarColecciones(cliente);
	}
	
	@Override
	public void inicializarDirecciones(Cliente cliente) {
		this.usuarioDAO.inicializarDirecciones(cliente);
	}

	@Override
	public void inicializarPedidos(Cliente cliente) {
		this.usuarioDAO.inicializarPedidos(cliente);
	}

	@Override
	public void inicializarHistorial(Cliente cliente) {
		this.usuarioDAO.inicializarHistorial(cliente);
	}

	@Override
	public void editarAvatarDe(Cliente cliente, String avatar, String extension) throws Exception {
		byte[] decodedBytes = org.apache.commons.codec.binary.Base64.decodeBase64(avatar.getBytes(StandardCharsets.UTF_8));
		InputStream inputStreamAvatar = new ByteArrayInputStream(decodedBytes);
		
		//Se valida la imagen
		this.isvalidImage(inputStreamAvatar, extension);
		
		//Se guarda la imagen en el servidor.
		String path = servletContext.getRealPath("/imagenes");
		Imagen imagen = this.fileSaver.guardarImagen(path, 
													 cliente.getId().toString(), 
													 cliente.getId().toString() + "_avatar" + extension, 
													 decodedBytes);
		
		//Poner el path de la imagen en el usuario antes de persistir su informacion.
		cliente.setImagenPerfil(imagen.getPath());
		
		System.out.println("Imagen de perfil guardada en " + cliente.getImagenPerfil());
		usuarioDAO.guardarUsuario(cliente);
		
		//Actualizar los objetos MiembroDeGCC del cliente
		this.actualizarAvataresDeMiembroDeGCC(cliente.getId(), imagen.getPath());
	}

	public void actualizarAvataresDeMiembroDeGCC(Integer idCliente, String avatar){
		List<MiembroDeGCC> pertenencias = miembroDeGCCDao.obtenerMiembrosDeGCCParaCliente(idCliente);
		for(MiembroDeGCC miembro : pertenencias){
			miembro.setAvatar(avatar);
			miembroDeGCCDao.actualizarMiembroDeGCC(miembro);
		}
	}
	
	public void isvalidImage(InputStream inputStreamAvatar, String extension) throws Exception {
		List<String> extensionesValidas = Arrays.asList(".gif", ".jpeg", ".png", ".jpg"); 
		if(!extensionesValidas.contains(extension)){
		    throw new Exception(extension + " no es una extension valida");
	}
	
	/*TODO:
	 * Arreglar validaciones de los MagicNumber de cada formato permitido
		
		if(extension.equals(".gif")){
			this.isValidGIF(inputStreamAvatar);
		}
		if(extension.equals(".jpeg")){
			this.isValidJPEG(inputStreamAvatar);
		}
		if(extension.equals(".png")){
			this.isValidPNG(inputStreamAvatar);
		}
	*/
	}

	/**
	 * Check if the image is a PNG. The first eight bytes of a PNG file always
	 * contain the following (decimal) values: 137 80 78 71 13 10 26 10 / Hex:
	 * 89 50 4e 47 0d 0a 1a 0a
	 * @throws Exception 
	 */
	public void isValidPNG(InputStream is) throws Exception {
		//TODO no funciona correctamente. Ver si es necesario este nivel de validacion.
	    try {
	        byte[] b = new byte[8];
	        is.read(b, 0, 8);
	        if (Arrays.equals(b, new BigInteger("89504e470d0a1a0a",16).toByteArray())) {
	            return;
	        }
	    } catch (Exception e) {
	    	throw new Exception("No es un PNG valido");
	    }
	    throw new Exception("No es un PNG valido");
	}

	/**
	 * Check if the image is a JPEG. JPEG image files begin with FF D8 and end
	 * with FF D9
	 * @throws Exception 
	 */
	public void isValidJPEG(InputStream is) throws Exception {
		//TODO no funciona correctamente. Ver si es necesario este nivel de validacion.
		try {
	        byte[] b = new byte[2];
	        Integer antePenultimo = -2;
	        Integer anteUltimo = -2;
	        Integer ultimo = -2;
	        is.read(b, 0, 2);
	        // check first 2 bytes:
	        if ((b[0]&0xff) != 0xff || (b[1]&0xff) != 0xd8) {
	        	throw new Exception("No es un JPEG valido");
	        }
	        // check last 2 bytes:
	        while(ultimo != -1){
	        	antePenultimo = anteUltimo;
	        	anteUltimo = ultimo;
	        	ultimo = is.read();
	        }
	        
	        //if ((b[0]&0xff) != 0xff || (b[1]&0xff) != 0xd9)
	        if ( antePenultimo != 0xff || anteUltimo != 0xd9) {
	        	throw new Exception("No es un JPEG valido");
	        }
	    } catch (Exception e) {
	        // Ignore
	    	throw new Exception("No es un JPEG valido");
	    }
	}

	/** Check if the image is a valid GIF. GIF files start with GIF and 87a or 89a.
	 * http://www.onicos.com/staff/iz/formats/gif.html
	 * @throws Exception 
	*/
	public void isValidGIF(InputStream is) throws Exception {
		//TODO no funciona correctamente. Ver si es necesario este nivel de validacion.
		try {
	        byte[] b=new byte[6];
	        is.read(b, 0, 6);
	        //check 1st 3 bytes
	        if(b[0]!='G' || b[1]!='I' || b[2]!='F') {
	        	throw new Exception("No es un GIF valido");
	        }
	        if(b[3]!='8' || !(b[4]=='7' || b[4]=='9') || b[5]!='a') {
	        	throw new Exception("No es un GIF valido");
	        }
	    } catch(Exception e) {
	    	throw new Exception("No es un GIF valido");
	    }
	}

	@Override
	public String obtenerAvatar(String mail) throws IOException {
		//obtener usuario
		Usuario usuario = usuarioDAO.obtenerUsuarioPorEmail(mail);
		
		//obtener ubicacion de la imagen, luego imagen
		File sourceImage = new File(usuario.getImagenPerfil());
		byte[] fileContent = Files.readAllBytes(sourceImage.toPath());
		
		String avatar = Base64.getEncoder().encodeToString(fileContent);

		return avatar;
	}
	
	public FileSaver getFileSaver(){
		return this.fileSaver;
	}
	
	public String getServerAbsolutPath(){
		return this.serverAbsolutPath;
	}

}
