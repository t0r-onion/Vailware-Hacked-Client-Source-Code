package toniqx.vailware.modules.render;

import net.minecraft.entity.Entity;

import java.awt.Color;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventRenderGUI;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventMove;
import toniqx.vailware.main.event.process.EventRenderWorld;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.ColorUtil;
import toniqx.vailware.main.util.impl.RenderUtils;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.main.util.impl.render.RenderUtil;
import toniqx.vailware.modules.Module;
import toniqx.vailware.ui.impl.drawLine;

public class Tracers extends Module {

    public Tracers() {
        super("Tracers", "", Keyboard.KEY_NONE, Category.RENDER);
        this.addSettings(mode, red, green, blue, rainbow);
    }

    public static ModeSetting mode = new ModeSetting("Mode", "Crosshair", "Crosshair");
    public BooleanSetting rainbow = new BooleanSetting("Rainbow", false);
    public NumberSetting red = new NumberSetting("Red", 255, 1, 255, 1);
    public NumberSetting green = new NumberSetting("Green", 110, 1, 255, 1);
    public NumberSetting blue = new NumberSetting("Blue", 230, 1, 255, 1);
    
	public Minecraft mc = Minecraft.getMinecraft();
    
	Color tracerColor;
	
    public void onEvent(Event e) {
    	if(e instanceof EventUpdate) {
    		if(mode.is("Crosshair")) {
    			super.mname = ChatFormatting.GRAY + "Crosshair" + "   ";
    		}
    	}
        if (e instanceof EventRenderWorld) {
			for (int i = 0; i < mc.theWorld.loadedEntityList.size(); i++) {
				Entity entity = mc.theWorld.loadedEntityList.get(i);
				if (entity != null && entity instanceof EntityPlayer && entity != mc.thePlayer) {
					if(rainbow.enabled) {
						tracerColor = new Color(ColorUtil.getRainbow(12, 0.75f, 1f, 5));
					}else {
						tracerColor = new Color((float) red.getValue() / 255, (float) green.getValue() / 255, (float) blue.getValue() / 255);
					}
					if(mode.is("Crosshair")) {
						tracer(entity, tracerColor);
					}
				}
            }
        }
    }
    
    private void tracer(final Entity e, final Color color) {
        GlStateManager.pushMatrix();
        GL11.glDisable(2929);
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glColor4f((float)color.getRed(), (float)color.getGreen(), (float)color.getBlue(), (float)color.getAlpha());
        GL11.glLineWidth(1.0f);
        GL11.glBegin(2);
        GL11.glVertex3d(0.0, this.mc.thePlayer.getEyeHeight(), 0.0);
        GL11.glVertex3d(e.posX - this.mc.thePlayer.posX, e.posY - this.mc.thePlayer.posY, e.posZ - this.mc.thePlayer.posZ);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glEnable(2896);
        GL11.glEnable(2929);
        GlStateManager.popMatrix();
    }
}