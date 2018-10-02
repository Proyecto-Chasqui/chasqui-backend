package chasqui.view.composer;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.cxf.common.util.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import chasqui.model.Usuario;
import chasqui.model.Vendedor;
import chasqui.services.impl.MailService;
import chasqui.services.interfaces.UsuarioService;

@SuppressWarnings({ "serial", "deprecation" })
public class LoginComposer  extends GenericForwardComposer<Component>{

	
	
	private AnnotateDataBinder binder;
	private Textbox usernameLoggin;
	private Textbox passwordLoggin;
	private Label labelError;
	private Button logginButton;
	private Popup emailPopUp;
	private Toolbarbutton olvidoSuPassword;
	private Textbox emailTextbox;
	private Button cerrarPopUpButton;
	private Window loginWindow;
	
	private UsuarioService service;
	private MailService mailService;

	@Override
	public void doAfterCompose(Component comp) throws Exception{
		Vendedor u = (Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		if(u != null){
			Executions.sendRedirect("/administracion.zul");
		}
		super.doAfterCompose(comp);
		binder = new AnnotateDataBinder(comp);
		service = (UsuarioService) SpringUtil.getBean("usuarioService");
		mailService = (MailService) SpringUtil.getBean("mailService");
		comp.addEventListener(Events.ON_NOTIFY, new EnvioEmailListener(this));
	}
	
	
	public void onClick$logginButton() throws Exception{
		String password = passwordLoggin.getValue();
		String usuario = usernameLoggin.getValue();
//		Vendedor v = (Vendedor) SecurityContextHolder.getContext().getAuthentication().getCredentials();
		if (!password.matches("^[a-zA-Z0-9]*$") || password.length() < 8){
			labelError.setVisible(true);
			passwordLoggin.setValue("");
			usernameLoggin.setValue("");
			binder.loadAll();
			return;
		};
		Vendedor user = null;
		try{
			
			user =(Vendedor) service.login(usuario,password);
			service.inicializarListasDe(user);
			Executions.getCurrent().getSession().setAttribute(Constantes.SESSION_USERNAME, user);
			Executions.sendRedirect("/administracion.zul");
		}catch(Exception e){
			labelError.setVisible(true);
			passwordLoggin.setValue("");
			usernameLoggin.setValue("");
		}
		
	}
	public void onOK$enter() throws Exception {
		this.onClick$logginButton();  
	}
	
	public void onOlvidoPassword(){
		emailPopUp.open(olvidoSuPassword);
	}
	
	
	public void bloquearPantalla(String msg){
		Clients.showBusy(msg);
	}
	
	public void desbloquearPantalla(){
		Clients.clearBusy();
	}
	
	public void onClick$emailButton(){
		bloquearPantalla("Procesando...");
		Events.echoEvent(Events.ON_NOTIFY, loginWindow, null);
	}
	
	public void onEnviarEmail() {
		try{
			String email = emailTextbox.getValue();
			if(StringUtils.isEmpty(email) || !EmailValidator.getInstance().isValid(email)){
				throw new WrongValueException(emailTextbox,"Por favor ingrese un email valido.");
			}
			Usuario u = service.obtenerUsuarioPorEmail(email);
			if(u != null && u instanceof Vendedor){
				mailService.enviarEmailRecuperoContraseña(email, u.getUsername());				
				Messagebox.show("El Email ha sido enviado con exito!","Información",Messagebox.OK,Messagebox.INFORMATION);
				desbloquearPantalla();
				emailPopUp.close();
			}else{
				desbloquearPantalla();
				throw new WrongValueException(emailTextbox,"Por favor ingrese un email valido.");
			}
		}catch(Exception e){
			desbloquearPantalla();
			throw new WrongValueException(emailTextbox,e.getMessage());
		}		
	}
	
	public void onClick$cerrarPopUpButton(){
		emailPopUp.close();
	}
	
	public Textbox getEmailTextbox() {
		return emailTextbox;
	}


	public void setEmailTextbox(Textbox emailTextbox) {
		this.emailTextbox = emailTextbox;
	}


	public Button getLogginButton() {
		return logginButton;
	}


	public void setLogginButton(Button logginButton) {
		this.logginButton = logginButton;
	
	}
	public Label getLabelError() {
		return labelError;
	}


	public void setLabelError(Label labelError) {
		this.labelError = labelError;
	}

	
	public Button getCerrarPopUpButton() {
		return cerrarPopUpButton;
	}


	public void setCerrarPopUpButton(Button cerrarPopUpButton) {
		this.cerrarPopUpButton = cerrarPopUpButton;
	}


	public Toolbarbutton getOlvidoSuPassword() {
		return olvidoSuPassword;
	}


	public void setOlvidoSuPassword(Toolbarbutton olvidoSuPassword) {
		this.olvidoSuPassword = olvidoSuPassword;
	}


	public AnnotateDataBinder getBinder() {
		return binder;
	}

	public void setBinder(AnnotateDataBinder binder) {
		this.binder = binder;
	}

	public Textbox getUsernameLoggin() {
		return usernameLoggin;
	}

	public void setUsernameLoggin(Textbox usernameLoggin) {
		this.usernameLoggin = usernameLoggin;
	}

	public Textbox getPasswordLoggin() {
		return passwordLoggin;
	}

	public void setPasswordLoggin(Textbox passwordLoggin) {
		this.passwordLoggin = passwordLoggin;
	}


	public Popup getEmailPopUp() {
		return emailPopUp;
	}


	public void setEmailPopUp(Popup emailPopUp) {
		this.emailPopUp = emailPopUp;
	}
	
}

class EnvioEmailListener implements EventListener<Event>{
	
	LoginComposer composer;
	
	public EnvioEmailListener (LoginComposer l){
		composer = l;
	}
	
	public void onEvent(Event event) throws Exception {
		composer.onEnviarEmail();
		
	}
	
}
