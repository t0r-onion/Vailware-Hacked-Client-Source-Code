package toniqx.vailware.ui.hud;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.mojang.realmsclient.gui.ChatFormatting;

import java.awt.Font;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import toniqx.vailware.Client;
import toniqx.vailware.font.GlyphPageFontRenderer;
import toniqx.vailware.main.command.commands.Radio;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventRenderGUI;
import toniqx.vailware.main.event.listeners.EventRotationMotion;
import toniqx.vailware.main.music.radio.RadioManager;
import toniqx.vailware.main.util.impl.CircleManager;
import toniqx.vailware.main.util.impl.ColorManager;
import toniqx.vailware.main.util.impl.ColorUtil;
import toniqx.vailware.main.util.impl.MathUtils;
import toniqx.vailware.main.util.impl.RenderUtils;
import toniqx.vailware.main.util.impl.ScaffoldRenderUtils;
import toniqx.vailware.main.util.impl.Stencil;
import toniqx.vailware.main.util.impl.TimerUtils;
import toniqx.vailware.main.util.impl.render.Draw;
import toniqx.vailware.modules.Module;
import toniqx.vailware.modules.Module.Category;
import toniqx.vailware.ui.clickui.ClickHUD;

public class ModuleArrayList extends GuiIngame {
	Minecraft mc = Minecraft.getMinecraft();
	FontRenderer font = mc.fontRendererObj;
	
	public static String customWM = "Vailware";
	
	public ModuleArrayList(Minecraft mcIN) {
		super(mcIN);
	}
	
	public static float yaw, pitch;
	
	@Override
	public void renderGameOverlay(float partialTicks) {
		super.renderGameOverlay(partialTicks);
			
		if(!Client.destruct) {
			if(!mc.gameSettings.showDebugInfo && Client.getModule("HUD").isEnabled()) {
				renderInfo();
				renderArrayList();
			}
		}
		
	}
	
	public RadioManager radio = new RadioManager();
	private float yOff;
	public static CircleManager Wcircles = new CircleManager();
	public static CircleManager Acircles = new CircleManager();
	public static CircleManager Scircles = new CircleManager();
	public static CircleManager Dcircles = new CircleManager();
	public static CircleManager Lcircles = new CircleManager();
	public static CircleManager Rcircles = new CircleManager();
	
