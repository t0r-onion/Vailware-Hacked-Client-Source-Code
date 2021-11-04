package toniqx.vailware.modules.movement;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import toniqx.vailware.Client;
import toniqx.vailware.main.bypass.Bypass;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventRotationMotion;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventMove;
import toniqx.vailware.main.event.process.EventPacket;
import toniqx.vailware.main.notification.Notification;
import toniqx.vailware.main.notification.impl.NotificationManager;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.TimerUtils;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.modules.Module;


public class LongJump extends Module {

    public boolean boosted;
    public boolean finished;
    public double posY = 0;
    public MovementUtil move;
    public double motionY;
    public NumberSetting yboost = new NumberSetting("YBoost", 2, 1, 5, 0.1);
    public NumberSetting timerSpeed = new NumberSetting("Timer", 0.8, 0.4, 1, 0.01);
    public NumberSetting speed = new NumberSetting("Speed", 0.1, 0.02, 0.5, 0.01);
    public BooleanSetting damage = new BooleanSetting("Damage", false);
    public BooleanSetting blink = new BooleanSetting("Blink", false);
	private ArrayList<Packet> packetList = new ArrayList<>();
    
    public LongJump() {
        super("LongJump", "", Keyboard.KEY_NONE, Category.MOVEMENT);
        addSettings(speed, yboost, timerSpeed, damage, blink);
    }
    
    public TimerUtils timer = new TimerUtils();

    public void onEnable() {
   	 	finished = false;
    	if(damage.enabled) {
    		Bypass.damage();
    	}
    }

    public void onDisable() {
		mc.timer.timerSpeed = 1f;
    	mc.thePlayer.speedInAir = 0.02F;
        try {
            for (Packet packets : packetList) {
                mc.getNetHandler().sendPacketNoEvent(packets);
            }
            packetList.clear();
        }
        catch (final ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }
    
    public void onEvent(Event e) {
    	mc.gameSettings.keyBindJump.pressed = false;
    	if(!mc.thePlayer.isInWater() && !mc.thePlayer.isInLava()) {
    		if(e instanceof EventUpdate && e.isPost()) {
    			mc.timer.timerSpeed = (float) timerSpeed.getValue();
    			double airSpeedReduce = 0.04 - (0.08 * (0.0 / 100));
    			mc.thePlayer.speedInAir = (float) airSpeedReduce;
    			if(mc.thePlayer.onGround) {
    				if(!finished && mc.thePlayer.moveForward != 0) {
    					mc.thePlayer.jump();
    				}
    				if(finished) {
    					toggle();
    				}
    				if(mc.thePlayer.onGround) {
    					finished = true;
    				}
    			} else {
    				mc.thePlayer.motionY += yboost.getValue() / 80;
    			}
    		}
            if(e instanceof EventPacket) {
            	if(blink.enabled) {
	                if(((EventPacket) e).getPacket() instanceof C0APacketAnimation || ((EventPacket) e).getPacket() instanceof C03PacketPlayer || ((EventPacket) e).getPacket() instanceof C00PacketKeepAlive || ((EventPacket) e).getPacket() instanceof C07PacketPlayerDigging || ((EventPacket) e).getPacket() instanceof C08PacketPlayerBlockPlacement) {
	                    e.setCancelled(true);
	                    packetList.add(((EventPacket) e).getPacket());
	                }
            	}
            }
    	}
    }
}