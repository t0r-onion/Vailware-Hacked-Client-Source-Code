package toniqx.vailware.modules.misc;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventReceivePacket;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventPacket;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.util.impl.server.PacketUtil;
import toniqx.vailware.modules.Module;
import toniqx.vailware.modules.Module.Category;

public class AutoRegister extends Module {

	public ModeSetting mode = new ModeSetting("Mode", "RedeDark", "RedeDark");
	
	public AutoRegister(){
		super("AutoRegister", "", Keyboard.KEY_NONE, Category.HIDDEN);
		this.addSettings(mode);
		toggled = false;
	}
	
	public static String pass = "vailware123";
	
	public void onEvent(Event e) {
		if(Client.getModule("AutoRegister").isEnabled()) {
			Client.getModule("AutoRegister").toggleSilent();
		}
	}
	
	public void onDisable() {
	}
}