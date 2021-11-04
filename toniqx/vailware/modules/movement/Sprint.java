package toniqx.vailware.modules.movement;

import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.entity.monster.EntityMob;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventMove;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.modules.Module;

public class Sprint extends Module {
	
	public static BooleanSetting alldir = new BooleanSetting("Omni", false);

	public Sprint(){
		super("Sprint", "", Keyboard.KEY_NONE, Category.MOVEMENT);
		this.addSettings(alldir);
	}
	
	public void onDisable(){
		mc.thePlayer.setSprinting(false);
	}
	
	public void onEvent(Event e){
		if(alldir.enabled) {
			super.mname = ChatFormatting.GRAY + "Omni" + "   ";
		}else {
			super.mname = ChatFormatting.GRAY + "";
		}
		
		if(e instanceof EventUpdate){
			if(e.isPre()){
				if(Client.getModule("Scaffold").isEnabled())
					return;
				if(Client.getModule("NoSlow").isEnabled()) {
					if(mc.thePlayer.moveForward > 0 && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally) {
						mc.thePlayer.setSprinting(true);
					}
				}else {
					if(mc.thePlayer.moveForward > 0 && !mc.thePlayer.isUsingItem() && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally)
						mc.thePlayer.setSprinting(true);	
				}
				
				if(alldir.isEnabled()) {
					if(!mc.thePlayer.isUsingItem() && !mc.thePlayer.isSneaking()) {
						mc.thePlayer.setSprinting(true);
					}
				}
			}
		}
	}
}