package toniqx.vailware.modules.movement;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0CPacketBoatInput;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovementInput;
import toniqx.vailware.Client;
import toniqx.vailware.main.bypass.Bypass;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventReceivePacket;
import toniqx.vailware.main.event.listeners.EventRotationMotion;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.listeners.PreMotionEvent;
import toniqx.vailware.main.event.process.EventPacket;
import toniqx.vailware.main.notification.Notification;
import toniqx.vailware.main.notification.impl.Color;
import toniqx.vailware.main.notification.impl.NotificationManager;
import toniqx.vailware.main.notification.impl.Type;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.PlayerUtils;
import toniqx.vailware.main.util.impl.RenderUtils;
import toniqx.vailware.main.util.impl.TimeHelper;
import toniqx.vailware.main.util.impl.Timer;
import toniqx.vailware.main.util.impl.TimerUtils;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.modules.Module;

public class Fly extends Module {

    float oldPitch;
    boolean hasPearled;
    Timer t;
	NumberSetting speed = new NumberSetting("Speed", 1, 0.1, 2, 0.1);
	NumberSetting speedncp = new NumberSetting("NCPSpeed", 1, 1, 10, 0.1);
	NumberSetting vticks = new NumberSetting("Verus Ticks", 1600, 0, 2000, 1);
	public static ModeSetting mode = new ModeSetting("Mode", "Creative", "NCP Dev", "Motion", "Creative", "EnderPearl", "Vicnix", "CubeCraft", "Rededark", "Damage", "Verus");
	private List<Packet> packets = new CopyOnWriteArrayList<>();
	
	public boolean hypixelDamaged = false;
	
	public Fly(){
		super("Fly", "", Keyboard.KEY_NONE, Category.MOVEMENT);
		this.addSettings(mode, speed, vticks, speedncp);
	}
	
	public int verusCooldown;
	
	public TimerUtils timer = new TimerUtils();
	
	public void onEnable() {
		verusCooldown = 100;
		timer.reset();
		
		if(mode.is("NCP Dev") && MovementUtil.isBlockUnder()) {
			mc.thePlayer.jump();
		}
		packets.clear();
		if(Client.premium && mode.is("Damage")) {
			if(mc.thePlayer.onGround) {
				Bypass.damage();
			}else {
				NotificationManager.getNotificationManager().createNotification( "Error", ChatFormatting.GRAY + "Must be on the Ground!", true, 1500, Type.ERROR, Color.GREEN);
				super.toggle();
			}
		}
		else if(!Client.premium && mode.is("Damage")){
			Client.noPremiumMSG("Damage", "Fly");
			super.toggle();
		}
		
		if(Client.premium && mode.is("Verus")) {
			if(mc.thePlayer.onGround) {
				Bypass.damage();
			}else {
				NotificationManager.getNotificationManager().createNotification( "Error", ChatFormatting.GRAY + "Must be on the Ground!", true, 1500, Type.ERROR, Color.GREEN);
				super.toggle();
			}
		}
		else if(!Client.premium && mode.is("Verus")){
			Client.noPremiumMSG("Verus", "Fly");
			super.toggle();
		}
	}
	
	public void onDisable(){
		hypixelDamaged = false;
		mc.thePlayer.motionX = 0;
		mc.thePlayer.motionZ = 0;
		packets.clear();
		mc.thePlayer.capabilities.isFlying = false;
		mc.timer.timerSpeed = 1.0f;
		mc.thePlayer.capabilities.allowFlying = false;
	}
	
	
	
