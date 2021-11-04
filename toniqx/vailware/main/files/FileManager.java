package toniqx.vailware.main.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.lwjgl.input.Keyboard;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import toniqx.AltManager.Alt;
import toniqx.vailware.Client;
import toniqx.vailware.main.notification.impl.Color;
import toniqx.vailware.main.notification.impl.NotificationManager;
import toniqx.vailware.main.notification.impl.Type;
import toniqx.vailware.main.settings.Setting;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.KeybindSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.modules.Module;
import toniqx.vailware.modules.misc.Spammer;
import toniqx.vailware.modules.player.NameProtect;
import toniqx.vailware.ui.hud.ModuleArrayList;

public class FileManager {

	public static String profileDir = Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/profiles/";
	private final File scriptsDir = new File(Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/scripts/");
	public static void startup() {
		
		File dir = new File(Minecraft.getMinecraft().mcDataDir + "/" + Client.name);
		File confdir = new File(Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/profiles/");
		
		if(!dir.exists()){
			dir.mkdir();
		}
		if(!confdir.exists()){
			confdir.mkdir();
		}
		
		File alts = new File(Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/alts.json");
		if(!alts.exists()){
			try {
				alts.createNewFile();
			} catch (IOException e) {
			}
		}
		
	}
	
	public static void configCreate(String configName) {
		File newconfdir = new File(Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/profiles/" + configName);
		if(!newconfdir.exists()){
			newconfdir.mkdir();
		}else {
			Client.addChatMessage("A config named " + ChatFormatting.YELLOW + configName + ChatFormatting.GRAY + " already exists!");
		}
		File configFile = new File(Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/profiles/" + configName + "/settings.ini");
		if(!configFile.exists()) {
			try {
				configFile.createNewFile();
				Client.addChatMessage("Successfully saved Configuration " + ChatFormatting.YELLOW +  configName + ChatFormatting.GRAY + "!");
				configSaveSettings(configName);
			} catch (IOException e) {
				fileError();
			}
		}
	}
	
	public static void configSaveSettings(String configName) {
    	Properties configProperty = new Properties();
    	Properties configModules = new Properties();
    	Properties configVars = new Properties();
    	Properties configVersion = new Properties();
    	for(Module m : Client.modules){
			for(Setting setting : m.settings) { 
				configVersion.setProperty("Version", Client.version);
				configVars.setProperty("Watermark", ModuleArrayList.customWM);
				configVars.setProperty("GenCrackedAltName", Alt.genname);
				configVars.setProperty("SpammerMessage", Spammer.message);
				configVars.setProperty("NameProtectName", NameProtect.name);
				if(m.isEnabled()) {
					try {
						configModules.setProperty(m.name, m.name);
					}catch(NullPointerException e) {
					
					}
				}
				if(setting instanceof ModeSetting) {
					try {
						String value = ((ModeSetting) setting).getMode();
						configProperty.setProperty(m.name + ";" + setting.name, value);
					}catch(NullPointerException e) {
						
					}
				}
				if(setting instanceof NumberSetting) {
					try {
						double i = ((NumberSetting) setting).getValue();
						String value = "" + i;
						configProperty.setProperty(m.name + ";" + setting.name, value);
					}catch(NullPointerException e) {
						
					}
				}
				if(setting instanceof KeybindSetting) {
					try {
						int i = ((KeybindSetting) setting).getKeyCode();
						String value = Keyboard.getKeyName(i);
						configProperty.setProperty(m.name + ";" + setting.name, value);
					}catch(NullPointerException e) {
						
					}catch(IndexOutOfBoundsException e1) {
						
					}
				}
				if(setting instanceof BooleanSetting) {
					try {
						boolean i = ((BooleanSetting) setting).enabled;
						String value = "" + i;
						configProperty.setProperty(m.name + ";" + setting.name, value);
					}catch(NullPointerException e) {
						
					}
				}
				
				try {
					configVersion.store(new FileOutputStream(Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/profiles/" + configName + "/version.ini"), null);
					configModules.store(new FileOutputStream(Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/profiles/" + configName + "/modules.ini"), null);
					configVars.store(new FileOutputStream(Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/profiles/" + configName + "/variables.ini"), null);
					configProperty.store(new FileOutputStream(Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/profiles/" + configName + "/settings.ini"), null);
				} catch (FileNotFoundException e) {
					notFound(configName);
				} catch (IOException e) {
					fileError();
				}
			}
    	}
	}
	
	public static boolean bool = false;
	
	public static void configLoad(String configName) {
		int l = 0;
    	Properties configProperty = new Properties();
    	Properties configVars = new Properties();
    	Properties configModules = new Properties();
    	Properties configVersion = new Properties();
    	try (InputStream moduleInput = new FileInputStream(Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/profiles/" + configName + "/modules.ini")) {
        	for(Module m : Client.modules){
        		configModules.load(moduleInput);
        		String shouldEnableModule = configModules.getProperty(m.name);
        		if(m.name.equalsIgnoreCase(shouldEnableModule)) {
        			if(!m.isEnabled()) {
        				m.toggle();
        			}
        		}else {
        			if(m.isEnabled()) {
        				m.toggle();
        			}
        		}
        	}
    	} catch (FileNotFoundException e1) {
			notFound(configName);
		} catch (IOException e1) {
			fileError();
		}
    	try (InputStream varInput = new FileInputStream(Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/profiles/" + configName + "/variables.ini")) {
    		if(!Client.premium) {
    			NotificationManager.getNotificationManager().createNotification("Error", "Some premium features where not loaded!", true, 1800, Type.WARNING, Color.GREEN);
    		}
        	for(Module m : Client.modules){
        		if(Client.premium) {
	        		configVars.load(varInput);
					ModuleArrayList.customWM = configVars.getProperty("Watermark");
					Alt.genname = configVars.getProperty("GenCrackedAltName");
					Spammer.message = configVars.getProperty("SpammerMessage");
					NameProtect.name = configVars.getProperty("NameProtectName");
        		}
        	}
    	} catch (FileNotFoundException e1) {
    		
		} catch (IOException e1) {
			fileError();
		}
    	try (InputStream versionInput = new FileInputStream(Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/profiles/" + configName + "/version.ini")) {
    		for(Module m : Client.modules){
    			if(l == 0) {
	        		configVersion.load(versionInput);
	        		String vers = configVersion.getProperty("Version");
	        		if(Client.getVersion().equalsIgnoreCase(vers)) {
	        			
	        		}else {
	        			Client.addChatMessage("This configuration is outdated! Made for " + ChatFormatting.YELLOW + "b" + vers);
	        		}
	        		l++;
    			}
        	}
    	} catch (FileNotFoundException e1) {
    		
		} catch (IOException e1) {
			fileError();
		} catch (NullPointerException e3) {
			e3.printStackTrace();
		}
        try (InputStream input = new FileInputStream(Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/profiles/" + configName + "/settings.ini")) {
        	configProperty.load(input);
        	for(Module m : Client.modules){
        		for(Setting setting : m.settings) { 
        			if(setting instanceof ModeSetting && setting != null) {
        				try {
	        				String value = configProperty.getProperty(m.name + ";" + setting.name);
	        				if(value != null) {
	        					((ModeSetting) setting).setMode(value);
	        				}
        				}catch(NullPointerException e){
        					
        				}
        			}
        			if(setting instanceof NumberSetting && setting != null) {
        				try {
	        				String value = configProperty.getProperty(m.name + ";" + setting.name);
	        				if(value != null) {
	        					double i = Double.parseDouble(value);
	        					((NumberSetting) setting).setValue(i);
	        				}
        				}catch(NullPointerException e){
        					
        				}catch(NumberFormatException e) {
        					
        				}
        			}
        			if(setting instanceof BooleanSetting && setting != null) {
        				try {
	        				String value = configProperty.getProperty(m.name + ";" + setting.name);
	        				if(value.equalsIgnoreCase("true") && value != null) {
	        					bool = true;
	        				}else if(value.equalsIgnoreCase("false") && value != null) {
	        					bool = false;
	        				}
	        				((BooleanSetting) setting).setEnabled(bool);
        				}catch(NullPointerException e){
        					
        				}
        			}
        			if(setting instanceof KeybindSetting && setting != null) {
        				try {
	        				String value = configProperty.getProperty(m.name + ";" + setting.name);
	        				if(value != null) {
	        					int keyBindToInt = Keyboard.getKeyIndex(value);
	        					((KeybindSetting) setting).setKeyCode(keyBindToInt);
	        				}
        				}catch(NullPointerException e){
        					
        				}
        			}
				}
			}
			Client.addChatMessage("Successfully loaded Configuration " + ChatFormatting.YELLOW + configName + ChatFormatting.GRAY + "!");
    	} catch (FileNotFoundException e) {
		} catch (IOException e) {
			fileError();
		} catch (NullPointerException e) {
			fileError();
		}
	}
	
	public static void configDelete(String configName) {
        Path modulesFile = Paths.get(Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/profiles/" + configName + "/modules.ini");
        Path varsFile = Paths.get(Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/profiles/" + configName + "/variables.ini");
        Path settingsFile = Paths.get(Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/profiles/" + configName + "/settings.ini");
        Path versionFile = Paths.get(Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/profiles/" + configName + "/version.ini");
		File confDir = new File(Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/profiles/" + configName);
		try {
			Files.delete(modulesFile);
			Files.delete(settingsFile);
			Files.delete(varsFile);
			Files.delete(versionFile);
			if(confDir.exists()){
				confDir.delete();
			}
			Client.addChatMessage("Successfully deleted Configuration " + ChatFormatting.YELLOW + configName + ChatFormatting.GRAY + "!");
		} catch (IOException e) {
			fileError();
		}
	}
	
	public static void configUpdate(String configName) {
        Path modulesFile = Paths.get(Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/profiles/" + configName + "/modules.ini");
        Path varsFile = Paths.get(Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/profiles/" + configName + "/variables.ini");
        Path settingsFile = Paths.get(Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/profiles/" + configName + "/settings.ini");
        Path versionFile = Paths.get(Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/profiles/" + configName + "/version.ini");
		File confDir = new File(Minecraft.getMinecraft().mcDataDir + "/" + Client.name + "/profiles/" + configName);
		try {
			Files.delete(modulesFile);
			Files.delete(settingsFile);
			Files.delete(varsFile);
			Files.delete(versionFile);
			if(confDir.exists()){
				confDir.delete();
			}
			configCreate(configName);
			Client.addChatMessage("Successfully updated Configuration " + ChatFormatting.YELLOW + configName + ChatFormatting.GRAY + "!");
		} catch (IOException e) {
			fileError();
		}
	}
	
	public static void invalidSyntax() {
		Client.addChatMessage("Invalid Syntax!");
	}
	
	public static void notFound(String args) {
		Client.addChatMessage(ChatFormatting.YELLOW + args + ChatFormatting.GRAY + " was not found!");
	}
	
	public static void fileError() {
		Client.addChatMessage("An error has occurred!");
	}
	
	
}