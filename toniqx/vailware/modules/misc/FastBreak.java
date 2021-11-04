package toniqx.vailware.modules.misc;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.modules.Module;

public class FastBreak extends Module {

	public FastBreak(){
		super("FastBreak", "", Keyboard.KEY_NONE, Category.MISC);
	}
	
	public void onEvent(Event e){
		mc.thePlayer.addPotionEffect(new PotionEffect(Potion.digSpeed.id,1));
	}
	
	public void onDisable() {
		mc.thePlayer.removePotionEffect(Potion.digSpeed.id);
	}
	
}
