package toniqx.vailware.main.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;

import toniqx.vailware.Client;
import toniqx.vailware.main.command.Command;
import toniqx.vailware.modules.Module;
import toniqx.vailware.modules.player.NameProtect;
import toniqx.vailware.ui.hud.ModuleArrayList;
import toniqx.vailware.ui.hud.TabGUI;

public class Watermark extends Command {

	public Watermark() {
		super("Watermark", "Changes the client Watermark.", "watermark", "n");
	}

	@Override
	public void onCommand(String[] args, String command) {
		if(Client.premium) {
			if (args[0].equalsIgnoreCase("reset")) {
	            ModuleArrayList.customWM = "Vailware";
	        } else {
	        	ModuleArrayList.customWM = String.join(" ", args);
	        	ModuleArrayList.customWM = ModuleArrayList.customWM.replace("&", "\247");
	        	ModuleArrayList.customWM = ModuleArrayList.customWM.replace("\247", "\247");
	        }
		}else {
			Client.noPremiumMSG("This", "Command");
		}
	}
	
}
