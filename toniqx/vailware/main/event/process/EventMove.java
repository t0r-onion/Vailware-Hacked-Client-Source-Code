package toniqx.vailware.main.event.process;

import net.minecraft.client.Minecraft;
import toniqx.vailware.main.event.Event;

public class EventMove extends Event<EventMove> {

    public double x, y, z;

    public EventMove(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    /*public void setSpeed(double moveSpeed) {
        setSpeed(moveSpeed, Minecraft.getMinecraft().thePlayer.rotationYaw, Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe, Minecraft.getMinecraft().thePlayer.movementInput.moveForward);
    }*/

    public void setSpeed(double moveSpeed, float pseudoYaw, double pseudoStrafe, double pseudoForward) {
        double forward = pseudoForward;
        double strafe = pseudoStrafe;
        float yaw = pseudoYaw;

        if (forward != 0.0) {
            if (strafe > 0.0) {
                yaw += ((forward > 0.0) ? -45 : 45);
            } else if (strafe < 0.0) {
                yaw += ((forward > 0.0) ? 45 : -45);
            }
            strafe = 0.0F;
            if (forward > 0.0) {
                forward = 1F;
            } else if (forward < 0.0) {
                forward = -1F;
            }
        }

        if (strafe > 0.0) {
            strafe = 1F;
        } else if (strafe < 0.0) {
            strafe = -1F;
        }
        double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
        double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
        x = (forward * moveSpeed * mx + strafe * moveSpeed * mz);
        z = (forward * moveSpeed * mz - strafe * moveSpeed * mx);
    }

    public void setSpeed(double moveSpeed) {
        float forward = Minecraft.getMinecraft().thePlayer.movementInput.moveForward;
        float strafe = Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe;
        float yaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            Minecraft.getMinecraft().thePlayer.motionX = 0.0;
            Minecraft.getMinecraft().thePlayer.motionZ = 0.0;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0f) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0f) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0f;
                if (forward > 0.0f) {
                    forward = 1.0f;
                } else if (forward < 0.0f) {
                    forward = -1.0f;
                }
            }
            double xDist = forward * moveSpeed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * moveSpeed * Math.sin(Math.toRadians(yaw + 90.0f));
            double zDist = forward * moveSpeed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * moveSpeed * Math.cos(Math.toRadians(yaw + 90.0f));
            setX(Minecraft.getMinecraft().thePlayer.motionX = xDist);
            setZ(Minecraft.getMinecraft().thePlayer.motionZ = zDist);
        }
    }

	public void setSpeed(double moveSpeed, float yaw) {
		float forward = Minecraft.getMinecraft().thePlayer.movementInput.moveForward;
		float strafe = Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe;
		if ((double) forward == 0.0D && (double) strafe == 0.0D) {
			Minecraft.getMinecraft().thePlayer.motionX = 0.0D;
			Minecraft.getMinecraft().thePlayer.motionZ = 0.0D;
		} else {
			if ((double) forward != 0.0D) {
				if (strafe > 0.0F) {
					yaw += (float) ((double) forward > 0.0D ? -45 : 45);
				} else if (strafe < 0.0F) {
					yaw += (float) ((double) forward > 0.0D ? 45 : -45);
				}

				strafe = 0.0F;
				if (forward > 0.0F) {
					forward = 1.0F;
				} else if (forward < 0.0F) {
					forward = -1.0F;
				}
			}

			double xDist = (double) forward * moveSpeed * Math.cos(Math.toRadians((double) (yaw + 90.0F)))
					+ (double) strafe * moveSpeed * Math.sin(Math.toRadians((double) (yaw + 90.0F)));
			double zDist = (double) forward * moveSpeed * Math.sin(Math.toRadians((double) (yaw + 90.0F)))
					- (double) strafe * moveSpeed * Math.cos(Math.toRadians((double) (yaw + 90.0F)));
			Minecraft.getMinecraft().thePlayer.motionX = xDist;
			Minecraft.getMinecraft().thePlayer.motionZ = zDist;
			this.setX(xDist);
			this.setZ(zDist);
		}

	}
    
}

