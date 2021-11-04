package toniqx.vailware.ui.hud;

import java.awt.Color;
import java.awt.Font;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import toniqx.vailware.Client;
import toniqx.vailware.main.command.commands.PlayerInfo;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventKey;
import toniqx.vailware.main.event.listeners.EventRenderGUI;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.settings.Setting;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.KeybindSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.ColorManager;
import toniqx.vailware.main.util.impl.ColorUtil;
import toniqx.vailware.main.util.impl.render.RenderUtil;
import toniqx.vailware.modules.Module;
import toniqx.vailware.ui.clickui.ClickHUD;
import toniqx.vailware.ui.clickui.lib.elements.Draw;

public class TabGUI extends Module {

	public int currentTab;
	public boolean expanded;
    public int customColor;
	
	public static ModeSetting watermarkstyle = new ModeSetting("Style", "Default", "Default", "Simple", "CSGO", "Jello", "Extra");
	public static BooleanSetting watermark = new BooleanSetting("Watermark", true);
	public static BooleanSetting isTabGui = new BooleanSetting("TabGUI", true);
	public static BooleanSetting isArrayList = new BooleanSetting("ArrayList", true);
	
	public TabGUI(){
		super("HUD", "", Keyboard.KEY_NONE, Category.CUSTOMIZE);
		this.addSettings(watermarkstyle, isTabGui, isArrayList, watermark);
		toggled = !toggled;
	}
	
	public static int hoverColor = 0x50AAAAAA;
	public static boolean hover = false;
	public static boolean hover2 = false;
	public static boolean hover3 = false;
	
