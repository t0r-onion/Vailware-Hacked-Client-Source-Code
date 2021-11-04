package toniqx.vailware.main.util.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class ScaffoldRotationUtils {
	
	public static float updateRotation(float current, float intended, float factor) {
		float var4 = MathHelper.wrapAngleTo180_float(intended - current);

		if (var4 > factor) {
			var4 = factor;
		}

		if (var4 < -factor) {
			var4 = -factor;
		}

		return current + var4;
	}
	
    public static float getYawChange(double posX, double posZ, float playerYaw, Double playerX, Double playerZ) {
        double deltaX = posX - playerX;
        double deltaZ = posZ - playerZ;
        double yawToEntity;
        if ((deltaZ < 0.0D) && (deltaX < 0.0D)) {
            yawToEntity = 90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else if ((deltaZ < 0.0D) && (deltaX > 0.0D)) {
            yawToEntity = -90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else {
            yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
        }
        return MathHelper.wrapAngleTo180_float(-(playerYaw - (float) yawToEntity));
    }

    public static float getPitchChange(Entity entity, double posY, float playerPitch, Double playerX, Double playerZ) {
        double deltaX = entity.posX - playerX;
        double deltaZ = entity.posZ - playerZ;
        double deltaY = posY - 2.2D + entity.getEyeHeight() - Minecraft.getMinecraft().thePlayer.posY;
        double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
        return -MathHelper.wrapAngleTo180_float(playerPitch - (float) pitchToEntity) - 2.5F;
    }
    
    public static float[] getRotationFromPosition(double x, double z, double y) {
        double xDiff = x - Minecraft.getMinecraft().thePlayer.posX;
        double zDiff = z - Minecraft.getMinecraft().thePlayer.posZ;
        double yDiff = y - Minecraft.getMinecraft().thePlayer.posY - 1.2;

        double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / 3.141592653589793D);
        return new float[]{yaw, pitch};
    }
    
    public static float[] getRotations(Entity ent) {
        double x = ent.posX;
        double z = ent.posZ;
        double y = ent.posY + (ent.getEyeHeight() / 2.0F);
        return getRotationFromPosition(x, z, y);
    }
    
    public static float[] getRotations(EntityLivingBase entityIn, float speed) {
        float yaw = updateRotation(Minecraft.getMinecraft().thePlayer.rotationYaw,
                getRotations(entityIn)[0],
                speed);
        float pitch = updateRotation(Minecraft.getMinecraft().thePlayer.rotationPitch,
                getRotations(entityIn)[1],
                speed);
        return new float[]{yaw, pitch};
    }
    
    public static Vec3 getVectorForRotation(float pitch, float yaw)
    {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3((double)(f1 * f2), (double)f3, (double)(f * f2));
    }

    public final static float[] getRotations(Vec3 pos, boolean predict, double predictionFactor) {
    	final Vec3 playerPos = new Vec3(Minecraft.getMinecraft().thePlayer.posX + (predict ? Minecraft.getMinecraft().thePlayer.motionX * predictionFactor : 0), Minecraft.getMinecraft().thePlayer.posY+ (predict ? Minecraft.getMinecraft().thePlayer.motionY * predictionFactor : 0), Minecraft.getMinecraft().thePlayer.posZ + (predict ? Minecraft.getMinecraft().thePlayer.motionZ * predictionFactor : 0));
		
    	final double diffX = pos.xCoord + 0.5 - playerPos.xCoord;
    	final double diffY = pos.yCoord + 0.5 - (playerPos.yCoord + Minecraft.getMinecraft().thePlayer.getEyeHeight());
    	final double diffZ = pos.zCoord + 0.5 - playerPos.zCoord;
        
    	final double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        double yaw = Math.toDegrees (Math.atan2(diffZ, diffX)) - 90.0f;
        double pitch = -Math.toDegrees(Math.atan2(diffY, dist));
        yaw = Minecraft.getMinecraft().thePlayer.rotationYaw + MathHelper.wrapAngleTo180_double(yaw - Minecraft.getMinecraft().thePlayer.rotationYaw);
        pitch = Minecraft.getMinecraft().thePlayer.rotationPitch + MathHelper.wrapAngleTo180_double(pitch - Minecraft.getMinecraft().thePlayer.rotationPitch);
        return new float[] { (float) yaw, (float) pitch };
    }
}

