package toniqx.vailware.modules.render;

import org.lwjgl.input.Keyboard;

import toniqx.vailware.modules.Module;
import toniqx.vailware.modules.Module.Category;

public class Keystrokes extends Module {

	public Keystrokes(){
		super("KeyStrokes", "", Keyboard.KEY_NONE, Category.RENDER);
	}
	
}
