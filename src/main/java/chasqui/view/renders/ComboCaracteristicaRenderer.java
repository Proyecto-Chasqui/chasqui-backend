package chasqui.view.renders;


import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.Image;

import chasqui.services.interfaces.ICaracteristica;

public class ComboCaracteristicaRenderer implements ComboitemRenderer<ICaracteristica> {

	public void render(Comboitem item, ICaracteristica c, int arg2) throws Exception {
		item.setLabel("   " +c.getNombre());
		
	}

}
