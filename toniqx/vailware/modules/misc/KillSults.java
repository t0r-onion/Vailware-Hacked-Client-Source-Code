package toniqx.vailware.modules.misc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S02PacketChat;
import toniqx.vailware.Client;
import toniqx.vailware.main.command.Command;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventReceivePacket;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.modules.Module;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;

public class KillSults extends Module {
	
	public KillSults() {
		super("KillSults", "", Keyboard.KEY_NONE, Category.MISC);
	}
	
	public int i;
	
	@Override
	public void onEvent(Event e) {
		if (e instanceof EventReceivePacket && e.isPre()) {
			
			EventReceivePacket packetEvent = (EventReceivePacket) e;
			if (packetEvent.packet instanceof S02PacketChat) {
				
				S02PacketChat packet = (S02PacketChat) packetEvent.packet;
				if (packet.getChatComponent().getUnformattedText().toLowerCase().replaceAll("", "").contains("foi morto por " + ChatFormatting.GRAY + mc.thePlayer.getName().toLowerCase()) || packet.getChatComponent().getUnformattedText().toLowerCase().replaceAll("", "").contains("no void por " + mc.thePlayer.getName().toLowerCase()) || packet.getChatComponent().getUnformattedText().toLowerCase().replaceAll("", "").contains("morrue para " + ChatFormatting.GRAY + mc.thePlayer.getName().toLowerCase())) {
					if(i == 1) {
						mc.thePlayer.sendChatMessage("Kids like you lead to the creation of birth control!");
					}
					else if(i == 2) {
						mc.thePlayer.sendChatMessage("Sucks too suck doesnt it?");
					}
					else if(i == 3) {
						mc.thePlayer.sendChatMessage("Here im feeling generous, take an L.");
					}
					else if(i == 4) {
						mc.thePlayer.sendChatMessage("Why are you mad bruh, its just a block game.");
					}
					else if(i == 5) {
						mc.thePlayer.sendChatMessage("Friend me so we can talk about how useless you are.");
					}
					else if(i == 6) {
						mc.thePlayer.sendChatMessage("If you had Vailware maybe you wouldnt be so bad.");
					}
					else if(i == 7) {
						mc.thePlayer.sendChatMessage("Hey, Awkward to be a spectator now isn't it?");
					}
					else if(i == 8) {
						mc.thePlayer.sendChatMessage("Good gaming chair, obviously you dont have one loser.");
					}
					else if(i == 9) {
						mc.thePlayer.sendChatMessage("I bet you look like you were drawn with my left hand.");
					}
					else if(i == 10) {
						mc.thePlayer.sendChatMessage("Sheeesh, my dinner has better aim than you, and its dead.");
					}
					else if(i == 11) {
						mc.thePlayer.sendChatMessage("If everyone was that bad, losing wouldnt be so pathetic.");
					}
					i++;
					if(i > 11) {
						i = 0;
					}
				}

			}
			
		}
		
	}
}
