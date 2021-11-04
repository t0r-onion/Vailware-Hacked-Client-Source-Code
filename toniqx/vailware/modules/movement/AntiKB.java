package toniqx.vailware.modules.movement;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0CPacketBoatInput;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventRotationMotion;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventMove;
import toniqx.vailware.main.event.process.EventPacket;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.modules.Module;

public class AntiKB extends Module {

	public ModeSetting mode = new ModeSetting("Mode", "Packet", "Packet", "Redesky", "Ground", "Spoof", "Motion");
	
	public AntiKB(){
		super("AntiKB", "", Keyboard.KEY_NONE, Category.MOVEMENT);
		this.addSettings(mode);
	}
	
    public static boolean shouldEnable;
    boolean velHurt;
    long atkTimer;
    long velTimer;
	
    public void onEvent(Event e) {
    	if(mode.is("Packet")) {
			super.mname = ChatFormatting.GRAY + "Packet" + "   ";
	        if (e instanceof EventPacket && e.isPre() && e.isIncoming()) {
	
	            EventPacket event = (EventPacket) e;
	            Packet<?> packet = event.getPacket();
	            if (packet instanceof S12PacketEntityVelocity) {
	
	            	S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) packet;
	            	
	            	if (s12.getEntityID() == mc.thePlayer.getEntityId()) {
	            		e.setCancelled(true);
	            	}
	            }
	            
	            if (packet instanceof S27PacketExplosion) {
	                e.setCancelled(true);
	            }
	        }
    	}else if(mode.is("Spoof")) {
			super.mname = ChatFormatting.GRAY + "Spoof" + "   ";
            if (e instanceof EventPacket && e.isPre() && e.isIncoming()) {

                EventPacket event = (EventPacket) e;
                Packet<?> packet = event.getPacket();

                if (packet instanceof S12PacketEntityVelocity) {

                    S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) packet;

                    if (s12.getEntityID() == mc.thePlayer.getEntityId()) {
                    	sendPosition((((S12PacketEntityVelocity) packet).getMotionX() / 8000), (((S12PacketEntityVelocity) packet).getMotionY()) / 8000, (((S12PacketEntityVelocity) packet).getMotionZ()) / 8000, mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, (((S12PacketEntityVelocity) packet).getMotionY()) / 8000, 0.0)).size() > 0, mc.thePlayer.moveForward > 0);
                    }
                }
                
	            if (packet instanceof S27PacketExplosion) {
	                e.setCancelled(true);
	            }
            }
    	}else if(mode.is("Motion")) {
			super.mname = ChatFormatting.GRAY + "Motion" + "   ";
    		if (mc.thePlayer.hurtTime > 0) {
        		mc.thePlayer.motionY = 0.75F;
        		mc.thePlayer.motionX = 0.75F;
        		mc.thePlayer.motionZ = 0.75F;
    		}
    	}else if(mode.is("Ground")) {
			super.mname = ChatFormatting.GRAY + "Ground" + "   ";
    		if (mc.thePlayer.hurtTime > 0) {
    			mc.thePlayer.onGround = true;
    		}
    	}else if(mode.is("Redesky")) {
    		super.mname = ChatFormatting.GRAY + "Redesky" + "   ";
    		if(mc.thePlayer.hurtTime > 0 || Client.killAura.target != null) {
				if(e instanceof EventRotationMotion) {
		            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.28, mc.thePlayer.posZ, false));
				}
    			mc.thePlayer.onGround = true;
    		}
    	}
    }
    
    public static void sendPosition(double x, double y, double z, boolean ground, boolean moving) {
        if (!moving) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + y, mc.thePlayer.posZ, ground));
        } else {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z, ground));
        }
    }

}

