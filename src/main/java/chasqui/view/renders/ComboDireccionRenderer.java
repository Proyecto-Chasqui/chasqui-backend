package chasqui.view.renders;

import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;

import chasqui.model.Direccion;

public class ComboDireccionRenderer implements ComboitemRenderer<Direccion>{
	
	public void render(Comboitem item, Direccion c, int arg2) throws Exception {
		item.setLabel("" +c.getAlias());
		
	}

}
