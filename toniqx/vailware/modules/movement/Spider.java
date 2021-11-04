package toniqx.vailware.modules.movement;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventMove;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.modules.Module;

public class Spider extends Module {

	public Spider(){
		super("Spider", "", Keyboard.KEY_NONE, Category.MOVEMENT);
	}
	
	@Override
	public void onEvent(Event e) {
		if(mc.thePlayer.isCollidedHorizontally) {
			mc.thePlayer.motionY = 0.4f;
		}
	}
	
	
}
