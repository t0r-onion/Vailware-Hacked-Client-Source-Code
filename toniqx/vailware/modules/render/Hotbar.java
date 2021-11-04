package toniqx.vailware.modules.render;

import java.awt.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventRenderGUI;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventMove;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.ColorUtil;
import toniqx.vailware.main.util.impl.MathUtils;
import toniqx.vailware.main.util.impl.RenderUtils;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.main.util.impl.render.Draw;
import toniqx.vailware.modules.Module;

public class Hotbar extends Module {
	

	public BooleanSetting counter = new BooleanSetting("Extra", true);
	public GuiChat chat;
	
	public Hotbar() {
		
		super("Hotbar", "", Keyboard.KEY_NONE, Category.RENDER);
		this.addSettings(counter);
		toggled = false;
	}
	
	public void onEnable() {
		super.onEnable();
	}	
	
	public void onEvent(Event e) {
		if(e instanceof EventRenderGUI) {
			ScaledResolution sr = new ScaledResolution(mc);
			FontRenderer fr = mc.fontRendererObj;
			
			double posX = Math.round(mc.thePlayer.posX);
			double posY = Math.round(mc.thePlayer.posY);
			double posZ = Math.round(mc.thePlayer.posZ);
			
			double pitch =  Math.round(mc.thePlayer.rotationPitch);
	 		double yaw =  Math.round(mc.thePlayer.rotationYaw);
			if(mc.currentScreen == chat) {
				if(counter.isEnabled()) {
					double calc = (MathUtils.square(mc.thePlayer.motionX) + MathUtils.square(mc.thePlayer.motionZ));
			        double bps = MathUtils.round((Math.sqrt(calc) * 20) * mc.timer.timerSpeed, 2);
					
			        RenderUtils.glColor(0x80000000);
					Draw.drawRoundedRect(10, GuiScreen.height - 42, bps < 5 ? 50 : bps < 20 ? 50 + bps / 2 : 50 + bps / 8, 24, 4);
					mc.FontRendererArray.drawString("BPS:" + ChatFormatting.GRAY + " " + bps, 12, GuiScreen.height - 42, Client.color);
					mc.FontRendererArray.drawString("FPS:" + ChatFormatting.GRAY + " " + mc.debugFPS, 12, GuiScreen.height - 31, Client.color);
				}
			}
		}
	}
}