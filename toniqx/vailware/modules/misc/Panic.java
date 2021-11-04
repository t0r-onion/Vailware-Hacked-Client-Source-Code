package toniqx.vailware.modules.misc;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.modules.Module;

public class Panic extends Module {

	public Panic() {
		super("Panic", "", Keyboard.KEY_NONE, Category.MISC	);
	}

	@Override
	public void onEnable() {
		for(Module m : Client.getModules()){
			if(m.isEnabled()){
				m.toggle();
				m.onDisable();
			}
		}
	}
	
	@Override
	public void onDisable() {
		
	}
}
