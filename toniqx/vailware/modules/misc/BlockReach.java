package toniqx.vailware.modules.misc;

import org.lwjgl.input.Keyboard;

import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.modules.Module;

public class BlockReach extends Module {
	
	public static NumberSetting reach = new NumberSetting("Reach", 4, 4, 20, 1);
	
	public BlockReach(){
		super("BlockReach", "", Keyboard.KEY_NONE, Category.MISC);
		this.addSettings(reach);
	}
	
}