package toniqx.vailware.modules.misc;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.modules.Module;

public class Timer extends Module {

	public NumberSetting speed = new NumberSetting("Speed", 1, 0.1, 3, 0.1);

		public Timer() {
			super("Timer", "", Keyboard.KEY_NONE, Category.MISC);
			this.addSettings(speed);
		}

		public void onEvent(Event e){
			if(e instanceof EventUpdate){
				if(e.isPre()){
				net.minecraft.util.Timer.timerSpeed = (float) speed.getValue();
			}
		}
	}
		public void onDisable(){
			net.minecraft.util.Timer.timerSpeed = 1.0f;
		}
	}