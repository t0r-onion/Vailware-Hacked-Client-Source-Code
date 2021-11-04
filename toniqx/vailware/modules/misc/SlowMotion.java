package toniqx.vailware.modules.misc;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.modules.Module;

public class SlowMotion extends Module {
	
	public SlowMotion() {
		super("SlowMotion", "", Keyboard.KEY_NONE, Category.MISC);
	}

	public void onEvent(Event e){
		if(e instanceof EventUpdate){
			if(e.isPre()){
			net.minecraft.util.Timer.timerSpeed = (float) 0.7F;
		}
	}
}
	public void onDisable(){
		net.minecraft.util.Timer.timerSpeed = 1.0f;
	}
}