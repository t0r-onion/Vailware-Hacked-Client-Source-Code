package toniqx.vailware.modules.misc;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemLeaves;
import net.minecraft.item.ItemSword;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.modules.Module;

public class FastPlace extends Module {

	public FastPlace(){
		super("FastPlace", "", Keyboard.KEY_NONE, Category.MISC);
	}
	
	@Override
	public void onEvent(Event e) {
		if (mc.thePlayer.getCurrentEquippedItem() != null && !(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemEnderPearl)){
			Minecraft.getMinecraft().rightClickDelayTimer = 0;
		}
	}
	
}
