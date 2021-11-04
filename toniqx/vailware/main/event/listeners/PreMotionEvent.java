package toniqx.vailware.main.event.listeners;

import toniqx.vailware.main.event.Event;

public class PreMotionEvent extends Event {

	public double x;
	public double y;
	public double z;

	public PreMotionEvent(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Object getLocation() {
		// TODO Auto-generated method stub
		return null;
	}
}
