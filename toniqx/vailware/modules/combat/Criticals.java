package toniqx.vailware.modules.combat;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.BlockPos;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventRotationMotion;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventPacket;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.util.impl.MathUtils;
import toniqx.vailware.main.util.impl.ScaffoldMovementUtils;
import toniqx.vailware.main.util.impl.Timer;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.main.util.impl.server.Stopwatch;
import toniqx.vailware.modules.Module;
import net.minecraft.block.material.Material;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;


public class Criticals extends Module {
	
	public static ModeSetting smode = new ModeSetting("Mode", "Packet", "Packet", "Hypixel", "Minis");
	
	public Criticals(){
		super("Criticals", "", Keyboard.KEY_NONE, Category.COMBAT);
		this.addSettings(smode);
	}
	
	public Timer timer = new Timer();

	private int FallStack;
	
	private static transient int stage = 1;
			
	public void onEvent(Event e){
		if (smode.is("Packet")) {
			super.mname = ChatFormatting.GRAY + "Packet" + "   ";
			if (e instanceof EventPacket) {
				Packet packet = ((EventPacket)e).packet;
					
				if (packet instanceof C02PacketUseEntity) {
					
					if (!ScaffoldMovementUtils.isOnGround(0.0000001)) {
						return;
					}
						
					C02PacketUseEntity attack = (C02PacketUseEntity) packet;
						
					if (timer.hasTimeElapsed(250, true) && attack.getAction() == Action.ATTACK) {
							
						double[] crits = new double[] {0.11, 0.1100013579, 0.1090013579};
							
						for(short i = 0; i < crits.length; i++) {
							double offset = crits[i];
							mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
						}
				            
						try {
							mc.thePlayer.onCriticalHit(mc.theWorld.getEntityByID(attack.getEntityId()));
						} catch (Exception e2) {
							
						}
				            
						}
						
					}
				}
    	}
		else if (smode.is("Hypixel")) {
			super.mname = ChatFormatting.GRAY + "Hypixel" + "   ";
            if (e.isPre()) {
				EventRotationMotion event = (EventRotationMotion) e;
                if (mc.thePlayer.motionY < 0 && MovementUtil.isBlockUnder() && mc.thePlayer.onGround && !Client.getModule("Speed").isEnabled() && !Client.getModule("Flight").isEnabled() && !mc.gameSettings.keyBindJump.isKeyDown() && mc.thePlayer.fallDistance == 0) {
                    event.setOnGround(false);
                    if (FallStack >= 0 && FallStack < 0.1 && mc.thePlayer.ticksExisted % 2 == 0) {
                        double value = 0.0624 + MathUtils.getRandomInRange(1E-8, 1E-7);
                        FallStack += value;
                        event.setY(mc.thePlayer.posY + value);
                    } else {
                        event.setY(mc.thePlayer.posY + 1E-8);
                        if (FallStack < 0) {
                            FallStack = 0;
                            event.setOnGround(true);
                            event.setY(mc.thePlayer.posY);
                        }
                    }
                }
            }
    	}
        if(e instanceof EventUpdate) {
            if(e.isPre()) {
            	if (smode.is("Minis")) {
					super.mname = ChatFormatting.GRAY + "Minis" + "   ";
					if (e instanceof EventPacket) {
						Packet packet = ((EventPacket)e).packet;
						if (packet instanceof C02PacketUseEntity) {
							
							C02PacketUseEntity attack = (C02PacketUseEntity) packet;
								
							if (attack.getAction() == Action.ATTACK) {
								if(!mc.thePlayer.isInWater() && !mc.thePlayer.isInsideOfMaterial(Material.lava) && mc.thePlayer.onGround){
									mc.thePlayer.motionY = 0.2F;
										mc.thePlayer.fallDistance = 1F;
										mc.thePlayer.onGround = false;
								}
							}
						}
                	}
            	}
            }
        }
	}
}