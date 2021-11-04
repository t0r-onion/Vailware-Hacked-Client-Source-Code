package toniqx.vailware.main.command.commands;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import toniqx.vailware.Client;
import toniqx.vailware.main.command.Command;
import toniqx.vailware.main.files.FileManager;
import toniqx.vailware.modules.Module;
import toniqx.vailware.modules.player.NameProtect;

public class Config extends Command {

	public Config() {
		super("Config", "Save & Load Configurations", "config", "cfg");
	}

	@Override
	public void onCommand(String[] args, String command) {
		if(args.length == 2 && args != null) {
			String configCommand = args[0];
			String configName = args[1];
			if(configCommand.equalsIgnoreCase("save")) {
				FileManager.configCreate(configName);
			}
			else if(configCommand.equalsIgnoreCase("load")) {
				FileManager.configLoad(configName);
			}
			else if(configCommand.equalsIgnoreCase("delete")) {
				FileManager.configDelete(configName);
			}
			else if(!(configCommand.equalsIgnoreCase("save") || configCommand.equalsIgnoreCase("delete") || configCommand.equalsIgnoreCase("load"))){
				FileManager.invalidSyntax();
			}
		}else {
			FileManager.invalidSyntax();
		}
	}
}
