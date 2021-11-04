package toniqx.vailware.modules.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.util.MathHelper;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventPacket;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.modules.Module;

public class AntiBot extends Module {
    public ModeSetting mode = new ModeSetting("Mode", "Mineplex", "Mineplex", "Hypixel", "Advanced");

    public BooleanSetting removeinvis = new BooleanSetting("Hypixel Invisible", false);

    public static List<EntityPlayer> mineplexBots = new ArrayList<EntityPlayer>();
    public static ArrayList bots = new ArrayList();
    private static final ArrayList spawnedBots = new ArrayList();

    public AntiBot() {
        super("AntiBot", "", Keyboard.KEY_NONE, Category.COMBAT);
        this.addSettings(mode, removeinvis);
    }

    public void onEvent(Event e) {
        if(e instanceof EventPacket) {
            if(mode.is("Advanced")) {
                if(((EventPacket) e).getPacket() instanceof S0CPacketSpawnPlayer) {
                    S0CPacketSpawnPlayer packet = (S0CPacketSpawnPlayer)((EventPacket) e).getPacket();
                    double posX = packet.getX() / 32D;
                    double posY = packet.getY() / 32D;
                    double posZ = packet.getZ() / 32D;

                    double diffX = mc.thePlayer.posX - posX;
                    double diffY = mc.thePlayer.posY - posY;
                    double diffZ = mc.thePlayer.posZ - posZ;

                    double dist = MathHelper.sqrt_double(diffX * diffX + diffY * diffY + diffZ * diffZ);

                    if (dist <= 17 && posY > mc.thePlayer.posY + 1 && (posX != mc.thePlayer.posX && posY != mc.thePlayer.posY && posZ != mc.thePlayer.posZ)) {
                        e.setCancelled(true);
                    }
                }
            }

            if(mode.is("Hypixel")) {
                if(mc.thePlayer != null && !mc.isSingleplayer()) {
                    if(((EventPacket) e).getPacket() instanceof S18PacketEntityTeleport) {
                        S18PacketEntityTeleport s18PacketEntityTeleport = (S18PacketEntityTeleport) ((EventPacket) e).getPacket();
                        Entity entity = mc.theWorld.getEntityByID(s18PacketEntityTeleport.getEntityId());
                        if(entity != null && entity instanceof EntityPlayer) {
                            if(entity.isInvisible()) {
                                bots.add(entity);
                            }
                        }
                    }
                }
            }
        }

        if (e instanceof EventUpdate && e.isPre()) {

            if(mode.is("TabList")) {
                for (EntityPlayer player : mc.theWorld.playerEntities) {
                    if (player == mc.thePlayer)
                        continue;
                    if (!GuiPlayerTabOverlay.getPlayers().contains(player)) bots.add(player);
                }
            }

            if(mode.is("Hypixel")) {
                for (Entity entity : mc.theWorld.loadedEntityList) {
                    if ((entity instanceof EntityPlayer)) {
                        EntityPlayer player = (EntityPlayer) entity;
                        if ((player.getGameProfile().getName()).startsWith("§c") && !isInTablist(player) && player.isInvisible()) {
                            if (!bots.contains(player)) {
                                bots.add(player);
                            }
                        }
                        if(removeinvis.isEnabled()) {
                            if (((Entity) player).isInvisible() && player != this.mc.thePlayer) {
                                this.mc.theWorld.removeEntity((Entity) player);
                            }
                        }

                        if (player.isInvisible() && !bots.contains(player)) {
                            float xDist = (float) (mc.thePlayer.posX - player.posX);
                            float zDist = (float) (mc.thePlayer.posZ - player.posZ);
                            double horizontalReaach = MathHelper.sqrt_float(xDist * xDist + zDist * zDist);
                            if (horizontalReaach < .6) {
                                double vert = mc.thePlayer.posY - player.posY;
                                if (vert <= 5 && vert > 1) {
                                    if (mc.thePlayer.ticksExisted % 5 == 0) {
                                        bots.add(player);
                                    }
                                }
                            }
                        }
                        if (bots.contains(player) && player.hurtTime > 0 || player.fallDistance > 0) {
                            bots.remove(player);
                        }
                    }
                }
                if (!bots.isEmpty() && mc.thePlayer.ticksExisted % 20 == 0) {
                    for (int i = 0; i < bots.size(); i++) {
                        if (bots.contains(bots.get(i))) {
                            if (!mc.theWorld.playerEntities.contains(bots.get(i))) bots.remove(bots.get(i));
                        }
                    }
                }
            }

            if (mode.is("Mineplex")) {
                if (mc.thePlayer.ticksExisted % 50 == 0) {
                    spawnedBots.clear();
                }

                for (Object o : mc.theWorld.loadedEntityList) {
                    Entity en = (Entity) o;
                    if (en instanceof EntityPlayer && !(en instanceof EntityPlayerSP)) {
                        int ticks = en.ticksExisted;
                        double diffY = Math.abs(mc.thePlayer.posY - en.posY);
                        String name = en.getName();
                        String customname = en.getCustomNameTag();
                        if (customname == "" && !spawnedBots.contains(en))
                            spawnedBots.add(en);
                    }
                }
            }
            spawnedBots.forEach(mineplexBots -> mc.theWorld.removeEntity((Entity) mineplexBots));
        }
    }


    public static boolean isInTablist (EntityLivingBase player){
        if (mc.isSingleplayer()) {
            return true;
        }
        for (Object o : mc.getNetHandler().getPlayerInfoMap()) {
            NetworkPlayerInfo playerInfo = (NetworkPlayerInfo) o;
            if (playerInfo.getGameProfile().getName().equalsIgnoreCase(((EntityPlayer) player).getGameProfile().getName())) {
                return true;
            }
        }
        return false;
    }
}