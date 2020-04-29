package chasqui.view.renders;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import chasqui.model.Cliente;
import chasqui.model.MiembroDeGCC;
import chasqui.services.interfaces.UsuarioService;

public class MiembrosGCCRenderer implements ListitemRenderer<MiembroDeGCC>{
	private Window nodoWindow;
	public MiembrosGCCRenderer(Window w){
		nodoWindow = w;
	}
	@Override
	public void render(Listitem item, MiembroDeGCC data, int index) throws Exception {
		
		UsuarioService clienteService = (UsuarioService) SpringUtil.getBean("usuarioService");
		String nombre;
		String email;
		String telfijo;
		String celular;
		//verificar cuando el cliente no existe en chasqui.
		Cliente datacliente = clienteService.obtenerClientePorEmail(data.getEmail()); 
		nombre = datacliente.getNombre() + " " + datacliente.getApellido();
		email = datacliente.getEmail();
		telfijo = (datacliente.getTelefonoFijo().equals(""))? "N/D" : datacliente.getTelefonoFijo();
		celular = (datacliente.getTelefonoMovil().equals(""))? "N/D" : datacliente.getTelefonoMovil();

		//c.addForward(Events.ON_CLICK, this, Events.ON_NOTIFY, params1);
		
		Listcell c1 = new Listcell(String.valueOf(nombre));
		Listcell c2 = new Listcell(String.valueOf(email));
		Listcell c3 = new Listcell(String.valueOf(telfijo));
		Listcell c4 = new Listcell(String.valueOf(celular));
		
		Listcell c100 = new Listcell(); //Se usa como padre de las demas
	
		
		Hlayout hbox = new Hlayout();
		//hbox.setParent(c100);
		c1.setParent(item);
		c2.setParent(item);
		c3.setParent(item);
		c4.setParent(item);
		//c100.setParent(item); //Padre de las demas
		
	}

}
