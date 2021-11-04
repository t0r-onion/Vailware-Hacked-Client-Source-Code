package toniqx.vailware.main.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import toniqx.vailware.Client;
import toniqx.vailware.main.command.commands.Bind;
import toniqx.vailware.main.command.commands.Config;
import toniqx.vailware.main.command.commands.Info;
import toniqx.vailware.main.command.commands.Name;
import toniqx.vailware.main.command.commands.PlayerInfo;
import toniqx.vailware.main.command.commands.Premium;
import toniqx.vailware.main.command.commands.Radio;
import toniqx.vailware.main.command.commands.Spammer;
import toniqx.vailware.main.command.commands.Toggle;
import toniqx.vailware.main.command.commands.VClip;
import toniqx.vailware.main.command.commands.Watermark;
import toniqx.vailware.main.command.commands.altgen;
import toniqx.vailware.main.event.listeners.EventChat;


public class CommandManager {

	public List<Command> commands = new ArrayList<Command>();
	public String prefix = ".";
	
	public CommandManager() {
		setup();
	}
	
	public void setup() {
		commands.add(new Toggle());
		commands.add(new Bind());
		commands.add(new Premium());
		commands.add(new Info());
		commands.add(new Name());
		commands.add(new Watermark());
		commands.add(new Config());
		commands.add(new PlayerInfo());
		commands.add(new VClip());
		commands.add(new Spammer());
		commands.add(new Radio());
		commands.add(new altgen());
	}
	
	public void handleChat(EventChat event) {
		if(!Client.destruct) {
			String message = event.getMessage();
			
			if(!message.startsWith(prefix))
				return;
			
			event.setCancelled(true);
			
			message = message.substring(prefix.length());
			
			boolean foundCommand = false;
			
			if(message.split(" ").length > 0) {
				String commandName = message.split(" ")[0];
				
				for(Command c : commands) {
					if(c.aliases.contains(commandName) || c.name.equalsIgnoreCase(commandName)) {
						c.onCommand(Arrays.copyOfRange(message.split(" "), 1, message.split(" ").length), message);
						foundCommand = true;
						break;
					}
				}
			}	
		
			if(!foundCommand) {
				Client.addChatMessage("Could not find Command.");
			}
		}
	}
	
}