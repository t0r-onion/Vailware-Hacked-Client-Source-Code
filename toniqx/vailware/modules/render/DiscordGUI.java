package toniqx.vailware.modules.render;

import net.minecraft.client.Minecraft;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.modules.Module;

import org.lwjgl.input.Keyboard;

public class DiscordGUI extends Module {
    public static final int HEADER_SIZE = 20, HEADER_OFFSET = 2;
    private final Minecraft mc = Minecraft.getMinecraft();
	
    public DiscordGUI() {
        super("DiscordGUI", "", Keyboard.KEY_RSHIFT, Category.RENDER);
    }

    public void onEnable() {
        mc.displayGuiScreen(new toniqx.vailware.ui.clickui.DiscordHUD());
        toggle();
    }
}