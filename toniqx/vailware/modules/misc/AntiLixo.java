package toniqx.vailware.modules.misc;

import org.lwjgl.input.Keyboard;

import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.util.ChatComponentText;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventReceivePacket;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.modules.Module;

public class AntiLixo extends Module {
	
	public AntiLixo(){
		super("AntiLixo", "", Keyboard.KEY_NONE, Category.MISC);
	}
	
	@Override
	public void onEvent(Event e) {
		if (e instanceof EventReceivePacket && e.isPre()) {
			
			EventReceivePacket packetEvent = (EventReceivePacket) e;
			if (packetEvent.packet instanceof S02PacketChat) {
				
				S02PacketChat packet = (S02PacketChat) packetEvent.packet;
				
				if (packet.getChatComponent().getUnformattedText().toLowerCase().replaceAll("", "").contains("lixo")) {
					mc.thePlayer.sendChatMessage("você esqueceu de clicar?");
				}

			}
			
		}
		
	}
	
}