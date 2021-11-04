package toniqx.vailware.modules.render;

import net.minecraft.client.Minecraft;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.modules.Module;

import org.lwjgl.input.Keyboard;

public class Notifications extends Module {
	
    public Notifications() {
        super("Notifications", "", Keyboard.KEY_NONE, Category.RENDER);
    }
}