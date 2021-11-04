package toniqx.vailware.modules.render;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.model.ModelPlayer;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventMove;
import toniqx.vailware.main.notification.Notification;
import toniqx.vailware.main.notification.impl.NotificationManager;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.modules.Module;

public class Cape extends Module {

	public Cape(){
		super("Cape", "", Keyboard.KEY_NONE, Category.HIDDEN);
		toggled = true;
	}
	
	public void onEvent(Event e) {
		ModelPlayer.bipedCape.setTextureSize(64, 32);
		toggled = true;
	}
	
	public void onDisable() {
		toggleSilent();
	}
}