package toniqx.vailware.ui.clickui;

import static toniqx.vailware.ui.clickui.DiscordHUD.Handle.*;

import java.awt.Color;
import java.util.Comparator;

import org.lwjgl.input.Keyboard;
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

public class DiscordHUD extends GuiScreen {
	String selectedCategory;
	public boolean dragging;
	public boolean guiExpanded;
	public float width;
    float startX;
    float startY;
    float guiX = Client.posX;
    float guiY = Client.posY;
    public Module binding;
    public NumberSetting draggingNumber;
    public int accent, modBackground, settingsBackground, bindingBackground;
    public int sliderColor, categoryColor, enabledColor, textColor;
	float left = guiX;
	float top = guiY;
	float widthMax = 370;
	float height = 10;
	float right = left + width;
	float dropDownHeight = 220;
	float dropDownWidth = 70;
	float categoryX = dropDownWidth / 2;
	float categoryY = top + height / height * dropDownHeight / 2 - height - 58;
	float offset = 16f;
	float moffset = 11f;
	float profileWidth = 75;
	float profileHeight = 20;
	float bottom = height + dropDownHeight;
	float buttonWidth = 60;
	float buttonHeight = 16;
	float modButtonWidth = 60;
	float modButtonHeight = 10.5f;
	float scrollWidthText = profileWidth / 2 + dropDownWidth;
	float modScrollX = left + dropDownWidth + profileWidth / 2 - 2;
	float modScrollY = top + height + 1;
	int colorButton = 0x40000000;
	int colorDragButton = 0xFF1d1d1f;
	int colorModButton = 0x5051545c;
	int colorOutline = 0xFF202225;
	int colorDropdown = 0xFF2F3136;
	int colorProfile = 0xFF292B2F;
	float max = widthMax;
	float min = dropDownWidth + profileWidth - 1;
    SmootherStepAnimation openingAnimation = new SmootherStepAnimation(300, 1, Direction.FORWARDS);

    double categoryPos;
    
