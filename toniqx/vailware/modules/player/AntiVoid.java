package toniqx.vailware.modules.player;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventRotationMotion;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventPacket;
import toniqx.vailware.main.notification.Notification;
import toniqx.vailware.main.notification.impl.NotificationManager;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.util.impl.Data6d;
import toniqx.vailware.main.util.impl.ScaffoldMovementUtils;
import toniqx.vailware.main.util.impl.ServerUtils;
import toniqx.vailware.main.util.impl.Timer;
import toniqx.vailware.main.util.impl.AntiVoid.AntiVoidRandomUtils;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.modules.Module;

public class AntiVoid extends Module {
	
	public static ModeSetting mode = new ModeSetting("Mode", "Lagback", "Lagback", "Hypixel", "Packet");
	protected int cast = 0;
    int ticks;
    int startY;
    public boolean isVoid;
	
	public AntiVoid() {
		super("AntiVoid", "", Keyboard.KEY_NONE, Category.PLAYER);
		this.addSettings(mode);
	}
	
	public void onEnable() {
		super.onEnable();
	}
	
	public void onDisable() {
		cast = 0;
		super.onDisable();
	}
	
	public void onEvent(Event e) {
		if(mode.is("Lagback")) {
			if(mc.thePlayer != null && mc.theWorld != null) {
				if(mc.thePlayer.fallDistance > 3 && !MovementUtil.isBlockUnder()) {
					cast++;
					mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ);
					if(cast > 1) {
						cast = 0;
					}
				
				}
				if(mc.thePlayer.onGround && cast > 0) {
					cast = 0;
				}
			}
		}else if(mode.is("Packet")) {
			if (e instanceof EventPacket) {
	            final Packet p = ((EventPacket)e).getPacket();
	            if (p instanceof S08PacketPlayerPosLook) {
	                final S08PacketPlayerPosLook s08PacketPlayerPosLook = (S08PacketPlayerPosLook)((EventPacket)e).getPacket();
	            }
	        }
	        if (e instanceof EventUpdate) {
	            ++this.ticks;
	            this.isVoid = true;
	            for (int y = this.mc.thePlayer.getPosition().getY(); y >= 0; --y) {
	                if (!this.mc.theWorld.isAirBlock(new BlockPos(this.mc.thePlayer.getPosition().getX(), y, this.mc.thePlayer.getPosition().getZ()))) {
	                    this.isVoid = false;
	                    return;
	                }
	            }
	            if (this.mc.thePlayer.fallDistance >= 5.0f && this.mc.thePlayer.motionY < -0.2 && this.isVoid && this.ticks > 40) {
	                this.mc.thePlayer.fallDistance = 0.0f;
	                final double playerYaw = Math.toRadians(this.mc.thePlayer.rotationYaw);
	                this.ticks = 0;
	                this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 1.5, this.mc.thePlayer.posZ, false));
	                this.mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 50.0, this.mc.thePlayer.posZ, false));
	            }
	        }
		}else if(mode.is("Hypixel")) {
			if(mc.thePlayer != null && mc.theWorld != null) {
				if(e instanceof EventRotationMotion) {
    				EventRotationMotion event = (EventRotationMotion) e;
					
		            if (mc.thePlayer.fallDistance > 3 && !MovementUtil.isBlockUnder()) {
		                event.setX(-999);
		                event.setY(-999);
		                event.setZ(-999);
		                mc.thePlayer.fallDistance = 0;
		            }
				}
			}
	    }
	}
}
