package toniqx.vailware.modules.render;

import net.minecraft.client.Minecraft;
import toniqx.vailware.Client;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.modules.Module;

import org.lwjgl.input.Keyboard;

public class ClickGUI extends Module {
    public static final int HEADER_SIZE = 20, HEADER_OFFSET = 2;
    private final Minecraft mc = Minecraft.getMinecraft();
    public static ModeSetting style = new ModeSetting("Style", "New", "New", "DropDown");
    public ModeSetting theme = new ModeSetting("Mode", "Default", "Default");
    public BooleanSetting rainbow = new BooleanSetting("Rainbow", false);
    public static BooleanSetting light = new BooleanSetting("Light", false);
    public static NumberSetting height = new NumberSetting("Height", 300, 195, 500, 0.1);
    public static NumberSetting width = new NumberSetting("Width", 350, 250, 600, 0.1);
    public NumberSetting red = new NumberSetting("Red", 50, 1, 255, 1);
    public NumberSetting green = new NumberSetting("Green", 50, 1, 255, 1);
    public NumberSetting blue = new NumberSetting("Blue", 50, 1, 255, 1);
    public static BooleanSetting blur = new BooleanSetting("Blur", true);
	
    public ClickGUI() {
        super("ClickGUI", "", Keyboard.KEY_RSHIFT, Category.RENDER);
        addSettings(style, light, width, height, red, green, blue, blur, rainbow);
    }

    public void onEnable() {
    	if(!Client.destruct) {
    		if(style.is("New")) {
    			mc.displayGuiScreen(new VapeLiteGUI());
    		}else {
    			mc.displayGuiScreen(new toniqx.vailware.ui.clickui.ClickHUD());
    		}
    		
			super.toggle();
			super.onEnable();
    	}
    }
}
