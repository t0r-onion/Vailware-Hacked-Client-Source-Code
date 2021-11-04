package toniqx.vailware.ui.hud;

import java.awt.Color;
import java.util.Collections;
import java.util.Comparator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.listeners.EventRenderGUI;
import toniqx.vailware.main.notification.impl.NotificationManager;

public class HUD {

	public Minecraft mc = Minecraft.getMinecraft();
	
	public void draw(){
		ScaledResolution sr = new ScaledResolution(mc);
		FontRenderer fr = mc.fontRendererObj;
		Client.onEvent(new EventRenderGUI(sr));
		NotificationManager.getNotificationManager().onRender();
	}
	
}
