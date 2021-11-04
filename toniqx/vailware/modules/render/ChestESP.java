package toniqx.vailware.modules.render;

import java.awt.Color;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventRenderGUI;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventMove;
import toniqx.vailware.main.event.process.EventRenderWorld;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.ColorUtil;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.main.util.impl.render.EspUtil;
import toniqx.vailware.modules.Module;

public class ChestESP extends Module {

    public ChestESP() {
        super("ChestESP", "", Keyboard.KEY_NONE, Category.RENDER);
        this.addSettings(red, green, blue, rainbow);
    }
    
    public BooleanSetting rainbow = new BooleanSetting("Rainbow", false);
    public NumberSetting red = new NumberSetting("Red", 255, 1, 255, 1);
    public NumberSetting green = new NumberSetting("Green", 110, 1, 255, 1);
    public NumberSetting blue = new NumberSetting("Blue", 240, 1, 255, 1);


	Color chestColor;
    
    public void onEvent(Event e) {
    	super.mname = "";
        if (e instanceof EventRenderWorld) {
            for (TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {


                if (!(tileEntity instanceof TileEntityChest))
                    continue;

				if(rainbow.enabled) {
					chestColor = new Color(ColorUtil.getRainbow(12, 0.75f, 1f, 5));
				}else {
					chestColor = new Color((float) red.getValue() / 255, (float) green.getValue() / 255, (float) blue.getValue() / 255);
				}

                EspUtil.chestESPBox(tileEntity, 0, chestColor);
            }
        }

    }

}