package toniqx.vailware.modules.player;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventMove;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.ScaffoldMovementUtils;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.modules.Module;

public class NoSlow extends Module {
	
	public static ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "AAC4", "NCP", "Hypixel");
	
	public NoSlow(){
		super("NoSlow", "", Keyboard.KEY_NONE, Category.PLAYER);
		this.addSettings(mode);
	}
	
	public void onEvent(Event e) {
		if(mode.is("Hypixel")) {
			super.mname = ChatFormatting.GRAY + "Hypixel" + "   ";
			if (e instanceof EventUpdate && e.isPre()) {
					
				if (mc.thePlayer.isBlocking() && ScaffoldMovementUtils.isMoving() && ScaffoldMovementUtils.isOnGround(0.42D) && Client.killAura.target == null) {
					if (e.isPre()) {
						mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, new BlockPos(RandomUtils.nextDouble(4.9E-324D, 1.7976931348623157E308D), RandomUtils.nextDouble(4.9E-324D, 1.7976931348623157E308D), RandomUtils.nextDouble(4.9E-324D, 1.7976931348623157E308D)), EnumFacing.DOWN));
					} else {
						mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0.0F, 0.0F, 0.0F));
					}
				}
					
			}
		}
		else if (mode.is("Vanilla")) {
			super.mname = ChatFormatting.GRAY + "Vanilla" + "   ";
		}
		else if (mode.is("NCP")) {
			super.mname = ChatFormatting.GRAY + "NCP" + "   ";
			if (e instanceof EventUpdate && e.isPre()) {
				if (mc.thePlayer.isBlocking() && MovementUtil.isMoving() && MovementUtil.isOnGround(0.42D) && !(Client.killAura.target != null)) {
					if (e.isPre()) {
						mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, new BlockPos(RandomUtils.nextDouble(4.9E-324D, 1.7976931348623157E308D), RandomUtils.nextDouble(4.9E-324D, 1.7976931348623157E308D), RandomUtils.nextDouble(4.9E-324D, 1.7976931348623157E308D)), EnumFacing.DOWN));
					} else if (e.isPost() || e.isCancelled()) {
						mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0.0F, 0.0F, 0.0F));
					}
				}
					
			}
		}
		else if (mode.is("AAC4")) {
			super.mname = ChatFormatting.GRAY + "AAC4" + "   ";
			if (e instanceof EventUpdate) {
				if (mc.thePlayer.getItemInUseDuration() > 0) {
					if (mc.thePlayer.onGround) {
						mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
						mc.timer.timerSpeed = 1.06f;
						double mult = mc.thePlayer.isMoving() ? 1.3 : 1.45;
						mc.thePlayer.motionX *= mult;
						mc.thePlayer.motionZ *= mult;
						mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
					}
					else {
						mc.timer.timerSpeed = 1.02f;
					}
				}
				else mc.timer.timerSpeed = 1;
			}
		}
	}
	
}