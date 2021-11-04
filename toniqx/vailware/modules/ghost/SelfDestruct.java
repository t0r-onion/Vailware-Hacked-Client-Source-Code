package toniqx.vailware.modules.ghost;

import java.util.Collection;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import com.mojang.realmsclient.gui.ChatFormatting;

import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.modules.Module;

public class SelfDestruct extends Module {

	public SelfDestruct() {
		super("SelfDestruct", "", Keyboard.KEY_INSERT, Category.GHOST);
	}

	@Override
	public void onEnable() {
		for(Module m : Client.getModules()){
			if(m.isEnabled()){
				m.toggle();
				m.onDisable();
			}
			
			String key = Keyboard.getKeyName(Keyboard.KEY_NONE);
			m.keyCode.setKeyCode(Keyboard.getKeyIndex(key));
			Display.setTitle("Minecraft 1.8.8");
			
			Client.destruct = true;
		}
	}
	
	@Override
	public void onDisable() {
		
	}
}
