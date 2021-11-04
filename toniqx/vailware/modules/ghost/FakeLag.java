package toniqx.vailware.modules.ghost;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventPacket;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.Timer;
import toniqx.vailware.modules.Module;
import toniqx.vailware.modules.Module.Category;

public class FakeLag extends Module {
	
	private NumberSetting ping = new NumberSetting("Ping", 100, 20, 1000, 1);
	private static ArrayList<Packet> packets = new ArrayList<Packet>();
	private Packet sendPacket = null;
	
	public FakeLag(){
		super("FakeLag", "", Keyboard.KEY_NONE, Category.GHOST);
		this.addSettings(ping);
	}
	
	public void onEnable() {
		packets.clear();
		super.onEnable();
	}
	
	public void onDisable() {
		packets.clear();
	}
	
	private Timer timer = new Timer();
	
	public void onEvent(Event e) {
		
		if (e instanceof EventPacket) {
			
			if (e.isPre()) {
				
				EventPacket sendPacket = (EventPacket) e;
				
				if (this.sendPacket != null && sendPacket.packet instanceof C00PacketKeepAlive && sendPacket.packet == this.sendPacket) {
					
					this.sendPacket = null;
					
				}
				
				else if (sendPacket.packet instanceof C00PacketKeepAlive) {
					
					packets.add(sendPacket.packet);
					sendPacket.setCancelled(true);
					
				}
				
			}
			
		}
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				if (packets.size() > 500) {
					
					for (int i = 1; i < packets.size(); i++) {
						
						packets.remove(i);
						
					}
					
				}
				
				if (timer.hasTimeElapsed((long) (ping.getValue()), true)) {
					
					if (packets.size() >= 1) {
						
						this.sendPacket = packets.get(0);
						mc.thePlayer.sendQueue.addToSendQueue(packets.get(0));
						packets.remove(0);
						
					}
					
				}
				
				
			}
			
		}
		
	}
	
}