	public void onEvent(Event e){
		if(mode.is("EnderPearl")) {
			if(e instanceof EventRotationMotion) {
	            final int oldPitch = (int) mc.thePlayer.rotationPitch;
	            if (mc.thePlayer.hurtTime > 0) {
	                hypixelDamaged = true;
	            }
	            if (mc.thePlayer.onGround) {
	                mc.thePlayer.jump();
	                for(int i = 0; i < 9; i++) {
	                    final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
	                    if (mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemEnderPearl) {
	                    	mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(i));
	                    	mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, 90, mc.thePlayer.onGround));
	                    	mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
	                    	mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
	                    	mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, oldPitch, mc.thePlayer.onGround));
	                    }
	                }
	            }
			}


            if (hypixelDamaged) {
                mc.thePlayer.motionY = 0;
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.thePlayer.motionY = 1;
                } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.thePlayer.motionY = -1;
                }
               	mc.thePlayer.setSpeed(speed.getValue());
            }
        }
		if (mode.is("Creative")) {
			super.mname = "Creative" + " ";
			mc.timer.timerSpeed = 1.0f;
			mc.thePlayer.capabilities.setFlySpeed((float) speed.getValue() / 4);
			mc.thePlayer.capabilities.allowFlying = true;
			mc.thePlayer.capabilities.isFlying = true;
		}
		else if(mode.is("NCP Dev")) {
			super.mname = "NCP Dev" + " ";
			if(e instanceof EventRotationMotion) {
	            MovementUtil.setMotion(MovementUtil.getBaseMoveSpeed() * speedncp.getValue());
	            if (mc.thePlayer.ticksExisted % 2 == 0) {
	                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.28, mc.thePlayer.posZ, true));
	            } else {
	                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.28, mc.thePlayer.posZ, false));
	
	            }
	            mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketBoatInput(0, 0, true, true));
	            mc.thePlayer.motionY = 0;

			}
        }
		else if(mode.is("Vicnix")) {
			super.mname = "Vicnix" + " ";
			mc.timer.timerSpeed = 2;
			mc.thePlayer.onGround = false;
			mc.thePlayer.jumpMovementFactor = 0;
			if (mc.thePlayer.ticksExisted % 3 == 0) {
				mc.thePlayer.motionY = 0.080000000298023224;
				setMoveSpeed(2);
			} else {
				mc.thePlayer.motionY = -0.04;
				setSpeed(0);
			}

		}
		else if (mode.is("Damage")) {
			super.mname = "Damage" + " ";
			if(!Client.premium) {
				super.toggleSilent();
				Client.noPremiumMSG("Damage", "Fly");
			}
			mc.timer.timerSpeed = 1f;
			mc.thePlayer.capabilities.setFlySpeed((float) speed.getValue() / 4);
			mc.thePlayer.capabilities.isFlying = true;
			
			if(TimeHelper.hasReached(300L)) {
				mc.timer.timerSpeed = 0.9F;
				TimeHelper.reset();
			}
		}
		else if (mode.is("Rededark")) {
			super.mname = "Rededark" + " ";
			if(mc.gameSettings.keyBindForward.pressed) {
				mc.thePlayer.setSpeed(speed.getValue());
			}else {
				mc.thePlayer.setSpeed(0);
			}
			
			if(!mc.gameSettings.keyBindJump.pressed || mc.gameSettings.keyBindSneak.pressed) {
				mc.thePlayer.motionY = 0;
			}
			
			if(mc.gameSettings.keyBindJump.pressed) {
				mc.thePlayer.motionY = 0.2362D;
			}
			
			if(mc.gameSettings.keyBindSneak.pressed) {
				mc.thePlayer.motionY = -0.2363D;
			}
			
			mc.thePlayer.cameraYaw = 0.090909086F * 2;
			mc.timer.timerSpeed = 1.6F;
			
			if(mc.thePlayer.isInvisible()) {
				mc.timer.timerSpeed = 1.6F;
			}
			
			if(TimeHelper.hasReached(1000L)) {

				TimeHelper.reset();
			}
		}
		else if (mode.is("Verus")) {
			mc.gameSettings.keyBindBack.pressed = false;
			mc.gameSettings.keyBindLeft.pressed = false;
			mc.gameSettings.keyBindRight.pressed = false;
			super.mname = "Verus" + " ";
			if(e instanceof EventRotationMotion) {
				EventRotationMotion event = (EventRotationMotion) e;
				
				event.setOnGround(true);
				mc.thePlayer.onGround = true;
				mc.thePlayer.motionY = 0;
				if(mc.gameSettings.keyBindForward.pressed) {
					mc.thePlayer.setSpeed(.15);
				}
			}
			
			if(timer.hasReached(1000)) {
				if(timer.delay((float) vticks.getValue())) {
					mc.thePlayer.setSpeed(.15);
					mc.timer.timerSpeed = 1f;
				}else {
					mc.thePlayer.setSpeed(speed.getValue() * 3);
					mc.timer.timerSpeed = 1f;
					MovementUtil.doStrafe();
				}
			}
		}else if(mode.is("CubeCraft")) {
			super.mname = "CubeCraft" + " ";
            if (MovementUtil.isMoving()) {
                if (mc.thePlayer.ticksExisted % 3 == 0) {
                    mc.thePlayer.motionY = .2;
                    mc.timer.timerSpeed = 1F;
                    MovementUtil.doStrafe(2);
                } else {
                    mc.thePlayer.motionY = -.1;
                    mc.timer.timerSpeed = .5F;
                    MovementUtil.doStrafe(.1);
                }
            } else {
                stop(true);
                mc.timer.timerSpeed = 1F;
            }
		}else if(mode.is("Motion")) {
			super.mname = "Motion" + " ";
			mc.thePlayer.fallDistance = 0.0f;
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionY = 0.0;
            mc.thePlayer.motionZ = 0.0;


            final EntityPlayerSP entityPlayerSP = mc.thePlayer;
            entityPlayerSP.posY += 0.1;
            entityPlayerSP.posY -= 0.1;
            
            MovementUtil.doStrafe((float) 3);
            
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                entityPlayerSP.motionY += 1.5;
            }
            if (this.mc.gameSettings.keyBindSneak.isKeyDown()) {
            	entityPlayerSP.motionY -= 1.5;
            }
		}
	}
	
	public void vClip(int d) {
		mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + d, mc.thePlayer.posZ);
	}
	
	public void setDiagSpeed(float f) {
		double playerYaw = Math.toRadians(mc.thePlayer.rotationYaw + 90);
		mc.thePlayer.motionX = f * -Math.sin(playerYaw);
		mc.thePlayer.motionZ = f * Math.cos(playerYaw);
	}
	public void setMoveSpeed(int speed) {
		if (mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()) {
			setDiagSpeed(speed*-mc.thePlayer.moveStrafing);
		} else {
			setSpeed(speed * mc.thePlayer.moveForward);
		}
	}
	
	public void setSpeed(float f) {
		double playerYaw = Math.toRadians(mc.thePlayer.rotationYaw);
		mc.thePlayer.motionX = f * -Math.sin(playerYaw);
		mc.thePlayer.motionZ = f * Math.cos(playerYaw);
	}
	
	public final static void stop(boolean y) {
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
        if (y) mc.thePlayer.motionY = 0;
    }
	
}
