package toniqx.vailware.modules.player;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.network.play.client.C03PacketPlayer;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventMove;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.modules.Module;

public class Jesus extends Module {

	public static ModeSetting mode = new ModeSetting("Mode", "Simple", "Simple", "AAC4");
	
	public Jesus(){
		super("Jesus", "", Keyboard.KEY_NONE, Category.PLAYER);
		this.addSettings(mode);
	}
	
	
	@Override
	public void onDisable() {
		mc.timer.timerSpeed = 1;
	}
	
	public void onEvent(Event e) {
		if (mode.is("Simple")) {
			super.mname = ChatFormatting.GRAY + "Simple" + "   ";
		}
		else if (mode.is("AAC4")) {
			super.mname = ChatFormatting.GRAY + "AAC4" + "   ";
			if(mc.thePlayer.isInWater()) {
				mc.thePlayer.motionY = 0;
				mc.thePlayer.motionX *= 1.1;
				mc.thePlayer.motionZ *= 1.1;
			}
			else mc.timer.timerSpeed = 1;
		}
	}

}
