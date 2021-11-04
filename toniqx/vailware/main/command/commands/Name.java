package toniqx.vailware.main.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;

import toniqx.vailware.Client;
import toniqx.vailware.main.command.Command;
import toniqx.vailware.modules.player.NameProtect;

public class Name extends Command {

	public Name() {
		super("Name", "Changes name set in NameProtect", "name", "n");
	}

	@Override
	public void onCommand(String[] args, String command) {
		NameProtect.name = (String.join("", args));
		Client.addChatMessage("Name is now " + ChatFormatting.YELLOW + NameProtect.name);
	}
	
}