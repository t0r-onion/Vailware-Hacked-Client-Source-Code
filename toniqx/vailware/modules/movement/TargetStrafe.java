package toniqx.vailware.modules.movement;

import java.awt.Color;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventRender3D;
import toniqx.vailware.main.event.process.EventMove;
import toniqx.vailware.main.event.process.EventRenderWorld;
import toniqx.vailware.main.event.process.TargetStrafeEventMove;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.ScaffoldMovementUtils;
import toniqx.vailware.main.util.impl.ScaffoldRenderUtils;
import toniqx.vailware.main.util.impl.ScaffoldRotationUtils;
import toniqx.vailware.main.util.impl.TimerUtils;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.main.util.impl.movement.TargetMovementUtils;
import toniqx.vailware.main.util.impl.render.TargetStrafeUtils;
import toniqx.vailware.main.util.impl.render.TargetStrafeUtils.*;
import toniqx.vailware.main.util.impl.server.EntityValidator;
import toniqx.vailware.main.util.impl.server.RotationUtils;
import toniqx.vailware.main.util.impl.server.TargetRotationUtils;
import toniqx.vailware.modules.Module;
import toniqx.vailware.modules.combat.KillAura;


public class TargetStrafe extends Module {

	public TimerUtils timer = new TimerUtils();
	
	public TargetStrafe() {
		super("TargetStrafe", "", Keyboard.KEY_NONE, Category.MOVEMENT);
		this.addSettings(speed, distance);
	}
	
	public static NumberSetting speed = new NumberSetting("Speed", 1, 1, 2, 0.01);
	public static NumberSetting distance = new NumberSetting("Distance", 2, 0.4, 3, 0.1);
	
	public static transient boolean direction = false, forward = false, left = false, right = false, back = false;
	
	@Override
	public void onEnable() {
		
		forward = mc.gameSettings.keyBindForward.pressed;
		left = mc.gameSettings.keyBindLeft.pressed;
		right = mc.gameSettings.keyBindRight.pressed;
		back = mc.gameSettings.keyBindBack.pressed;
		super.onEnable();
	}
	
	boolean up;
	boolean down;
	
	@Override
	public void onEvent(Event e) {
		if(mc.gameSettings.keyBindLeft.pressed) {
			direction = false;
		}
		if(mc.gameSettings.keyBindRight.pressed) {
			direction = true;
		}
		
		if(!MovementUtil.isBlockUnder()) {
			direction = !direction;
		}
		
		if (mc.thePlayer.isCollidedHorizontally) {
			direction = !direction;
		}
		if(Client.killAura.target != null) {
	        if (e instanceof EventRenderWorld) {
	            if (canStrafe()) {
	                drawCircle(Client.killAura.target, mc.timer.elapsedPartialTicks, 0.6);
	            }
	        }
	        
	        float max = Client.killAura.target.height;
	        float min = 0.1f;
	        
			if(timer.hasReached(1)) {
				if(offset != max && offset < max && !down) {
					up = true;
				}else {
					up = false;
				}
				
				if(offset != min && offset > min && !up) {
					down = true;
				}else {
					down = false;
				}
				
				if(up) {
					offset = offset + 0.002f;
				}
				
				if(down) {
					offset = offset - 0.002f;
				}
				timer.reset();
			}
			
			
			if (e instanceof EventMove && e.isPre()) {
				
				EventMove event = (EventMove)e;
				KillAura k = Client.killAura;
				
				if (mc.thePlayer.isCollidedHorizontally) {
					direction = !direction;
				}
				
				if (k.target == null || !k.isEnabled()) {
					return;
				}else {
					
					double currentSpeed = MovementUtil.getSpeed();
					
					double yawChange = 45;
					
					float f = (float) ((ScaffoldRotationUtils.getRotations(k.target)[0] + (direction ? -yawChange : yawChange)) * 0.017453292F);
					double x2 = k.target.posX, z2 = k.target.posZ;
		            x2 -= (double)(MathHelper.sin(f) * (distance.getValue()) * -1);
		            z2 += (double)(MathHelper.cos(f) * (distance.getValue()) * -1);
		            
		            float currentSpeed1 = ScaffoldMovementUtils.getSpeed();
		            
					double backupMotX = mc.thePlayer.motionX, backupMotZ = mc.thePlayer.motionZ;
		            event.setSpeed(((currentSpeed + speed.getValue() - 1) / 100) * 90, ScaffoldRotationUtils.getRotationFromPosition(x2, z2, mc.thePlayer.posY)[0]);
		            mc.thePlayer.motionX = backupMotX;
		            mc.thePlayer.motionZ = backupMotZ;
		            
		            
		            if (currentSpeed > MovementUtil.getSpeed()) {
		            	direction = !direction;
		            }
				}
				
			}
			
		}
	}
	
	float offset;
	
	private void drawCircle(Entity entity, float partialTicks, double rad) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		TargetStrafeUtils.startSmooth();
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glLineWidth(4.2f);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		
		final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX;
		final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY + offset;
		final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ;
	 	      
		final float r = ((float) 1 / 255) * Color.WHITE.getRed();
		final float g = ((float) 1 / 255) * Color.WHITE.getGreen();
		final float b = ((float) 1 / 255) * Color.WHITE.getBlue();

		final double pix2 = Math.PI * 2.0D;
	
		for (int i = 0; i <= 90; ++i) {
			GL11.glColor3f(r, g, b);
			GL11.glVertex3d(x + rad * Math.cos(i * pix2 / 45.0), y, z + rad * Math.sin(i * pix2 / 45.0));
		}

		GL11.glEnd();
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		TargetStrafeUtils.endSmooth();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}
	
    public boolean canStrafe() {
		KillAura k = Client.killAura;
        return (k.isEnabled() && k.target != null);
    }
	
}

