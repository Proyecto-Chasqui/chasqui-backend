package chasqui.services.impl;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
/**
 * Este servicio se encarga de monitorizar las sesiones y previniendo multiples sesiones para un mismo usuario.
 * @author david
 *
 */
public class SessionListenerService {
	private Map<String,HttpSession> sessions = new HashMap<String,HttpSession>();
	public SessionListenerService() {
		
	}
	
	public void addOrReplaceSession(String username, HttpSession session) {
		if(sessions.containsKey(username)) {
			sessions.get(username).invalidate();
			sessions.replace(username,session);
			printSession(session,"-------------- SESION REEMPLAZADA PARA " + username +" -----------\r");
		}else {
			sessions.put(username, session);
			printSession(session,"---------------- NUEVA SESION PARA " + username +" -----------\r");
		}
	}

	public void removeSession(String username) {
		printSession(sessions.get(username),"---------------- SESION ELIMINADA DE "+ username +" -----------\r");
		sessions.remove(username);
	}
	
	private void printSession(HttpSession hses, String type) {
		StringBuilder result = new StringBuilder();
        result.append(type);
        result.append("HttpSession\r");
        result.append(".getId():\t\t\t" + hses.getId() + "\r");
        result.append(".getCreationTime():\t\t" + new Date(hses.getCreationTime()).toString() + "\r");
        result.append(".getLastAccessedTime():\t\t" + new Date(hses.getLastAccessedTime()).toString() + "\r");
        result.append("---------------- FIN DE REPORTE DE SESION -----------\r");

        System.out.println(result.toString());
	}
}
