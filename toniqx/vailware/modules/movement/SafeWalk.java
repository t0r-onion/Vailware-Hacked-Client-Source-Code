package toniqx.vailware.modules.movement;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventMove;
import toniqx.vailware.main.event.process.EventSafeWalk;
import toniqx.vailware.main.notification.Notification;
import toniqx.vailware.main.notification.impl.NotificationManager;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.modules.Module;

public class SafeWalk extends Module {

	public static ModeSetting mode = new ModeSetting("Mode", "Legit", "Legit", "Blatant");
	
	public SafeWalk(){
		super("SafeWalk", "", Keyboard.KEY_NONE, Category.MOVEMENT);
		this.addSettings(mode);
	}
	
	Minecraft mc = Minecraft.getMinecraft();
	
	@Override
	public void onEvent(Event e){
		if(mode.is("Legit")) {
			super.mname = ChatFormatting.GRAY + "Legit" + "   ";
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
		}else {
			super.mname = ChatFormatting.GRAY + "Blatant" + "   ";
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
			if(e instanceof EventSafeWalk) {
				e.setCancelled(true);
			}
		}
	}
	
	@Override
	public void onDisable() {
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
	}
}
