package toniqx.vailware.modules.movement;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.modules.Module;

public class AirJump extends Module {

	public AirJump(){
		super("AirJump", "", Keyboard.KEY_NONE, Category.MOVEMENT);
	}
	
	public void onEvent(Event e){
		if(e instanceof EventUpdate){
			if(e.isPre() && !Client.getModule("Speed").isEnabled()){
				mc.thePlayer.onGround = true;
			}
		}
	}
	
}
