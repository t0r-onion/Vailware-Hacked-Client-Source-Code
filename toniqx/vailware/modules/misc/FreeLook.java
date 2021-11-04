package toniqx.vailware.modules.misc;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemLeaves;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MovingObjectPosition;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventRotationMotion;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.util.impl.ScaffoldRenderUtils;
import toniqx.vailware.main.util.impl.ScaffoldRotationUtils;
import toniqx.vailware.modules.Module;

public class FreeLook extends Module {

	public static float oldYaw;
	public static float oldPitch;
	public static int oldView;
    public static MovingObjectPosition oldObjectMouseOver;
	
	public FreeLook(){
		super("FreeLook", "", Keyboard.KEY_NONE, Category.HIDDEN);
	}
	
	public void onEnable() {
		this.oldYaw = this.mc.thePlayer.rotationYaw;
		this.oldPitch = this.mc.thePlayer.rotationPitch;
		this.oldView = this.mc.gameSettings.thirdPersonView;
	}
	
	public void onDisable() {
		this.mc.thePlayer.rotationYaw = this.oldYaw;
		this.mc.thePlayer.rotationPitch = this.oldPitch;
		this.mc.gameSettings.thirdPersonView = oldView;
	}
	
	public void onEvent(Event e) {
        if(e instanceof EventRotationMotion) {
        	EventRotationMotion eventMotion = (EventRotationMotion) e;
            eventMotion.setYaw(oldYaw);
            eventMotion.setPitch(oldPitch);
            ScaffoldRenderUtils.CustomYaw = oldYaw;
            ScaffoldRenderUtils.CustomPitch = oldPitch;
            ScaffoldRenderUtils.SetCustomYaw = true;
            ScaffoldRenderUtils.SetCustomPitch = true;
            mc.gameSettings.thirdPersonView = 1;
        }
	}
	
}
