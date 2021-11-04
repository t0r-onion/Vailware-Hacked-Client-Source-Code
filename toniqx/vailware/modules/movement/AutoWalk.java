package toniqx.vailware.modules.movement;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.item.Item;
import net.minecraft.item.ItemLeaves;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.notification.Notification;
import toniqx.vailware.main.notification.impl.NotificationManager;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.modules.Module;

public class AutoWalk extends Module {

	public AutoWalk(){
		super("AutoWalk", "", Keyboard.KEY_NONE, Category.MOVEMENT);
	}
	
	public void onEvent(Event e) {
		mc.gameSettings.keyBindForward.pressed = true;
	}
	
	public void onDisable() {
		mc.gameSettings.keyBindForward.pressed = false;
	}
}
