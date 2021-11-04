package toniqx.vailware.modules.player;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.BlockPos;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventReceivePacket;
import toniqx.vailware.main.event.listeners.EventRotationMotion;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventMove;
import toniqx.vailware.main.event.process.EventPacket;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.main.util.impl.server.PacketUtil;
import toniqx.vailware.modules.Module;

public class NoFall extends Module {

	public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Redesky", "Hypixel", "Verus", "NCP");
	
	public NoFall(){
		super("NoFall", "", Keyboard.KEY_NONE, Category.PLAYER);
		this.addSettings(mode);
	}

    public void onEvent(Event e) {
        if (mode.is("Vanilla")) {
			super.mname = ChatFormatting.GRAY + "Vanilla" + "   ";
        }
        else if (mode.is("Redesky")) {
			super.mname = ChatFormatting.GRAY + "Redesky" + "   ";
        }
        else if (mode.is("Hypixel")) {
			super.mname = ChatFormatting.GRAY + "Hypixel" + "   ";
        }
        else if (mode.is("Verus")) {
			super.mname = ChatFormatting.GRAY + "Verus" + "   ";
        }
        else if (mode.is("NCP")) {
			super.mname = ChatFormatting.GRAY + "NCP" + "   ";
        }
        if (e instanceof EventUpdate && e.isPre() && MovementUtil.isBlockUnder()) {
        	if(mode.is("Verus")) {
        		EventUpdate event = (EventUpdate)e;
        		if (mc.thePlayer.fallDistance >= 2.5f) {
        			event.setOnGround(mc.thePlayer.ticksExisted % 2 == 0);
        		}
        	}
        	else if(mode.is("NCP")) {
        		if(!MovementUtil.isOnGround(0.001)){
            		EventUpdate event = (EventUpdate)e;
        			if(mc.thePlayer.fallDistance > 2.69){
            			event.setOnGround(true);
            			mc.thePlayer.fallDistance = 0;
            		}
            		if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, mc.thePlayer.motionY, 0.0D)).isEmpty()) {  
            			if(!event.isOnGround() && mc.thePlayer.motionY < -0.6){
            				event.setOnGround(true);
            			}		 
            		}
        		}
        	}
        	else if (mode.is("Vanilla") && this.mc.thePlayer.fallDistance > 2.0f) {
                this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, true));
            }
        }
        if (e instanceof EventRotationMotion && e.isPre() && MovementUtil.isBlockUnder()) {
        	EventRotationMotion eventMotion;
            if (mode.is("Redesky") && this.mc.thePlayer.fallDistance >= 3.0f) {
                eventMotion = (EventRotationMotion)e;
                eventMotion.setOnGround(true);
            }
            if (mode.is("Hypixel") && this.mc.thePlayer.fallDistance >= 3.0f && MovementUtil.isBlockUnder()) {
                eventMotion = (EventRotationMotion)e;
                eventMotion.setOnGround(true);
            }
        }
    }
}
	