	public void renderInfo() {
		ScaledResolution sr = new ScaledResolution(mc);
    	Gui.drawRect(0, 0, 0, 0, -1);
    	
    	if(Client.getModule("KeyStrokes").isEnabled()) {

    		float height = 26;
    		float width = 26;
    		
    		float offset = 28;
    		float posX = 32;
    		float posY = TabGUI.isTabGui.enabled ? 52 : -96;
    		
    		float forwardPosX = posX;
    		float leftPosX = posX - offset;
    		float rightPosX = posX + offset;
    		float RMBPosX = posX + 14;
    		float LMBPosX = posX - 28;
    		float backPosX = posX;
    		
    		float forwardPosY = posY + 130;
    		float leftPosY = posY + 130 + offset;
    		float rightPosY = posY + 130 + offset;
    		float LMBPosY = posY + 175 + offset - 2;
    		float backPosY = posY + 130 + offset;
    		float RMBPosY = posY + 175 + offset - 2;
    		float SpacePosX = posX - 28;
    		float SpacePosY = posY + 186;
    		
    		Gui.drawRect(leftPosX, leftPosY, leftPosX + width, leftPosY + height, mc.gameSettings.keyBindLeft.isKeyDown() ? 0x70ffa6f9 : 0x90000000);
    		Gui.drawRect(backPosX, backPosY, backPosX + width, backPosY + height, mc.gameSettings.keyBindBack.isKeyDown() ? 0x70ffa6f9 : 0x90000000);
    		Gui.drawRect(rightPosX, rightPosY, rightPosX + width, rightPosY + height, mc.gameSettings.keyBindRight.isKeyDown() ? 0x70ffa6f9 : 0x90000000);
    		Gui.drawRect(forwardPosX, forwardPosY, forwardPosX + width, forwardPosY + height, mc.gameSettings.keyBindForward.isKeyDown() ? 0x70ffa6f9 : 0x90000000);
    		Gui.drawRect(LMBPosX, LMBPosY, LMBPosX + width + 14, LMBPosY + height - 4, mc.gameSettings.keyBindAttack.isKeyDown() ? 0x70ffa6f9 : 0x90000000);
    		Gui.drawRect(RMBPosX, RMBPosY, RMBPosX + width + 14, RMBPosY + height - 4, mc.gameSettings.keyBindUseItem.isKeyDown() ? 0x70ffa6f9 : 0x90000000);
    		
    		Gui.drawRect(SpacePosX, SpacePosY, SpacePosX + width * 3 + offset / 6, SpacePosY + height / 2, mc.gameSettings.keyBindJump.isKeyDown() ? 0x70ffa6f9 : 0x90000000);
    	
    		
    		mc.FontRendererArray.drawCenteredString("W", forwardPosX + width / 2 - 1.8f, forwardPosY + offset / 5, -1);
    		mc.FontRendererArray.drawCenteredString("A", leftPosX + width / 2 - 1.8f, leftPosY + offset / 5, -1);
    		mc.FontRendererArray.drawCenteredString("S", rightPosX + width / 2 - 1.8f, rightPosY + offset / 5, -1);
    		mc.FontRendererArray.drawCenteredString("D", backPosX + width / 2 - 1.8f, backPosY + offset / 5, -1);
    		mc.FontRendererArray.drawCenteredString("_____", backPosX + width / 2 - 1.8f, SpacePosY - 5, -1);
    		mc.FontRendererArray.drawCenteredString("LMB", LMBPosX + width / 2 + 5f, LMBPosY + offset / 6, -1);
    		mc.FontRendererArray.drawCenteredString("RMB", RMBPosX + width / 2 + 5f, RMBPosY + offset / 6, -1);
    		
    	}	
    	
    	int count = 0;
		for(Module m : Client.modules){
			if(!m.toggled)
				continue;
		double offset = count*(font.FONT_HEIGHT + 6);
		}
		
		if(!Client.premium) {
			if(!customWM.equalsIgnoreCase("Vailware")) {
				customWM = "Vailware";
			}
		}
		
		if(radio.isRunning()) {
			int vol = Radio.volume / 1;
			mc.FontRendererSmall.drawCenteredString(Radio.volume > 0 ? "Radio Playing! ": "Radio Playing! " + ChatFormatting.RED + "(MUTED)" , GuiScreen.width / 2, 2, Color.yellow.getRGB());
		}
		
		if(TabGUI.watermark.isEnabled()) {
			int arrayCount = 0;
			if(TabGUI.watermarkstyle.is("Default")) {
				String astolfoWm = customWM;
				String wmText = astolfoWm.charAt(0) + "\247f" + astolfoWm.substring(1);
				mc.FontRendererNormal.drawString(wmText + " b" + Client.getVersion(), 1, 1, Client.color);
				mc.FontRendererMiniture.drawString((Client.premium) ? "Premium" : "Regular", mc.FontRendererNormal.getStringWidth(wmText) + mc.FontRendererNormal.getStringWidth(" b" + Client.getVersion()) + 8.5f, 2f, Client.color, false);
			}else if(TabGUI.watermarkstyle.is("CSGO")) {
				String astolfowm = customWM;
				String premium = (Client.premium) ? "Premium" : "Regular";
				String WatermarkText = astolfowm + " | " + Client.version + " | " + mc.debugFPS + " fps" + " | " + "1.8.8" + " | " + premium;
				Gui.drawRect(0, 0, 3.5f + mc.FontRendererSmallBold.getStringWidth(WatermarkText), 1.4f, ColorUtil.getRainbow(6, 0.8f, 1));
				Gui.drawRect(0, 1.4f, 3.5f + mc.FontRendererSmallBold.getStringWidth(WatermarkText), 11.5f, 0x99000000);
				mc.FontRendererSmallBold.drawString(WatermarkText, 0, 0, -1);
			}else if(TabGUI.watermarkstyle.is("Simple")) { 
				String astolfoWm = customWM;
				String wmText = astolfoWm.charAt(0) + "\247f" + astolfoWm.substring(1);
				mc.FontRendererNormal.drawString(wmText, 1, 1, Client.color);
			}else if(TabGUI.watermarkstyle.is("Jello")) {
				Gui.drawRect(0, 0, 0, 0, -1);
				mc.getTextureManager().bindTexture(new ResourceLocation("vail/jello.png"));
				Gui.drawModalRectWithCustomSizedTexture(-5, 6, 0, 0, 100, 40, 100, 40);
			}else if(TabGUI.watermarkstyle.is("Extra")) {
				String astolfoWm = customWM;
				String coords = "x" + Math.round(mc.thePlayer.posX) + " y" + Math.round(mc.thePlayer.posY) + " z" + Math.round(mc.thePlayer.posZ);
				String fps = mc.debugFPS + "";
				String wmText = astolfoWm.charAt(0) + astolfoWm.substring(1);
				mc.FontRendererBig.drawString(wmText, 1, 1, ColorUtil.getRainbow(12, 0.75f, 0.85f, 5));
				mc.FontRendererClean.drawString("Coords: " + ChatFormatting.GRAY + coords, 6, 16, ColorUtil.getRainbow(12, 0.75f, 0.95f, 5));
				mc.FontRendererClean.drawString("CPS: " + ChatFormatting.GRAY + 1, 6, 26, ColorUtil.getRainbow(12, 0.75f, 0.95f, 5));
				mc.FontRendererClean.drawString("FPS:" + ChatFormatting.GRAY + fps, 6, 36, ColorUtil.getRainbow(12, 0.75f, 0.95f, 5));
				arrayCount++;
			}
		}
	}
	
