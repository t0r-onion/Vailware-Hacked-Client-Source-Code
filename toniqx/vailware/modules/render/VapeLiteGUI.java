package toniqx.vailware.modules.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import toniqx.vailware.Client;
import toniqx.vailware.font.GlyphPageFontRenderer;
import toniqx.vailware.main.settings.Setting;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.RenderUtils;
import toniqx.vailware.main.util.impl.TimeHelper;
import toniqx.vailware.main.util.impl.Timer;
import toniqx.vailware.main.util.impl.TimerUtils;
import toniqx.vailware.modules.Module;
import toniqx.vailware.modules.Module.Category;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.Comparator;

public class VapeLiteGUI extends GuiScreen {
    private boolean close = false;
    private boolean closed;

    private float dragX, dragY;
    private boolean drag = false;
    private int valuemodx = 0;
    private static float modsRole, modsRoleNow;
    private static float valueRoleNow, valueRole;

    public float lastPercent;
    public float percent;
    public float percent2;
    public float lastPercent2;
    public float outro;
    public float lastOutro;

    static float windowX = 200, windowY = 200;
    static float width = 500, height = 310;

    static Handle selectType = Handle.Home;
    static Module.Category modCategory = Module.Category.COMBAT;
    static Module selectMod;

    float[] typeXAnim = new float[]{windowX + 10, windowX + 10, windowX + 10, windowX + 10};

    float hy = windowY + 40;
    
    TimerUtils valuetimer = new TimerUtils();

    GlyphPageFontRenderer smallRenderer = mc.FontRendererSmall;

    
    @Override
    public void initGui() {
        super.initGui();
        percent = 1.33f;
        lastPercent = 1f;
        percent2 = 1.33f;
        lastPercent2 = 1f;
        outro = 1;
        lastOutro = 1;
        valuetimer.reset();
    }

