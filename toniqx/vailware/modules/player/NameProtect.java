package toniqx.vailware.modules.player;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.util.ChatComponentText;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventReceivePacket;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventMove;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.modules.Module;

public class NameProtect extends Module {
	
	public static String name = "Me";
	
	public NameProtect() {
		super("NameProtect", "", Keyboard.KEY_NONE, Category.PLAYER);
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
		}
		
		if (e instanceof EventReceivePacket && e.isPre()) {
			
			EventReceivePacket packetEvent = (EventReceivePacket) e;
			if (packetEvent.packet instanceof S02PacketChat) {
				
				S02PacketChat packet = (S02PacketChat) packetEvent.packet;
				
				if (packet.getChatComponent().getUnformattedText().replaceAll("", "").contains(mc.getSession().getUsername())) {
					packet.chatComponent = new ChatComponentText(packet.getChatComponent().getFormattedText().replaceAll("", "").replaceAll(mc.getSession().getUsername(), name));
				}
				
			}
			else if (packetEvent.packet instanceof S3CPacketUpdateScore) {
				S3CPacketUpdateScore packet = (S3CPacketUpdateScore) packetEvent.packet;
				
				if (packet.getObjectiveName().replaceAll("", "").contains(mc.getSession().getUsername())){
					packet.setObjective(packet.getObjectiveName().replaceAll("", "").replaceAll(mc.getSession().getUsername(), name));
				}
				
				if (packet.getPlayerName().replaceAll("", "").contains(mc.getSession().getUsername())){
					packet.setName(packet.getPlayerName().replaceAll("", "").replaceAll(mc.getSession().getUsername(), name));
				}
				
			}
			
		}
		
	}
	
}