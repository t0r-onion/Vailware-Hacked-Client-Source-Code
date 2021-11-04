package toniqx.vailware.main.command.commands;

import toniqx.vailware.Client;
import toniqx.vailware.main.command.Command;

public class Info extends Command {

	public Info() {
		super("Info", "Displays client information.", "Info", "i");
	}

	@Override
	public void onCommand(String[] args, String command) {
		Client.addChatMessage("V" + Client.version + "By ToniQX");
	}
	
}
