package toniqx.vailware.ui.clickui;

import static toniqx.vailware.ui.clickui.ClickHUD.Handle.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import toniqx.vailware.Client;
import toniqx.vailware.main.settings.Setting;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.KeybindSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.ColorManager;
import toniqx.vailware.main.util.impl.ColorUtil;
import toniqx.vailware.main.util.impl.RenderUtils;
import toniqx.vailware.main.util.impl.TimerUtils;
import toniqx.vailware.main.util.impl.render.Draw;
import toniqx.vailware.main.util.impl.render.RenderUtil;
import toniqx.vailware.modules.Module;
import toniqx.vailware.modules.Module.Category;
import toniqx.vailware.modules.render.ClickGUI;
import toniqx.vailware.ui.clickui.lib.Direction;
import toniqx.vailware.ui.clickui.lib.impl.LinearAnimation;
import toniqx.vailware.ui.clickui.lib.impl.SmoothStepAnimation;
import toniqx.vailware.ui.clickui.lib.impl.SmootherStepAnimation;
import toniqx.vailware.ui.hud.HUD;

public class ClickHUD extends GuiScreen {
	public Category dragging;
    public int startX, startY;
    public Module binding;
    public NumberSetting draggingNumber;
    public int accent, modBackground, settingsBackground, bindingBackground;
    public int sliderColor, categoryColor, enabledColor, textColor;
    SmootherStepAnimation openingAnimation = new SmootherStepAnimation(300, 1, Direction.FORWARDS);

    double categoryPos;

    public TimerUtils boolTimer = new TimerUtils();
    public TimerUtils categoryTimer = new TimerUtils();
    public TimerUtils settingsTimer = new TimerUtils();
    
    public static float leftCount;
    
    
    public static int applyOpacity(int color, float opacity) {
        Color old = new Color(color);
        return new Color(old.getRed(), old.getGreen(), old.getBlue(), (int) (opacity * (color >> 24 & 255))).getRGB();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        handle(mouseX, mouseY, -1, DRAW);
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        handle(mouseX, mouseY, button, CLICK);
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
        handle(mouseX, mouseY, button, RELEASE);
    }
    
    public void drawDefaultBackground()
    {
        drawWorldBackground(0);
    }
    
    public void drawWorldBackground(int tint)
    {
        if (this.mc.theWorld != null)
        {
            drawGradientRect(0, 0, this.width, this.height, 0x84000000, 0x84000000);
        }
        else
        {
            drawBackground(tint);
        }
    }
    
