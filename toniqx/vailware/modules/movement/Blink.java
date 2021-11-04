package toniqx.vailware.modules.movement;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.Vec3;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.process.EventPacket;
import toniqx.vailware.main.util.impl.PlayerUtils;
import toniqx.vailware.main.util.impl.Timer;
import toniqx.vailware.modules.Module;
import toniqx.vailware.modules.Module.Category;

public class Blink extends Module {

	private ArrayList<Packet> packetList = new ArrayList<>();

    public Blink() {
        super("Blink", "", Keyboard.KEY_NONE, Category.PLAYER);
    }

    public void onDisable() {
        super.onDisable();
        try {
            for (Packet packets : packetList) {
                mc.getNetHandler().sendPacketNoEvent(packets);
            }
            packetList.clear();
        }
        catch (final ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }

    public void onEvent(Event e) {
        if(e instanceof EventPacket) {
            if(((EventPacket) e).getPacket() instanceof C03PacketPlayer || ((EventPacket) e).getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition || ((EventPacket) e).getPacket() instanceof C00PacketKeepAlive || ((EventPacket) e).getPacket() instanceof C0BPacketEntityAction || ((EventPacket) e).getPacket() instanceof C08PacketPlayerBlockPlacement) {
                e.setCancelled(true);
                packetList.add(((EventPacket) e).getPacket());
            }
        }
    }
}