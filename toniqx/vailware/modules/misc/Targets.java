package toniqx.vailware.modules.misc;


import java.awt.Color;
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
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.modules.Module;
import toniqx.vailware.modules.Module.Category;

public class Targets extends Module {
	
	public static ModeSetting target = new ModeSetting("Entity", "Player", "Player", "Animal", "Mob", "All");
	
	public Targets(){
		super("Targets", "", Keyboard.KEY_NONE, Category.CUSTOMIZE);
		this.addSettings(target);
		toggled = true;
	}
	
	public void onEvent(Event e) {
		if(Client.getModule("Targets").isEnabled()) {
			Client.getModule("Targets").toggleSilent();
		}
	}
	
	public void onEnable() {
		if(Client.getModule("Targets").isEnabled()) {
			Client.getModule("Targets").toggleSilent();
		}
	}
	
	public void onDisable() {
	}
}