	public void onEvent(Event e) {
		
		if(e instanceof EventRenderGUI) {
			if(isTabGui.isEnabled()) {
				FontRenderer fr = mc.fontRendererObj;
				
				int primaryColor = ColorUtil.getRainbow(4, 0.8f, 1), secondaryColor = ColorUtil.getRainbow(4, 0.8f, 0.6f);

	            GlStateManager.color(0, 0, 0, 0.72f);
				Draw.drawRoundedRect(4, 49.5f, 75, 30 + Module.Category.values().length * 15 - 43.5f, 3f);
				int count = 0;
				hoverColor = 0x40000000;
				for(Category c : Module.Category.values()) { 
					if(c.name.equalsIgnoreCase("HIDDEN")) {
						continue;
					}
	                boolean selected = count == currentTab;
					if(selected) {
						hover = true;
					}else {
						hover = false;
					}
					if(hover) {
						RenderUtil.glColor(ColorUtil.getRainbow(6, 0.50f, 1, (long) (ClickHUD.leftCount * 3)));
						Draw.drawRoundedRect(5, 52 + currentTab * 15, 2, 12 + currentTab / 16, 1);
					}
					mc.FontRendererArray.drawStringWithShadow(c.name, 8 + (selected ? 9f : -1f), 51.0f + count * 15, (selected ? Color.white.getRGB() : 0xFFebebeb));
					count++;
				}
			
				if(expanded) {
					Category category = (Module.Category.values()[currentTab]);
					List<Module> modules = Client.getModulesByCategory(category);
					
					if(modules.size() == 0)
						return; 
					
		            GlStateManager.color(0, 0, 0, 0.72f);
					Draw.drawRoundedRect(80.5f, 50, 72, 30 + modules.size() * 14 - 28.5f, 3f);
					count = 0;
					for(Module m : modules) {
						
		                boolean selected = count == category.moduleIndex;
						if(selected) {
							hover2 = true;
						}else {
							hover2 = false;
						}
						if(hover2) {
							RenderUtil.glColor(ColorUtil.getRainbow(6, 0.50f, 1, (long) (ClickHUD.leftCount * 3)));
							Draw.drawRoundedRect(81.5f, 52 + category.moduleIndex * 14, 2, 12 + category.moduleIndex / 16, 1);
						}
						if(!m.isEnabled()) {
							mc.FontRendererClean.drawStringWithShadow(m.name, 84 + (selected ? 9f : -1f), 51.0f + count * 14, (selected ? Color.white.getRGB() : 0xFFebebeb));
						}else {
							mc.FontRendererClean.drawStringWithShadow(m.name, 84 + (selected ? 9f : -1f), 51.0f + count * 14, ColorUtil.getRainbow(6, 0.50f, 1, (long) (ClickHUD.leftCount * 3)));
						}
						
						count++;
					}
					
					
				}
			}
		}
		if(e instanceof EventKey) {
			int code = ((EventKey)e).code;
			
			Category category = (Module.Category.values()[currentTab]);
			List<Module> modules = Client.getModulesByCategory(category);
			
			if(expanded && isTabGui.enabled && !modules.isEmpty() && modules.get(category.moduleIndex).expanded) {
				Module module = modules.get(category.moduleIndex);
				
				if(!module.settings.isEmpty() && module.settings.get(module.index).focused && module.settings.get(module.index) instanceof KeybindSetting) {
					if(code != Keyboard.KEY_RETURN && code != Keyboard.KEY_UP && code != Keyboard.KEY_DOWN && code != Keyboard.KEY_RIGHT && code != Keyboard.KEY_LEFT) {
						KeybindSetting keyBind = (KeybindSetting)module.settings.get(module.index);
						
						keyBind.code = code;
						keyBind.focused = false;
						
						return;
					}
				}
			}
				
			if(code == Keyboard.KEY_UP) {
				if(expanded && isTabGui.enabled) {
					if(expanded && !modules.isEmpty() && modules.get(category.moduleIndex).expanded) {
						Module module = modules.get(category.moduleIndex);
						
						if(module.settings.get(module.index).focused) {
							Setting setting = module.settings.get(module.index);
							
							if(setting instanceof ModeSetting) {
								((ModeSetting)setting).cycle();
							}
							if(setting instanceof BooleanSetting) {
								((BooleanSetting)setting).toggle();
							}
							if(setting instanceof NumberSetting) {
								((NumberSetting)setting).increment(true);
							}
							
						}else {
							if(module.index <= 0) {
								module.index = module.settings.size() - 1;
							}else
								module.index--;
						}
					}else {
						if(category.moduleIndex <= 0) {
							category.moduleIndex = modules.size() - 1;
						}else
							category.moduleIndex--;
					}
				}else {
					if(currentTab <= 0) {
						currentTab = Module.Category.values().length - 2;
					}else
						currentTab--;
				}
			}
				
			if(code == Keyboard.KEY_DOWN) {
				if(expanded && isTabGui.enabled) {
					if(expanded && !modules.isEmpty() && modules.get(category.moduleIndex).expanded) {
						Module module = modules.get(category.moduleIndex);
						
						if(module.settings.get(module.index).focused) {
							Setting setting = module.settings.get(module.index);
							
							if(setting instanceof ModeSetting) {
								((ModeSetting)setting).cycle();
							}
							if(setting instanceof BooleanSetting) {
								((BooleanSetting)setting).toggle();
							}
							if(setting instanceof NumberSetting) {
								((NumberSetting)setting).increment(false);
							}
						}else {
							if(module.index >= module.settings.size() - 1) {
								module.index = 0;
							}else
								module.index++;
						}
					}else {
						if(category.moduleIndex >= modules.size() - 1) {
							category.moduleIndex = 0;
						}else
							category.moduleIndex++;
						}
					}else {
						if(currentTab >= Module.Category.values().length - 2) {
							currentTab = 0;
						}else
							currentTab++;
					}
				}
			
			if(code == Keyboard.KEY_RETURN) {
				if(expanded && isTabGui.enabled && modules.size() != 0) {
					Module module = modules.get(category.moduleIndex);

					if(expanded && !modules.isEmpty() && modules.get(category.moduleIndex).expanded) {
					}else
						module.toggle();
				}
			}
			
			if(code == Keyboard.KEY_RIGHT) {
				if(expanded && isTabGui.enabled && modules.size() != 0) {
					Module module = modules.get(category.moduleIndex);
					if(!module.expanded) {
						
					}
					else if(module.expanded)
							module.settings.get(module.index).focused = !module.settings.get(module.index).focused;
				}else
					expanded = true;

			}
			
			if(code == Keyboard.KEY_LEFT) {
				if(expanded && isTabGui.enabled && !modules.isEmpty() && modules.get(category.moduleIndex).expanded) {
					Module module = modules.get(category.moduleIndex);
					
					if(module.settings.get(module.index).focused) {
						module.settings.get(module.index).focused = false;
						
				}else {
					modules.get(category.moduleIndex).expanded = false;
				}
			}else
				expanded = false;
			}
		}
	}
	
	public void onDisable() {
		super.toggle();
	}
	
}