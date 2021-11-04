package toniqx.vailware.modules.misc;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.modules.Module;
import toniqx.vailware.modules.Module.Category;

public class ArrayList extends Module {
	
	public static ModeSetting mode = new ModeSetting("Bar", "Left", "Left", "Right", "Outline", "None");
	public static BooleanSetting bkgd = new BooleanSetting("Background", true);
	public static ModeSetting hmode = new ModeSetting("Theme", "Astolfo", "Astolfo", "Sigma", "Flux", "Color", "Rainbow");
	public static NumberSetting red = new NumberSetting("Red", 1, 1, 255, 0.1);
	public static NumberSetting green = new NumberSetting("Green", 1, 1, 255, 0.1);
	public static NumberSetting blue = new NumberSetting("Blue", 1, 1, 255, 0.1);
	public static NumberSetting saturation = new NumberSetting("Saturation", 1, 0.1, 1, 0.1);
	public static NumberSetting brightness = new NumberSetting("Brightness", 1, 0.1, 1, 0.1);
	public static NumberSetting speed = new NumberSetting("Speed", 5, 1, 10, 0.1);
	public static NumberSetting offset = new NumberSetting("Offset", 100, 1, 200, 1);
	
	public ArrayList(){
		super("ArrayList", "", Keyboard.KEY_NONE, Category.CUSTOMIZE);
		this.addSettings(mode, hmode, red, green, blue, saturation, brightness, speed, offset, bkgd);
		toggled = true;
	}
	
	public void onEvent(Event e) {
		if(Client.getModule("ArrayList").isEnabled()) {
			Client.getModule("ArrayList").toggleSilent();
		}
	}
	
	public void onEnable() {
		if(Client.getModule("ArrayList").isEnabled()) {
			Client.getModule("ArrayList").toggleSilent();
		}
	}
	
	public void onDisable() {
	}
}