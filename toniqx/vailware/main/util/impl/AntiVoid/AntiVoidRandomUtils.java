package toniqx.vailware.main.util.impl.AntiVoid;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSnow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import toniqx.vailware.main.util.impl.Data6d;

public class AntiVoidRandomUtils {
	
	public static void damagePlayer(double damage) {
		
		Minecraft mc = Minecraft.getMinecraft();
		
		if (damage > MathHelper.floor_double(mc.thePlayer.getMaxHealth()))
			damage = MathHelper.floor_double(mc.thePlayer.getMaxHealth());

		double offset = 0.0625;
		if (mc.thePlayer != null && mc.getNetHandler() != null && mc.thePlayer.onGround) {
			for (int i = 0; i <= ((3 + damage) / offset); i++) {
				mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
						mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
				mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
						mc.thePlayer.posY, mc.thePlayer.posZ, (i == ((3 + damage) / offset))));
			}
		}
		
	}
	
    public static boolean isPosSolid(BlockPos pos) {
        Block block = Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock();
        if ((block.getMaterial().isSolid() || !block.isTranslucent() || block instanceof BlockLadder || block instanceof BlockCarpet
                || block instanceof BlockSnow || block instanceof BlockSkull)
                && !block.getMaterial().isLiquid() && !(block instanceof BlockContainer)) {
            return true;
        }
        return false;
    }
    
	public static String getTeamName(int num, Scoreboard board) {
		ScoreObjective objective = board.getObjectiveInDisplaySlot(1);
        Collection collection = board.getSortedScores(objective);
        ArrayList arraylist = Lists.newArrayList(Iterables.filter(collection, new Predicate()
        {
            public static final String __OBFID = "CL_00001958";
            public boolean apply(Score p_apply_1_)
            {
                return p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#");
            }
            public boolean apply(Object p_apply_1_)
            {
                return this.apply((Score)p_apply_1_);
            }
        }));
        
        try {
			Score score = (Score) arraylist.get(num);
			ScorePlayerTeam scoreplayerteam = board.getPlayersTeam((score).getPlayerName());
			//String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, ((Score) score).getPlayerName()) + ": " + EnumChatFormatting.RED + ((Score) score).getScorePoints();
			String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, ((Score) score).getPlayerName());
			return s;
		} catch (Exception e) {
			//e.printStackTrace();
		}
        
        return "ERROR";
        
	}
	
	public static String getFormattedDate() {
		
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

		String formattedDate = formatter.format(LocalDate.now());
		return formattedDate;
		
	}
	
	public static String getFormattedDateAndTime(long millis) {
		
		String dateAndTime = "";
		Timestamp time = new Timestamp(millis);
		dateAndTime += time.getMonth() + 1;
		dateAndTime += "/" + time.getDate();
		dateAndTime += "/" + (time.getYear() + 1900);
		dateAndTime += " " + (time.getHours());
		dateAndTime += ":" + (time.getMinutes() + 1 <= 9 ? "0" + (time.getMinutes() + 1) : (time.getMinutes() + 1));
		return dateAndTime;
		
	}
	
	// Made by lavaflowglow 4/5/2021 6:58 PM
	public static void connectToServer(String ip, int port, Minecraft mc) {
		ServerData connect = new ServerData("temp", ip + ":" + port, false);
		mc.displayGuiScreen(new GuiConnecting(mc.currentScreen, mc, connect));
	}
	// Made by lavaflowglow 4/5/2021 6:58 PM
	
	// Made by lavaflowglow 4/5/2021 1:30 PM
	public static void setPosAndMotionWithData6d(Data6d posAndMotion) {
		
		Minecraft.getMinecraft().thePlayer.setPosition(posAndMotion.x, posAndMotion.y, posAndMotion.z);
		Minecraft.getMinecraft().thePlayer.motionX = posAndMotion.motionX;
		Minecraft.getMinecraft().thePlayer.motionY = posAndMotion.motionY;
		Minecraft.getMinecraft().thePlayer.motionZ = posAndMotion.motionZ;
		
	}
	// Made by lavaflowglow 4/5/2021 1:30 PM
	
}
