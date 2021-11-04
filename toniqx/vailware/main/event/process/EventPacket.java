package toniqx.vailware.main.event.process;

import net.minecraft.network.Packet;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.EventDirection;

public class EventPacket extends Event<EventPacket> {

    public Packet packet;

    public EventPacket(Packet packet, EventDirection direction) {
        this.packet = packet;
        this.direction = direction;
    }


    public <T extends Packet> T getPacket() {
        return (T) packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

}
