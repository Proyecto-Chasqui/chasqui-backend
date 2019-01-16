package chasqui.service.websocket.messages;

import java.util.List;

public class Messages {
	private List<StatusMessage> mensajes;
	
	public Messages(List<StatusMessage> mensajes) {
		setMensajes(mensajes);
	}

	public List<StatusMessage> getMensajes() {
		return mensajes;
	}

	public void setMensajes(List<StatusMessage> mensajes) {
		this.mensajes = mensajes;
	}
}
