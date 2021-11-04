package toniqx.vailware.modules.combat;

import org.lwjgl.input.Keyboard;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.modules.Module;

public class NoYaw extends Module {
	
	public NoYaw(){
		super("NoYaw", "", Keyboard.KEY_NONE, Category.COMBAT);
	}
	
	public void onEvent(Event e) {
		if(mc.thePlayer.hurtTime > 6 && MovementUtil.isBlockUnder()) {
			mc.thePlayer.motionY = 0;
			mc.thePlayer.isCollided = true;
		}
	}	
}