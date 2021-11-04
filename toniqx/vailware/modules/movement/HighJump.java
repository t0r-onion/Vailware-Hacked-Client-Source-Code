package toniqx.vailware.modules.movement;

import net.minecraft.util.*;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventReceivePacket;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventPacket;
import toniqx.vailware.main.notification.impl.Color;
import toniqx.vailware.main.notification.impl.NotificationManager;
import toniqx.vailware.main.notification.impl.Type;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.modules.Module;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraft.network.play.server.*;
import net.minecraft.client.entity.*;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.*;

public class HighJump extends Module
{
    public ModeSetting modeSetting;
    public BooleanSetting doCoolDown;
    
    int ticks;
    long lastEnableTime;
    
    public HighJump() {
        super("HighJump", "", Keyboard.KEY_NONE, Category.MOVEMENT);
        this.modeSetting = new ModeSetting("Mode", "Normal", "High", "Normal");
        this.doCoolDown = new BooleanSetting("CoolDown", true);
        this.addSettings(modeSetting);
    }
    
    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            ++this.ticks;
            if (this.modeSetting.is("Normal")) {
                if (this.ticks >= 20) {
                    this.toggle();
                    return;
                }
                if (!this.mc.thePlayer.onGround) {
                    final EntityPlayerSP thePlayer = this.mc.thePlayer;
                    thePlayer.motionY += 0.08;
                    this.mc.timer.timerSpeed = 0.9f;
                    final float var1 = this.mc.thePlayer.rotationYaw * 0.017453292f;
                    final EntityPlayerSP thePlayer2 = this.mc.thePlayer;
                    thePlayer2.motionX -= MathHelper.sin(var1) * 0.069f;
                    final EntityPlayerSP thePlayer3 = this.mc.thePlayer;
                    thePlayer3.motionZ += MathHelper.cos(var1) * 0.069f;
                    return;
                }
                this.ticks = 0;
            }
            if (this.modeSetting.is("High")) {
                this.mc.timer.timerSpeed = 0.2f;
                ++this.ticks;
                this.ticks = 0;
                this.mc.thePlayer.setJumping(true);
                this.mc.thePlayer.fallDistance = 0.0f;
                this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 10.0, this.mc.thePlayer.posZ, false));
                this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 50.0, this.mc.thePlayer.posZ, false));
            }
        }
        if (e instanceof EventPacket && this.modeSetting.is("High")) {
            if (((EventPacket)e).getPacket() instanceof S12PacketEntityVelocity) {
                final S12PacketEntityVelocity packet = (S12PacketEntityVelocity)((EventPacket)e).getPacket();
                if (packet.getMotionY() > 18000 && packet.getEntityID() == this.mc.thePlayer.getEntityId()) {
                    this.mc.thePlayer.performHurtAnimation();
                    this.toggle();
                }
            }
            if (((EventPacket)e).getPacket() instanceof S08PacketPlayerPosLook) {
                final S08PacketPlayerPosLook packet2 = (S08PacketPlayerPosLook)((EventPacket)e).getPacket();
                packet2.yaw = this.mc.thePlayer.rotationYaw;
                packet2.pitch = this.mc.thePlayer.rotationPitch;
            }
        }
    }
    
    @Override
    public void onEnable() {
        this.ticks = 0;
        if (this.modeSetting.is("Normal")) {
            if (Minecraft.getSystemTime() - this.lastEnableTime < 3000L && this.doCoolDown.isEnabled()) {
                NotificationManager.getNotificationManager().createNotification("Anti-Lagback", "Please wait " + (3.0f - Math.round((float)(Minecraft.getSystemTime() - this.lastEnableTime)) / 100.0f / 10.0f) + "seconds! ]", true, Math.round((float)(Minecraft.getSystemTime() - this.lastEnableTime)), Type.INFO, Color.GREEN);
                this.toggled = false;
                return;
            }
            if (!this.mc.thePlayer.onGround) {
                this.toggled = false;
                return;
            }
            this.mc.thePlayer.jump();
        }
    }

	@Override
    public void onDisable() {
        this.mc.timer.timerSpeed = 1.0f;
        this.lastEnableTime = Minecraft.getSystemTime();
    }
}