    public void drawBackground(int tint)
    {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        mc.getTextureManager().bindTexture(optionsBackground);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0D, (double)this.height, 0.0D).tex(0.0D, (double)((float)this.height / 32.0F + (float)tint)).color(64, 64, 64, 255).endVertex();
        worldrenderer.pos((double)this.width, (double)this.height, 0.0D).tex((double)((float)this.width / 32.0F), (double)((float)this.height / 32.0F + (float)tint)).color(64, 64, 64, 255).endVertex();
        worldrenderer.pos((double)this.width, 0.0D, 0.0D).tex((double)((float)this.width / 32.0F), (double)tint).color(64, 64, 64, 255).endVertex();
        worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, (double)tint).color(64, 64, 64, 255).endVertex();
        tessellator.draw();
    }

    public void keyTyped(char character, int key) {
        if (key == Client.clickGui.getKey() || key == 1) {
            this.mc.displayGuiScreen(null);
            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
        }

        if (binding != null) {
            if (key == Keyboard.KEY_SPACE || key == Keyboard.KEY_ESCAPE || key == Keyboard.KEY_DELETE)
                binding.getKeyBind().setKeyCode(Keyboard.KEY_NONE);
            else
                binding.getKeyBind().setKeyCode(key);

            binding = null;
        }
    }
    
    public boolean isDragging = false;
   
    public void handle(int mouseX, int mouseY, int button, Handle type) {
    	
    	if(ClickGUI.style.is("Rise")) {
    		
    		int colorMain = 0xFF232329;
    		int colorButton = 0xFF2D3037;
    		int colorButtonFill = 0xFF33363d;
    		int colorText = 0xFFdcdfe6;
    		int colorModuleDisabled = 0xFF979fa8;
    		int colorSelectedDisabled = Client.color - 20;
    		int colorSelected = Client.color - 2;
    		
            float left = Client.posX,
                    top = Client.posY,
                    width = (float) ClickGUI.width.getValue(),
                    right = left + width,
            		height = (float) ClickGUI.height.getValue(),
            		minheight = 195,
            		minwidth = 250,
            		moduleOffset = 24,
            		categoryOffset = 20;
    		
            RenderUtils.glColor(colorMain);
            Draw.drawRoundedRect(left, top, width, height, 6);
            RenderUtils.glColor(0xFF474747);
            Gui.drawRect(left, top + height - 25, left + 96, top + height - 21, 0xFF474747);
            Gui.drawRect(left + 90, top + height - 25, left + 96, top + height, 0xFF474747);
            Draw.drawRoundedRect(left, top + height - 25, 96, 25, 6);
            RenderUtils.glColor(0xFFFFFFFF);
            mc.FontRendererLarge.drawCenteredString(Client.username, left + 46, top + height - 21, -1);
            
            boolean hoveringDragbar = isHovered(left, top, left + width, top + 26, mouseX, mouseY);

        	if (type == CLICK && hoveringDragbar && button == 0) {
        		isDragging = true;
        		startX = (int) (mouseX - Client.posX);
        		startY = (int) (mouseY - Client.posY);
        	}
        	
            if (type == RELEASE && button == 0) {
            	isDragging = false;
            	draggingNumber = null;
            }
        	
            if(isDragging) {
                Client.posX = mouseX - startX;
                Client.posY = mouseY - startY;
            }
            
            int categoryCount = 0;
            
            mc.FontRendererHuge.drawCenteredString(Client.name, left + 40, top + 2, colorText);
            
            String selectedCategory = Client.selectedCategory;
            
            mc.FontRendererNormal.drawString(selectedCategory, left + 94, top + 6, -1, false);
            
            for(Category c : Category.values()) {
            	if(c.name.equalsIgnoreCase("Hidden"))
            		return;
            	String category = c.name;
    	        boolean hoveringCategory = isHovered(left, top + 30 + categoryCount * categoryOffset, left + 90, top + 30 + categoryCount * categoryOffset + 6 + 11, mouseX, mouseY);
    	        
            	if(c.name.equalsIgnoreCase(selectedCategory)) {
            		RenderUtil.glColor(colorSelected);
            		Gui.drawRect(left, top + 29 + categoryCount * categoryOffset, left + 90, top + 32 + categoryCount * categoryOffset + 6 + 11, colorSelected);
            		mc.FontRendererLarge.drawStringWithShadow(category, left + 22, top + 30 + categoryCount * categoryOffset, -1);
            	}else {
            		mc.FontRendererLarge.drawString(category, left + 22, top + 30 + categoryCount * categoryOffset, 0xFFe6edff, false);
            	}
            	
            	
            	
            	if(type == CLICK && hoveringCategory & button == 0) {
            		Client.selectedCategory = category;
            	}
            	
	            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            	
            	int moduleCount = 0;
            	
            	for (Module m : Client.getModulesByCategory(c)) {
    	        	if(m.category.name.equalsIgnoreCase(selectedCategory)) {
    	        		boolean hoveringModule = isHovered(left + 96, top + 25 + moduleCount * moduleOffset + c.offset, left + width - 3, top + 25 + moduleCount * moduleOffset + 19 + c.offset, mouseX, mouseY);
    	        		
       	        		boolean hoveringModuleBox = isHovered(left + 96, top + 25, left + width - 3, top + 25 + height, mouseX, mouseY);
    	        		
    	        		
    	            	RenderUtils.scissor(left + 96, top + 25, left + width, height - 25);
    	        		String modName = m.name;

    	        		if(hoveringModule && !hoveringDragbar && type == CLICK && button == 0) {
    	        			m.toggle();
    	        		}
    	        		
                        if (type == CLICK && hoveringModule && !hoveringDragbar && button == 1) {
                        	m.expanded = !m.expanded;
                        }
    	        		
    	        		
    	        		if(m.category.offset > 0) {
    	        			m.category.offset = 0;
    	        		}
    	        		
	                	if (hoveringModuleBox && Mouse.hasWheel()) {
	                        int wheel = Mouse.getDWheel();
	                        if (wheel < 0) {
	                        	m.category.offset -= 14;	
	                        } else if (wheel > 0) {
	                        	if(m.category.offset < 0) {
	                        		m.category.offset += 14;
	                        	}
	                        }
	                	}
    	        		
    	        		if(!m.isEnabled()) {
    	        			RenderUtils.glColor(colorButton);
    	        			if(m.expanded) {
    	        				Draw.drawRoundedRect(left + 96, top + 25 + moduleCount * moduleOffset + c.offset, width - 99, 20, 4);
    	        			}else {
    	        				Draw.drawRoundedRect(left + 96, top + 25 + moduleCount * moduleOffset + c.offset, width - 99, 20, 4);
    	        			}
    	        			mc.FontRendererNormal.drawStringWithShadow(modName, left + 102, top + 27 + moduleCount * moduleOffset + c.offset, colorModuleDisabled);
    	        			RenderUtils.glColor(colorModuleDisabled);
    	        			Draw.drawRoundedRect(left + width - 18, top + 25 + moduleCount * moduleOffset + c.offset + 7, 10, 5, 2.5f);
    	        			
    	        			RenderUtils.glColor(colorSelectedDisabled);
    	        			Draw.drawRoundedRect(left + width - 19, top + 24.5f + moduleCount * moduleOffset + c.offset + 7, 6, 6, 3);
    	        		}else {
    	        			RenderUtils.glColor(colorButtonFill);
    	        			if(m.expanded) {
    	        				Draw.drawRoundedRect(left + 96, top + 25 + moduleCount * moduleOffset + c.offset, width - 99, 20, 4);
    	        			}else {
    	        				Draw.drawRoundedRect(left + 96, top + 25 + moduleCount * moduleOffset + c.offset, width - 99, 20, 4);
    	        			}
    	        			mc.FontRendererNormal.drawStringWithShadow(modName, left + 102, top + 27 + moduleCount * moduleOffset + c.offset, colorSelected);
    	        			RenderUtils.glColor(colorText);
    	        			Draw.drawRoundedRect(left + width - 18, top + 25 + moduleCount * moduleOffset + c.offset + 7, 10, 5, 2.5f);
    	        			
    	        			RenderUtils.glColor(colorSelected);
    	        			Draw.drawRoundedRect(left + width - 13, top + 24.5f + moduleCount * moduleOffset + c.offset + 7, 6, 6, 3);
    	        		}
    	        		
    	        		moduleCount++;
    	        		
	                	if(m.isExpanded()) {
		            		for (Setting setting : m.getSettings()) {
			                    String settingText;
		                    	settingText = setting.name.toLowerCase();
			                    boolean hoveringSetting = isHovered(left + 5, top + 23 + moduleCount * moduleOffset + c.offset, left + width - 4, top + 40 + moduleCount * moduleOffset + c.offset, mouseX, mouseY);
		
			                    
			                    if(m.isEnabled()) {
			                    	RenderUtils.glColor(colorButtonFill);
			                    }else {
			                    	RenderUtils.glColor(colorButton);
			                    }
			                    Draw.drawRoundedRect(left + 96, top + 14 + moduleCount * moduleOffset + c.offset, width - 99, 31, 4);
			                    
			                    if (setting instanceof BooleanSetting) {
			                        BooleanSetting bool = (BooleanSetting) setting;
			                        String title = bool.isEnabled() ? "On" : "Off";
			                        
			                        if (type == CLICK && hoveringSetting && button == 0) {
			                            bool.toggle();
			                        }
			                        title = title.toLowerCase();
		
			                        mc.FontRendererNormal.drawStringWithShadow(title, right - 30, top + 23 + moduleCount * moduleOffset + c.offset, -1);
			                    }
			                    if (setting instanceof KeybindSetting) {
	                                KeybindSetting bind = (KeybindSetting) setting;
	                                String title = Keyboard.getKeyName(bind.getKeyCode());
	                                
	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                    binding = m;
	                                }
	                                title = title.toLowerCase();
	                                
	                                mc.FontRendererNormal.drawCenteredString(title, right + 12 - mc.FontRendererNormal.getStringWidth(settingText), top + 23 + moduleCount * moduleOffset + c.offset, -1);

	                            }
			                    int sliderWidth = (int) width - 99;
			                    float sliderLeft = left + 96;
			                    if (setting instanceof NumberSetting) {
	                                NumberSetting number = (NumberSetting) setting;
	                                float percent = (float) ((number.getValue() - number.getMinimum()) / (number.getMaximum() - number.getMinimum())),
	                                        numbersliderWidth = percent * sliderWidth;
	                                
	                                if (draggingNumber != null && draggingNumber == number) {
	                                    double mousePercent = Math.min(1, Math.max(0, (mouseX - sliderLeft) / sliderWidth)), newValue = (mousePercent * (number.getMaximum() - number.getMinimum())) + number.getMinimum();
	                                    number.setValue(newValue);
	                                }
	                                
	                                Gui.drawRect(sliderLeft, top + 25 + moduleCount * moduleOffset + c.offset, numbersliderWidth + sliderLeft, top + 20 + moduleCount * moduleOffset + 17 + c.offset, 0xFFde97bc);
	                                String title = String.valueOf(number.getValue());

	                                if (type == CLICK && !hoveringDragbar && hoveringSetting && button == 0) {
	                                    draggingNumber = number;
	                                }
	                                
	                                if (type == RELEASE && hoveringSetting && button == 0) {
	                                    draggingNumber = null;
	                                }

	                                title = title.toLowerCase();

	                                mc.FontRendererNormal.drawCenteredString(title, right - 26, top + 23 + moduleCount * moduleOffset + c.offset, -1);
	                            }
	                            if (setting instanceof ModeSetting) {
	                                ModeSetting mode = (ModeSetting) setting;
	                                String title = mode.getMode();

	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                    mode.cycle();
	                                }
	                                title = title.toLowerCase();
	                                    
	                                mc.FontRendererNormal.drawCenteredString(title, right - 30, top + 23 + moduleCount * moduleOffset + c.offset, -1);
	                            }

	                            mc.FontRendererNormal.drawStringWithShadow(settingText, left + 100, top + 23 + moduleCount * moduleOffset + c.offset, -1);
		
	                            moduleCount++;
			                }
		            		
		            	}
    	        		
    	        	}

            	}
            	GL11.glDisable(GL11.GL_SCISSOR_TEST);
            	
            	categoryCount++;
            }
           
        	
            
            
    	}else if(ClickGUI.style.is("VapeLite")) {
    		
    		int colorMain = 0xFF232329;
    		int colorButton = 0xFF2D3037;
    		int colorButtonFill = 0xFF33363d;
    		int colorEnabled = 0xFF2E75DE;
    		int colorDisabled = 0xFF242427;
    		int colorText = 0xFFdcdfe6;
    		int colorModuleDisabled = 0xFF979fa8;
    		int colorGray = 0xFF4A4B4B;
    		int colorSelected = Client.color - 2;
    		
            float left = Client.posX,
                    top = Client.posY,
                    width = 440,
                    right = left + width,
            		height = 290,
            		minheight = 195,
            		minwidth = 250,
            		moduleOffset = 32,
            		categoryOffset = 25;
    		
            int backgroundColor = 0xFF151618;
            
            RenderUtils.glColor(colorMain);
            Gui.drawRect(left, top + 16, left + width, top + height, backgroundColor);
            Gui.drawRect(left, top, left + width, top + 16, -1);
            
            mc.fontRendererObj.drawString(Client.username, left + 2, top + 4.5f, Color.BLACK.getRGB(), false);
            
            boolean hoveringX = isHovered(left + width - 13, top, left + width, top + 16, mouseX, mouseY);
            
            if(hoveringX) {
                mc.FontRendererNormal.drawString("x", left + width - 10, top, Color.RED.getRGB(), false);
            }else {
            	mc.FontRendererNormal.drawString("x", left + width - 10, top, colorGray, false);
            }
            
            boolean hoveringDragbar = isHovered(left, top, left + width, top + 16, mouseX, mouseY);

            
            
        	if (type == CLICK && hoveringDragbar && button == 0) {
        		isDragging = true;
        		startX = (int) (mouseX - Client.posX);
        		startY = (int) (mouseY - Client.posY);
        	}
        	
            if (type == RELEASE && button == 0) {
            	isDragging = false;
            	draggingNumber = null;
            }
        	
            if(isDragging) {
                Client.posX = mouseX - startX;
                Client.posY = mouseY - startY;
            }
            
            int categoryCount = 0;
            
            mc.FontRendererSmallBold.drawString(ChatFormatting.BOLD + Client.name.toUpperCase(), left + 5, top + height - 16, colorText, false);
            
            String selectedCategory = Client.selectedCategory;
            
            mc.FontRendererNormal.drawString("Home", left + 16, top + 26, -1, false);
            
            for(Category c : Category.values()) {
            	if(c.name.equalsIgnoreCase("Hidden"))
            		return;
            	String category = c.name;
    	        boolean hoveringCategory = isHovered(left, top + 60 + categoryCount * categoryOffset, left + 90, top + 60 + categoryCount * categoryOffset + 6 + 11, mouseX, mouseY);

            	if(c.name.equalsIgnoreCase(selectedCategory)) {
            		RenderUtil.glColor(0xFF2E75DE);
            		Draw.drawRoundedRect(left + 23, top + 75 + categoryCount * categoryOffset, 9, 1.8f, 1f);
            		mc.FontRendererNormal.drawString(category, left + 22, top + 60 + categoryCount * categoryOffset, -1, false);
            	}else {
            		mc.FontRendererNormal.drawString(category, left + 22, top + 60 + categoryCount * categoryOffset, colorGray, false);
            	}
            	
            	if(type == CLICK && hoveringCategory & button == 0) {
            		Client.selectedCategory = category;
            	}
            	
	            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            	
            	int moduleCount = 0;
            	
            	for (Module m : Client.getModulesByCategory(c)) {
    	        	if(m.category.name.equalsIgnoreCase(selectedCategory)) {	
    	            	RenderUtils.scissor(left + 96, top + 60, left + width, height - 60);
    	            	
       	        		boolean hoveringModuleBox = isHovered(left + 96, top + 25, left + width - 3, top + 25 + height, mouseX, mouseY);
    	        		boolean hoveringModule = isHovered(left + 96, top + 60 + moduleCount * moduleOffset + c.offset, left + width - 48, top + 87 + moduleCount * moduleOffset + c.offset, mouseX, mouseY);
    	        	
    	        		String modName = m.name;

    	        		if(hoveringModule && !hoveringDragbar && type == CLICK && button == 0) {
    	        			m.toggle();
    	        		}
    	        		
                        if (type == CLICK && hoveringModule && !hoveringDragbar && button == 1) {
                        	m.expanded = !m.expanded;
                        }
    	        		
    	        		
    	        		if(m.category.offset > 60) {
    	        			m.category.offset = 60;
    	        		}
    	        		
    	        		moduleCount++;
    	        		
    	        		if(!m.isEnabled()) {
    	        			RenderUtils.glColor(0xFF242427);
    	        			Draw.drawRoundedRect(left + 96, top + 28 + moduleCount * moduleOffset + c.offset, 25, 28, 3);
    	        			RenderUtils.glColor(0xFF262629);
    	        			Draw.drawRoundedRect(left + 367, top + 28 + moduleCount * moduleOffset + c.offset, 25, 28, 3);
    	        			Gui.drawRect(left + 117, top + 28 + moduleCount * moduleOffset + c.offset, left + 380, top + 56 + moduleCount * moduleOffset + c.offset, 0xFF1F1F22);
    	        			mc.FontRendererArray.drawString(modName, left + 128, top + 35 + moduleCount * moduleOffset + c.offset, colorModuleDisabled, false);
    	        			mc.FontRendererLarge.drawString(".", left + 383, top + 25 + moduleCount * moduleOffset + c.offset, colorModuleDisabled, false);
    	        			mc.FontRendererLarge.drawString(".", left + 383, top + 29 + moduleCount * moduleOffset + c.offset, colorModuleDisabled, false);
    	        			mc.FontRendererLarge.drawString(".", left + 383, top + 33 + moduleCount * moduleOffset + c.offset, colorModuleDisabled, false);
    	        			RenderUtils.glColor(0xFF484A4E);
    	        			Draw.drawRoundedRect(left + width - 90, top + 30 + moduleCount * moduleOffset + c.offset + 7, 18, 10f, 5f);
    	        			RenderUtils.glColor(0xFF242427);
    	        			Draw.drawRoundedRect(left + width - 89, top + 31 + moduleCount * moduleOffset + c.offset + 7, 16, 8f, 4f);
    	        			RenderUtils.glColor(0xFF484A4E);
    	        			Draw.drawRoundedRect(left + width - 88, top + 32f + moduleCount * moduleOffset + c.offset + 7, 6, 6, 3f);
    	        		}else {
    	        			RenderUtils.glColor(0xFF2E75DE);
    	        			Draw.drawRoundedRect(left + 96, top + 28 + moduleCount * moduleOffset + c.offset, 25, 28, 3);
    	        			RenderUtils.glColor(0xFF2E2E33);
    	        			Draw.drawRoundedRect(left + 367, top + 28 + moduleCount * moduleOffset + c.offset, 25, 28, 3);
    	        			Gui.drawRect(left + 117, top + 28 + moduleCount * moduleOffset + c.offset, left + 380, top + 56 + moduleCount * moduleOffset + c.offset, 0xFF262629);
    	        			mc.FontRendererArray.drawString(modName, left + 128, top + 35 + moduleCount * moduleOffset + c.offset, colorModuleDisabled, false);
    	        			mc.FontRendererLarge.drawString(".", left + 383, top + 25 + moduleCount * moduleOffset + c.offset, colorModuleDisabled, false);
    	        			mc.FontRendererLarge.drawString(".", left + 383, top + 29 + moduleCount * moduleOffset + c.offset, colorModuleDisabled, false);
    	        			mc.FontRendererLarge.drawString(".", left + 383, top + 33 + moduleCount * moduleOffset + c.offset, colorModuleDisabled, false);
    	        			RenderUtils.glColor(0xFF2E75DE);
    	        			Draw.drawRoundedRect(left + width - 90, top + 30 + moduleCount * moduleOffset + c.offset + 7, 18, 10f, 5f);RenderUtils.glColor(0xFF484A4E);
    	        			RenderUtils.glColor(0xFFFFFFFF);
    	        			Draw.drawRoundedRect(left + width - 80, top + 31.5f + moduleCount * moduleOffset + c.offset + 7, 6.5f, 6.5f, 3f);
    	        		}
    	        		
	                	if(m.isExpanded()) {
		            		for (Setting setting : m.getSettings()) {
			                    String settingText;
		                    	settingText = setting.name.toLowerCase();
			                    boolean hoveringSetting = isHovered(left + 5, top + 23 + moduleCount * moduleOffset + c.offset, left + width - 4, top + 40 + moduleCount * moduleOffset + c.offset, mouseX, mouseY);
		
			                    
			                    if(m.isEnabled()) {
			                    	RenderUtils.glColor(colorButtonFill);
			                    }else {
			                    	RenderUtils.glColor(colorButton);
			                    }
			                    Draw.drawRoundedRect(left + 96, top + 14 + moduleCount * moduleOffset + c.offset, width - 99, 31, 4);
			                    
			                    if (setting instanceof BooleanSetting) {
			                        BooleanSetting bool = (BooleanSetting) setting;
			                        String title = bool.isEnabled() ? "On" : "Off";
			                        
			                        if (type == CLICK && hoveringSetting && button == 0) {
			                            bool.toggle();
			                        }
			                        title = title.toLowerCase();
		
			                        mc.FontRendererNormal.drawStringWithShadow(title, right - 30, top + 23 + moduleCount * moduleOffset + c.offset, -1);
			                    }
			                    if (setting instanceof KeybindSetting) {
	                                KeybindSetting bind = (KeybindSetting) setting;
	                                String title = Keyboard.getKeyName(bind.getKeyCode());
	                                
	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                    binding = m;
	                                }
	                                title = title.toLowerCase();
	                                
	                                mc.FontRendererNormal.drawCenteredString(title, right + 12 - mc.FontRendererNormal.getStringWidth(settingText), top + 23 + moduleCount * moduleOffset + c.offset, -1);

	                            }
			                    int sliderWidth = (int) width - 99;
			                    float sliderLeft = left + 96;
			                    if (setting instanceof NumberSetting) {
	                                NumberSetting number = (NumberSetting) setting;
	                                float percent = (float) ((number.getValue() - number.getMinimum()) / (number.getMaximum() - number.getMinimum())),
	                                        numbersliderWidth = percent * sliderWidth;
	                                
	                                if (draggingNumber != null && draggingNumber == number) {
	                                    double mousePercent = Math.min(1, Math.max(0, (mouseX - sliderLeft) / sliderWidth)), newValue = (mousePercent * (number.getMaximum() - number.getMinimum())) + number.getMinimum();
	                                    number.setValue(newValue);
	                                }
	                                
	                                Gui.drawRect(sliderLeft, top + 25 + moduleCount * moduleOffset + c.offset, numbersliderWidth + sliderLeft, top + 20 + moduleCount * moduleOffset + 17 + c.offset, 0xFFde97bc);
	                                String title = String.valueOf(number.getValue());

	                                if (type == CLICK && !hoveringDragbar && hoveringSetting && button == 0) {
	                                    draggingNumber = number;
	                                }
	                                
	                                if (type == RELEASE && hoveringSetting && button == 0) {
	                                    draggingNumber = null;
	                                }

	                                title = title.toLowerCase();

	                                mc.FontRendererNormal.drawCenteredString(title, right - 26, top + 23 + moduleCount * moduleOffset + c.offset, -1);
	                            }
	                            if (setting instanceof ModeSetting) {
	                                ModeSetting mode = (ModeSetting) setting;
	                                String title = mode.getMode();

	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                    mode.cycle();
	                                }
	                                title = title.toLowerCase();
	                                    
	                                mc.FontRendererNormal.drawCenteredString(title, right - 30, top + 23 + moduleCount * moduleOffset + c.offset, -1);
	                            }

	                            mc.FontRendererNormal.drawStringWithShadow(settingText, left + 100, top + 23 + moduleCount * moduleOffset + c.offset, -1);
		
	                            moduleCount++;
			                }
		            	
		            		
		            	}
    	        	
	                	if (hoveringModuleBox && Mouse.hasWheel()) {
	                        int wheel = Mouse.getDWheel();
	                        if (wheel < 0) {
	                        	if(m.category.offset > -1000) {
	                        		m.category.offset -= 18;	
	                        	}
	                        } else if (wheel > 60) {
	                        	if(m.category.offset < 0) {
	                        		m.category.offset += 18;
	                        	}
	                        }
	                	}
	                	
    	        	}

            	}
            	GL11.glDisable(GL11.GL_SCISSOR_TEST);
            	
            	categoryCount++;
            }

    	}else if(ClickGUI.style.is("Novoline")) {
    		if(ClickGUI.blur.isEnabled()) {
	            drawDefaultBackground();
	    	}
	    	
	        float red = (float) (Client.clickGui.red.getValue() / 255);
	        float green = (float) (Client.clickGui.green.getValue() / 255);
	        float blue = (float) (Client.clickGui.blue.getValue() / 255);
	        int colormodule = new Color(red, green, blue, 1f).getRGB();
	        int colorcategory = new Color(red, green, blue, 1).getRGB();
	        int colorslider = new Color(red, green, blue, 1f).getRGB();

	        Client.modules.sort(Comparator.comparingDouble(m -> {
	                    String modulesText = ((Module) m).getName();
	
	                    return mc.FontRendererNormal.getStringWidth(modulesText);
	
	                })
	                        .reversed()
	        );
	        if (type == RELEASE && button == 0) {
	            dragging = null;
	            draggingNumber = null;
	            if (binding != null)
	                binding = null;
	        }
	        
	        
	        
	        if (dragging != null) {
	            dragging.posX = mouseX - startX;
	            dragging.posY = mouseY - startY;
	        }
	
	        toniqx.vailware.modules.render.ClickGUI clickGui = Client.clickGui;
	        String theme = clickGui.theme.getMode();
	        float offset = (float) (1);
	        float rainbowSpeed = (float) (1);
	        boolean custom = true;
	        categoryPos = 8;
	
	
	        // this makes the text for the category always readable
	        if (theme.equals("Default")) {
	            if ((red * 255) + (green * 255) >= 445) {
	                categoryColor = 0xff000000;
	            }
	        }
	            categoryColor = -1;
	
	        textColor = -1;
	        
	        for (Category category : Category.values()) {
	        	if(category.name.equalsIgnoreCase("HIDDEN")) {
	        		continue;
	        	}
	            float left = category.posX,
	                    top = category.posY,
	                    width = 98,
	                    right = left + width;
	
	            boolean hoveringCategory = isHovered(left, top, left + width, top + 18, mouseX, mouseY);
	            leftCount = left;
	            if (type == CLICK && hoveringCategory && button == 0) {
	                dragging = category;
	                startX = mouseX - category.posX;
	                startY = mouseY - category.posY;
	                return;
	            }
	
	            if (type == CLICK && hoveringCategory && button == 1) {
	                category.expanded = !category.expanded;
	                return;
	            }
	            int count = 0;
	
	            HUD hud = Client.hud;
	
	            String categoryText;
	            categoryText = category.name;
	
	
	            GlStateManager.color(255, 255, 255);
                Gui.drawRect(left - 1, top + 3.2f, left + width + 1, top + 18, 0xFF171617);
                Gui.drawRect(0, 0, 0, 0, accent);
	            mc.FontRendererNormal.drawStringWithShadow(categoryText, left + 1.8f, (float) (top + categoryPos) - 5, categoryColor);
	            int lastModBackground = -1;
	            GL11.glEnable(GL11.GL_SCISSOR_TEST);
	
	            boolean isGradient = false;
          
	            if (category.expanded)
	            	
	            	RenderUtils.scissor(left - 1, top + 18 + count * 17, width + 2, 600);
	            
	                for (Module m : Client.getModulesByCategory(category)) {
	                	if(m.category.name.equalsIgnoreCase("HIDDEN")) {
	                		continue;
	                	}
	                	
		            	boolean hoveringModule = isHovered(left, top + 18 + count * 17 + category.offset, left + width, top + 18 + count * 17 + 18 + category.offset, mouseX, mouseY);
		            	
		            	if(m.category.offset > 0) {
		            		m.category.offset = 0;
		            	}
	                    
	                    if (type == CLICK && hoveringModule && button == 0) {
	                        m.toggle();
	                        return;
	                    }
	
	                    if (type == CLICK && hoveringModule && button == 1) {
	                        m.setExpanded(!m.isExpanded());
	                        return;
	                    }
	                    
	                    Gui.drawRect(left - 1, top + 18 + count * 17 + category.offset, left + width + 1, top + 18 + count * 17 + 19 + category.offset, 0xFF1E1D1E);
	                    Gui.drawRect(left, top + 18 + count * 17 + category.offset, left + width, top + 18 + count * 17 + 19 + category.offset, 0xFF282729);
	                    Gui.drawRect(left - 1, top + 18 + count * 17 + 19 + category.offset, left + width + 1, top + 18 + count * 17 + 20 + category.offset, 0xFF1E1D1E);
	
	                    Gui.drawRect(0, 0, 0, 0, accent);
	
	                    String textModule;
	                        textModule = m.name;
	
	                    if (!m.isEnabled() || m.getName().equals("HUD") || m.getName().equals("ArrayList")) {
	                    	mc.FontRendererArray.drawStringWithShadow(textModule, left + 1, top + 20.5f + count * 17 + category.offset, -1);
	                    }
	                  
	
	                    int textColorHighlight = -1;
	
	                    if (theme.equals("Default"))
	                        textColorHighlight = applyOpacity(new Color(red, green, blue, 1).getRGB(), 1);
	
	
	                    if (m.isEnabled() && !m.getName().equals("HUD") && !m.getName().equals("ArrayList")) {
	                        Gui.drawRect(left, top + 18 + count * 17 + category.offset, left + width, top + 18.6f + count * 17 + 18 + category.offset, clickGui.rainbow.isEnabled() ? ColorUtil.getRainbow(12, 0.6f, 1f, (long) (left * 2)) : colormodule);
	                        mc.FontRendererArray.drawStringWithShadow(textModule, left + 1, top + 20.5f + count * 17 + category.offset, -1);
	                    }
	                    if(!m.expanded) {
	                    	mc.fontRendererObj.drawStringWithShadow("\u02C5", left + width - 7, top + 24.8f + count * 17 + category.offset, 0xFFFFFFFF);
	                    }else if(m.expanded) {
	                    	mc.fontRendererObj.drawStringWithShadow("\u02C4", left + width - 7, top + 24.8f + count * 17 + category.offset, 0xFFFFFFFF);
	                    }
	
	
	                    count++;
	
	                    lastModBackground = modBackground;
	
	                    if (m.isExpanded()) {
	                        for (Setting setting : m.getSettings()) {
	                        	
	                            boolean hoveringSetting = isHovered(left, top + 18 + count * 17 + m.category.offset, left + width, top + 18 + count * 17 + 18 + m.category.offset, mouseX, mouseY);
	
	    	                	if (hoveringSetting && Mouse.hasWheel()) {
	    	                        int wheel = Mouse.getDWheel();
	    	                        if (wheel < 0) {
	    	                        	m.category.offset -= 5;	
	    	                        } else if (wheel > 0) {
	    	                        	if(m.category.offset < 0) {
	    	                        		m.category.offset += 5;
	    	                        	}
	    	                        }
	    	                	}
	    	                    
	                            
	                            if (setting instanceof BooleanSetting) {
	                                BooleanSetting bool = (BooleanSetting) setting;
	                                Gui.drawRect(0, 0, 0, 0, accent);
	                                Gui.drawRect(left - 1, top + 18 + count * 17 + category.offset, left + width + 1, top + 18 + count * 17 + 18 + category.offset, 0xFF1E1D1E);
	                                Gui.drawRect(left, top + 18 + count * 17 + category.offset, left + width, top + 18 + count * 17 + 18 + category.offset, 0xFF252525);
	
	                                String title = bool.isEnabled() ? "On" : "Off";
	
	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                    bool.toggle();
	                                }
	                                title = title.toLowerCase();
	
	                                mc.FontRendererArray.drawStringWithShadow(title, right - 16, top + 19 + count * 17 + category.offset, -1);
	                            }
	                            if (setting instanceof NumberSetting) {
	                                NumberSetting number = (NumberSetting) setting;
	                                float percent = (float) ((number.getValue() - number.getMinimum()) / (number.getMaximum() - number.getMinimum())),
	                                        numberWidth = percent * width;
	
	                                if (draggingNumber != null && draggingNumber == number) {
	                                    double mousePercent = Math.min(1, Math.max(0, (mouseX - left) / width)),
	                                            newValue = (mousePercent * (number.getMaximum() - number.getMinimum())) + number.getMinimum();
	                                    number.setValue(newValue);
	                                }
	                                Gui.drawRect(0, 0, 0, 0, accent);
	                                Gui.drawRect(left - 1, top + 18 + count * 17 + category.offset, left + width + 1, top + 18 + count * 17 + 18 + category.offset, 0xFF1E1D1E);
	                                Gui.drawRect(left, top + 18 + count * 17 + category.offset, left + width, top + 18 + count * 17 + 18 + category.offset, 0xFF252525);
	                                Gui.drawRect(left, top + 19f + count * 17 + category.offset, width + left, top + 16 + count * 17 + 17 + category.offset, 0xFF1E1D1E);
	                                Gui.drawRect(left, top + 19f + count * 17 + category.offset, numberWidth + left, top + 16 + count * 17 + 17 + category.offset, Client.clickGui.rainbow.isEnabled() ? ColorUtil.getRainbow(12, 0.5f, 1f, (long) (left * 2)) : colorslider);
	                                
	                                String title = String.valueOf(number.getValue());
	
	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                    draggingNumber = number;
	                                }
	
	                                title = title.toLowerCase();
	
	                                mc.FontRendererArray.drawCenteredString(title, right - 16, top + 19 + count * 17 + category.offset, -1);
	                            }
	                            if (setting instanceof ModeSetting) {
	                                ModeSetting mode = (ModeSetting) setting;
	                                Gui.drawRect(0, 0, 0, 0, accent);
	                                Gui.drawRect(left - 1, top + 18 + count * 17 + category.offset, left + width + 1, top + 18 + count * 17 + 18 + category.offset, 0xFF1E1D1E);
	                                Gui.drawRect(left, top + 18 + count * 17 + category.offset, left + width, top + 18 + count * 17 + 18 + category.offset, 0xFF252525);
	
	                                String title = mode.getMode();
	
	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                	mode.cycle();
	                                }
	                                if (type == CLICK && hoveringSetting && button == 1) {
	                                	mode.cycleback();
	                                }
	                                title = title.toLowerCase();
	                                    
	                                mc.FontRendererArray.drawCenteredString(title, right - 22, top + 19 + count * 17 + category.offset, -1);
	                            }
	                            if (setting instanceof KeybindSetting) {
	                                KeybindSetting bind = (KeybindSetting) setting;
	                                Gui.drawRect(0, 0, 0, 0, accent);
	                                Gui.drawRect(left - 1, top + 18 + count * 17 + category.offset, left + width + 1, top + 18 + count * 17 + 18 + category.offset, 0xFF1E1D1E);
	                                Gui.drawRect(left, top + 18 + count * 17 + category.offset, left + width, top + 18 + count * 17 + 18 + category.offset, binding == m ? 0xFF3c3d3c : 0xFF252525);
	
	                                String title = Keyboard.getKeyName(bind.getKeyCode());
	
	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                    binding = m;
	                                }
	                                title = title.toLowerCase();
	                                
	                                mc.FontRendererArray.drawStringWithShadow(title, right - 4 - mc.FontRendererNormal.getStringWidth(title), top + 19 + count * 17 + category.offset, -1);
	
	                            }
	                            String settingText;
	                                settingText = setting.name.toLowerCase();
	
	                                mc.FontRendererNormal.drawStringWithShadow(settingText, left + 1.5f, top + 18 + count * 17 + category.offset, -1);
	
	                            count++;
	
	                        }
	                    }
	                }
		        GL11.glDisable(GL11.GL_SCISSOR_TEST);
	            left += 105;
	        }
	        
	        if (type == CLICK && button == 0) {
	            dragging = null;
	            // binding = null;
	        }
	
	        Client.modules.sort(Comparator.comparingDouble(m -> {
	                    String modulesText = ((Module) m).getDisplayName();
	
	                    return mc.FontRendererNormal.getStringWidth(modulesText);
	
	                })
	                        .reversed()
	        );
	    }else if(ClickGUI.style.is("DropDown")) {
	    	if(ClickGUI.blur.isEnabled()) {
	            drawDefaultBackground();
	    	}
	    	
	        float red = (float) (Client.clickGui.red.getValue() / 255);
	        float green = (float) (Client.clickGui.green.getValue() / 255);
	        float blue = (float) (Client.clickGui.blue.getValue() / 255);
	        int colormodule = new Color(red, green, blue, 1f).getRGB();
	        int colorcategory = new Color(red, green, blue, 1).getRGB();
	        int colorslider = new Color(red, green, blue, 1f).getRGB();

	        if (!openingAnimation.isDone()) {
	            GL11.glEnable(GL11.GL_SCISSOR_TEST);
	            RenderUtil.scissor(width / 2d - (openingAnimation.getOutput() * width / 2d), height / 2d - (openingAnimation.getOutput() * height / 2d), (openingAnimation.getOutput() * width), (openingAnimation.getOutput() * width));
	        } else {
	            if (openingAnimation.getDirection() == Direction.BACKWARDS){
	                this.mc.displayGuiScreen(null);
	                if (this.mc.currentScreen == null) {
	                    this.mc.setIngameFocus();
	                }
	            }
	        }
	
	        Client.modules.sort(Comparator.comparingDouble(m -> {
	                    String modulesText = ((Module) m).getName();
	
	                    return mc.FontRendererNormal.getStringWidth(modulesText);
	
	                })
	                        .reversed()
	        );
	        if (type == RELEASE && button == 0) {
	            dragging = null;
	            draggingNumber = null;
	            if (binding != null)
	                binding = null;
	        }
	
	        if (dragging != null) {
	            dragging.posX = mouseX - startX;
	            dragging.posY = mouseY - startY;
	        }
	
	        //importing all of the settings
	
	        toniqx.vailware.modules.render.ClickGUI clickGui = Client.clickGui;
	        String theme = clickGui.theme.getMode();
	        float offset = (float) (1);
	        float rainbowSpeed = (float) (1);
	        boolean custom = true;
	        categoryPos = 8;
	
	
	        // this makes the text for the category always readable
	        if (theme.equals("Default")) {
	            if ((red * 255) + (green * 255) >= 445) {
	                categoryColor = 0xff000000;
	            }
	        }
	            categoryColor = -1;
	
	        textColor = -1;
	        
	        for (Category category : Category.values()) {
	        	if(category.name.equalsIgnoreCase("HIDDEN")) {
	        		continue;
	        	}
	            float left = category.posX,
	                    top = category.posY,
	                    width = 94,
	                    right = left + width;
	
	            boolean hoveringCategory = isHovered(left, top, left + width, top + 18, mouseX, mouseY);
	            leftCount = left;
	            if (type == CLICK && hoveringCategory && button == 0) {
	                dragging = category;
	                startX = mouseX - category.posX;
	                startY = mouseY - category.posY;
	                return;
	            }
	
	            if (type == CLICK && hoveringCategory && button == 1) {
	                category.expanded = !category.expanded;
	                return;
	            }
	            int count = 0;
	
	            HUD hud = Client.hud;
	
	            String categoryText;
	                categoryText = category.name;
	
	            GlStateManager.color(255, 255, 255);
                Gui.drawRect(left - 1, top, left + width + 1, top + 18, 0xFF171617);
                Gui.drawRect(0, 0, 0, 0, accent);
	            mc.FontRendererNormal.drawStringWithShadow(categoryText, left + 3.6f, (float) (top + categoryPos) - 6, categoryColor);
	            int lastModBackground = -1;
	
	            boolean isGradient = false;
	
	            
	            Gui.drawRect(left, top, left - 1, top + 18 + count * 17 + 1, clickGui.rainbow.isEnabled() ? ColorUtil.getRainbow(12, 0.6f, 1f, (long) (left * 2)) : colormodule);
                Gui.drawRect(left + width, top, left + width + 1, top + 18 + count * 17 + 1, clickGui.rainbow.isEnabled() ? ColorUtil.getRainbow(12, 0.6f, 1f, (long) (left * 2)) : colormodule);
                Gui.drawRect(left - 1, top + 18 + count * 17 + 1, left + width + 1, top + 18 + count * 17, clickGui.rainbow.isEnabled() ? ColorUtil.getRainbow(12, 0.6f, 1f, (long) (left * 2)) : colormodule);
                Gui.drawRect(left - 1, top - 1, left + width + 1, top, clickGui.rainbow.isEnabled() ? ColorUtil.getRainbow(12, 0.6f, 1f, (long) (left * 2)) : colormodule);
	            
	            if (category.expanded)
	                for (Module m : Client.getModulesByCategory(category)) {
	                	if(m.category.name.equalsIgnoreCase("HIDDEN")) {
	                		continue;
	                	}
	                    boolean hoveringModule = isHovered(left, top + 18 + count * 17, left + width, top + 18 + count * 17 + 18, mouseX, mouseY);
 	                    if (type == CLICK && hoveringModule && button == 0) {
	                        m.toggle();
	                        return;
	                    }
	
	                    if (type == CLICK && hoveringModule && button == 1) {
	                        m.setExpanded(!m.isExpanded());
	                        return;
	                    }
	                    
	                    Gui.drawRect(left, top, left - 1, top + 18 + count * 17 + 19, clickGui.rainbow.isEnabled() ? ColorUtil.getRainbow(12, 0.6f, 1f, (long) (left * 2)) : colormodule);
	                    Gui.drawRect(left + width, top, left + width + 1, top + 18 + count * 17 + 19, clickGui.rainbow.isEnabled() ? ColorUtil.getRainbow(12, 0.6f, 1f, (long) (left * 2)) : colormodule);
	                    Gui.drawRect(left, top + 18 + count * 17, left + width, top + 18 + count * 17 + 19, 0xFF1F1F1F);
	                    Gui.drawRect(left - 1, top + 18 + count * 17 + 18, left + width + 1, top + 18 + count * 17 + 19, clickGui.rainbow.isEnabled() ? ColorUtil.getRainbow(12, 0.6f, 1f, (long) (left * 2)) : colormodule);
	                    Gui.drawRect(left - 1, top - 1, left + width + 1, top, clickGui.rainbow.isEnabled() ? ColorUtil.getRainbow(12, 0.6f, 1f, (long) (left * 2)) : colormodule);
	
	                    Gui.drawRect(0, 0, 0, 0, accent);
	
	                    String textModule;
	                        textModule = m.name;
	
	                    if (!m.isEnabled() || m.getName().equals("HUD") || m.getName().equals("ArrayList")) {
	                    	mc.FontRendererSmall.drawStringWithShadow(textModule, left + 1, top + 20.5f + count * 17, -1);
	                    }
	                  
	
	                    int textColorHighlight = -1;
	
	                    if (theme.equals("Default"))
	                        textColorHighlight = applyOpacity(new Color(red, green, blue, 1).getRGB(), 1);
	
	
	                    if (m.isEnabled() && !m.getName().equals("HUD") && !m.getName().equals("ArrayList")) {
	                        Gui.drawRect(left, top + 18 + count * 17, left + width, top + 18.6f + count * 17 + 18, clickGui.rainbow.isEnabled() ? ColorUtil.getRainbow(12, 0.6f, 1f, (long) (left * 2)) : colormodule);
	                        mc.FontRendererSmall.drawStringWithShadow(textModule, left + 1, top + 20.5f + count * 17, -1);
	                    }
	                    if(!m.expanded) {
	                    	mc.fontRendererObj.drawStringWithShadow("\u02C5", left + width - 7, top + 24.8f + count * 17, 0xFFFFFFFF);
	                    }else if(m.expanded) {
	                    	mc.fontRendererObj.drawStringWithShadow("\u02C4", left + width - 7, top + 24.8f + count * 17, 0xFFFFFFFF);
	                    }
	
	
	                    count++;
	
	                    lastModBackground = modBackground;
	
	                    if (m.isExpanded()) {
	                        for (Setting setting : m.getSettings()) {
	                        	
	                            boolean hoveringSetting = isHovered(left, top + 18 + count * 17, left + width, top + 18 + count * 17 + 18, mouseX, mouseY);
	
	                            if (setting instanceof BooleanSetting) {
	                                BooleanSetting bool = (BooleanSetting) setting;
	                                Gui.drawRect(0, 0, 0, 0, accent);
	                                Gui.drawRect(left - 1, top + 18 + count * 17, left + width + 1, top + 18 + count * 17 + 18, Client.clickGui.rainbow.isEnabled() ? ColorUtil.getRainbow(12, 0.5f, 1f, (long) (left * 2)) : colormodule);
	                                Gui.drawRect(left, top + 18 + count * 17, left + width, top + 18 + count * 17 + 18, 0xFF212121);
	
	                                Gui.drawRect(left, top + 35 + count * 17, left + width, top + 18 + count * 17 + 18, clickGui.rainbow.isEnabled() ? ColorUtil.getRainbow(12, 0.6f, 1f, (long) (left * 2)) : colormodule);
	                                
	                                String title = bool.isEnabled() ? "On" : "Off";
	                                
	                                if(!bool.isEnabled()) {
	                                	RenderUtils.glColor(Color.GRAY.getRGB());
	                                	Draw.drawRoundedRect(left + width - 12, top + 4 + count * 17 + 19, 6, 6, 3);
	                                }else {
	                                	RenderUtils.glColor(Client.clickGui.rainbow.isEnabled() ? ColorUtil.getRainbow(12, 0.5f, 1f, (long) (left * 2)) : colormodule);
	                                	Draw.drawRoundedRect(left + width - 12, top + 4 + count * 17 + 19, 6, 6, 3);
	                                }
	                                
	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                    bool.toggle();
	                                }
	                                title = title.toLowerCase();
	
	                            }
			                    String settingText;
		                    	settingText = setting.name.toLowerCase();
		                    	
	                            if (setting instanceof NumberSetting) {
	                            	float sliderWidth = width - 2;
	                            	float sliderStart = left + 1;
	                            	
	                                NumberSetting number = (NumberSetting) setting;
	                                float percent = (float) ((number.getValue() - number.getMinimum()) / (number.getMaximum() - number.getMinimum())),
	                                        numbersliderWidth = percent * sliderWidth;
	
	                                if (draggingNumber != null && draggingNumber == number) {
	                                	double mousePercent = Math.min(1, Math.max(0, (mouseX - sliderStart) / sliderWidth)),
	                                    		newValue = (mousePercent * (number.getMaximum() - number.getMinimum())) + number.getMinimum();
	                                    number.setValue(newValue);
	                                }
	                                
	                                Gui.drawRect(left - 1, top + 18 + count * 17, left + width + 1, top + 18 + count * 17 + 18, Client.clickGui.rainbow.isEnabled() ? ColorUtil.getRainbow(12, 0.5f, 1f, (long) (left * 2)) : colormodule);
	                                Gui.drawRect(left, top + 18 + count * 17, left + width, top + 18 + count * 17 + 18, 0xFF212121);
	                                Gui.drawRect(sliderStart, top + 4 + count * 17 + 17, left + width - 1, top + 8 + count * 17 + 17, 0xFF191919);
	                                Gui.drawRect(sliderStart, top + 4 + count * 17 + 17, numbersliderWidth + sliderStart, top + 8 + count * 17 + 17, Client.clickGui.rainbow.isEnabled() ? ColorUtil.getRainbow(12, 0.5f, 1f, (long) (left * 2)) : colormodule);
	
	                                Gui.drawRect(left, top + 35 + count * 17, left + width, top + 18 + count * 17 + 18, clickGui.rainbow.isEnabled() ? ColorUtil.getRainbow(12, 0.6f, 1f, (long) (left * 2)) : colormodule);
	                                
	                                String title = String.valueOf(number.getValue());
	
	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                    draggingNumber = number;
	                                }
	
	                                title = title.toLowerCase();
	
	                                mc.FontRendererTiny.drawCenteredString(title, right - 17, top + 25 + count * 17, -1);
	                            }
	                            if (setting instanceof ModeSetting) {
	                                ModeSetting mode = (ModeSetting) setting;
	                                Gui.drawRect(0, 0, 0, 0, accent);
	                                Gui.drawRect(left - 1, top + 18 + count * 17, left + width + 1, top + 18 + count * 17 + 18, Client.clickGui.rainbow.isEnabled() ? ColorUtil.getRainbow(12, 0.5f, 1f, (long) (left * 2)) : colormodule);
	                                Gui.drawRect(left, top + 18 + count * 17, left + width, top + 18 + count * 17 + 18, 0xFF212121);
	
	                                Gui.drawRect(left, top + 35 + count * 17, left + width, top + 18 + count * 17 + 18, clickGui.rainbow.isEnabled() ? ColorUtil.getRainbow(12, 0.6f, 1f, (long) (left * 2)) : colormodule);
	                                
	                                String title = mode.getMode();
	
	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                	mode.cycle();
	                                }
	                                if (type == CLICK && hoveringSetting && button == 1) {
	                                	mode.cycleback();
	                                }
	                                title = title.toLowerCase();
	                                    
	                                mc.FontRendererTiny.drawCenteredString(title, right - 19, top + 21 + count * 17, -1);
	                            }
	                            if (setting instanceof KeybindSetting) {
	                                KeybindSetting bind = (KeybindSetting) setting;
	                                Gui.drawRect(0, 0, 0, 0, accent);
	                                Gui.drawRect(left - 1, top + 18 + count * 17, left + width + 1, top + 18 + count * 17 + 18, Client.clickGui.rainbow.isEnabled() ? ColorUtil.getRainbow(12, 0.5f, 1f, (long) (left * 2)) : colormodule);
	                                Gui.drawRect(left, top + 18 + count * 17, left + width, top + 18 + count * 17 + 18, binding == m ? 0xFF282828 : 0xFF212121);
	
	                                Gui.drawRect(left, top + 35 + count * 17, left + width, top + 18 + count * 17 + 18, clickGui.rainbow.isEnabled() ? ColorUtil.getRainbow(12, 0.6f, 1f, (long) (left * 2)) : colormodule);
	                                
	                                String title = Keyboard.getKeyName(bind.getKeyCode());
	
	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                    binding = m;
	                                }
	                                title = title.toLowerCase();
	                                
	                                mc.FontRendererTiny.drawCenteredString(title, right + 8 - mc.FontRendererTiny.getStringWidth(settingText), top + 20 + count * 17, -1);
	                                
	                            }
	                            if(setting instanceof NumberSetting) {
	                            	mc.FontRendererTiny.drawStringWithShadow(settingText, left + 1.5f, top + 25 + count * 17, -1);
	                            }else {
	                            	mc.FontRendererSmall.drawStringWithShadow(settingText, left + 1.5f, top + 20 + count * 17, -1);
	                            }
	                            count++;
	
	                        }
	                    }
	                }
	            left += 105;
	        }
	        
	        if (type == CLICK && button == 0) {
	            dragging = null;
	            // binding = null;
	        }
	
	        Client.modules.sort(Comparator.comparingDouble(m -> {
	                    String modulesText = ((Module) m).getDisplayName();
	
	                    return mc.FontRendererNormal.getStringWidth(modulesText);
	
	                })
	                        .reversed()
	        );
	        GL11.glDisable(GL11.GL_SCISSOR_TEST);
	    }else if (ClickGUI.style.is("Astolfo")) {
	    	if(ClickGUI.blur.isEnabled()) {
	            drawDefaultBackground();
	    	}
	    	
	        if (!openingAnimation.isDone()) {
	            GL11.glEnable(GL11.GL_SCISSOR_TEST);
	            RenderUtil.scissor(width / 2d - (openingAnimation.getOutput() * width / 2d), height / 2d - (openingAnimation.getOutput() * height / 2d), (openingAnimation.getOutput() * width), (openingAnimation.getOutput() * width));
	        } else {
	            if (openingAnimation.getDirection() == Direction.BACKWARDS){
	                this.mc.displayGuiScreen(null);
	                if (this.mc.currentScreen == null) {
	                    this.mc.setIngameFocus();
	                }
	            }
	        }

	        Client.modules.sort(Comparator.comparingDouble(m -> {
	                    String modulesText = ((Module) m).getName();

	                    return mc.FontRendererNormal.getStringWidth(modulesText);

	                })
	                        .reversed()
	        );
	        if (type == RELEASE && button == 0) {
	            dragging = null;
	            draggingNumber = null;
	            if (binding != null)
	                binding = null;
	        }

	        if (dragging != null) {
	            dragging.posX = mouseX - startX;
	            dragging.posY = mouseY - startY;
	        }

	        //importing all of the settings

	        toniqx.vailware.modules.render.ClickGUI clickGui = Client.clickGui;
	        String theme = clickGui.theme.getMode();
	        float offset = (float) (1);
	        float red = (float) (1 / 255);
	        float green = (float) (1 / 255);
	        float blue = (float) (1 / 255);
	        float rainbowSpeed = (float) (1);
	        boolean custom = true;
	        categoryPos = 8;


	        // this makes the text for the category always readable
	        if (theme.equals("Default")) {
	            if ((red * 255) + (green * 255) >= 445) {
	                categoryColor = 0xff000000;
	            }
	        }
	            categoryColor = -1;

	        textColor = -1;
	        
	        for (Category category : Category.values()) {
	        	if(category.name.equalsIgnoreCase("HIDDEN")) {
	        		continue;
	        	}
	            float left = category.posX,
	                    top = category.posY,
	                    width = 89,
	                    right = left + width;
	            leftCount = left;
	            boolean hoveringCategory = isHovered(left, top, left + width, top + 18, mouseX, mouseY);

	            if (type == CLICK && hoveringCategory && button == 0) {
	                dragging = category;
	                startX = mouseX - category.posX;
	                startY = mouseY - category.posY;
	                return;
	            }

	            if (type == CLICK && hoveringCategory && button == 1) {
	                category.expanded = !category.expanded;
	                return;
	            }
	            int count = 0;


	            HUD hud = Client.hud;

	            String categoryText;
	                categoryText = category.name;

	            GlStateManager.color(255, 255, 255);
	            Gui.drawRect(left - 1.7, top + 3.2f, left + width + 1.7, top + 18, 0xFFFF90D4);
	            Gui.drawRect(0, 0, 0, 0, accent);
	            mc.FontRendererNormal.drawStringWithShadow(categoryText, left - 0.4f, (float) (top + categoryPos) - 5, categoryColor);
	            int lastModBackground = -1;

	            boolean isGradient = false;

	            if (category.expanded)
	                for (Module m : Client.getModulesByCategory(category)) {
	                	if(m.category.name.equalsIgnoreCase("HIDDEN")) {
	                		continue;
	                	}
	                    boolean hoveringModule = isHovered(left, top + 18 + count * 18, left + width, top + 18 + count * 18 + 18, mouseX, mouseY);

	                    if (type == CLICK && hoveringModule && button == 0) {
	                        m.toggle();
	                        return;
	                    }

	                    if(hoveringModule) {
	                    	Gui.drawRect(left - 1, top + 18 + count * 18, left + width + 1, top + 18 + count * 18 + 18, 0x50575757);
	                    }
	                    
	                    if (type == CLICK && hoveringModule && button == 1) {
	                        m.setExpanded(!m.isExpanded());
	                        return;
	                    }
	                    
	                    Gui.drawRect(left - 1, top + 18 + count * 18, left + width + 1, top + 18 + count * 18 + 18, 0x99000000);

	                    Gui.drawRect(0, 0, 0, 0, accent);

	                    //this makes the text light up WIP as it is only configured for discord mode
	                    String textModule;
	                        textModule = m.name;

	                    if (!m.isEnabled() || m.getName().equals("HUD") || m.getName().equals("ArrayList")) {
	                    	mc.FontRendererArray.drawStringWithShadow(textModule, left + 1, top + 20.5f + count * 18, 0xFFbcbcc4);
	                    }
	                  

	                    int textColorHighlight = -1;

	                    if (theme.equals("Default"))
	                        textColorHighlight = applyOpacity(new Color(red, green, blue, 1).getRGB(), 1);


	                    if (m.isEnabled() && !m.getName().equals("HUD") && !m.getName().equals("ArrayList")) {
	                        mc.FontRendererArray.drawStringWithShadow(textModule, left + 1, top + 20.5f + count * 18, 0xFFde97bc);
	                    }
	                    if(!m.expanded) {
	                    	if(m.isEnabled()) {
	                    		mc.fontRendererObj.drawStringWithShadow("\u02C5", left + width - 7, top + 24.8f + count * 18, 0xFFC382A4);
	                    	}else {
	                    		mc.fontRendererObj.drawStringWithShadow("\u02C5", left + width - 7, top + 24.8f + count * 18, 0xFFFFFFFF);
	                    	}
	                    }else if(m.expanded) {
	                    	if(m.isEnabled()) {
	                    		mc.fontRendererObj.drawStringWithShadow("\u02C4", left + width - 7, top + 24.8f + count * 18, 0xFFC382A4);
	                    	}else {
	                    		mc.fontRendererObj.drawStringWithShadow("\u02C4", left + width - 7, top + 24.8f + count * 18, 0xFFFFFFFF);
	                    	}
	                    }


	                    count++;

	                    lastModBackground = modBackground;

	                    if (m.isExpanded()) {
	                        for (Setting setting : m.getSettings()) {
	                        	
	                            boolean hoveringSetting = isHovered(left, top + 18 + count * 18, left + width, top + 18 + count * 18 + 18, mouseX, mouseY);

	                            if (setting instanceof BooleanSetting) {
	                                BooleanSetting bool = (BooleanSetting) setting;
	                                Gui.drawRect(0, 0, 0, 0, accent);
	                                Gui.drawRect(left, top + 18 + count * 18, left + width, top + 18 + count * 18 + 18, 0xA9000000);

	                                String title = bool.isEnabled() ? "On" : "Off";

	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                    bool.toggle();
	                                }
	        	                    if(hoveringSetting) {
	        	                    	Gui.drawRect(left, top + 18 + count * 18, left + width, top + 18 + count * 18 + 17, 0x40383838);
	        	                    }
	                                title = title.toLowerCase();

	                                mc.FontRendererArray.drawStringWithShadow(title, right - 16, top + 19 + count * 18, -1);
	                            }
	                            if (setting instanceof NumberSetting) {
	                                NumberSetting number = (NumberSetting) setting;
	                                float percent = (float) ((number.getValue() - number.getMinimum()) / (number.getMaximum() - number.getMinimum())),
	                                        numberWidth = percent * width;

	                                if (draggingNumber != null && draggingNumber == number) {
	                                    double mousePercent = Math.min(1, Math.max(0, (mouseX - left) / width)),
	                                            newValue = (mousePercent * (number.getMaximum() - number.getMinimum())) + number.getMinimum();
	                                    number.setValue(newValue);
	                                }
	                                Gui.drawRect(0, 0, 0, 0, accent);
	                                Gui.drawRect(left, top + 18 + count * 18, left + width, top + 18 + count * 18 + 18, 0xA9000000);

	                                //this is the slider and the numberwidth helps it move and the sliderColor changes the color of it. We need to implement new color for diffrent modes
	                                Gui.drawRect(left, top + 33f + count * 18, numberWidth + left, top + 18 + count * 18 + 17, 0xFFde97bc);

	                                //this int controls the color or the circle dot. II made a draw rect to override the color of the other drawrects
	                                int colorslider = 10;
	                                Gui.drawRect(0, 0, 0, 0, colorslider);

	                                String title = String.valueOf(number.getValue());

	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                    draggingNumber = number;
	                                }
	        	                    if(hoveringSetting) {
	        	                    	Gui.drawRect(left, top + 18 + count * 18, left + width, top + 18 + count * 18 + 17, 0x40383838);
	        	                    }

	                                title = title.toLowerCase();

	                                mc.FontRendererArray.drawCenteredString(title, right - 16, top + 19 + count * 18, -1);
	                            }
	                            if (setting instanceof ModeSetting) {
	                                ModeSetting mode = (ModeSetting) setting;
	                                Gui.drawRect(0, 0, 0, 0, accent);
	                                Gui.drawRect(left, top + 18 + count * 18, left + width, top + 18 + count * 18 + 18, 0xA9000000);

	                                String title = mode.getMode();

	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                	mode.cycle();
	                                }
	                                if (type == CLICK && hoveringSetting && button == 1) {
	                                	mode.cycleback();
	                                }
	        	                    if(hoveringSetting) {
	        	                    	Gui.drawRect(left, top + 18 + count * 18, left + width, top + 18 + count * 18 + 17, 0x40383838);
	        	                    }
	                                title = title.toLowerCase();
	                                    
	                                mc.FontRendererArray.drawCenteredString(title, right - 22, top + 19 + count * 18, -1);
	                            }
	                            if (setting instanceof KeybindSetting) {
	                                KeybindSetting bind = (KeybindSetting) setting;
	                                Gui.drawRect(0, 0, 0, 0, accent);
	                                Gui.drawRect(left, top + 18 + count * 18, left + width, top + 18 + count * 18 + 18, binding == m ? 0xBF000000 : 0xA9000000);

	                                String title = Keyboard.getKeyName(bind.getKeyCode());

	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                    binding = m;
	                                }
	        	                    if(hoveringSetting) {
	        	                    	Gui.drawRect(left, top + 18 + count * 18, left + width, top + 18 + count * 18 + 17, 0x40383838);
	        	                    }
	                                title = title.toLowerCase();
	                                
	                                mc.FontRendererArray.drawStringWithShadow(title, right - 4 - mc.FontRendererNormal.getStringWidth(title), top + 19 + count * 18, -1);

	                            }
	                            String settingText;
	                                settingText = setting.name.toLowerCase();

	                                mc.FontRendererNormal.drawStringWithShadow(settingText, left + 1.5f, top + 18 + count * 18, 0xFFbcbcc4);

	                            count++;

	                        }
	                    }
	                }
	            left += 105;
	        }
	        
	        if (type == CLICK && button == 0) {
	            dragging = null;
	            // binding = null;
	        }

	        Client.modules.sort(Comparator.comparingDouble(m -> {
	                    String modulesText = ((Module) m).getDisplayName();

	                    return mc.FontRendererNormal.getStringWidth(modulesText);

	                })
	                        .reversed()
	        );
	        GL11.glDisable(GL11.GL_SCISSOR_TEST);
	    }else if(ClickGUI.style.is("Zeroday")) {
	    	if(ClickGUI.blur.isEnabled()) {
	            drawDefaultBackground();
	    	}
	    	
	        float red = (float) (Client.clickGui.red.getValue() / 255);
	        float green = (float) (Client.clickGui.green.getValue() / 255);
	        float blue = (float) (Client.clickGui.blue.getValue() / 255);
	        int colormodule = new Color(red, green, blue, 0.8f).getRGB();
	        int colorcategory = new Color(red, green, blue, 1).getRGB();
	        int colorslider = new Color(red, green, blue, 0.7f).getRGB();

	        if (!openingAnimation.isDone()) {
	            GL11.glEnable(GL11.GL_SCISSOR_TEST);
	            RenderUtil.scissor(width / 2d - (openingAnimation.getOutput() * width / 2d), height / 2d - (openingAnimation.getOutput() * height / 2d), (openingAnimation.getOutput() * width), (openingAnimation.getOutput() * width));
	        } else {
	            if (openingAnimation.getDirection() == Direction.BACKWARDS){
	                this.mc.displayGuiScreen(null);
	                if (this.mc.currentScreen == null) {
	                    this.mc.setIngameFocus();
	                }
	            }
	        }
	
	        Client.modules.sort(Comparator.comparingDouble(m -> {
	                    String modulesText = ((Module) m).getName();
	
	                    return mc.FontRendererNormal.getStringWidth(modulesText);
	
	                })
	                        .reversed()
	        );
	        if (type == RELEASE && button == 0) {
	            dragging = null;
	            draggingNumber = null;
	            if (binding != null)
	                binding = null;
	        }
	
	        if (dragging != null) {
	            dragging.posX = mouseX - startX;
	            dragging.posY = mouseY - startY;
	        }
	
	        //importing all of the settings
	
	        toniqx.vailware.modules.render.ClickGUI clickGui = Client.clickGui;
	        String theme = clickGui.theme.getMode();
	        float offset = (float) (1);
	        float rainbowSpeed = (float) (1);
	        boolean custom = true;
	        categoryPos = 8;
	        textColor = -1;
	        
	        for (Category category : Category.values()) {
	        	if(category.name.equalsIgnoreCase("HIDDEN")) {
	        		continue;
	        	}
	            float left = category.posX,
	                    top = category.posY,
	                    width = 96,
	                    right = left + width;
	
	            boolean hoveringCategory = isHovered(left, top - 3, left + width, top + 18, mouseX, mouseY);
	            leftCount = left;
	            if (type == CLICK && hoveringCategory && button == 0) {
	                dragging = category;
	                startX = mouseX - category.posX;
	                startY = mouseY - category.posY;
	                return;
	            }
	
	            if (type == CLICK && hoveringCategory && button == 1) {
	                category.expanded = !category.expanded;
	                return;
	            }
	            int count = 0;
	
	            HUD hud = Client.hud;
	
	            String categoryText;
	                categoryText = category.name;
	
	            GlStateManager.color(255, 255, 255);
                Gui.drawRect(left, top - 3, left + width, top + 18, ColorUtil.getRainbow(6, 0.50f, 1, (long) (left * 3)));
                Gui.drawRect(left, top - 3, left + 22, top + 18, 0x50000000);
	            mc.FontRendererLargeBold.drawString(ChatFormatting.BOLD + categoryText, left + 25, (float) (top + categoryPos) - 10, -1, false);
	            int lastModBackground = -1;
	
	            boolean isGradient = false;
	
	            if (category.expanded)
	                for (Module m : Client.getModulesByCategory(category)) {
	                	if(m.category.name.equalsIgnoreCase("HIDDEN")) {
	                		continue;
	                	}
	                    boolean hoveringModule = isHovered(left, top + 18 + count * 17, left + width, top + 18 + count * 17 + 17, mouseX, mouseY);
 	                    if (type == CLICK && hoveringModule && button == 0) {
	                        m.toggle();
	                        return;
	                    }
	
	                    if (type == CLICK && hoveringModule && button == 1) {
	                        m.setExpanded(!m.isExpanded());
	                        return;
	                    }
	                    
	                    Gui.drawRect(left, top + 18f + count * 17, left + width, top + 19 + count * 17 + 17 - 1, 0xFF3A393A);

	                    String textModule;
	                    textModule = m.name;
	
	                    if (!m.isEnabled() || m.getName().equals("HUD") || m.getName().equals("ArrayList")) {
	                    	mc.FontRendererArray.drawStringWithShadow(textModule, left + 3, top + 20.5f + count * 17, 0xFFededed);
	                    }
	                  
	
	                    int textColorHighlight = -1;

	                    if (m.isEnabled() && !m.getName().equals("HUD") && !m.getName().equals("ArrayList")) {
	                        Gui.drawRect(left, top + 18f + count * 17, left + width, top + 17 + count * 17 + 18, ColorUtil.getRainbow(6, 0.50f, 0.9f, (long) (left * 3)));
	                        mc.FontRendererArray.drawString(textModule, left + 3, top + 20.5f + count * 17, -1, false);
	                    }
	                    
                        if(m.expanded) {
                        	mc.getTextureManager().bindTexture(new ResourceLocation("vail/uparrow.png"));
                        }else {
            				mc.getTextureManager().bindTexture(new ResourceLocation("vail/downarrow.png"));
                        }
                        
                        Gui.drawModalRectWithCustomSizedTexture(left + width - 10, top + 22.8f + count * 17, 0, 0, 7, 7, 7, 7);
	                    
	                    if(hoveringModule) {
	                    	Gui.drawRect(left, top + 18f + count * 17, left + width, top + 17 + count * 17 + 18, 0x20FFFFFF);
	                    }
	                    
	                    count++;
	
	                    lastModBackground = modBackground;
	
	                    if (m.isExpanded()) {
	                        for (Setting setting : m.getSettings()) {
	                            String settingText;
                                settingText = setting.name;

	                            boolean hoveringSetting = isHovered(left, top + 18 + count * 17, left + width, top + 18 + count * 17 + 18, mouseX, mouseY);
	
	                            if (setting instanceof BooleanSetting) {
	                                BooleanSetting bool = (BooleanSetting) setting;
	                                Gui.drawRect(left, top + 18 + count * 17, left + width, top + 18 + count * 17 + 18, 0xFF3A393A);
	
	                                String title = bool.isEnabled() ? "On" : "Off";
	
	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                    bool.toggle();
	                                }
	                                title = title.toLowerCase();
	
		                            if(hoveringSetting) {
		                            	Gui.drawRect(left + 2, top + 18 + count * 17, left + width - 2, top + 18 + count * 17 + 18, 0xFF363436);
		                            }else {
		                            	Gui.drawRect(left + 2, top + 18 + count * 17, left + width - 2, top + 18 + count * 17 + 18, 0xFF333233);
		                            }
	                                mc.FontRendererClean.drawStringWithShadow(title, right - 16, top + 20 + count * 17, -1);
	                                
	                            }
	                            if (setting instanceof NumberSetting) {
	                            	float sliderWidth = width - 20;
	                            	float sliderStart = left + 10;
	                            	
	                            	Gui.drawRect(left, top + 18 + count * 17, left + width, top + 18 + count * 17 + 18, 0xFF3A393A);
	                            	
	                                NumberSetting number = (NumberSetting) setting;
	                                float percent = (float) ((number.getValue() - number.getMinimum()) / (number.getMaximum() - number.getMinimum())),
	                                        numbersliderWidth = percent * sliderWidth;
	
	                                if (draggingNumber != null && draggingNumber == number) {
	                                	double mousePercent = Math.min(1, Math.max(0, (mouseX - sliderStart) / sliderWidth)),
	                                    		newValue = (mousePercent * (number.getMaximum() - number.getMinimum())) + number.getMinimum();
	                                    number.setValue(newValue);
	                                }
	                                
	                                String title = String.valueOf(number.getValue());
	
		                            if(hoveringSetting) {
		                            	Gui.drawRect(left + 2, top + 18 + count * 17, left + width - 2, top + 18 + count * 17 + 18, 0xFF363436);
		                            }else {
		                            	Gui.drawRect(left + 2, top + 18 + count * 17, left + width - 2, top + 18 + count * 17 + 18, 0xFF333233);
		                            }

	                                Gui.drawRect(sliderStart, top + 9 + count * 17 + 17, left + width - 10, top + 13 + count * 17 + 17, 0xFF3A393A);
	                                Gui.drawRect(sliderStart, top + 9 + count * 17 + 17, numbersliderWidth + sliderStart, top + 13 + count * 17 + 17, ColorUtil.getRainbow(6, 0.50f, 1, (long) (left * 3)));
                            
	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                    draggingNumber = number;
	                                }
	
	                                title = title.toLowerCase();
	
	                                mc.FontRendererTinyBold.drawCenteredString(ChatFormatting.BOLD + title, right - width / 2 + 12, top + 23 + count * 17, -1);
	                            	
	                            }
	                            
	                            if (setting instanceof ModeSetting) {
	                                ModeSetting mode = (ModeSetting) setting;  	
	                                
	                                Gui.drawRect(left, top + 18 + count * 17, left + width, top + 18 + count * 17 + 18, 0xFF3A393A);
	
	                                String title = mode.getMode();
	
	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                	mode.cycle();
	                                }
	                                if (type == CLICK && hoveringSetting && button == 1) {
	                                	mode.cycleback();
	                                }
		                            if(hoveringSetting) {
		                            	Gui.drawRect(left + 2, top + 18 + count * 17, left + width - 2, top + 18 + count * 17 + 18, 0xFF363436);
		                            }else {
		                            	Gui.drawRect(left + 2, top + 18 + count * 17, left + width - 2, top + 18 + count * 17 + 18, 0xFF333233);
		                            }
	                                mc.FontRendererClean.drawStringWithShadow(title, right - 18 - mc.FontRendererClean.getStringWidth(title), top + 20 + count * 17, -1);

	                                if(mode.Expanded) {
	                                	Gui.drawRect(left + width + 2, top + 25 + count * 17 - 6, left + width + 12.5f, top + 44 + count * 17 + 10, 0xFF3A393A);
	                                	mc.getTextureManager().bindTexture(new ResourceLocation("vail/uparrow.png"));
	                                }else {
		                				mc.getTextureManager().bindTexture(new ResourceLocation("vail/downarrow.png"));
	                                }
	                                Gui.drawModalRectWithCustomSizedTexture(left + width - 12.5f, top + 25 + count * 17, 0, 0, 4, 4, 4, 4);
	                            }
	                            if (setting instanceof KeybindSetting) {
	                                KeybindSetting bind = (KeybindSetting) setting;
	                                Gui.drawRect(left, top + 18 + count * 17, left + width, top + 18 + count * 17 + 18, binding == m ? 0xFF4A4A4A : 0xFF3A393A);
	
	                                String title = Keyboard.getKeyName(bind.getKeyCode());
	
	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                    binding = m;
	                                }
	                                title = title.toLowerCase();
	                                
		                            if(hoveringSetting) {
		                            	Gui.drawRect(left + 2, top + 18 + count * 17, left + width - 2, top + 18 + count * 17 + 18, 0xFF363436);
		                            }else {
		                            	Gui.drawRect(left + 2, top + 18 + count * 17, left + width - 2, top + 18 + count * 17 + 18, 0xFF333233);
		                            }
	                                
	                                mc.FontRendererClean.drawStringWithShadow(title, right - 4 - mc.FontRendererNormal.getStringWidth(title), top + 19 + count * 17, -1);
	                            	
	                            }
	                            
	                            if(setting instanceof NumberSetting) {
	                            	mc.FontRendererTiny.drawStringWithShadow(settingText, left + 4.5f, top + 17 + count * 17, -1);
	                            }else {
	                            	mc.FontRendererClean.drawStringWithShadow(settingText, left + 4.5f, top + 20 + count * 17, -1);
	                            }
	                            
	                            count++;

	                        }
	                    }
	                }
                Gui.drawRect(left, top + 18, left + width, top + 18.6f, 0x20000000);
                Gui.drawRect(left, top + 18, left + width, top + 18.6f, 0x20000000);
                Gui.drawRect(left, top + 18, left + width, top + 18.8f, 0x20000000);
	            left += 105;
	        }
	        
	        if (type == CLICK && button == 0) {
	            dragging = null;
	            // binding = null;
	        }
	
	        Client.modules.sort(Comparator.comparingDouble(m -> {
	                    String modulesText = ((Module) m).getDisplayName();
	
	                    return mc.FontRendererNormal.getStringWidth(modulesText);
	
	                })
	                        .reversed()
	        );
	        GL11.glDisable(GL11.GL_SCISSOR_TEST);
	    }else if(ClickGUI.style.is("Vape")) {
	    	if(ClickGUI.blur.isEnabled()) {
	            drawDefaultBackground();
	    	}
	    	
	        float red = (float) (Client.clickGui.red.getValue() / 255);
	        float green = (float) (Client.clickGui.green.getValue() / 255);
	        float blue = (float) (Client.clickGui.blue.getValue() / 255);
	        int colormodule = new Color(red, green, blue, 0.8f).getRGB();
	        int colorcategory = new Color(red, green, blue, 1).getRGB();
	        int colorslider = new Color(red, green, blue, 0.7f).getRGB();

	        if (!openingAnimation.isDone()) {
	            GL11.glEnable(GL11.GL_SCISSOR_TEST);
	            RenderUtil.scissor(width / 2d - (openingAnimation.getOutput() * width / 2d), height / 2d - (openingAnimation.getOutput() * height / 2d), (openingAnimation.getOutput() * width), (openingAnimation.getOutput() * width));
	        } else {
	            if (openingAnimation.getDirection() == Direction.BACKWARDS){
	                this.mc.displayGuiScreen(null);
	                if (this.mc.currentScreen == null) {
	                    this.mc.setIngameFocus();
	                }
	            }
	        }
	
	        Client.modules.sort(Comparator.comparingDouble(m -> {
	                    String modulesText = ((Module) m).getName();
	
	                    return mc.FontRendererNormal.getStringWidth(modulesText);
	
	                })
	                        .reversed()
	        );
	        if (type == RELEASE && button == 0) {
	            dragging = null;
	            draggingNumber = null;
	            if (binding != null)
	                binding = null;
	        }
	
	        if (dragging != null) {
	            dragging.posX = mouseX - startX;
	            dragging.posY = mouseY - startY;
	        }
	
	        //importing all of the settings
	
	        toniqx.vailware.modules.render.ClickGUI clickGui = Client.clickGui;
	        String theme = clickGui.theme.getMode();
	        float offset = (float) (1);
	        float rainbowSpeed = (float) (1);
	        boolean custom = true;
	        categoryPos = 8;
	        textColor = -1;
	        
	        for (Category category : Category.values()) {
	        	if(category.name.equalsIgnoreCase("HIDDEN")) {
	        		continue;
	        	}
	            float left = category.posX,
	                    top = category.posY,
	                    width = 98,
	                    right = left + width;
	
	            boolean hoveringCategory = isHovered(left, top + 3, left + width, top + 18, mouseX, mouseY);
	            leftCount = left;
	            if (type == CLICK && hoveringCategory && button == 0) {
	                dragging = category;
	                startX = mouseX - category.posX;
	                startY = mouseY - category.posY;
	                return;
	            }
	
	            if (type == CLICK && hoveringCategory && button == 1) {
	                category.expanded = !category.expanded;
	                return;
	            }
	            int count = 0;
	
	            HUD hud = Client.hud;
	
	            String categoryText;
	                categoryText = category.name;
	
	            RenderUtil.glColor(0xFF1A1A1A);
                Draw.drawRoundedRect(left, top + 3, width, 18, 3);
            	GL11.glColor4f(1, 1, 1, 1);
                
	            mc.FontRendererClean.drawString(categoryText, left + 19, (float) (top + categoryPos) - 3, ColorUtil.getRainbow(7, 0.60f, 1f, (long) (left)), false);
	            int lastModBackground = -1;
	
	            boolean isGradient = false;
	            
                if(!category.expanded) {
                	mc.fontRendererObj.drawStringWithShadow("\u02C5", left + width - 6, (float) (top + categoryPos) + 1, Color.gray.getRGB());
                }else if(category.expanded) {
                	mc.fontRendererObj.drawStringWithShadow("\u02C4", left + width - 6, (float) (top + categoryPos) + 1, Color.gray.getRGB());
                }
	            
	            if (category.expanded)
	                for (Module m : Client.getModulesByCategory(category)) {
	                	if(m.category.name.equalsIgnoreCase("HIDDEN")) {
	                		continue;
	                	}
	    	            RenderUtil.glColor(0xFF1A1A1A);
	                	Draw.drawRoundedRect(left, top + 5 + count * 17 + 15, width, 18, 3);
	                	
	                	
	                    boolean hoveringModule = isHovered(left, top + 18 + count * 17, left + width, top + 18 + count * 17 + 17, mouseX, mouseY);
 	                    if (type == CLICK && hoveringModule && button == 0) {
	                        m.toggle();
	                        return;
	                    }
	
	                    if (type == CLICK && hoveringModule && button == 1) {
	                        m.setExpanded(!m.isExpanded());
	                        return;
	                    }
	                    
	                    Gui.drawRect(left, top + 18f + count * 17, left + width, top + 19 + count * 17 + 17 - 1, 0xFF1A1A1A);

	                    String textModule;
	                    textModule = m.name;
	
	                    if(hoveringModule) {
	                    	Gui.drawRect(left, top + 18f + count * 17, left + width, top + 17 + count * 17 + 18, 0xFF212020);
	                    }

	                    if (!m.isEnabled() || m.getName().equals("HUD") || m.getName().equals("ArrayList")) {
	                    	mc.FontRendererTiny.drawStringWithShadow(textModule, left + 3, top + 21.5f + count * 17, 0xFF898a8c);
	                    }
	
	                    int textColorHighlight = -1;

	                    if (m.isEnabled() && !m.getName().equals("HUD") && !m.getName().equals("ArrayList")) {
	                        Gui.drawRect(left, top + 18f + count * 17, left + width, top + 17 + count * 17 + 18, ColorUtil.getRainbow(7, 0.60f, 1f, (long) (left)));
	                        mc.FontRendererTiny.drawString(textModule, left + 3, top + 21.5f + count * 17, 0xFF333333, false);
	                    }
	                    
	                    mc.FontRendererLarge.drawStringWithShadow(".", left + width - 9, top + 11f + count * 17, Color.gray.getRGB());
	                    mc.FontRendererLarge.drawStringWithShadow(".", left + width - 9, top + 14f + count * 17, Color.gray.getRGB());
	                    mc.FontRendererLarge.drawStringWithShadow(".", left + width - 9, top + 17f + count * 17, Color.gray.getRGB());
	                    
	                    
	                    count++;
	
	                    lastModBackground = modBackground;
	
	                    if (m.isExpanded()) {
	                        for (Setting setting : m.getSettings()) {
	                            String settingText;
                                settingText = setting.name;

	                            boolean hoveringSetting = isHovered(left, top + 18 + count * 17, left + width, top + 18 + count * 17 + 18, mouseX, mouseY);
	
	                            if (setting instanceof BooleanSetting) {
	                                BooleanSetting bool = (BooleanSetting) setting;
	                                Gui.drawRect(left, top + 18 + count * 17, left + width, top + 18 + count * 17 + 18, 0xFF1A1A1A);
	
	                                String title = bool.isEnabled() ? "On" : "Off";

	                                RenderUtils.glColor(ColorUtil.getRainbow(7, 0.60f, 1f, (long) (left)));
	                                Draw.drawRoundedRect(left + width - 16, top + 4 + count * 17 + 19, 12, 6, 3);
	                                if(bool.enabled) {
	                                	RenderUtils.glColor(0xFF000000);
	                                	Draw.drawRoundedRect(left + width - 9, top + 5 + count * 17 + 19, 4, 4, 2f);
	                                }else {
	                                    RenderUtils.glColor(0xFF000000);
	                                    Draw.drawRoundedRect(left + width - 15, top + 5 + count * 17 + 19, 4, 4, 2f);
	                                }
	                                
	                                GL11.glColor4f(1, 1, 1, 1);
	                                
	                                
	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                    bool.toggle();
	                                    if(!bool.enabled) {
	                                    	Client.boolTranslation = 0;
	                                    }else {
	                                    	Client.boolTranslation = 10;
	                                    }
	                                    
	                                }
	                                title = title.toLowerCase();
	
	                            }
	                            if (setting instanceof NumberSetting) {
	                            	float sliderWidth = width - 58;
	                            	float sliderStart = left + 54;
	                            	
	                            	Gui.drawRect(left, top + 18 + count * 17, left + width, top + 18 + count * 17 + 18, 0xFF1A1A1A);
	                            	
	                                NumberSetting number = (NumberSetting) setting;
	                                float percent = (float) ((number.getValue() - number.getMinimum()) / (number.getMaximum() - number.getMinimum())),
	                                        numbersliderWidth = percent * sliderWidth;
	
	                                if (draggingNumber != null && draggingNumber == number) {
	                                	double mousePercent = Math.min(1, Math.max(0, (mouseX - sliderStart) / sliderWidth)),
	                                    		newValue = (mousePercent * (number.getMaximum() - number.getMinimum())) + number.getMinimum();
	                                    number.setValue(newValue);
	                                }
	                                
	                                String title = String.valueOf(number.getValue());

	                                Gui.drawRect(sliderStart, top + 12 + count * 17 + 17, left + width - 4, top + 14 + count * 17 + 17, 0xFF3A393A);
	                                Gui.drawRect(sliderStart, top + 12 + count * 17 + 17, numbersliderWidth + sliderStart, top + 14 + count * 17 + 17, ColorUtil.getRainbow(7, 0.60f, 1f, (long) (left)));
                            
	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                    draggingNumber = number;
	                                }
	
	                                title = title.toLowerCase();
	
	                                mc.FontRendererTiny.drawCenteredString(title, right - width / 2 + 25, top + 19 + count * 17, -1);
	                            	
	                            }
	                            
	                            if (setting instanceof ModeSetting) {
	                                ModeSetting mode = (ModeSetting) setting;  	
	                                
	                                Gui.drawRect(left, top + 18 + count * 17, left + width, top + 18 + count * 17 + 18, 0xFF1A1A1A);
	
	                                String title = mode.getMode();
	
	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                	mode.cycle();
	                                }
	                                if (type == CLICK && hoveringSetting && button == 1) {
	                                	mode.cycleback();
	                                }

	                                mc.FontRendererTiny.drawCenteredString(title, right - 24, top + 21 + count * 17, -1);

	                            }
	                            if (setting instanceof KeybindSetting) {
	                                KeybindSetting bind = (KeybindSetting) setting;
	                                Gui.drawRect(left, top + 18 + count * 17, left + width, top + 18 + count * 17 + 18, binding == m ? 0xFF1F1F1F : 0xFF1A1A1A);
	
	                                String title = Keyboard.getKeyName(bind.getKeyCode());
	
	                                if (type == CLICK && hoveringSetting && button == 0) {
	                                    binding = m;
	                                }
	                                title = title.toLowerCase();
	                                
	                                mc.FontRendererTiny.drawCenteredString(title, right - 12, top + 21 + count * 17, -1);
	                            	
	                            }
	                            
	                            mc.FontRendererTiny.drawStringWithShadow(settingText, left + 2f, top + 21 + count * 17, 0xFFBFBFBF);
	                            
	                            count++;

	                        }
	                    }
	                }
                left += 105;
	        }
	        
	        if (type == CLICK && button == 0) {
	            dragging = null;
	            // binding = null;
	        }
	
	        Client.modules.sort(Comparator.comparingDouble(m -> {
	                    String modulesText = ((Module) m).getDisplayName();
	
	                    return mc.FontRendererNormal.getStringWidth(modulesText);
	
	                })
	                        .reversed()
	        );
	        GL11.glDisable(GL11.GL_SCISSOR_TEST);
	    }
    	
    	if(!ClickGUI.style.is("Rise")) {
    		RenderUtils.glColor(0xFFFFFFFF);
    		Draw.drawRoundedRect(5, GuiScreen.height - 26, mc.FontRendererClean.getStringWidth(Client.username) + 10, 15, 3);
    		RenderUtils.glColor(0xFF202021);
    		Draw.drawRoundedRect(6, GuiScreen.height - 25, mc.FontRendererClean.getStringWidth(Client.username) + 8, 13, 3);
    		RenderUtils.glColor(0xFFFFFFFF);
    		mc.FontRendererClean.drawStringWithShadow(Client.username, 10, GuiScreen.height - 24.5f, 0xFFFFFFFF);
    	}
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public boolean isHovered(float left, float top, float right, float bottom, int mouseX, int mouseY) {
        return mouseX >= left && mouseY >= top && mouseX < right && mouseY < bottom;
    }

    public enum Handle {
        DRAW,
        CLICK,
        RELEASE
    }

}
