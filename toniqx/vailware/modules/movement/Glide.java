package toniqx.vailware.modules.movement;

import java.util.List;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.util.MathHelper;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.modules.Module;

public class Glide extends Module {
	
	public NumberSetting gspeed = new NumberSetting("Speed", 0.1, 0.1, 0.3, 0.1);
	
	public Glide(){
		super("Glide", "", Keyboard.KEY_NONE, Category.MOVEMENT);
		this.addSettings(gspeed);
	}
	
	
	public void onEvent(Event e) {
		if(mc.thePlayer.fallDistance != 0 && mc.thePlayer.motionY != 0){
			mc.thePlayer.motionY = -gspeed.getValue();
		}
	}
}