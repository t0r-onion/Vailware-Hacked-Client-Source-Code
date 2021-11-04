package toniqx.vailware.modules.render;

import java.awt.Color;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventRender3D;
import toniqx.vailware.main.event.process.EventRenderWorld;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.RenderUtils;
import toniqx.vailware.modules.Module;

public class BlockOverlay extends Module {
	
	public static NumberSetting red = new NumberSetting("Red", 1, 1, 255, 1);
	public static NumberSetting green = new NumberSetting("Green", 1, 1, 255, 1);
	public static NumberSetting blue = new NumberSetting("Blue", 1, 1, 255, 1);
	
	public BlockOverlay(){
		super("BlockOverlay", "", Keyboard.KEY_NONE, Category.RENDER);
		this.addSettings(red, green, blue);
	}

    public void onEvent(Event e) {
    	if(e instanceof EventRenderWorld) {
            float redColor = (float) red.getValue() / 255;
            float greenColor = (float) green.getValue() / 255;
            float blueColor = (float) blue.getValue() / 255;

            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                BlockPos pos = mc.objectMouseOver.getBlockPos();
                Block block = mc.theWorld.getBlockState(pos).getBlock();
                RenderManager renderManager = mc.getRenderManager();
                String s = block.getLocalizedName();
                mc.getRenderManager();
                double x = (double)pos.getX() - renderManager.getRenderPosX();
                mc.getRenderManager();
                double y = (double)pos.getY() - renderManager.getRenderPosY();
                mc.getRenderManager();
                double z = (double)pos.getZ() - renderManager.getRenderPosZ();
                GL11.glPushMatrix();
                GL11.glEnable((int)3042);
                GL11.glBlendFunc((int)770, (int)771);
                GL11.glDisable((int)3553);
                GL11.glEnable((int)2848);
                GL11.glDisable((int)2929);
                GL11.glDepthMask((boolean)false);
                Color c = new Color(redColor, greenColor, blueColor);
                int r = c.getRed();
                int g = c.getGreen();
                int b = c.getBlue();
                RenderUtils.glColor(new Color(r, g, b, 50).getRGB());
                double minX = block instanceof BlockStairs || Block.getIdFromBlock((Block)block) == 134 ? 0.0 : block.getBlockBoundsMinX();
                double minY = block instanceof BlockStairs || Block.getIdFromBlock((Block)block) == 134 ? 0.0 : block.getBlockBoundsMinY();
                double minZ = block instanceof BlockStairs || Block.getIdFromBlock((Block)block) == 134 ? 0.0 : block.getBlockBoundsMinZ();
                RenderUtils.drawBoundingBox((AxisAlignedBB)new AxisAlignedBB(x + minX, y + minY, z + minZ, x + block.getBlockBoundsMaxX(), y + block.getBlockBoundsMaxY(), z + block.getBlockBoundsMaxZ()));
                RenderUtils.glColor(new Color(redColor, greenColor, blueColor).getRGB());
                GL11.glLineWidth((float)0.5f);
                GL11.glDisable((int)2848);
                GL11.glEnable((int)3553);
                GL11.glEnable((int)2929);
                GL11.glDepthMask((boolean)true);
                GL11.glDisable((int)3042);
                GL11.glPopMatrix();
            }
            GL11.glColor4f(1f, 1f, 1f, 1f);
        }
    }
}