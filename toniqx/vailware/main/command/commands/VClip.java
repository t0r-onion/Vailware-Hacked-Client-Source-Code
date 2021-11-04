package toniqx.vailware.main.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import toniqx.vailware.Client;
import toniqx.vailware.main.command.Command;
import toniqx.vailware.main.files.FileManager;
import toniqx.vailware.modules.player.NameProtect;

public class VClip extends Command {

	public VClip() {
		super("VClip", "VClip downwards", "vclip", "v");
	}

	public Minecraft mc = Minecraft.getMinecraft();
	
	@Override
	public void onCommand(String[] args, String command) {
		if(Client.premium) {
			if(args.length == 2) {
				String commandType = args[0];
				String amount = args[1];
				if (amount.contains("-") || amount.contains(".")) {
					Client.addChatMessage("Cannot use Decimals and Value must not contain negative numbers or letters.");
				}else {
					try {
						int pos = Integer.parseInt(amount);
						if(commandType.equalsIgnoreCase("Down")) {
							mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - pos, mc.thePlayer.posZ);
							Client.addChatMessage("Clipped " + ChatFormatting.YELLOW + pos + ChatFormatting.GRAY + " down.");
						}
						else if(commandType.equalsIgnoreCase("Up")) {
							mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + pos, mc.thePlayer.posZ);
							Client.addChatMessage("Clipped " + ChatFormatting.YELLOW + pos + ChatFormatting.GRAY + " up.");
						}else if(!commandType.equalsIgnoreCase("Up") && !commandType.equalsIgnoreCase("Down")) {
							FileManager.invalidSyntax();
						}
					}catch (NumberFormatException e1) {
						Client.addChatMessage("Amount must be a Number");
					}
				}
			}else {
				FileManager.invalidSyntax();
			}
		}else {
			Client.noPremiumMSG("This", "Command");
		}
	}
}