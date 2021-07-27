package chasqui.view.genericEvents;

import java.util.Map;
import java.util.HashMap;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

public class CreatedListener<T extends ICreatedCallback> implements EventListener<Event> {

  public static String ON_CREATED = "onCreatedNewObject";

  public static Object createData(String objectType, Object newObject) {
    Map<String,Object>params = new HashMap<String,Object>();
    params.put("objectType", objectType);
    params.put("newObject", newObject);
    return params;
  }

	T composer;
	
	public CreatedListener(T composer){
		this.composer = composer;
	}
	
	public void onEvent(Event event) throws Exception {
		if(event.getName().equals(ON_CREATED)){
      Object data =  event.getData();
      Map<String,Object> params = (Map<String,Object>) data;
      String objectType = (String) params.get("objectType");
      Object newObject = params.get("newObject");
			composer.onCreatedCallback(objectType, newObject);
		}
		
	}
}