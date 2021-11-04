package toniqx.vailware.main.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;

import toniqx.vailware.Client;
import toniqx.vailware.main.command.Command;
import toniqx.vailware.modules.player.NameProtect;

public class Spammer extends Command {

	public Spammer() {
		super("Spammer", "Changes spammer message", "spammer", "s");
	}

	@Override
	public void onCommand(String[] args, String command) {
		String spammermessage = (String.join("", args));
		toniqx.vailware.modules.misc.Spammer.message = spammermessage;
		Client.addChatMessage("Spammer Message is now " + ChatFormatting.YELLOW + toniqx.vailware.modules.misc.Spammer.message);
	}
	
}