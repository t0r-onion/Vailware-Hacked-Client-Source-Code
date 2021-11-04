package toniqx.vailware.main.util.impl.server;

import net.minecraft.client.Minecraft;

public class SpeedUtil {
    static Minecraft mc = Minecraft.getMinecraft();

    public static void setSpeed(float speed) {
        mc.thePlayer.motionX = -Math.sin(getDirection()) * (double) speed;
        mc.thePlayer.motionZ = Math.cos(getDirection()) * (double) speed;
    }

    public static float getDirection() {
        float var1 = mc.thePlayer.rotationYaw;
        if (mc.thePlayer.moveForward < 0.0f) {
            var1 += 180.0f;
        }
        float forward = 1.0f;
        forward = mc.thePlayer.moveForward < 0.0f ? -0.5f : (mc.thePlayer.moveForward > 0.0f ? 0.5f : 1.0f);
        if (mc.thePlayer.moveStrafing > 0.0f) {
            var1 -= 90.0f * forward;
        }
        if (mc.thePlayer.moveStrafing < 0.0f) {
            var1 += 90.0f * forward;
        }
        return var1 *= Math.PI / 180.0F;
    }

    public void setWalkSpeed(double speed) {
        mc.thePlayer.capabilities.setPlayerWalkSpeed((float) speed);
    }

    public void setFlySpeed(double speed) {
        mc.thePlayer.capabilities.setFlySpeed((float) speed);
    }

    // 
    public void daytimeSpeed(double speed) {
        mc.theWorld.setWorldTime((long) speed);
    }

}
