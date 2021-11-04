package toniqx.vailware.modules.ghost;

import org.lwjgl.input.Keyboard;

import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.modules.Module;

public class Parkour extends Module {

	public Parkour() {
		super("Parkour", "", Keyboard.KEY_NONE, Category.GHOST);
	}
	
	@Override
	public void onDisable() {
		mc.gameSettings.keyBindJump.pressed = false;
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			
			if (mc.thePlayer.ticksExisted > 0 && !mc.gameSettings.keyBindJump.isPressed() && isOnGround(0.000000001) && !mc.thePlayer.capabilities.isFlying && !Client.getModule("Fly").isEnabled() && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getBlock() != null && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getBlock() == Blocks.air) {
				mc.thePlayer.jump();
			}
		}
		
	}
	
    public static boolean isOnGround(double height) {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
    }
	
}