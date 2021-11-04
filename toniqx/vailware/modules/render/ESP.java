
package toniqx.vailware.modules.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventRenderGUI;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventRenderWorld;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.ColorManager;
import toniqx.vailware.main.util.impl.ColorUtil;
import toniqx.vailware.main.util.impl.RenderUtils;
import toniqx.vailware.main.util.impl.render.EntityUtils;
import toniqx.vailware.main.util.impl.render.EspUtil;
import toniqx.vailware.main.util.impl.render.RenderUtil;
import toniqx.vailware.modules.Module;
import toniqx.vailware.modules.misc.Targets;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.mojang.realmsclient.gui.ChatFormatting;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ESP extends Module {
    public final BooleanSetting outline = new BooleanSetting("Rectangles", true);
    public final List<Entity> collectedEntities = new ArrayList<>();
    private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
    private final int backgroundColor = new Color(0, 0, 0, 120).getRGB();
    private final int black = Color.BLACK.getRGB();
    private final BooleanSetting localPlayer = new BooleanSetting("Local Player", true);
    private final BooleanSetting mobs = new BooleanSetting("Mobs", false);
    private final BooleanSetting animals = new BooleanSetting("Animals", false);
    private final BooleanSetting armorBar = new BooleanSetting("Armor bar", false);
    public ModeSetting mode = new ModeSetting("Mode", "Outline", "Outline"),
            theme = new ModeSetting("Color", "Custom", "Custom", "Rainbow");
    public BooleanSetting healthBar = new BooleanSetting("Health", false),
            droppedItems = new BooleanSetting("Items", false);
    
    public ModeSetting mode2 = new ModeSetting("Mode", "2D", "2D", "3D");
    public BooleanSetting rainbow = new BooleanSetting("Rainbow", false);
    public NumberSetting red = new NumberSetting("Red", 255, 1, 255, 1);
    public NumberSetting green = new NumberSetting("Green", 110, 1, 255, 1);
    public NumberSetting blue = new NumberSetting("Blue", 230, 1, 255, 1);
    
    Color color2;

    public ESP() {
        super("ESP", "", Keyboard.KEY_NONE, Category.RENDER);
        this.addSettings(mode2, red, green, blue, rainbow);
    }
    
	int chestColor;

    public void onEvent(Event e) {
    	if(e instanceof EventUpdate) {
    		if(mode2.is("2D")) {
    			super.mname = ChatFormatting.GRAY + "2D" + "   ";
    		}else if(mode2.is("3D")) {
    			super.mname = ChatFormatting.GRAY + "3D" + "   ";
    		}
    	}
    	if(e instanceof EventRenderWorld && mode2.is("3D")) {
            collectEntities();
            final List<Entity> collectedEntities = this.collectedEntities;
            for (int i = 0, collectedEntitiesSize = collectedEntities.size(); i < collectedEntitiesSize; i++) {
				if(rainbow.enabled) {
					chestColor = new Color(ColorUtil.getRainbow(12, 0.75f, 1f, 5)).getRGB();
				}else {
					chestColor = new Color((float) red.getValue() / 255, (float) green.getValue() / 255, (float) blue.getValue() / 255).getRGB();
				}
                final Entity entity = collectedEntities.get(i);
                if (isValid(entity)) {
            		GL11.glBlendFunc(770, 771);
            		GL11.glEnable(GL11.GL_BLEND);
            		GL11.glLineWidth(2.0F);
            		GL11.glDisable(GL11.GL_TEXTURE_2D);
            		GL11.glDisable(GL11.GL_DEPTH_TEST);
            		GL11.glDepthMask(false);
            		RenderUtils.glColor(chestColor);
            		Minecraft.getMinecraft().getRenderManager();
            		RenderGlobal.func_181561_a(new AxisAlignedBB(entity.boundingBox.minX - 0.05 - entity.posX + (entity.posX - Minecraft.getMinecraft().getRenderManager().renderPosX), entity.boundingBox.minY - entity.posY + (entity.posY - Minecraft.getMinecraft().getRenderManager().renderPosY), entity.boundingBox.minZ - 0.05 - entity.posZ + (entity.posZ - Minecraft.getMinecraft() .getRenderManager().renderPosZ), entity.boundingBox.maxX + 0.05 - entity.posX + (entity.posX - Minecraft.getMinecraft() .getRenderManager().renderPosX), entity.boundingBox.maxY + 0.1 - entity.posY + (entity.posY - Minecraft.getMinecraft().getRenderManager().renderPosY), entity.boundingBox.maxZ+ 0.05 - entity.posZ + (entity.posZ - Minecraft.getMinecraft().getRenderManager().renderPosZ)));
            		GL11.glEnable(GL11.GL_DEPTH_TEST);
            		GL11.glEnable(GL11.GL_TEXTURE_2D);
            		GL11.glDepthMask(true);
            		GL11.glDisable(GL11.GL_BLEND);
                }
            }
    	}
        if (e instanceof EventRenderGUI && mode2.is("2D")) {


            GL11.glPushMatrix();
            collectEntities();
            final float partialTicks = mc.timer.renderPartialTicks;
            final ScaledResolution scaledResolution = ((EventRenderGUI) e).sr;
            final int scaleFactor = scaledResolution.getScaleFactor();
            final double scaling = scaleFactor / Math.pow(scaleFactor, 2.0D);
            GL11.glScaled(scaling, scaling, scaling);
            final int black = this.black;
            final int background = backgroundColor;
            float scale = 0.65F;
            final RenderManager renderMng = mc.getRenderManager();
            final EntityRenderer entityRenderer = mc.entityRenderer;
            final boolean outline = this.outline.isEnabled();
            final boolean health = this.healthBar.isEnabled();
            final boolean armor = this.armorBar.isEnabled();

            if (Targets.target.is("Mob")) {
                mobs.setEnabled(true);
            }else {
                mobs.setEnabled(false);
            }
            if (Targets.target.is("Animal")) {
                animals.setEnabled(true);
            }else {
                animals.setEnabled(false);
            }
            

            final List<Entity> collectedEntities = this.collectedEntities;
            for (int i = 0, collectedEntitiesSize = collectedEntities.size(); i < collectedEntitiesSize; i++) {
                final Entity entity = collectedEntities.get(i);
                if (isValid(entity) && RenderUtil.isInViewFrustrum(entity)) {
                    final double x = RenderUtil.interpolate(entity.posX, entity.lastTickPosX, partialTicks);
                    final double y = RenderUtil.interpolate(entity.posY, entity.lastTickPosY, partialTicks);
                    final double z = RenderUtil.interpolate(entity.posZ, entity.lastTickPosZ, partialTicks);
                    final double width = entity.width / 1.5;
                    final double height = entity.height + (entity.isSneaking() ? -0.3 : 0.2);

                    final AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
                    final List<Vector3d> vectors = Arrays.asList(new Vector3d(aabb.minX, aabb.minY, aabb.minZ), new Vector3d(aabb.minX, aabb.maxY, aabb.minZ),
                            new Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ),
                            new Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ),
                            new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));
                    entityRenderer.setupCameraTransform(partialTicks, 0);
                    Vector4d position = null;

                    for (int i1 = 0, vectorsSize = vectors.size(); i1 < vectorsSize; i1++) {
                        Vector3d vector = vectors.get(i1);
                        vector = project2D(scaleFactor, vector.x - renderMng.viewerPosX, vector.y - renderMng.viewerPosY, vector.z - renderMng.viewerPosZ);

                        if (vector != null && vector.z >= 0.0 && vector.z < 1.0) {
                            if (position == null) {
                                position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
                            }

                            position.x = Math.min(vector.x, position.x);
                            position.y = Math.min(vector.y, position.y);
                            position.z = Math.max(vector.x, position.z);
                            position.w = Math.max(vector.y, position.w);
                        }
                    }

                    if (position != null) {
                        entityRenderer.setupOverlayRendering();
                        final double posX = position.x;
                        final double posY = position.y;
                        final double endPosX = position.z;
                        final double endPosY = position.w;


                        int count = 0;

                        if (theme.is("Custom")) {
                            color2 = new Color((int) 255, (int) 255, (int) 255);
                        }
                        
                        


        				if(rainbow.enabled) {
        					chestColor = new Color(ColorUtil.getRainbow(12, 0.75f, 1f, 5)).getRGB();
        				}else {
        					chestColor = new Color((float) red.getValue() / 255, (float) green.getValue() / 255, (float) blue.getValue() / 255).getRGB();
        				}


                        if (outline) {
                            if (mode.is("Outline")) {
                                Gui.drawRect(posX - 1, posY, posX + 0.5D, endPosY + .5, chestColor);
                                Gui.drawRect(posX - 1, posY - .5, endPosX + .5, posY + .5 + 0.5D, chestColor);
                                Gui.drawRect(endPosX - .5 - 0.5D, posY, endPosX + .5, endPosY + .5, chestColor);
                                Gui.drawRect(posX - 1, endPosY - 0.5D - .5, endPosX + .5, endPosY + .5, chestColor);
                                Gui.drawRect(posX - .5, posY, posX + 0.5D - .5, endPosY, chestColor);
                                Gui.drawRect(posX, endPosY - 0.5D, endPosX, endPosY, chestColor);
                                Gui.drawRect(posX - .5, posY, endPosX, posY + 0.5D, chestColor);
                                Gui.drawRect(endPosX - 0.5D, posY, endPosX, endPosY, chestColor);
                            }

                        }

                        count++;

                        final boolean living = entity instanceof EntityLivingBase;

                        if (living) {
                            final EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                            if (health) {
                                float hp = entityLivingBase.getHealth();
                                final float maxHealth = entityLivingBase.getMaxHealth();
                                if (hp > maxHealth) {
                                    hp = maxHealth;
                                }

                                final double hpPercentage = hp / maxHealth;

                                final double hpHeight = (endPosY - posY) * hpPercentage;

                                Gui.drawRect(posX - 3.5, posY - .5, posX - 1.5, endPosY + 0.5, background);

                                if (hp > 0) {
                                    int colorrectCode = entityLivingBase.getHealth() > 15 ? 0xff4DF75B : entityLivingBase.getHealth() > 10 ? 0xffF1F74D : entityLivingBase.getHealth() > 7 ? 0xffF7854D : 0xffF7524D;
                                    Gui.drawRect(posX - 3, endPosY, posX - 2, endPosY - hpHeight, colorrectCode);

                                    final float absorption = entityLivingBase.getAbsorptionAmount();
                                    if (absorption > 0.0F) {
                                        Gui.drawRect(posX - 3, endPosY, posX - 2, endPosY - (((endPosY - posY) / 6D) * absorption / 2D), new Color(Potion.absorption.getLiquidColor()).getRGB());
                                    }
                                }
                            }
                        }
                    }
                }
            }


            GlStateManager.enableBlend();

            entityRenderer.setupOverlayRendering();
            GL11.glPopMatrix();
        }

    }

    private boolean isFriendly(final EntityPlayer player) {
        return EntityUtils.isOnSameTeam(player);
    }

    private void collectEntities() {
        collectedEntities.clear();

        final List<Entity> playerEntities = mc.theWorld.loadedEntityList;
        for (int i = 0, playerEntitiesSize = playerEntities.size(); i < playerEntitiesSize; i++) {
            final Entity entity = playerEntities.get(i);
            if (isValid(entity)) {
                collectedEntities.add(entity);
            }
        }
    }

    private Vector3d project2D(int scaleFactor, double x, double y, double z) {
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);

        if (GLU.gluProject((float) x, (float) y, (float) z, modelview, projection, viewport, vector)) {
            return new Vector3d(vector.get(0) / scaleFactor, (Display.getHeight() - vector.get(1)) / scaleFactor, vector.get(2));
        }

        return null;
    }

    private boolean isValid(Entity entity) {
        if (entity == mc.thePlayer && (!localPlayer.isEnabled() || mc.gameSettings.thirdPersonView == 0)) {
            return false;
        }

        if (entity.isDead) {
            return false;
        }

        if (!Targets.target.is("All") && entity.isInvisible()) {
            return false;
        }

        if (droppedItems.isEnabled() && entity instanceof EntityItem && mc.thePlayer.getDistanceToEntity(entity) < 10) {
            return true;
        }

        if (animals.isEnabled() && entity instanceof EntityAnimal) {
            return true;
        }

        if (Targets.target.is("Player") && (entity instanceof EntityPlayer)) {
        	if(entity != mc.thePlayer) {
        		return true;
        	}
        }

        return mobs.isEnabled() && (entity instanceof EntityMob || entity instanceof EntitySlime || entity instanceof EntityDragon || entity instanceof EntityGolem);
    }


    public enum BoxMode {
        BOX, CORNERS
    }

    public enum BoxColor {
        WHITE, RED,
    }
}
