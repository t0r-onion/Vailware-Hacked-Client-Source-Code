package toniqx.vailware.main.event.listeners;

import net.minecraft.network.Packet;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.EventDirection;
import toniqx.vailware.main.event.types.EventType;

public class EventReceivePacket extends Event{
	
	public Packet packet;
	
	public EventReceivePacket(EventType type, EventDirection dir, Packet packet) {
		
		this.setType(type);
		this.setDirection(dir);
		this.packet = packet;
		
	}
	
}