	public CopyOnWriteArrayList<Module> displayModules = new CopyOnWriteArrayList<Module>();
	
	public static int ArrayColor = 0xFFFFFFFF;
	
	public void renderArrayList() {
    	Gui.drawRect(0, 0, 0, 0, -1);
        float red = (float) (toniqx.vailware.modules.misc.ArrayList.red.getValue() / 255);
        float green = (float) (toniqx.vailware.modules.misc.ArrayList.green.getValue() / 255);
        float blue = (float) (toniqx.vailware.modules.misc.ArrayList.blue.getValue() / 255);
		
		this.displayModules = new CopyOnWriteArrayList<Module>();
		this.displayModules.addAll(Client.modules);
		Client.modules.sort(Comparator.comparingDouble(m -> mc.FontRendererClean.getStringWidth(((Module)m).name)).reversed());
		this.displayModules.sort(Comparator.comparingDouble(m -> mc.FontRendererClean.getStringWidth(((Module)m).getName() + "  " + ((Module)m).mname)).reversed());
		
		ScaledResolution sr = new ScaledResolution(mc);
		
		int count = 0;
		int rectOffset = -1;
		int wideOffset = -1;
		for(Module m : this.displayModules){
			if(!m.toggled || m.name.equalsIgnoreCase("HUD") || m.name.equalsIgnoreCase("Targets") || m.getCategory().name().equalsIgnoreCase("HIDDEN"))
				continue;
			
			FontRenderer fr = mc.fontRendererObj;
			double offsetValue = 2.6;
			double offset = count*(mc.FontRendererNormal.FONT_HEIGHT + offsetValue);
			
			if (toniqx.vailware.modules.misc.ArrayList.hmode.is("Astolfo")) {
				ArrayColor = ColorUtil.astolfoColors(count * 9, 500);
			}
			else if(toniqx.vailware.modules.misc.ArrayList.hmode.is("Color")) {
		    	GlStateManager.color(1, 1, 1);
				ArrayColor = ColorManager.fade(new java.awt.Color(red, green, blue, 1), count -1 * -16, (int) (count - 1 * -6)).getRGB();
			}
			else if(toniqx.vailware.modules.misc.ArrayList.hmode.is("Sigma")) {
				ArrayColor = Color.WHITE.brighter().getRGB();
			}
			else if(toniqx.vailware.modules.misc.ArrayList.hmode.is("Flux")) {
				ArrayColor = ColorUtil.getRainbow(3f, 0.4197f, 0.932f, count * -169);
			}
			else if(toniqx.vailware.modules.misc.ArrayList.hmode.is("Rainbow")) {
				ArrayColor = ColorUtil.getRainbow((float) toniqx.vailware.modules.misc.ArrayList.speed.getValue(), (float) toniqx.vailware.modules.misc.ArrayList.saturation.getValue(), (float) toniqx.vailware.modules.misc.ArrayList.brightness.getValue(), count * -(long) toniqx.vailware.modules.misc.ArrayList.offset.getValue());
			}
			
			rectOffset = (int) (sr.getScaledWidth() - mc.FontRendererClean.getStringWidth(m.getDisplayName() + m.mname));
			
			if(m.translationYaw < mc.FontRendererClean.getStringWidth(" " + m.getDisplayName() + " " + ChatFormatting.GRAY + m.mname + " ")) {
				m.translationYaw++;
			}else if(m.translationYaw > mc.FontRendererClean.getStringWidth(" " + m.getDisplayName() + " " + ChatFormatting.GRAY + m.mname + " ")) {
				m.translationYaw--;
			}
			if(m.translationPitch < mc.FontRendererClean.getStringWidth(" " + m.getDisplayName() + " " + ChatFormatting.GRAY + m.mname + " ")) {
				m.translationPitch++;
			}else if(m.translationPitch > mc.FontRendererClean.getStringWidth(" " + m.getDisplayName() + " " + ChatFormatting.GRAY + m.mname + " ")) {
				m.translationPitch--;
			}
			
			if(TabGUI.isArrayList.enabled) {
				if(toniqx.vailware.modules.misc.ArrayList.mode.is("Outline")) {
					if(count > 0) {
						Gui.drawRect(wideOffset - m.translationYaw + mc.FontRendererClean.getStringWidth(" " + m.getDisplayName() + " " + ChatFormatting.GRAY + m.mname + " ") - 9, offset, sr.getScaledWidth() - mc.FontRendererClean.getStringWidth(m.getDisplayName() + m.mname) - 9 - m.translationYaw + mc.FontRendererClean.getStringWidth(" " + m.getDisplayName() + " " + ChatFormatting.GRAY + m.mname + " "), offset + 1, ArrayColor);	
					}
					ArrayList<Module> toggledModules = new ArrayList<Module>(Client.modules);
	                toggledModules.removeIf(module -> !module.isEnabled() || module.getName().equals("HUD") || module.getName().equals("ClickGUI") || module.getCategory().name().equalsIgnoreCase("HIDDEN"));
					if(toniqx.vailware.modules.misc.ArrayList.bkgd.enabled) {
						Gui.drawRect(sr.getScaledWidth() - mc.FontRendererClean.getStringWidth(m.getDisplayName() + m.mname) - m.translationYaw + mc.FontRendererClean.getStringWidth(" " + m.getDisplayName() + " " + ChatFormatting.GRAY + m.mname + " ") - 8, offset, sr.getScaledWidth(), 2.6f + mc.FontRendererClean.FONT_HEIGHT + offset, 0x89000000);
					}
					Gui.drawRect(sr.getScaledWidth() - mc.FontRendererClean.getStringWidth(m.getDisplayName() + m.mname) - m.translationYaw + mc.FontRendererClean.getStringWidth(" " + m.getDisplayName() + " " + ChatFormatting.GRAY + m.mname + " ") - 8, offset, sr.getScaledWidth() - mc.FontRendererClean.getStringWidth(m.getDisplayName() + m.mname) - m.translationYaw + mc.FontRendererClean.getStringWidth(" " + m.getDisplayName() + " " + ChatFormatting.GRAY + m.mname + " ") - 9, offsetValue + mc.FontRendererClean.FONT_HEIGHT + offset, ArrayColor);
					if ((count == toggledModules.size() - 1) && toniqx.vailware.modules.misc.ArrayList.mode.is("Outline")) {
		                Gui.drawRect(rectOffset - m.translationYaw + mc.FontRendererClean.getStringWidth(" " + m.getDisplayName() + " " + ChatFormatting.GRAY + m.mname + " ") - 9, offset + 12, sr.getScaledWidth(), offset + 11, ArrayColor);
		            }
					mc.FontRendererClean.drawString(" " + m.getDisplayName() + " " + ChatFormatting.GRAY + m.mname + " ", sr.getScaledWidth() - mc.FontRendererClean.getStringWidth(m.getDisplayName() + m.mname) - m.translationYaw + mc.FontRendererClean.getStringWidth(" " + m.getDisplayName() + " " + ChatFormatting.GRAY + m.mname + " ") - 8f, (float) (-1f + offset), ArrayColor, false);
				}
				else if(toniqx.vailware.modules.misc.ArrayList.mode.is("Left")) {
					if(toniqx.vailware.modules.misc.ArrayList.bkgd.enabled) {
						Gui.drawRect(sr.getScaledWidth() - mc.FontRendererClean.getStringWidth(m.getDisplayName() + m.mname) - m.translationYaw + mc.FontRendererClean.getStringWidth(" " + m.getDisplayName() + " " + ChatFormatting.GRAY + m.mname + " ") - 8, offset, sr.getScaledWidth(), 2.6f + mc.FontRendererClean.FONT_HEIGHT + offset, 0x89000000);
					}
					Gui.drawRect(sr.getScaledWidth() - mc.FontRendererClean.getStringWidth(m.getDisplayName() + m.mname) - m.translationYaw + mc.FontRendererClean.getStringWidth(" " + m.getDisplayName() + " " + ChatFormatting.GRAY + m.mname + " ") - 9, offset, sr.getScaledWidth() - mc.FontRendererClean.getStringWidth(m.getDisplayName() + m.mname) - m.translationYaw + mc.FontRendererClean.getStringWidth(" " + m.getDisplayName() + " " + ChatFormatting.GRAY + m.mname + " ") - 8, offsetValue + mc.FontRendererClean.FONT_HEIGHT + offset, ArrayColor);
					mc.FontRendererClean.drawString(" " + m.getDisplayName() + " " + ChatFormatting.GRAY + m.mname + " ", sr.getScaledWidth() - mc.FontRendererClean.getStringWidth(m.getDisplayName() + m.mname) - m.translationYaw + mc.FontRendererClean.getStringWidth(" " + m.getDisplayName() + " " + ChatFormatting.GRAY + m.mname + " ") - 8f, (float) (-1f + offset), ArrayColor, false);
				}
				else if (toniqx.vailware.modules.misc.ArrayList.mode.is("Right")) {
					if(toniqx.vailware.modules.misc.ArrayList.bkgd.enabled) {
						Gui.drawRect(sr.getScaledWidth() - mc.FontRendererClean.getStringWidth(m.getDisplayName() + m.mname) - m.translationYaw + mc.FontRendererClean.getStringWidth(" " + m.getDisplayName() + " " + ChatFormatting.GRAY + m.mname + " ") - 8, offset, sr.getScaledWidth(), 2.6f + mc.FontRendererClean.FONT_HEIGHT + offset, 0x89000000);
					}
					Gui.drawRect(sr.getScaledWidth(), offset, sr.getScaledWidth() - 1.3f, offsetValue + mc.FontRendererClean.FONT_HEIGHT + offset, ArrayColor);
					mc.FontRendererClean.drawString(" " + m.getDisplayName() + " " + ChatFormatting.GRAY + m.mname + " ", sr.getScaledWidth() - mc.FontRendererClean.getStringWidth(m.getDisplayName() + m.mname) - m.translationYaw + mc.FontRendererClean.getStringWidth(" " + m.getDisplayName() + " " + ChatFormatting.GRAY + m.mname + " ") - 8f, (float) (-1f + offset), ArrayColor, false);
				}
				else if (toniqx.vailware.modules.misc.ArrayList.mode.is("None")) {
					if(toniqx.vailware.modules.misc.ArrayList.bkgd.enabled) {
						Gui.drawRect(sr.getScaledWidth() - mc.FontRendererClean.getStringWidth(m.getDisplayName() + m.mname) - m.translationYaw + mc.FontRendererClean.getStringWidth(" " + m.getDisplayName() + " " + ChatFormatting.GRAY + m.mname + " ") - 8, offset, sr.getScaledWidth(), 2.6f + mc.FontRendererClean.FONT_HEIGHT + offset, 0x89000000);
					}
					mc.FontRendererClean.drawString(" " + m.getDisplayName() + " " + ChatFormatting.GRAY + m.mname + " ", sr.getScaledWidth() - mc.FontRendererClean.getStringWidth(m.getDisplayName() + m.mname) - m.translationYaw + mc.FontRendererClean.getStringWidth(" " + m.getDisplayName() + " " + ChatFormatting.GRAY + m.mname + " ") - 8f, (float) (-1f + offset), ArrayColor, false);
				}
				
				count++;
				wideOffset = (int) (sr.getScaledWidth() - mc.FontRendererClean.getStringWidth(m.getDisplayName() + m.mname));
			}
		}
	
		Client.onEvent(new EventRenderGUI(sr));
	}
}