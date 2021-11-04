package toniqx.vailware.modules.world;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventReceivePacket;
import toniqx.vailware.main.event.process.EventPacket;
import toniqx.vailware.main.notification.Notification;
import toniqx.vailware.main.notification.impl.Color;
import toniqx.vailware.main.notification.impl.NotificationManager;
import toniqx.vailware.main.notification.impl.Type;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.util.impl.ScaffoldMovementUtils;
import toniqx.vailware.main.util.impl.ServerUtils;
import toniqx.vailware.modules.Module;

public class NoRotate extends Module {
	
	public NoRotate() {
		super("NoRotate", "", Keyboard.KEY_NONE, Category.EXPLOIT);
	}
	
	public void onEnable() {
		super.onEnable();
		if (ServerUtils.isOnHypixel()) {
			NotificationManager.getNotificationManager().createNotification( "Warning", ChatFormatting.YELLOW + this.getName() + " " + ChatFormatting.GRAY + "Doesnt bypass Hypixel!", true, 1500, Type.WARNING, Color.GREEN);
			toggle();
		}
		
	}
	
    public void onEvent(Event e) {
    	if (e instanceof EventPacket && e.isIncoming() && e.isPre()) {
    		EventPacket event = (EventPacket) e;
    		Packet packet = event.getPacket();

    		if (packet instanceof S08PacketPlayerPosLook) {
    			S08PacketPlayerPosLook s08 = (S08PacketPlayerPosLook) packet;
    			if (mc.thePlayer == null)
    				return;

    			s08.yaw = mc.thePlayer.rotationYaw;
    			s08.pitch = mc.thePlayer.rotationPitch;
    		}
    	}
    }
}