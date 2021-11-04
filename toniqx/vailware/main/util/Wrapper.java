package toniqx.vailware.main.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.GameSettings;
import toniqx.vailware.font.GlyphPageFontRenderer;

public class Wrapper {
	
    public static boolean authorized;
	
	public static Minecraft getMinecraft() {
		return Minecraft.getMinecraft();
	}

	public static EntityPlayerSP getPlayer() {
		return getMinecraft().thePlayer;
	}

	public static WorldClient getWorld() {
		return getMinecraft().theWorld;
	}

	public static PlayerControllerMP getPlayerController() {
		return getMinecraft().playerController;
	}

	public static GameSettings getGameSettings() {
		return getMinecraft().gameSettings;
	}

    public static EntityRenderer getRenderer() {
        return getMinecraft().entityRenderer;
    }

	public static GlyphPageFontRenderer getCSGOFontRenderer() {
		// TODO Auto-generated method stub
		return null;
	}
}
