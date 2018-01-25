package chasqui.view.composer;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.cxf.common.util.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import chasqui.exceptions.UsuarioInexistenteException;
import chasqui.exceptions.VendedorInexistenteException;
import chasqui.model.Usuario;
import chasqui.model.Vendedor;
import chasqui.security.Encrypter;
import chasqui.services.impl.MailService;
import chasqui.services.interfaces.UsuarioService;
import chasqui.services.interfaces.VendedorService;

@SuppressWarnings({"serial","deprecation"})
public class AltaUsuarioComposer extends GenericForwardComposer<Component> {

	@SuppressWarnings("unused")
	private Button buttonGuardar;
	private Textbox textboxEmail;
	private Textbox textboxNombre;
	private Textbox textboxContraseñaRepita;
	private Textbox textboxContraseña;
	private Textbox textboxUsername;
	private Window usuariosActualesWindow;
	private AnnotateDataBinder binder;
	private Vendedor model;
	
	private UsuarioService service;
	private MailService mailService;
	private Encrypter encrypter;
	private VendedorService vendedorService;
	private Textbox textboxUrlBase;
	
	private String passwordInicial;
	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		binder = new AnnotateDataBinder(comp);
		service = (UsuarioService) SpringUtil.getBean("usuarioService");
		mailService = (MailService) SpringUtil.getBean("mailService");
		vendedorService = (VendedorService) SpringUtil.getBean("vendedorService");
		encrypter = (Encrypter) SpringUtil.getBean("encrypter");
		comp.addEventListener(Events.ON_NOTIFY, new GuardarUsuarioEventListener(this));
		comp.addEventListener(Events.ON_USER, new GuardarUsuarioEventListener(this));
		binder.loadAll();
	}
	
	
	public void onClick$buttonGuardar(){
		validacionesParaGuardar();
		this.bloquearPantalla("Guardando Nuevo Usuario...");
		Events.echoEvent(Events.ON_NOTIFY,self,null);
	}
	
	private void validacionesParaGuardar() {
		String username = textboxUsername.getValue();
		String email = textboxEmail.getValue();
		String nombre = textboxNombre.getValue();
		String urlBase = textboxUrlBase.getValue();
		if(StringUtils.isEmpty(username)){
			throw new WrongValueException(textboxUsername,"El usuario no deber ser vacio!");
		}
		
		if(StringUtils.isEmpty(nombre)){
			throw new WrongValueException(textboxNombre,"El nombre no deber ser vacio!");
		}
		
		if(StringUtils.isEmpty(urlBase)){
			throw new WrongValueException(textboxUrlBase,"Se debe ingresar la pagina de chasqui del vendedor");
		}
		
		if(email != null && !EmailValidator.getInstance().isValid(email)){
			throw new WrongValueException(textboxEmail,"Por favor ingrese un email valido.");
		}
		
		Vendedor v = null;
		try{		
			v = vendedorService.obtenerVendedor(username);			
		}catch(VendedorInexistenteException e){
			
		}
		if(v != null && model != null && !v.getId().equals(model.getId())){
			throw new WrongValueException(textboxNombre,"Ya existe el usuario con el nombre ingresado");
		}
		Usuario u;
		try {
			u = service.obtenerUsuarioPorEmail(email);
			if( (u != null && model == null) || (u != null && model != null && !u.getId().equals(model.getId())) ){
				throw new WrongValueException(textboxEmail,"Ya existe el usuario con el mail ingresado");
			}
		} catch (UsuarioInexistenteException e) {
			
		}
		
		validarPassword();
	}

	
	private void validarPassword(){
		String nuevaClave = textboxContraseña.getValue();
		String nuevaClaveRepita = textboxContraseñaRepita.getText();	
		
		if(StringUtils.isEmpty(nuevaClave)){
			throw new WrongValueException(textboxContraseña,"La contraseña no debe ser vacia!");
		}
			
		if(StringUtils.isEmpty(nuevaClaveRepita)){
			throw new WrongValueException(textboxContraseñaRepita,"La contraseña no debe ser vacia!");
		}
		
		if(!StringUtils.isEmpty(nuevaClaveRepita) && !nuevaClave.equals(nuevaClaveRepita)){
			WrongValueException e1 = new WrongValueException(textboxContraseña,"Las contraseñas no coinciden!");
			WrongValueException e2 = new WrongValueException(textboxContraseñaRepita,"Las contraseñas no coinciden!");
			throw new WrongValuesException(new WrongValueException[] {e1,e2});
		}		
		if(!StringUtils.isEmpty(nuevaClave) && (!nuevaClave.matches("^[a-zA-Z0-9]*$")|| nuevaClave.length() < 8)){
		  throw new WrongValueException(textboxContraseña,"La nueva contraseña no cumple con los requisitos, debe tener al menos 8 caracteres, entre letras y números");
		}
	}
	
	private Vendedor actualizarVendedor(String nombre,String username,String email,String pwd, String urlBase) throws Exception{
		if(model != null){
			model.setUsername(username);
			model.setEmail(email);
			model.setNombre(nombre);
			model.setPassword(encrypter.encrypt(pwd));
			model.setUrl(urlBase);
			if(model.getImagenPerfil() == null){
				model.setImagenPerfil("/imagenes/usuarios/ROOT/perfil.jpg");				
			}
			return model;
		}
		return new Vendedor(nombre,username,email,encrypter.encrypt(pwd),urlBase);
	}
	
	
	public void onBlur$textboxUsername(){
		chequearTodosLosCamposEnBlanco();
	}
	
	public void onBlur$textboxContraseña(){
		chequearTodosLosCamposEnBlanco();
	}
	
	public void onBlur$textboxContraseñaRepita(){
		chequearTodosLosCamposEnBlanco();
	}
	
	public void onBlur$textboxEmail(){
		chequearTodosLosCamposEnBlanco();
	}
	

	public void onBlur$textboxNombre(){
		chequearTodosLosCamposEnBlanco();
	}
	
	public void chequearTodosLosCamposEnBlanco(){
		String username = textboxUsername.getValue();
		String email = textboxEmail.getValue();
		String nombre = textboxNombre.getValue();
		String nuevaClave = textboxContraseña.getValue();
		String nuevaClaveRepita = textboxContraseñaRepita.getText();	
		
		if(StringUtils.isEmpty(username) && StringUtils.isEmpty(nombre) && StringUtils.isEmpty(email) && StringUtils.isEmpty(nuevaClave) && StringUtils.isEmpty(nuevaClaveRepita)){
			model = null;
		}
		this.binder.loadAll();
	}
	
	public void guardar(){
		try{      	
			//Guardar
			String username = textboxUsername.getValue();
			String email = textboxEmail.getValue();
			String pwd = textboxContraseña.getValue();
			String nombre = textboxNombre.getValue();
			String urlBase = textboxUrlBase.getValue();
			if(!textboxContraseña.getValue().equals("")){
				pwd = textboxContraseña.getValue();
			}else{
				pwd = encrypter.decrypt(passwordInicial);
			}
			Vendedor v = actualizarVendedor(nombre,username,email,pwd,urlBase);
			if(v.getId() == null){
				mailService.enviarEmailBienvenidaVendedor(email, username, pwd);
			}
			service.guardarUsuario(v);
			
			Map<String,Object>params = new HashMap<String,Object>();
			params.put("usuario", v);
			params.put("accion", "crear");
			Events.sendEvent(Events.ON_NOTIFY, usuariosActualesWindow, params);
		}catch(Exception e){
			e.printStackTrace();
			alert(e.getMessage());
		}finally{
			desbloquearPantalla();
			limpiarCampos();
		}
	}
	
	public void limpiarCampos(){
		textboxEmail.setValue(null);
		textboxNombre.setValue(null);
		textboxContraseñaRepita.setValue(null);
		textboxContraseña.setValue(null);
		textboxUsername.setValue(null);
		textboxUrlBase.setValue(null);
		binder.loadAll();
	}
	
	public void bloquearPantalla(String msg){
		Clients.showBusy(msg);
	}
	
	private void desbloquearPantalla(){
		Clients.clearBusy();
	}
	
	public void llenarCombosConUser(Vendedor user){
		limpiarCampos();
		textboxUsername.setValue(user.getUsername());
		textboxEmail.setValue(user.getEmail());
		textboxUrlBase.setValue(user.getUrl());		
		// parche momentaneo hasta que todos los vendedores que fueron dados de alta ANTES de agregar este campo
		// lo tengan incluido
		if(user.getNombre() != null){
			textboxNombre.setValue(user.getNombre());			
		}
		passwordInicial = user.getPassword();
		model = user;
	}


	public Window getUsuariosActualesWindow() {
		return usuariosActualesWindow;
	}
	public void setUsuariosActualesWindow(Window usuariosActualesWindow) {
		this.usuariosActualesWindow = usuariosActualesWindow;
	}


	public Textbox getTextboxUrlBase() {
		return textboxUrlBase;
	}


	public void setTextboxUrlBase(Textbox textboxUrlBase) {
		this.textboxUrlBase = textboxUrlBase;
	}
	
	
	
	
}

class GuardarUsuarioEventListener implements EventListener<Event>{

	AltaUsuarioComposer composer;
	
	public GuardarUsuarioEventListener(AltaUsuarioComposer composer){
		this.composer = composer;
	}
	
	public void onEvent(Event event) throws Exception {
		if(event.getName().equals(Events.ON_USER)){
			if(event.getData() instanceof Window){
				Window usuariosActualesWindow = (Window) (event.getData());
				composer.setUsuariosActualesWindow(usuariosActualesWindow);				
			}else{
				@SuppressWarnings("unchecked")
				Map<String,Object> params = (Map<String,Object>) event.getData();
				if(params.get("accion").equals("editar")){
					composer.llenarCombosConUser((Vendedor) params.get("usuario"));					
				}
				if(params.get("accion").equals("eliminar")){
					composer.limpiarCampos();
				}
			}
		}else{
			composer.guardar();			
		}
		
	}
	
}
