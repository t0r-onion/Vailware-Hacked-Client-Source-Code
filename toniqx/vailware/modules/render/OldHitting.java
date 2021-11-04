package toniqx.vailware.modules.render;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.MathHelper;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventSwordBlockAnimation;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.modules.Module;
import toniqx.vailware.modules.combat.KillAura;

public class OldHitting extends Module {
	
	public static ModeSetting animationSetting = new ModeSetting("Animation", "1.7", "1.7", "Tap", "Exhibition", "Slide", "Remix", "Swirl", "Astolfo", "Stab", "Swank");
	public static NumberSetting armY = new NumberSetting("ArmY", 1, 0.1, 2, 0.1);
	public static NumberSetting ackSpeed = new NumberSetting("ArmSpeed", 1, 1, 10, 0.1);
	public static NumberSetting itemSize = new NumberSetting("ItemSize", 0.4, 0.1, 1, 0.01);
	private float var15;
	
	public OldHitting() {
		super("Animations", "", Keyboard.KEY_NONE, Category.CUSTOMIZE);
		this.addSettings(animationSetting, armY, ackSpeed, itemSize);
	}
	
	public void onEnable() {
		ItemRenderer.customSwordBlockAnimation = true;
	}
	
	public void onDisable() {
		ItemRenderer.customSwordBlockAnimation = false;
	}
	
