package toniqx.vailware.main.bypass;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import toniqx.vailware.main.util.impl.server.PacketUtil;

public class Bypass {

	public static Minecraft mc = Minecraft.getMinecraft();
	
	public static double positionOffset;

	public static double speedMult;

	public static double stageTwoMult;

	public static double startOffset;

	public static double jumpModifier;

	public static double damageOffset;

	public static double damageY;

	public static double damageYTwo;

	public static double yDown;

	public static double yUp;
    
	public static void damage() {
		if (mc.theWorld.getCollidingBoundingBoxes((Entity)mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, 3.0001D, 0.0D).expand(0.0D, 0.0D, 0.0D)).isEmpty()) {
            mc.thePlayer.sendQueue.addToSendQueue((Packet)new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.0001D, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendQueue((Packet)new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendQueue((Packet)new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
        }
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42D, mc.thePlayer.posZ);
    }
	
    public static float getMaxFallDist() {
        PotionEffect potioneffect = mc.thePlayer.getActivePotionEffect(Potion.jump);
        int f = potioneffect != null ? (potioneffect.getAmplifier() + 1) : 0;
        return mc.thePlayer.getMaxFallHeight() + f;
    }
	
}