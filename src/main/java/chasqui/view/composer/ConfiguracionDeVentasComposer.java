package chasqui.view.composer;

import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

import chasqui.model.Vendedor;
import chasqui.services.interfaces.UsuarioService;

public class ConfiguracionDeVentasComposer extends GenericForwardComposer<Component>{
	
	private Vendedor vendedorLogueado;
	private AnnotateDataBinder binder;
	private UsuarioService usuarioService;
	private Button permitirVentasButton;
	private boolean ventasPermitidas;
	private Textbox textboxMensaje;
	private Component component;
	
	public void doAfterCompose(Component comp) throws Exception{
		vendedorLogueado =(Vendedor) Executions.getCurrent().getSession().getAttribute(Constantes.SESSION_USERNAME);
		super.doAfterCompose(comp);
		component = comp;
		usuarioService = (UsuarioService) SpringUtil.getBean("usuarioService");
		this.ventasPermitidas = vendedorLogueado.isVentasHabilitadas();
		syncButtonVentasHabilitadas();
		binder = new AnnotateDataBinder(comp);
		binder.loadAll();			
	}
	
	private void syncButtonVentasHabilitadas() {
		if(ventasPermitidas) {
			permitirVentasButton.setLabel("Si");
			permitirVentasButton.setImage("/imagenes/if_toggle-right.png");
		}else {
			permitirVentasButton.setLabel("No");
			permitirVentasButton.setImage("/imagenes/if_toggle-left.png");
		}
		String mensaje = vendedorLogueado.getMensajeVentasDeshabilitadas();
		if( mensaje != null) {
			textboxMensaje.setValue(mensaje);
		}		
	}

	public void onClick$permitirVentasButton() {
		if(ventasPermitidas) {
			permitirVentasButton.setLabel("No");
			permitirVentasButton.setImage("/imagenes/if_toggle-left.png");
			this.ventasPermitidas = !ventasPermitidas;
		}else {
			permitirVentasButton.setLabel("Si");
			permitirVentasButton.setImage("/imagenes/if_toggle-right.png");
			this.ventasPermitidas = !ventasPermitidas;
		}
	}
	
	public void onClick$guardarButtonVentas() {
		vendedorLogueado.setVentasHabilitadas(ventasPermitidas);
		vendedorLogueado.setMensajeVentasDeshabilitadas(textboxMensaje.getValue());
		usuarioService.guardarUsuario(vendedorLogueado);
		Clients.showNotification("Los cambios se guardaron correctamente", "info", component, "middle_center", 3000,true);
	}


	public Vendedor getVendedorLogueado() {
		return vendedorLogueado;
	}

	public Button getPermitirVentasButton() {
		return permitirVentasButton;
	}

	public Textbox getTextboxMensaje() {
		return textboxMensaje;
	}

	public void setVendedorLogueado(Vendedor vendedorLogueado) {
		this.vendedorLogueado = vendedorLogueado;
	}

	public void setPermitirVentasButton(Button permitirVentasButton) {
		this.permitirVentasButton = permitirVentasButton;
	}

	public void setTextboxMensaje(Textbox textboxMensaje) {
		this.textboxMensaje = textboxMensaje;
	}
}
