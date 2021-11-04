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

public class Step extends Module {

	public NumberSetting height = new NumberSetting("Height", 1, 0.5, 2, 0.1);
	
	public Step(){
		super("Step", "", Keyboard.KEY_NONE, Category.MOVEMENT);
		this.addSettings(height);
	}
	
	public void onDisable() {
		mc.thePlayer.stepHeight = 0.5f;
	}
	
	@Override
	public void onEvent(Event e) {
		mc.thePlayer.stepHeight = (float) height.getValue();
	}
	
	
}
