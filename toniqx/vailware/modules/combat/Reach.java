package toniqx.vailware.modules.combat;

import org.lwjgl.input.Keyboard;

import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.modules.Module;

public class Reach extends Module {
	
	public static NumberSetting range = new NumberSetting("Range", 4, 3, 8, 0.1);
	
	public Reach(){
		super("Reach", "", Keyboard.KEY_NONE, Category.COMBAT);
		this.addSettings(range);
	}
	
}