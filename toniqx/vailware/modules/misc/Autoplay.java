package toniqx.vailware.modules.misc;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.process.EventPacket;
import toniqx.vailware.modules.Module;

public class Autoplay extends Module {
	
	public Autoplay() {
        super("Autoplay", "", Keyboard.KEY_NONE , Category.MISC);
    }

    public void onEvent(Event e) {
    	if (e instanceof EventPacket) {

            EventPacket event = (EventPacket) e;
            Packet<?> packet = event.getPacket();

            if (packet instanceof S02PacketChat) {
                S02PacketChat s02PacketChat = (S02PacketChat) packet;

                if (!s02PacketChat.getChatComponent().getUnformattedText().isEmpty()) {
                    String message = s02PacketChat.getChatComponent().getUnformattedText();

                    if (message.contains("You won! Want to play again?") || message.contains("You died! Want to play again?") && isHypixel()) {
                        Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C01PacketChatMessage("/play solo_insane"));
                        if (message.contains("You won! Want to play again?") && isHypixel() && Minecraft.getMinecraft().thePlayer.ticksExisted % 25 == 0) {
                        	Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage("/play solo_insane"));
                        }
                    }

                }
            }
        }
    }
    public boolean isHypixel () {
        return Minecraft.getMinecraft().getCurrentServerData().serverIP.contains("hypixel");
    }
    
}
