package toniqx.vailware.main.event.listeners;

import toniqx.vailware.main.event.Event;

public class EventKey extends Event<EventKey> {

	public int code;
	
	public EventKey(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
}
