package toniqx.vailware.main.command.commands;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import toniqx.vailware.Client;
import toniqx.vailware.main.command.Command;

public class PlayerInfo extends Command {

	public PlayerInfo() {
		super("PlayerInfo", "Displays a players information.", "playerinfo", "pi");
	}

	Minecraft mc = Minecraft.getMinecraft();
	
	public void onCommand(String[] args, String command) {
        if(args.length == 1 && args != null) {
            String playerName = args[0];
			
			List<Entity> targets = mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
			targets = targets.parallelStream().filter(entity -> !entity.isDead && ((EntityLivingBase)entity).getHealth() > 0).collect(Collectors.toList());
			
			if(!targets.isEmpty()) {
				Entity target = targets.get(0);
				if(target != null && target instanceof EntityPlayer) {
					target = target.getEntityWorld().getPlayerEntityByName(playerName);
				}
			
				if(target != null && target instanceof EntityPlayer) {
					float health = (((EntityLivingBase) target).getHealth());
					float armor = (((EntityLivingBase) target).getTotalArmorValue());
					float x = (float) ((EntityLivingBase) target).posX;
					float y = (float) ((EntityLivingBase) target).posY;
					float z = (float) ((EntityLivingBase) target).posZ;
					String healthfloatToString = health + "";
					String armorfloatToString = armor + "";
					String xfloatToString = x + "";
					String yfloatToString = y + "";
					String zfloatToString = z + "";
					Client.addChatMessage("Name: " + ChatFormatting.LIGHT_PURPLE + playerName);
					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(""));
					Client.addChatMessage("Health: " + ChatFormatting.YELLOW + healthfloatToString);
					Client.addChatMessage("Armor: " + ChatFormatting.YELLOW + armorfloatToString);
					Client.addChatMessage("X: " + ChatFormatting.YELLOW + xfloatToString);
					Client.addChatMessage("Y: " + ChatFormatting.YELLOW + yfloatToString);
					Client.addChatMessage("Z: " + ChatFormatting.YELLOW + zfloatToString);
				}
			}
        }
    }
}