package toniqx.vailware.modules.misc;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import io.netty.util.internal.ThreadLocalRandom;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.TimeHelper;
import toniqx.vailware.modules.Module;

public class Spammer extends Module {
	
	public static NumberSetting delay = new NumberSetting("Delay", 40, 0, 200, 1);
	
	public Spammer() {
		super("Spammer", "", Keyboard.KEY_NONE, Category.MISC);
		this.addSettings(delay);
	}
	
	public static TimeHelper timer = new TimeHelper();
	public static String message = "dsc.gg/vailware";
	
	public void onEvent(Event e) {
		if(e instanceof EventUpdate && e.isPre()) {
			if (timer.hasReached((long) (delay.getValue() * 16))){
				int randomNum = ThreadLocalRandom.current().nextInt(100, 2300 + 1);
				int randomNum1 = ThreadLocalRandom.current().nextInt(200, 900 + 1);
				int randomNum2 = ThreadLocalRandom.current().nextInt(220, 600 + 1);
				int randomNum3 = ThreadLocalRandom.current().nextInt(250, 700 + 1);
				int randomNum4 = ThreadLocalRandom.current().nextInt(270, 430 + 1);
				int randomNum5 = ThreadLocalRandom.current().nextInt(290, 460 + 1);
				int randomNum6 = ThreadLocalRandom.current().nextInt(10, 42 + 1);
				int randomMessage = ThreadLocalRandom.current().nextInt(1, 12 + 1);
				mc.thePlayer.sendChatMessage(randomNum6 + randomMessage + " : " + randomNum + randomNum1 + randomNum2 + randomNum3 + randomNum4);
				timer.reset();
			}
		}
	}
}