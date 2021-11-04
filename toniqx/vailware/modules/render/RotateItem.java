package toniqx.vailware.modules.render;

import org.lwjgl.input.Keyboard;

import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.modules.Module;

public class RotateItem extends Module {
	
	public RotateItem() {
		super("360 Item", "", Keyboard.KEY_NONE, Category.RENDER);
	}
	
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
		}
		
	}
	
}
