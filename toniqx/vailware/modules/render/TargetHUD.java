package toniqx.vailware.modules.render;


import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventRenderGUI;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventMove;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.RenderUtils;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.modules.Module;
import toniqx.vailware.modules.Module.Category;
import toniqx.vailware.modules.combat.KillAura;
import toniqx.vailware.modules.misc.Targets;

public class TargetHUD extends Module {

	Minecraft mc = Minecraft.getMinecraft();
	FontRenderer fr = mc.fontRendererObj;
	ScaledResolution sr = new ScaledResolution(mc);
	private float lastHealth;
	
	public TargetHUD(){
		super("TargetHUD", "", Keyboard.KEY_NONE, Category.RENDER);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventRenderGUI) {
			if(Client.killAura.isEnabled() && Client.killAura.target != null && Client.killAura.target.getHealth() > 0 && Client.killAura.target.getDistanceToEntity(mc.thePlayer) < KillAura.range.getValue()) {
			
				try {
					GlStateManager.pushMatrix();

                    float width = (float)(GuiScreen.width / 2.0D + 120.0D);
                    float height = (float)(GuiScreen.height / 2.2D);

                    Gui.drawRect((width - 71.5F), (height + 35.0F), (width - 30.0F + 109.7f), (height + 78.0F), 0xB9000000);


                    float health = Client.killAura.target.getHealth();
                    float healthPercentage = health / Client.killAura.target.getMaxHealth();
                    float targetHealthPercentage = 0.0F;
                    if (healthPercentage != this.lastHealth) {
                        float diff = healthPercentage - this.lastHealth;
                        targetHealthPercentage = this.lastHealth;
                        this.lastHealth += diff / 8.0F;
                    }
                    
                    Gui.drawRect((width - 29), (height + 71), (width - 30.0F + 108.5F), (height + 76), Color.GRAY.getRGB());
                    Gui.drawRect((width - 29), (height + 71), (width - 30.0F + 108.5F * targetHealthPercentage), (height + 76), Client.color);

                    mc.FontRendererNormal.drawCenteredString(((EntityPlayer) Client.killAura.target).getName(), (int) (width + 20), (int) (height + 35), 0xFFAAFFFF);
                    mc.FontRendererClean.drawStringWithShadow(String.format("%.1f", Client.killAura.target.getHealth()), (int) (width + 76.5f - mc.FontRendererClean.getStringWidth(String.format("%.1f", Client.killAura.target.getHealth()))), (int) (height + 59), -1);

                    try {
                        RenderUtils.drawFace((int) (width - 70), (int) (height + 37), 8, 8, 8, 8, 40, 40, 64, 64, (AbstractClientPlayer) Client.killAura.target);
                    }catch(Exception exception) {
                    }

                    GlStateManager.popMatrix();
				}catch(Exception e1) {
					
				}
			}
			
		}
	}
}