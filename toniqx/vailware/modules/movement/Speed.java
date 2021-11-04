package toniqx.vailware.modules.movement;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MathHelper;
import toniqx.vailware.Client;
import toniqx.vailware.main.bypass.Bypass;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventRotationMotion;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventMove;
import toniqx.vailware.main.event.process.EventPacket;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.TimerUtils;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.modules.Module;

public class Speed extends Module {
	
	public static ModeSetting smode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Verus", "Jartex", "Redesky", "LowHop", "YPort", "NCP", "GodHop");
	public NumberSetting mspeed = new NumberSetting("Speed", 1, 1, 2, 0.1);

	public Speed(){
		super("Speed", "", Keyboard.KEY_NONE, Category.MOVEMENT);
		this.addSettings(smode, mspeed);
	}
	
	public static double speed = 2F;
	
	public void onDisable(){
		mc.timer.timerSpeed = 1.0f;
		mc.thePlayer.speedInAir = 0.02f;
		mc.gameSettings.keyBindJump.pressed = false;
	}
	
	public TimerUtils timer = new TimerUtils();
	
	public void onEvent(Event e){
		if(Client.getModule("Fly").isEnabled())
			return;
        if(e instanceof EventUpdate) {
            if(e.isPre()) {
            	if(mc.thePlayer.moveForward > 0 && !mc.thePlayer.isUsingItem() && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally) {
            			mc.thePlayer.setSprinting(true);
            	}
				if(smode.is("Verus")) {
					super.mname = ChatFormatting.GRAY + "Verus" + "   ";
					if(!mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava()) {
						mc.gameSettings.keyBindJump.pressed = false;
						if (!MovementUtil.isMoving()) {
							MovementUtil.stop(false);
							mc.timer.timerSpeed = 1f;
							return;
						}
						if (mc.thePlayer.onGround) {
							mc.thePlayer.jump();
						} else {
							mc.thePlayer.moveStrafing = 2f;
							if(!Client.getModule("Scaffold").isEnabled()) {
								MovementUtil.doStrafe(mspeed.getValue() / 6);
							}
						}
					}
				}
				else if(smode.is("Vanilla")) {
					super.mname = ChatFormatting.GRAY + "Vanilla" + "   ";
				    if((mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0)&& !mc.thePlayer.isSneaking() && mc.thePlayer.onGround) {
				    	mc.thePlayer.motionX *= mspeed.getValue() / 1.5f;
				    	mc.thePlayer.motionZ *= mspeed.getValue() / 1.5f;
				    	mc.gameSettings.keyBindJump.pressed = false;
				    }
				}
				else if(smode.is("YPort")) {
					super.mname = ChatFormatting.GRAY + "YPort" + "   ";
					if(!mc.thePlayer.isInWater() && mc.thePlayer.moveForward != 0  && !mc.thePlayer.isInLava()) {
						if(mc.thePlayer.onGround) {
							mc.thePlayer.jump();
						}else {
							if(!(mc.thePlayer.fallDistance > 0)) {
								mc.thePlayer.motionY = -mspeed.getValue() / 9;
							}
						}
					}
				}
				else if(smode.is("LowHop")) {
					super.mname = ChatFormatting.GRAY + "LowHop" + "   ";
					if(!Client.premium) {
						super.toggle();
						Client.noPremiumMSG("LowHop", "Speed");
					}
				    if((mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0)&& !mc.thePlayer.isSneaking() && mc.thePlayer.onGround && !Client.getModule("AirJump").isEnabled()) {
				    	mc.thePlayer.jump(); 
				    	mc.thePlayer.motionX *= mspeed.getValue() / 1.4;
				    	mc.thePlayer.motionY *= 0.6;
				    	mc.thePlayer.motionZ *= mspeed.getValue() / 1.4;
				    	mc.gameSettings.keyBindJump.pressed = false;
				    }
				}
				else if(smode.is("Redesky")) {
					mc.gameSettings.keyBindJump.pressed = false;
					super.mname = ChatFormatting.GRAY + "Redesky" + "   ";
					if(mc.gameSettings.keyBindForward.pressed && !mc.thePlayer.isOnLadder() && !Client.getModule("Scaffold").isEnabled() && !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava()) {
						mc.gameSettings.keyBindJump.pressed = true;
					}
				}
				else if(smode.is("GodHop")) {
					super.mname = ChatFormatting.GRAY + "GodHop" + "   ";
					if(!mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava()) {
						mc.gameSettings.keyBindJump.pressed = false;
						if (!MovementUtil.isMoving()) {
							MovementUtil.stop(false);
							mc.timer.timerSpeed = 1f;
							return;
						}
						if (mc.thePlayer.onGround) {
							mc.thePlayer.jump();
						} else {
							mc.thePlayer.moveStrafing = 200f;
							if(!Client.getModule("Scaffold").isEnabled()) {
								MovementUtil.doStrafe(mspeed.getValue() / 2);
							}
						}
					}
				}
				else if(smode.is("Jartex")) {
					mc.gameSettings.keyBindJump.pressed = false;
					super.mname = ChatFormatting.GRAY + "Jartex" + "   ";
					if(!mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava()) {
						mc.gameSettings.keyBindJump.pressed = false;
						if (!MovementUtil.isMoving()) {
							MovementUtil.stop(false);
							mc.timer.timerSpeed = 1f;
							return;
						}
						if (mc.thePlayer.onGround) {
							mc.thePlayer.jump();
							mc.timer.timerSpeed = 2.0f;
						} else {
							mc.thePlayer.moveStrafing = 0.2f;
							if(!Client.getModule("Scaffold").isEnabled()) {
								MovementUtil.doStrafe();
							}
							mc.timer.timerSpeed = 1f;
						}
					}
				}
				else if(smode.is("NCP")) {
					mc.gameSettings.keyBindJump.pressed = false;
					super.mname = ChatFormatting.GRAY + "NCP" + "   ";
					if(mc.gameSettings.keyBindForward.pressed && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava()) {
						if(mc.thePlayer.onGround) {
							mc.timer.timerSpeed = 1f;
							mc.gameSettings.keyBindJump.pressed = false;
							double speedValue = 0.27;
							double yaw = Math.toRadians(this.mc.thePlayer.rotationYaw);
							double x = -Math.sin(yaw) * speedValue;
							double z = Math.cos(yaw) * speedValue;
							mc.thePlayer.onGround = true;
							mc.thePlayer.motionX = x;
							mc.thePlayer.motionZ = z;
							mc.thePlayer.jump();
						}else {
							if(timer.hasReached(1100)) {
								mc.timer.timerSpeed = 0.7f;
								timer.reset();
							}else {
								mc.timer.timerSpeed = 2.22f;
							}
						}
					}
				}
            }
        }
	}
}