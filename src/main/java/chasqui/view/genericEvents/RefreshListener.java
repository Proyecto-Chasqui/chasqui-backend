package chasqui.view.genericEvents;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

public class RefreshListener<T extends Refresher> implements EventListener<Event> {

	T composer;
	
	public RefreshListener(T composer){
		this.composer = composer;
	}
	
	
	public void onEvent(Event event) throws Exception {
		if(event.getName().equals(Events.ON_RENDER)){
			composer.refresh();			
		}
		
	}

}