	public void onEvent(Event e) {
		
		float angle = (int) ((System.currentTimeMillis() / 1.5) % 360);
	    angle =  (angle > 180 ? 360 - angle : angle) * 2;
	    angle /= 180f;
	    
	    float angle2 = (int) ((System.currentTimeMillis() / 3.5) % 120);
	    angle2 =  (angle2 > 30 ? 120 - angle2 : angle2) * 2;
	    angle2 /= 1f;
	    
	    
	    float angle3 = (int) ((System.currentTimeMillis() / 3.5) % 110);
	    angle3 =  (angle3 > 30 ? 110 - angle3 : angle3) * 2;
	    angle3 /= 1f;
	    
		int random1 = 0;
		int random2 = 0;
		int random3 = 0;
		int random4 = 0;
		float random5 = 0;
		int random6 = 0;
		int random7 = 0;
		float random8 = 0;
		int random10 = 0;
		
		if(animationSetting.is("1.7")) {
			super.mname = ChatFormatting.GRAY + "1.7" + "   ";
		}
		else if(animationSetting.is("Swank")) {
			super.mname = ChatFormatting.GRAY + "Swank" + "   ";
		}
		else if(animationSetting.is("Remix")) {
			super.mname = ChatFormatting.GRAY + "Remix" + "   ";
		}
		else if(animationSetting.is("Tap")) {
			super.mname = ChatFormatting.GRAY + "Tap" + "   ";
		}
		else if(animationSetting.is("Slide")) {
			super.mname = ChatFormatting.GRAY + "Slide" + "   ";
		}
		else if(animationSetting.is("Exhibition")) {
			super.mname = ChatFormatting.GRAY + "Exhibition" + "   ";
		}
		else if(animationSetting.is("Swirl")) {
			super.mname = ChatFormatting.GRAY + "Swirl" + "   ";
    		random4 = (int) 0.2f;
    		random1 = (int) (System.currentTimeMillis() / 2 % 360);
    		random2 = 1;
    		random5 = 1;
    		random4 = -59;
    		random6 = -1;
    		random7 = 0;
    		random10 = 3;
		}
		else if(animationSetting.is("Stab")) {
			super.mname = ChatFormatting.GRAY + "Stab" + "   ";
    		random4 = (int) 0.2f;
    		random1 = 80;
    		random2 = 1;
    		random5 = angle;
    		random4 = -59;
    		random6 = -1;
    		random7 = 0;
    		random8 = random5 - 2;
    		random10 = 3;
		}
		else if(animationSetting.is("Astolfo")) {
			super.mname = ChatFormatting.GRAY + "Astolfo" + "   ";
		}
		
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				
			}
			
		}
		
		if (e instanceof EventSwordBlockAnimation) {
			
			if (e.isPre()) {
				
				ItemRenderer ir = mc.getItemRenderer();
				float partialTicks = ir.partTicks;
				
				float f = 1.0F - (mc.getItemRenderer().prevEquippedProgress + (ir.equippedProgress - ir.prevEquippedProgress) * partialTicks);
				EntityPlayerSP entityplayersp = this.mc.thePlayer;
				float f1 = entityplayersp.getSwingProgress(partialTicks);
				float swingProgress = mc.thePlayer.getSwingProgress(partialTicks);
				float swingProgressReversed = 1.0f - swingProgress;
				if (this.animationSetting.is("1.7")) {
					if(Client.killAura.target != null) {
						GlStateManager.translate(-0.15f, 0.15f, -0.2f);
						ir.transformFirstPersonItem(f, swingProgress);
						ir.func_178103_d();
					}else {
						GlStateManager.translate(-0.15f, 0.15f, -0.2f);
						ir.transformFirstPersonItem(0, swingProgress - 1);
						ir.func_178103_d();
					}
				}
				else if (this.animationSetting.is("Swank")) {
					if(Client.killAura.target != null) {
						ir.transformFirstPersonItem(f / 2.0f, f1);
                        var15 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927f);
                        GlStateManager.rotate(var15 * 30.0f, - var15, -0.0f, 9.0f);
                        GlStateManager.rotate(var15 * 40.0f, 1.0f, - var15, -0.0f);
					}else {
						GlStateManager.translate(-0.15f, 0.15f, -0.2f);
						ir.transformFirstPersonItem(0, swingProgress - 1);
						ir.func_178103_d();
					}
				}
				else if (this.animationSetting.is("Remix")) {
					if(Client.killAura.target != null) {
                        ir.transformFirstPersonItem(f / 2, f1);
                        float var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                        var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                        GlStateManager.rotate(var9 * 50.0F / 9.0F, -var9, -0.0F, 90.0F);
                        GlStateManager.rotate(var9 * 50.0F, 200.0F, -var9 / 2.0F, -0.0F);
                        ir.func_178103_d();
					}else {
						GlStateManager.translate(-0.15f, 0.15f, -0.2f);
						ir.transformFirstPersonItem(0, swingProgress - 1);
						ir.func_178103_d();
					}
				}
				else if (this.animationSetting.is("Slide")) {
					if(Client.killAura.target != null) {
						ir.transformFirstPersonItem(0, 0.0f);
                        ir.func_178103_d();
                        float var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927f);
                        GlStateManager.translate(-0.05f, -0.0f, 0.35f);
                        GlStateManager.rotate(-var9 * (float)60.0 / 2.0f, -15.0f, -0.0f, 9.0f);
                        GlStateManager.rotate(-var9 * (float)70.0, 1.0f, -0.4f, -0.0f);
					}else {
						GlStateManager.translate(-0.15f, 0.15f, -0.2f);
						ir.transformFirstPersonItem(0, swingProgress - 1);
						ir.func_178103_d();
					}
				}
				else if (this.animationSetting.is("Exhibition")) {
					if(Client.killAura.target != null) {
                        float f81 = MathHelper.sin((float) (MathHelper.sqrt_float(f1) * 3.0));
                        ir.transformFirstPersonItem(f, 0.0f);
                        GlStateManager.translate(0.1F, 0.4F, -0.1F);
                        GL11.glRotated(-f81 * 20.0F, f81 / 2, 0.0F, 9.0F);
                        GL11.glRotated(-f81 * 50.0F, 0.8F, f81 / 2, 0F);
                        ir.func_178103_d();
					}else {
						GlStateManager.translate(-0.15f, 0.15f, -0.2f);
						ir.transformFirstPersonItem(0, swingProgress - 1);
						ir.func_178103_d();
					}
				}
				else if (this.animationSetting.is("Tap")) {
					if(Client.killAura.target != null) {
						GlStateManager.translate(-0.15f, 0.15f, -0.2f);
						ir.transformFirstPersonItem(0, swingProgress - 1);
						ir.func_178103_d();
					}else {
						GlStateManager.translate(-0.15f, 0.15f, -0.2f);
						ir.transformFirstPersonItem(0, swingProgress - 1);
						ir.func_178103_d();
					}
				}
				else if (this.animationSetting.is("Swirl")) {
					if(Client.killAura.target != null) {
						GlStateManager.translate(random8, -1.4f, -random5 - 2.5f);
						GlStateManager.rotate(random4, random6 , random7, random10 - 20);
						GlStateManager.rotate(-random1, random2, random3, 0.0F);
						GlStateManager.rotate(40.0F, 0.0F, 1.0F, 0.0F);
					}else {
						GlStateManager.translate(-0.15f, 0.15f, -0.2f);
						ir.transformFirstPersonItem(0, swingProgress - 1);
						ir.func_178103_d();
					}
				}
				else if (this.animationSetting.is("Stab")) {
					if(Client.killAura.target != null) {
						GlStateManager.translate(random7 + 0.4f, -0.3f, -random5 - 2);
						GlStateManager.rotate(random4, random6 + 10, random7, random10);
						GlStateManager.rotate(-random1, random2, random3, 0.0F);
			        	GlStateManager.rotate(70.0F, 4.0F, 12.0F, 7.0F);
					}else {
						GlStateManager.translate(-0.15f, 0.15f, -0.2f);
						ir.transformFirstPersonItem(0, swingProgress - 1);
						ir.func_178103_d();
					}
				}
				else if (this.animationSetting.is("Spin")) {
					if(Client.killAura.target != null) {
						GlStateManager.translate(0.2f, 0.1f, -0.4f);
						ir.transformFirstPersonItem(0, 0);
						GlStateManager.rotate(-8, 0, 0, 1);
						GL11.glTranslatef(-1.0f, 0.4f, 0);
						GlStateManager.rotate(swingProgress * 360, 1, 0, -1);
						GL11.glTranslatef(1.0f, -0.4f, 0);
						ir.func_178103_d();
					}else {
						GlStateManager.translate(-0.15f, 0.15f, -0.2f);
						ir.transformFirstPersonItem(0, swingProgress - 1);
						ir.func_178103_d();
					}
				}
				else if (this.animationSetting.is("Astolfo")) {
					if(Client.killAura.target != null) {
						astolfoCircle(mc.thePlayer.getSwingProgress(partialTicks));
						ir.func_178103_d();
					}else {
						GlStateManager.translate(-0.15f, 0.15f, -0.2f);
						ir.transformFirstPersonItem(0, swingProgress - 1);
						ir.func_178103_d();
					}
				}
				
			}
			
		}
		
	}
	
	private static transient int astolfoTicks = 0;
	
	private void astolfoCircle(float swingProgress) {
        float f1 = MathHelper.sin((float) (MathHelper.sqrt_float(swingProgress) * Math.PI));
        this.astolfoTicks++;
        GlStateManager.translate(-0.0F, -0.2F, -0.6F);
        GlStateManager.rotate(-this.astolfoTicks * 0.07F * 30.0F, 0.0F, 0.0F, -1.0F);
        GlStateManager.rotate(44.0F, 0.0F, 1.0F, 0.6F);
        GlStateManager.rotate(44.0F, 1.0F, 0.0F, -0.6F);
        GlStateManager.translate(1.0F, -0.2F, 0.5F);
        GlStateManager.rotate(-44.0F, 1.0F, 0.0F, -0.6F);
        GlStateManager.scale(0.5D, 0.5D, 0.5D);
      }
	
}