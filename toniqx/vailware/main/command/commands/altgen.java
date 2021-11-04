package toniqx.vailware.main.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;

import toniqx.AltManager.Alt;
import toniqx.vailware.Client;
import toniqx.vailware.main.command.Command;
import toniqx.vailware.modules.player.NameProtect;

public class altgen extends Command {

	public altgen() {
		super("Altgen", "Changes alt name thats generated", "altgen", "ag");
	}

	@Override
	public void onCommand(String[] args, String command) {
		if(Client.premium) {
			Alt.genname = (String.join("", args));
			Client.addChatMessage("Account Name will now be " + ChatFormatting.YELLOW + Alt.genname);
		}else {
			Client.noPremiumMSG("This", "Command");
		}
	}
	
}