package toniqx.vailware.modules.misc;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Timer;
import net.minecraft.util.Vec3;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventRender3D;
import toniqx.vailware.main.event.listeners.EventRotationMotion;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventRenderWorld;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.util.impl.MathUtils;
import toniqx.vailware.main.util.impl.RenderUtils;
import toniqx.vailware.main.util.impl.ScaffoldRenderUtils;
import toniqx.vailware.main.util.impl.ScaffoldRotationUtils;
import toniqx.vailware.main.util.impl.server.angle.Angle;
import toniqx.vailware.main.util.impl.server.angle.AngleUtility;
import toniqx.vailware.main.util.impl.server.vector.Vector;
import toniqx.vailware.main.util.impl.server.vector.impl.Vector2;
import toniqx.vailware.main.util.impl.server.vector.impl.Vector3;
import toniqx.vailware.modules.Module;

public class BedBreaker extends Module {

	BlockPos target;
	
	BlockPos target2;

	public BooleanSetting noSwing = new BooleanSetting("No Swing", true);
	public BooleanSetting rotations = new BooleanSetting("Rotations", false);
	public BedBreaker() {
		super("Breaker", "", Keyboard.KEY_NONE, Category.MISC);
		this.addSettings(rotations, noSwing);
	}

	@Override
	public void onEvent(Event e) {
		if(e instanceof EventRenderWorld) {
			if(target != null) {
				Vec3 targetVec = RenderUtils.getRenderPos(target.getX(), target.getY(), target.getZ());
				RenderUtils.drawSolidBlockESP(targetVec.xCoord, targetVec.yCoord, targetVec.zCoord, 1f, 0.5f, 0.5f, 0.5f);
			}
			if(target2 != null) {
				Vec3 targetVec = RenderUtils.getRenderPos(target2.getX(), target2.getY(), target2.getZ());
				RenderUtils.drawSolidBlockESP(targetVec.xCoord, targetVec.yCoord, targetVec.zCoord, 1f, 0.5f, 0.5f, 0.5f);
			}
		}
		if(e instanceof EventRotationMotion) {
			if(e.isPre()) {
				target = null;
				target2 = null;
				int radius = 4;
		
				for (int x = -radius; x < radius; x++) {
					for (int y = radius + 1; y > -radius + 1; y--) {
						for (int z = -radius; z < radius; z++) {
		
							double xBlock = (mc.thePlayer.posX + (double) x);
							double yBlock = (mc.thePlayer.posY + (double) y);
							double zBlock = (mc.thePlayer.posZ + (double) z);
		
							BlockPos blockPos = new BlockPos(xBlock, yBlock, zBlock);
							Block block = mc.theWorld.getBlockState(blockPos).getBlock();
		
							if (!(block instanceof BlockBed)) {
								continue;
							}
							if(target != null) {
								target2 = blockPos;
								return;
							}
							target = blockPos;
							
							EventRotationMotion event = (EventRotationMotion)e;
							
							if (Client.killAura.target == null) {
								float[] rots = ScaffoldRotationUtils.getRotationFromPosition(target.getX(), target.getZ(), target.getY());
								if(rotations.enabled) {
									event.setYaw(rots[0]);
									event.setPitch(rots[1]);
									ScaffoldRenderUtils.setCustomYaw(rots[0]);
									ScaffoldRenderUtils.setCustomPitch(rots[1]);
								}
							}
							
						}
					}
				}
			}else if(e.isPost()) {
				if(target != null) {
					if(!noSwing.enabled) {
						mc.thePlayer.swingItem();
					}
					mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(Action.START_DESTROY_BLOCK, target, EnumFacing.NORTH));
					mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(Action.STOP_DESTROY_BLOCK, target, EnumFacing.NORTH));
				}
			}
		}
	}

}