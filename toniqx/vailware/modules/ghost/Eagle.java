package toniqx.vailware.modules.ghost;

import org.lwjgl.input.Keyboard;

import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.modules.Module;

public class Eagle extends Module {

	public Eagle() {
		super("Eagle", "", Keyboard.KEY_NONE, Category.GHOST);
	}
	
	@Override
	public void onDisable() {
		mc.gameSettings.keyBindSneak.pressed = false;
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			
			if (mc.thePlayer.ticksExisted > 0 && MovementUtil.isOnGround(0.000000001) && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getBlock() != null && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getBlock() == Blocks.air) {
				
				mc.gameSettings.keyBindSneak.pressed = true;
				
			}else {
				mc.gameSettings.keyBindSneak.pressed = false;
			}
			
		}
		
	}
	
}