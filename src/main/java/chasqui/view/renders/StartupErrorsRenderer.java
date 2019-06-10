package chasqui.view.renders;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;

public class StartupErrorsRenderer implements ListitemRenderer<String> {

	Window admWindow;
	
	public StartupErrorsRenderer(Component w){
		admWindow = (Window) w;
	}
	
	@Override
	public void render(Listitem item, String data, int index) throws Exception {
		Listcell c1 = new Listcell(data);
		c1.setParent(item);
	}

}