    public static int applyOpacity(int color, float opacity) {
        Color old = new Color(color);
        return new Color(old.getRed(), old.getGreen(), old.getBlue(), (int) (opacity * (color >> 24 & 255))).getRGB();
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
    
    public void handle(int mouseX, int mouseY, int button, Handle type) {
    	if(!Client.premium) {
    		if(ClickGUI.style.is("Zeroday") || ClickGUI.style.is("Vape") || ClickGUI.style.is("Astolfo") || ClickGUI.style.is("Novoline")) {
    			ClickGUI.style.cycle();
    		}
    	}
    	
    	selectedCategory = Client.selectedCategory;
        guiX = Client.posX;
        guiY = Client.posY;
    	widthMax = 370;
    	left = guiX;
    	top = guiY;
    	height = 10;
    	right = left + width;
    	dropDownHeight = 250;
    	dropDownWidth = 40;
    	categoryX = dropDownWidth / 2 + 4.5f;
    	categoryY = top + height / height * dropDownHeight / 2 - height - 102;
    	offset = 27f;
    	moffset = 11f;
    	profileWidth = 75;
    	profileHeight = 20;
    	bottom = height + dropDownHeight;
    	buttonWidth = 60;
    	buttonHeight = 16;
    	modButtonWidth = 60;
    	modButtonHeight = 10.5f;
    	scrollWidthText = profileWidth / 2 + dropDownWidth;
    	modScrollX = left + dropDownWidth + profileWidth / 2 - 7;
    	modScrollY = top + height + 16;
    	colorButton = 0x40000000;
    	colorDragButton = 0xFF1d1d1f;
    	colorModButton = 0x5051545c;
    	colorOutline = 0xFF202225;
    	colorDropdown = 0xFF2F3136;
    	colorProfile = 0xFF292B2F;
    	max = widthMax;
    	min = dropDownWidth + profileWidth - 1;
        if (!openingAnimation.isDone()) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtil.scissor(width / 2d + Client.posX - (openingAnimation.getOutput() * width / 2d), height / 2d - (openingAnimation.getOutput() * height / 2d - Client.posY), (openingAnimation.getOutput() * width), (openingAnimation.getOutput() * width));
        } else {
            if (openingAnimation.getDirection() == Direction.BACKWARDS){
                this.mc.displayGuiScreen(null);
                if (this.mc.currentScreen == null) {
                    this.mc.setIngameFocus();
                }
            }
        }
    	
    	boolean hoveringDragbar = isHovered(left, top, left + width, top + height, mouseX, mouseY);
    	
    	if (type == CLICK && hoveringDragbar && button == 0) {
    		dragging = true;
    		startX = mouseX - guiX;
    		startY = mouseY - guiY;
    	}
    	
        if (type == RELEASE && button == 0) {
    		dragging = false;
        }
    	
        if(dragging) {
            Client.posX = mouseX - startX;
            Client.posY = mouseY - startY;
        }
        
    	int count = 0;
       	int scount = 0;
    	int mcount = 0;
    	int catCount = 0;	    		
    	HUD hud = Client.hud;
    		
    	String categoryText;
    	
    	GlStateManager.color(255, 255, 255);
    	Gui.drawRect(left, top, left + width + 1, top + height, colorDragButton);
    	Gui.drawRect(left, top + height, left + dropDownWidth, top + height + dropDownHeight, colorOutline);
    	Gui.drawRect(left + dropDownWidth, top + height, left + dropDownWidth + width - dropDownWidth + 1, top + height + dropDownHeight, colorDropdown);
    	Gui.drawRect(left + dropDownWidth, top + bottom - profileHeight, left + dropDownWidth + profileWidth, top + bottom, colorProfile);
    	Gui.drawRect(left + dropDownWidth, top + height + profileHeight - 5, left + dropDownWidth + profileWidth, top + height + profileHeight - 6, colorProfile);
    	RenderUtil.glColor(0xF9292B2E);
    	Draw.drawRoundedRect(left + dropDownWidth * 2 / 2 + profileWidth + profileWidth / 14, top + + profileHeight / 2 + height - 4, 244, 238, 4);

		mc.FontRendererNormal.drawCenteredString(Client.username, left + dropDownWidth + 36, top + dropDownHeight - 8, 0xFFFFFFFF);
    	
    	mc.FontRendererNormal.drawCenteredString(selectedCategory, left + dropDownWidth * 2 - 5, top + height + profileHeight - 20, -1);
    	
        for (Category category : Category.values()) {
	        boolean hoveringCategory = isHovered(left + categoryX - dropDownWidth / 2 + 2, categoryY + catCount * offset, left + categoryX + buttonWidth - dropDownWidth / 2 - 32, categoryY + catCount * offset + buttonHeight + 10, mouseX, mouseY);
	        
        	if(category.name.equalsIgnoreCase("HIDDEN")) {
        		continue;
        	}
        	
        	if(category.name.equalsIgnoreCase(selectedCategory)) {
        		RenderUtil.glColor(0xFF292B2E);
        		Draw.drawRoundedRect(left + categoryX - dropDownWidth / 2 + 2, categoryY + catCount * offset, 26, 26, 8);
        	}else {
        		RenderUtil.glColor(0xFF292B2E);
        		Draw.drawRoundedRect(left + categoryX - dropDownWidth / 2 + 2, categoryY + catCount * offset, 26, 26, hoveringCategory ? 8 : 12);
        	}
        	
        	String name = category.name;
        	String nameCat = "";
        	if(name.equalsIgnoreCase("Combat")) {
        		nameCat = "CT";
        	}else if(name.equalsIgnoreCase("Movement")) {
        		nameCat = "MT";
        	}else if(name.equalsIgnoreCase("Render")) {
        		nameCat = "RR";
        	}else if(name.equalsIgnoreCase("Player")) {
        		nameCat = "PR";
        	}else if(name.equalsIgnoreCase("Misc")) {
        		nameCat = "MC";
        	}else if(name.equalsIgnoreCase("Exploit")) {
        		nameCat = "ET";
        	}else if(name.equalsIgnoreCase("Ghost")) {
        		nameCat = "GT";
        	}else if(name.equalsIgnoreCase("Customize")) {
        		nameCat = "CE";
        	}

	        Gui.drawRect(left + dropDownWidth + profileWidth, top + height, left + dropDownWidth + profileWidth - 2, top + height + dropDownHeight - profileHeight, colorProfile);
	        mc.FontRendererLarge.drawCenteredString(nameCat, left + categoryX - 6, categoryY + catCount * 27 + buttonHeight / 2 - 3, -1);

	        for (Module m : Client.getModulesByCategory(category)) {
	        	if(m.category.name.equalsIgnoreCase(selectedCategory)) {
	        		String mName = m.name;

	        		boolean hoveringModule = isHovered(left + dropDownWidth + 1, modScrollY + count * moffset, left + dropDownWidth + profileWidth - 3, modButtonHeight + modScrollY + count * moffset, mouseX, mouseY);
	    	        
	        		if(hoveringModule) {
	        			Gui.drawRect(left + dropDownWidth + 1, modScrollY + count * moffset, left + dropDownWidth + profileWidth - 3, modButtonHeight + modScrollY + count * moffset, colorModButton);
	        		}else if(!hoveringModule) {
	    	        	Gui.drawRect(left + dropDownWidth + 1, modScrollY + count * moffset, left + dropDownWidth + profileWidth - 3, modButtonHeight + modScrollY + count * moffset, 0xFF2F3136);
	    	        }
	            	if (type == CLICK && hoveringModule && button == 0) {
	            		m.toggle();
	            	}

	        		if(m.isEnabled()) {
	        			mc.FontRendererTiny.drawCenteredString("# " + mName.toLowerCase(), modScrollX, modScrollY + 0.5f + count * moffset, Client.color);
	        		}else {
	        			mc.FontRendererTiny.drawCenteredString("# " + mName.toLowerCase(), modScrollX, modScrollY + 0.5f + count * moffset, -1);
	        		}
	        		
	        		if(m.expanded) {
	        			mc.FontRendererTiny.drawCenteredString(">", modScrollX + 34, modScrollY + 0.5f + count * moffset, -1);
	        		}else {
	        			mc.FontRendererTiny.drawCenteredString("...", modScrollX + 34, modScrollY - 1.5f + count * moffset, -1);
	        		}
	            	
                    if (type == CLICK && hoveringModule && button == 1) {
                        String check = m.name;
                        if (type == CLICK && hoveringModule && button == 1) {
                            m.expanded = !m.expanded;
                        }
                    } else if (type == CLICK && !hoveringModule && button == 1) {
                        for(Module module : Client.getModulesByCategory(category)) {
                            if(m.isExpanded()) {
                            	m.setExpanded(false);
                            }
                        }
                    }
                    
	            	if(m.expanded) {
	            		for (Setting setting : m.getSettings()) {
		                    String settingText;
	                    	settingText = setting.name.toLowerCase();
		                    boolean hoveringSetting = isHovered(left + profileWidth + dropDownWidth + 5, top + 19 + scount * 12, left + width - 4, top + 13 + scount * 12 + 18, mouseX, mouseY);
	
		                    if (setting instanceof BooleanSetting) {
		                        BooleanSetting bool = (BooleanSetting) setting;
		                        String title = bool.isEnabled() ? "On" : "Off";
	  
		                        if (type == CLICK && hoveringSetting && button == 0) {
		                            bool.toggle();
		                        }
		                        title = title.toLowerCase();
	
		                        mc.FontRendererSmall.drawStringWithShadow(title, right - 30, top + 18 + scount * 12, -1);
		                    }
		                    if (setting instanceof KeybindSetting) {
                                KeybindSetting bind = (KeybindSetting) setting;
                                String title = Keyboard.getKeyName(bind.getKeyCode());

                                if (type == CLICK && hoveringSetting && button == 0) {
                                    binding = m;
                                }
                                title = title.toLowerCase();
                                
                                mc.FontRendererSmall.drawCenteredString(title, right + 12 - mc.FontRendererNormal.getStringWidth(settingText), top + 19 + scount * 12, -1);

                            }
		                    int sliderWidth = 243;
		                    float sliderLeft = left + 121;
		                    if (setting instanceof NumberSetting) {
                                NumberSetting number = (NumberSetting) setting;
                                float percent = (float) ((number.getValue() - number.getMinimum()) / (number.getMaximum() - number.getMinimum())),
                                        numbersliderWidth = percent * sliderWidth;

                                if (draggingNumber != null && draggingNumber == number) {
                                    double mousePercent = Math.min(1, Math.max(0, (mouseX - sliderLeft) / sliderWidth)), newValue = (mousePercent * (number.getMaximum() - number.getMinimum())) + number.getMinimum();
                                    number.setValue(newValue);
                                }
                                
                                Gui.drawRect(sliderLeft, top + 19.5f + scount * 12, numbersliderWidth + sliderLeft, top + 13.5f + scount * 12 + 17, 0xFFde97bc);
                                String title = String.valueOf(number.getValue());

                                if (type == CLICK && hoveringSetting && button == 0) {
                                    draggingNumber = number;
                                }

                                title = title.toLowerCase();

                                mc.FontRendererSmall.drawCenteredString(title, right - 26, top + 19 + scount * 12, -1);
                            }
                            if (setting instanceof ModeSetting) {
                                ModeSetting mode = (ModeSetting) setting;
                                String title = mode.getMode();

                                if (type == CLICK && hoveringSetting && button == 0) {
                                    mode.cycle();
                                }
                                title = title.toLowerCase();
                                    
                                mc.FontRendererSmall.drawCenteredString(title, right - 30, top + 19 + scount * 12, -1);
                            }

                            mc.FontRendererSmall.drawStringWithShadow(settingText, left + dropDownWidth + profileWidth + 8, top + 18 + scount * 12, -1);
	
		                    scount++;
		                }
	            		
	            	}
	            	

	        		count++;
	        	
	        	}

	 	    }
	        
	        if (type == RELEASE && button == 0) {
	            draggingNumber = null;
	            if (binding != null)
	                binding = null;
	        }
	        
        	if (type == CLICK && hoveringCategory && button == 0) {
                Client.selectedCategory = name;
        	}
        	
	        catCount++;
	        
        }
    	
    	mc.FontRendererTiny.drawString("Vailware", left + 2, top, -1, false);
    	int lastModBackground = -1;
    	
    	GL11.glDisable(GL11.GL_SCISSOR_TEST);
    	
		if(guiX + width > GuiScreen.width) {
			Client.posX = Client.posX - 1;
		}
		if(guiX < 0) {
			Client.posX = Client.posX + 1;
		}
		if(guiY < 0) {
			Client.posY = Client.posY + 1;
		}
		if(guiY + dropDownHeight + height > GuiScreen.height) {
			Client.posY = Client.posY - 1;
		}
    }
    

    @Override
    public void initGui() {
    	width = 370;
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
