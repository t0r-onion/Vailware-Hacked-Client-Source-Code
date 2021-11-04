package toniqx.vailware.modules.render;

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

public class Fullbright extends Module {

	public Fullbright(){
		super("Fullbright", "", Keyboard.KEY_NONE, Category.RENDER);
	}
	
	public void onEnable(){
		mc.gameSettings.gammaSetting = 300;
		super.onEnable();
	}
	
	public void onDisable(){
		mc.gameSettings.gammaSetting = (float) 0.5;
		super.onDisable();
	}
	
	
}