    public float smoothTrans(double current, double last){
        return (float) (current + (last - current) / (Minecraft.getDebugFPS() / 10));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        ScaledResolution sResolution = new ScaledResolution(mc);
        ScaledResolution sr = new ScaledResolution(mc);

        
        float valuey = windowY + 100 + valueRoleNow;
        
        Client.modules.sort(Comparator.comparingDouble(m -> {
                    String modulesText = ((Module) m).getName();

                    return smallRenderer.getStringWidth(modulesText);

                })
                        .reversed()
        );

        float outro = smoothTrans(this.outro, lastOutro);
        if (mc.currentScreen == null) {
            GlStateManager.translate(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, 0);
            GlStateManager.scale(outro, outro, 0);
            GlStateManager.translate(-sr.getScaledWidth() / 2, -sr.getScaledHeight() / 2, 0);
        }

        percent = smoothTrans(this.percent, lastPercent);
        percent2 = smoothTrans(this.percent2, lastPercent2);


        if (percent > 0.98) {
            GlStateManager.translate(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, 0);
            GlStateManager.scale(percent, percent, 0);
            GlStateManager.translate(-sr.getScaledWidth() / 2, -sr.getScaledHeight() / 2, 0);
        } else {
            if (percent2 <= 1) {
                GlStateManager.translate(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, 0);
                GlStateManager.scale(percent2, percent2, 0);
                GlStateManager.translate(-sr.getScaledWidth() / 2, -sr.getScaledHeight() / 2, 0);
            }
        }

        if(percent <= 1.5 && close) {
            percent = smoothTrans(this.percent, 2);
            percent2 = smoothTrans(this.percent2, 2);
        }

        if(percent >= 1.4  &&  close){
            percent = 1.5f;
            closed = true;
            mc.currentScreen = null;
        }

        RenderUtils.drawGradientRect(0, 0, sResolution.getScaledWidth(), sResolution.getScaledHeight(), new Color(107, 147, 255, 100).getRGB(), new Color(0, 0, 0, 30).getRGB());

        if (isHovered(windowX, windowY, windowX + width, windowY + 20, mouseX, mouseY) && Mouse.isButtonDown(0)) {
            if (dragX == 0 && dragY == 0) {
                dragX = mouseX - windowX;
                dragY = mouseY - windowY;
            } else {
                windowX = mouseX - dragX;
                windowY = mouseY - dragY;
            }
            drag = true;
        } else if (dragX != 0 || dragY != 0) {
            dragX = 0;
            dragY = 0;
        }
        
        if(ClickGUI.light.enabled) {
        	RenderUtils.drawRect(windowX, windowY, windowX + width, windowY + height, 0xFFffeeed);
        }else {
        	RenderUtils.drawRect(windowX, windowY, windowX + width, windowY + height, new Color(21, 22, 25).getRGB());	
        }
        
        if (selectMod == null) {
        	RenderUtils.drawRoundRect(windowX + 20, hy + smallRenderer.getFontHeight() + 2, windowX + 30, hy + smallRenderer.getFontHeight() + 4, new Color(51, 112, 203).getRGB());
            if (ClickGUI.light.enabled) {
            	mc.FontRendererArray.drawString(Client.username, windowX + 10, windowY + height - 20, 0xFF000000, false);
            }else {
            	mc.FontRendererArray.drawString(Client.username, windowX + 10, windowY + height - 20, -1, false);
            }
        }

        float typeX = windowX + 20;
        int i = 0;

        for (Enum<?> e : Handle.values()) {
            if (!isHovered(windowX, windowY, windowX + width, windowY + 20, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                if (typeXAnim[i] != typeX) {
                    typeXAnim[i] += (typeX - typeXAnim[i]) / 20;
                }
            } else {
                if (typeXAnim[i] != typeX) {
                    typeXAnim[i] = typeX;
                }
            }
            if (e != Handle.Settings) {
                if (e == selectType) {
                	RenderUtils.drawRoundRect(windowX + 20, hy + smallRenderer.getFontHeight() + 2, windowX + 30, hy + smallRenderer.getFontHeight() + 4, new Color(51, 112, 203).getRGB());
                    RenderUtils.drawImage(typeXAnim[i], windowY + 10, 16, 16, new ResourceLocation("vail/" + e.name() + ".png"), new Color(255, 255, 255));
                    smallRenderer.drawString(e.name(), typeXAnim[i] + 20, windowY + 12.5f, new Color(255, 255, 255).getRGB(), false);
                    typeX += (32 + smallRenderer.getStringWidth(e.name() + " "));
                }
            }
            i++;
        }
      
        if (selectType == Handle.Home) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(0, 2 * ((int) (sr.getScaledHeight_double() - (windowY + height))) + 40, (int) (sr.getScaledWidth_double() * 2), (int) ((height) * 2) - 160);
            if (selectMod == null) {
                float cateY = windowY + 65;
                for (Category c : Category.values()) {
                	
                	if(c.name.equalsIgnoreCase("HIDDEN")) {
                		continue;
                	}
                	
                	RenderUtils.drawRoundRect(windowX + 20, hy + smallRenderer.getFontHeight() + 2, windowX + 30, hy + smallRenderer.getFontHeight() + 4, new Color(51, 112, 203).getRGB());
                	
                    if (c.name == modCategory.name) {

                        GL11.glPushMatrix();
                        GlStateManager.color(108, 109, 113);
                        if (ClickGUI.light.enabled) {
                        	smallRenderer.drawString(c.name(), windowX + 20, cateY, 0xFF3370CB, false);
                        }else {
                        	smallRenderer.drawString(c.name(), windowX + 20, cateY, new Color(255, 255, 255).getRGB(), false);
                        }
                        GL11.glPopMatrix();

                        if (isHovered(windowX, windowY, windowX + width, windowY + 20, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                            hy = cateY;
                        } else {
                            if (hy != cateY) {
                                hy += (cateY - hy) / 20;
                            }
                        }
                    } else {
                        GL11.glPushMatrix();
                        GlStateManager.color(255, 255, 255);
                        smallRenderer.drawString(c.name(), windowX + 20, cateY, new Color(108, 109, 113).getRGB(), false);
                        GL11.glPopMatrix();
                    }
                    
                    cateY += 25;
                }
            }

            if (selectMod != null) {
                if (valuemodx > -80) {
                    valuemodx -= 5;
                }
            } else {
                if (valuemodx < 0) {
                    valuemodx += 5;
                }
            }
            
            if (selectMod != null) {
                RenderUtils.drawRoundRect(windowX + 430 + valuemodx, windowY + 60, windowX + width, windowY + height - 20, new Color(32, 31, 35).getRGB());
                RenderUtils.drawRoundRect(windowX + 430 + valuemodx, windowY + 60, windowX + width, windowY + 85, new Color(39, 38, 42).getRGB());
                RenderUtils.drawImage(windowX + 435 + valuemodx, windowY + 65, 16, 16, new ResourceLocation("vail/back.png"), new Color(82, 82, 85));
                if (isHovered(windowX + 435 + valuemodx, windowY + 65, windowX + 435 + valuemodx + 16, windowY + 65 + 16, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                    selectMod = null;
                    valuetimer.reset();
                }


                int dWheel = Mouse.getDWheel();
                if (isHovered(windowX + 430 + (int) valuemodx, windowY + 60, windowX + width, windowY + height - 20, mouseX, mouseY)) {
                    if (dWheel < 0 && Math.abs(valueRole) + 170 < (selectMod.settings.size() * 25)) {
                        valueRole -= 32;
                    }
                    if (dWheel > 0 && valueRole < 0) {
                        valueRole += 32;
                    }
                }

                if (valueRoleNow != valueRole) {
                    valueRoleNow += (valueRole - valueRoleNow) / 20;
                    valueRoleNow = (int) valueRoleNow;
                }

                if(selectMod == null) {
                    return;
                }

                for (Setting setting : selectMod.getSettings()) {
                    String settingText;
                    settingText = setting.name;

                    RenderUtils.drawRoundRect(windowX + 20, hy + smallRenderer.getFontHeight() + 2, windowX + 30, hy + smallRenderer.getFontHeight() + 4, new Color(51, 112, 203).getRGB());
                    mc.FontRendererNormal.drawCenteredString(selectMod.name, windowX + 424, windowY + 66, -1);
                    
                    if (setting instanceof BooleanSetting) {
                        BooleanSetting bool = (BooleanSetting) setting;

                        if (valuey + 4 > windowY + 100) {
                            if (bool.isEnabled()) {
                            	RenderUtils.drawRoundRect(windowX + 20, hy + smallRenderer.getFontHeight() + 2, windowX + 30, hy + smallRenderer.getFontHeight() + 4, new Color(51, 112, 203).getRGB());
                                smallRenderer.drawString(bool.name, windowX + 445 + valuemodx, valuey + 4, -1, false);
                                setting.optionAnim = 100;
                                RenderUtils.drawRoundedRect(windowX + width - 30, valuey + 2, windowX + width - 10, valuey + 12, 4, new Color(33, 94, 181, (int) (setting.optionAnimNow / 100 * 255)).getRGB());
                                RenderUtils.drawCircle(windowX + width - 25 + 10 * (setting.optionAnimNow / 100f), valuey + 7, (float) 3, new Color(255, 255, 255).getRGB());
                            } else {
                            	RenderUtils.drawRoundRect(windowX + 20, hy + smallRenderer.getFontHeight() + 2, windowX + 30, hy + smallRenderer.getFontHeight() + 4, new Color(51, 112, 203).getRGB());
                                smallRenderer.drawString(bool.name, windowX + 445 + valuemodx, valuey + 4, new Color(73, 72, 76).getRGB(), false);
                                setting.optionAnim = 0;
                                RenderUtils.drawRoundedRect(windowX + width - 30, valuey + 2, windowX + width - 10, valuey + 12, 4, new Color(59, 60, 65).getRGB());
                                RenderUtils.drawRoundedRect(windowX + width - 29, valuey + 3, windowX + width - 11, valuey + 11, 3, new Color(32, 31, 35).getRGB());
                                RenderUtils.drawCircle(windowX + width - 25 + 10 * (setting.optionAnimNow / 100f), valuey + 7, (float) 3, new Color(59, 60, 65).getRGB());
                            }
                            if (isHovered(windowX + width - 30, valuey + 2, windowX + width - 10, valuey + 12, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                                if (valuetimer.hasReached(300)) {
                                    bool.toggle();
                                    valuetimer.reset();
                                }
                            }
                        }

                        if (setting.optionAnimNow != setting.optionAnim) {
                            setting.optionAnimNow += (setting.optionAnim - setting.optionAnimNow) / 20;
                        }
                        valuey += 25;
                    }

                    if (setting instanceof ModeSetting) {
                        ModeSetting mode = (ModeSetting) setting;
                        String title = mode.getMode();

                        if (valuey + 4 > windowY + 100 & valuey < (windowY + height)) {
                            RenderUtils.drawRoundedRect(windowX + 445 + valuemodx, valuey + 2, windowX + width - 5, valuey + 22, 2, new Color(46, 45, 48).getRGB());
                            RenderUtils.drawRoundedRect(windowX + 446 + valuemodx, valuey + 3, windowX + width - 6, valuey + 21, 2, new Color(32, 31, 35).getRGB());
                            smallRenderer.drawString(settingText + ":", windowX + 450 + valuemodx, valuey + 6, new Color(230, 230, 230).getRGB(), false);
                            smallRenderer.drawString(mode.getMode(), windowX + 567 - smallRenderer.getStringWidth(mode.getMode()) + valuemodx, valuey + 6, new Color(230, 230, 230).getRGB(), false);
                            
                            if (isHovered(windowX + 445 + valuemodx, valuey + 2, windowX + width - 5, valuey + 22, mouseX, mouseY) && Mouse.isButtonDown(0) && valuetimer.hasReached(300)) {
                                mode.cycle();
                                valuetimer.reset();
                            }
                        }
                        valuey += 25;
                    }

                    if (setting instanceof NumberSetting) {
                        NumberSetting number = (NumberSetting) setting;

                        if (valuey + 4 > windowY + 100) {

                            String title = String.valueOf(number.getValue());

                            float present = (float) (((windowX + width - 11) - (windowX + 450 + valuemodx)) * (number.getValue() - number.getMinimum()) / (number.getMaximum() - number.getMinimum()));

                            RenderUtils.drawRoundRect(windowX + 20, hy + smallRenderer.getFontHeight() + 2, windowX + 30, hy + smallRenderer.getFontHeight() + 4, new Color(51, 112, 203).getRGB());
                            smallRenderer.drawString(settingText, windowX + 445 + valuemodx, valuey + 5, new Color(73, 72, 76).getRGB(), false);
                            smallRenderer.drawCenteredString(title, windowX + width - 20, valuey + 5, new Color(255, 255, 255).getRGB());
                            RenderUtils.drawRect(windowX + 450 + valuemodx, valuey + 20, windowX + width - 11, valuey + 21.5f, new Color(77, 76, 79).getRGB());
                            RenderUtils.drawRect(windowX + 450 + valuemodx, valuey + 20, windowX + 450 + valuemodx + present, valuey + 21.5f, new Color(43, 116, 226).getRGB());
                            RenderUtils.drawCircle(windowX + 450 + valuemodx + present, valuey + 21f, 5, new Color(32, 31, 35).getRGB());
                            RenderUtils.drawCircle(windowX + 450 + valuemodx + present, valuey + 21f, 5, new Color(32, 31, 35).getRGB());
                            RenderUtils.drawCircle(windowX + 450 + valuemodx + present, valuey + 21f, 4, new Color(44, 115, 224).getRGB());
                            RenderUtils.drawCircle(windowX + 450 + valuemodx + present, valuey + 21f, 4, new Color(44, 115, 224).getRGB());

                            if (isHovered(windowX + 450 + valuemodx, valuey + 8, windowX + width - 11, valuey + 23.5f, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                                float render2 = (float) number.getMinimum();
                                double max = number.getMaximum();
                                double inc = 0.01;
                                double valAbs = (double) mouseX - ((double) (windowX + 450 + valuemodx));
                                double perc = valAbs / (((windowX + width - 11) - (windowX + 450 + valuemodx)));
                                perc = Math.min(Math.max(0.0D, perc), 1.0D);
                                double valRel = (max - render2) * perc;
                                double val = render2 + valRel;
                                val = (double) Math.round(val * (1.0D / inc)) / (1.0D / inc);
                                number.setValue(Double.valueOf(val));
                            }
                        }
                        valuey += 25;
                    }
                }
            }

            float modY = windowY + 70 + modsRoleNow;
            
            for (Module m : Client.getModulesByCategory(modCategory)) {
            	if(m.category.name.equalsIgnoreCase("HIDDEN")) {
            		continue;
            	}
                if (isHovered(windowX + 100 + valuemodx, modY - 10, windowX + 425 + valuemodx, modY + 25, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                    if (valuetimer.hasReached(300) && modY + 40 > (windowY + 70) && modY < (windowY + height)) {
                        m.toggle();
                        valuetimer.reset();
                    }
                } else if (isHovered(windowX + 100 + valuemodx, modY - 10, windowX + 425 + valuemodx, modY + 25, mouseX, mouseY) && Mouse.isButtonDown(1)) {
                    if (valuetimer.hasReached(300)) {
                        if (selectMod != m) {
                            valueRole = 0;
                            selectMod = m;
                        } else if (selectMod == m) {
                            selectMod = null;
                        }
                        valuetimer.reset();
                    }
                }

                RenderUtils.drawRoundRect(windowX + 100 + valuemodx, modY - 10, windowX + 125 + valuemodx, modY + 25, new Color(37, 35, 39).getRGB());
                RenderUtils.drawRoundRect(windowX + 410 + valuemodx, modY - 10, windowX + 425 + valuemodx, modY + 25, new Color(39, 38, 42).getRGB());
                smallRenderer.drawString(".", windowX + 416 + valuemodx, modY - 5, new Color(66, 64, 70).getRGB(), false);
                smallRenderer.drawString(".", windowX + 416 + valuemodx, modY - 1, new Color(66, 64, 70).getRGB(), false);
                smallRenderer.drawString(".", windowX + 416 + valuemodx, modY + 3, new Color(66, 64, 70).getRGB(), false);

                if (m.optionAnimNow != m.optionAnim) {
                    m.optionAnimNow += (m.optionAnim - m.optionAnimNow) / 20;
                }

                if (isHovered(windowX + 100 + valuemodx, modY - 10, windowX + 425 + valuemodx, modY + 25, mouseX, mouseY)) {
                    if (ClickGUI.light.enabled) {
	                	if (m.isEnabled()) {
	                        RenderUtils.drawRoundRect(windowX + 128 + valuemodx, modY - 10, windowX + 412 + valuemodx, modY + 25, 0xFFd1cdcd);
	                    } else {
	                        RenderUtils.drawRoundRect(windowX + 128 + valuemodx, modY - 10, windowX + 412 + valuemodx, modY + 25, 0xFFd1cdcd);
	                    }
                    }else {
	                	if (m.isEnabled()) {
	                        RenderUtils.drawRoundRect(windowX + 128 + valuemodx, modY - 10, windowX + 412 + valuemodx, modY + 25, new Color(43, 41, 45).getRGB());
	                    } else {
	                        RenderUtils.drawRoundRect(windowX + 128 + valuemodx, modY - 10, windowX + 412 + valuemodx, modY + 25, new Color(35, 34, 36).getRGB());
	                    }
                    }
                } else {
                    if (ClickGUI.light.enabled) {
	                    if (m.isEnabled()) {
	                        RenderUtils.drawRoundRect(windowX + 128 + valuemodx, modY - 10, windowX + 412 + valuemodx, modY + 25, 0xFFbfbfbf);
	                    } else {
	                        RenderUtils.drawRoundRect(windowX + 128 + valuemodx, modY - 10, windowX + 412 + valuemodx, modY + 25, 0xFFc9c7c7);
	                    }
                    }else {
	                    if (m.isEnabled()) {
	                        RenderUtils.drawRoundRect(windowX + 128 + valuemodx, modY - 10, windowX + 412 + valuemodx, modY + 25, new Color(36, 34, 38).getRGB());
	                    } else {
	                        RenderUtils.drawRoundRect(windowX + 128 + valuemodx, modY - 10, windowX + 412 + valuemodx, modY + 25, new Color(32, 31, 33).getRGB());
	                    }
                    }
                }
                
                if (m.isEnabled()) {
                    if (ClickGUI.light.enabled) {
                    	smallRenderer.drawString(m.getName(), windowX + 135 + valuemodx, modY + 2.5f, 0xFF3370CB, false);
                    }else {
                    	smallRenderer.drawString(m.getName(), windowX + 135 + valuemodx, modY + 2.5f, -1, false);
                    }
                    RenderUtils.drawRoundRect(windowX + 100 + valuemodx, modY - 10, windowX + 125 + valuemodx, modY + 25, new Color(41, 117, 221, (int) (m.optionAnimNow / 100f * 255)).getRGB());
                    RenderUtils.drawImage(windowX + 105 + valuemodx, modY, 16, 16, new ResourceLocation("vail/module.png"), new Color(220, 220, 220));
                    m.optionAnim = 100;

                    RenderUtils.drawRoundedRect(windowX + 380 + valuemodx, modY + 2, windowX + 400 + valuemodx, modY + 12, 4, new Color(33, 94, 181, (int) (m.optionAnimNow / 100f * 255)).getRGB());
                    RenderUtils.drawCircle(windowX + 385 + 10.5f * m.optionAnimNow / 100 + valuemodx, modY + 7, (float) 3, new Color(255, 255, 255).getRGB());
                } else {
                    smallRenderer.drawString(m.getName(), windowX + 135 + valuemodx, modY + 2.5f, new Color(108, 109, 113).getRGB(), false);
                    RenderUtils.drawImage(windowX + 105 + valuemodx, modY, 16, 16, new ResourceLocation("vail/module.png"), new Color(92, 90, 94));
                    m.optionAnim = 0;
                    
                    if(ClickGUI.light.enabled) {
	                    RenderUtils.drawRoundedRect(windowX + 380 + valuemodx, modY + 2, windowX + 400 + valuemodx, modY + 12, 4, 0xFF878080);
	                    RenderUtils.drawRoundedRect(windowX + 381 + valuemodx, modY + 3, windowX + 399 + valuemodx, modY + 11, 3, 0xFF919191);
	                    RenderUtils.drawCircle(windowX + 385 + 10 * m.optionAnimNow / 100 + valuemodx, modY + 7, (float) 3, 0xFFc4c4c4);
                    }else {
                    	RenderUtils.drawRoundedRect(windowX + 380 + valuemodx, modY + 2, windowX + 400 + valuemodx, modY + 12, 4, new Color(59, 60, 65).getRGB());
                        RenderUtils.drawRoundedRect(windowX + 381 + valuemodx, modY + 3, windowX + 399 + valuemodx, modY + 11, 3, new Color(29, 27, 31).getRGB());
                        RenderUtils.drawCircle(windowX + 385 + 10 * m.optionAnimNow / 100 + valuemodx, modY + 7, (float) 3, new Color(59, 60, 65).getRGB());
                    }
                }

                modY += 40;
            }

            int dWheel2 = Mouse.getDWheel();

            if (isHovered(windowX + 100 + valuemodx, windowY + 60, windowX + 425 + valuemodx, windowY + height, mouseX, mouseY)) {
                if (dWheel2 < 0 && Math.abs(modsRole) + 220 < (Client.getModulesByCategory(modCategory).size() * 40)) {
                    modsRole -= 32;
                }
                if (dWheel2 > 0 && modsRole < 0) {
                    modsRole += 32;
                }
            }

            if (modsRoleNow != modsRole) {
                modsRoleNow += (modsRole - modsRoleNow) / 20;
                modsRoleNow = (int) modsRoleNow;
            }


            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }

        int dWheel2 = Mouse.getDWheel();
        
        if (isHovered(windowX + 100 + valuemodx, windowY + 60, windowX + 425 + valuemodx, windowY + height, mouseX, mouseY)) {
            if (dWheel2 < 0 && Math.abs(modsRole) + 220 < (Client.getModulesByCategory(modCategory).size() * 40)) {
                modsRole -= 32;
            }
            if (dWheel2 > 0 && modsRole < 0) {
                modsRole += 32;
            }
        }

        if (modsRoleNow != modsRole) {
            modsRoleNow += (modsRole - modsRoleNow) / 20;
            modsRoleNow = (int) modsRoleNow;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        float typeX = windowX + 20;
        for (Enum<?> e : Handle.values()) {
            if (e != Handle.Settings) {
                if (e == selectType) {
                    if (isHovered(typeX, windowY + 10, (float) (typeX + 16 + smallRenderer.getStringWidth(e.name() + " ")), windowY + 10 + 16, mouseX, mouseY)) {
                        selectType = (Handle) e;
                    }
                    typeX += (32 + smallRenderer.getStringWidth(e.name() + " "));
                }
            }
        }

        if (selectType == Handle.Home) {
            float cateY = windowY + 65;
            for (Module.Category m : Module.Category.values()) {
            	if(m.name.equalsIgnoreCase("HIDDEN")) {
            		continue;
            	}
                if (isHovered(windowX, cateY - 8, windowX + 50, cateY + smallRenderer.getFontHeight() + 8, mouseX, mouseY)) {
                    if (modCategory != m) {
                        modsRole = 0;
                    }

                    modCategory = m;
                    for (Module mod : Client.getModulesByCategory(modCategory)){
                        mod.optionAnim = 0;
                        mod.optionAnimNow = 0;

                    }
                }

                cateY += 25;
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if(!closed && keyCode == Keyboard.KEY_ESCAPE){
            close = true;
            mc.mouseHelper.grabMouseCursor();
            mc.inGameHasFocus = true;
            return;
        }

        if(close) {
            this.mc.displayGuiScreen(null);
        }

        try {
            super.keyTyped(typedChar, keyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    enum Handle {
        Home,
        Settings
    }
}