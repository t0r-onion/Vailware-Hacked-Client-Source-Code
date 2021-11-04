package toniqx.vailware.modules;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.newdawn.slick.Sound;
import org.newdawn.slick.openal.SoundStore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.play.server.S02PacketChat;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.notification.Notification;
import toniqx.vailware.main.notification.impl.Color;
import toniqx.vailware.main.notification.impl.NotificationManager;
import toniqx.vailware.main.notification.impl.Type;
import toniqx.vailware.main.settings.Setting;
import toniqx.vailware.main.settings.impl.KeybindSetting;

public class Module {

	public String name, displayName;
	public String mname, moduleName;
    protected static int categoryCount;
	public boolean toggled;
	public KeybindSetting keyCode = new KeybindSetting(0);
	public Category category;
	public static Minecraft mc = Minecraft.getMinecraft();
	
	public boolean expanded;
	public float offset;
	public int index;
	public List<Setting> settings = new ArrayList<Setting>();
	public int animation;
    public boolean disableOnLagback;
    public boolean requiresDisabler;
    public float translationYaw = 0;
    public float translationPitch = 0;
    public float optionAnim = 0;
    public float optionAnimNow = 0;
    
	public Module(String name, String mname, int key, Category c) {
		this.name = name;
		this.mname = mname;
		keyCode.code = key;
		this.category = c;
		this.addSettings(keyCode);
	}
	
	public void addSettings(Setting... settings) {
		this.settings.addAll(Arrays.asList(settings));
		this.settings.sort(Comparator.comparingInt(s -> s == keyCode ? 1 : 0));
	}
	
	public void removeSettings(Setting... settings) {
		this.settings.removeAll(Arrays.asList(settings));
	}
	
	public boolean isEnabled() {
		if(Client.destruct)
			return false;
		return toggled;
	}
	
	public int getKey() {
		return keyCode.code;
	}
	
	public void onEvent(Event e) {
	
	}
	
	public String getDisplayName() {
		return displayName == null ? name : displayName;
	 
	 }
	 
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	 
	 }
	 
	public void toggle() {
		if(!Client.destruct) {
			toggled = !toggled;
			if(toggled) {
				onEnable();
				//mc.thePlayer.playSound("random.click", 100, 1.2f);
				toggledOnEvent();
				translationYaw = 0;
				translationPitch = 0;
				try {
					if(Client.getModule("Notifications").isEnabled()) {
						NotificationManager.getNotificationManager().createNotification( "Toggle", ChatFormatting.GRAY + "Enabled " + ChatFormatting.YELLOW + name, true, 1800, Type.INFO, Color.GREEN);
					}
				}catch(NullPointerException e) {
					
				}
			}else {
				onDisable();
				//mc.thePlayer.playSound("random.click", 100, 1.5f);
				toggledOffEvent();
				translationYaw = 0;
				translationPitch = 0;
				try {
					if(Client.getModule("Notifications").isEnabled()) {
						NotificationManager.getNotificationManager().createNotification( "Toggle", ChatFormatting.GRAY + "Disabled " + ChatFormatting.YELLOW + name, true, 1800, Type.INFO, Color.GREEN);
					}
				}catch(NullPointerException e) {
					
				}
			}
		}
	}
	
    public void toggleSilent() {
		if(!Client.destruct) {
	        toggled = !toggled;
	        if (toggled) {
	            onEnable();
				toggledOnEvent();
				translationYaw = 0;
				translationPitch = 0;
	        } else {
	            onDisable();
				toggledOffEvent();
				translationYaw = 0;
				translationPitch = 0;
	        }
		}
    }
	
	public void onEnable() {

	}
	
	public void playSound() {
	    mc.thePlayer.playSound("random.click", 100, 0.1f);
	}
	
	public void toggledOnEvent() {
		if(!Client.destruct) {
			
		}
	}
	
	public void toggledOffEvent() {
		if(!Client.destruct) {
		
		}
	}
	
	public void onRender() {
		if(!Client.destruct) {
			
		}
	}
	
	public void onDisable() {
		
	}

    public KeybindSetting getKeyBind() {
        return keyCode;
    }
	
	public void onUpdate() {
	
	}
	
	public void onEventWhenDisabled(Event e) {
		
	}
	
	public String getName() {
		return name;
	}
	
	public Category getCategory() {
		return category;
	}
	
    public List<Setting> getSettings() {
        return settings;
    }
    
    public boolean isExpanded() {
        return expanded;
    }
    
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
    
		
    public enum Category {
        COMBAT("Combat"),
        MOVEMENT("Movement"),
        RENDER("Render"),
        PLAYER("Player"),
        MISC("Misc"),
        EXPLOIT("Exploit"),
    	GHOST("Ghost"),
    	CUSTOMIZE("Customize"),
    	HIDDEN("Hidden");

        public String name;
        public int moduleIndex;
        public int posX, posY;
        public int dPosX, dPosY;
        public float offset;
        public boolean expanded;

        Category(String name) {
            this.name = name;
            posX = 85 + (categoryCount * 100);
            posY = 80;
            offset = 0;
            dPosX = 80;
            dPosY = 80;
            expanded = true;
            categoryCount++;
        }
    }

	public void setup() {}

}