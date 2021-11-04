package toniqx.vailware.main.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;

import toniqx.vailware.Client;
import toniqx.vailware.main.command.Command;
import toniqx.vailware.main.music.radio.RadioManager;
import toniqx.vailware.main.music.radio.gui.RadioGUI;
import toniqx.vailware.modules.player.NameProtect;

public class Radio extends Command {

	public Radio() {
		super("Radio", "Start Stop or Change link of Radio.", "radio", "r");
	}
	
	public static int volume = 10;
	
	@Override
	public void onCommand(String[] args, String command) {
		if (args.length == 2) {
			String commandType = args[0];
			String vol = args[1];
			if(commandType.equalsIgnoreCase("volume")) {
				try {
					volume = Integer.parseInt(vol);
					RadioGUI.radio.setVolume(volume);
					Client.addChatMessage("Volume Set");
				}catch (NumberFormatException e1) {
					Client.addChatMessage("Volume must be a Number");
				}
			}
		}
	}
}