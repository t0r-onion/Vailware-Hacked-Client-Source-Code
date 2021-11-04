package toniqx.vailware.modules.ghost;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.modules.Module;

public class LeftClickTimer extends Module { 
	public LeftClickTimer() {
		super("NoClickDelay", "", Keyboard.KEY_NONE, Category.GHOST);
	}
	
	@Override
	public void onEvent(Event e) {
		Minecraft.getMinecraft().leftClickCounter = 0;
	}
	
}
