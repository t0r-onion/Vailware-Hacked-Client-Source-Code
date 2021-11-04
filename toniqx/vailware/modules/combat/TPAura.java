package toniqx.vailware.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventRotationMotion;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.process.EventPacket;
import toniqx.vailware.main.event.process.EventRenderWorld;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.MathUtils;
import toniqx.vailware.main.util.impl.RenderUtils;
import toniqx.vailware.main.util.impl.ScaffoldRenderUtils;
import toniqx.vailware.main.util.impl.ScaffoldRotationUtils;
import toniqx.vailware.main.util.impl.Timer;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.main.util.impl.render.RenderUtil;
import toniqx.vailware.main.util.impl.server.AStarCustomPathFinder;
import toniqx.vailware.main.util.impl.server.Vec3;
import toniqx.vailware.main.util.impl.server.angle.Angle;
import toniqx.vailware.main.util.impl.server.angle.AngleUtility;
import toniqx.vailware.main.util.impl.server.vector.impl.Vector3;
import toniqx.vailware.modules.Module;
import toniqx.vailware.modules.Module.Category;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class TPAura extends Module {

    private double dashDistance = 5;
    public static final String RANGE = "RANGE";
    public static final String PLAYERS = "PLAYERS";
    public static final String ANIMALS = "OTHERS";
    public static final String TEAMS = "TEAMS";
    public static final String INVISIBLES = "INVISIBLES";
    public static final String ESP = "ESP";
    public static final String PATHESP = "PATH";
    public static final String CPS = "CPS";
    public static final String MAXT = "MAXTARGET";
    public static final String MODE = "MODE";
    public static final String TIMER = "TIMER";
    private ArrayList<Vec3> path = new ArrayList<>();
    private List<Vec3>[] test = new ArrayList[50];
    private List<EntityLivingBase> targets = new CopyOnWriteArrayList<>();
    private Timer cps = new Timer();
    public static Timer timer = new Timer();
    public static boolean canReach;

    public ModeSetting mode = new ModeSetting("Render", "Box", "Box", "Line");
    public BooleanSetting mplayers = new BooleanSetting("Players", true);
    public BooleanSetting mpathESP = new BooleanSetting("Draw", true);
    public NumberSetting mrange = new NumberSetting("Range", 30, 8, 100, 0.1);
    public NumberSetting maps = new NumberSetting("APS", 7, 1, 20, 0.1);
    public NumberSetting mtargets = new NumberSetting("Targets", 5, 1, 10, 0.1);
    
    public TPAura() {
        super("TPAura", "", Keyboard.KEY_NONE, Category.COMBAT);
        addSettings(mode, mrange, maps, mtargets, mpathESP);
    }


    @Override
    public void onEnable() {
    	timer.reset();
    	targets.clear();
    }

    float yaw, pitch;
    
    public void onEvent(Event e) {
        int maxtTargets = ((Number) mtargets.getValue()).intValue();
        
        targets = getTargets();
    	if (e instanceof EventRotationMotion) {
	        if (targets.size() > 0) {
	            for (int i = 0; i < (targets.size() > maxtTargets ? maxtTargets : targets.size()); i++) {
	            	EntityLivingBase T = targets.get(i);
	            	EventRotationMotion event = (EventRotationMotion) e;
	    			
	    			final AngleUtility angleUtil = new AngleUtility(10, 190, 10, 10); //can be adjusted
	    	
	    			Vector3<Double> targetPos= new Vector3<>(T.posX, T.posY, T.posZ);
	    			Vector3<Double> playerPos= new Vector3<>(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
	    	            
	    			Angle distance = angleUtil.calculateAngle(targetPos, playerPos);
	    	
	    			yaw = distance.getYaw();
	    			pitch = distance.getPitch();
	    	            
	    			event.setYaw((float) (yaw + MathUtils.randomNumber(2.5F, -0.2F)));
	    			event.setPitch((float) (pitch + MathUtils.randomNumber(2.5F, -0.2F)));
	    	            
	    			ScaffoldRenderUtils.setCustomPitch(pitch);
	    			ScaffoldRenderUtils.setCustomYaw(yaw);
	    			ScaffoldRenderUtils.SetCustomPitch = true;
	    			ScaffoldRenderUtils.SetCustomYaw = true;
	            }
		
	        }
    	}
        
        if (e instanceof EventUpdate) {
            EventUpdate em = (EventUpdate) e;
            int delayValue = (20 / ((Number) maps.getValue()).intValue()) * 50;
            if (em.isPre()) {
                targets = getTargets();
            		
                if (cps.hasTimeElapsed(delayValue, true))
                    if (targets.size() > 0) {
                        test = new ArrayList[50];
                        for (int i = 0; i < (targets.size() > maxtTargets ? maxtTargets : targets.size()); i++) {
                            EntityLivingBase T = targets.get(i);
                            Vec3 topFrom = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                            Vec3 to = new Vec3(T.posX, T.posY, T.posZ);
                            
                            path = computePath(topFrom, to);
                            test[i] = path;
                            for (Vec3 pathElm : path) {
                            	
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
                            }
                            
                            mc.thePlayer.swingItem();
                            mc.playerController.attackEntity(mc.thePlayer, T);
                            Collections.reverse(path);
                            for (Vec3 pathElm : path) {
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
                            }
                        }
                        cps.reset();
                    }
            }else {
            	if(em.isPost()) {
            		if(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && mc.thePlayer.getHeldItem() != null) {
            			mc.thePlayer.setItemInUse(mc.thePlayer.getCurrentEquippedItem(), (int) maps.getValue());
            		}
            	}
            }
        }
        if (e instanceof EventRenderWorld) {

        	EventRenderWorld er = (EventRenderWorld) e;
            if (!path.isEmpty() && (Boolean) mpathESP.isEnabled()) {
                for (int i = 0; i < targets.size(); i++) {
                    try {
                        if (test != null)
                            for (Vec3 pos : test[i]) {
                                if (pos != null)
                                    drawPath(pos);
                            }
                    } catch (Exception e1) {

                    }
                }

                if (cps.hasTimeElapsed(1000, true)) {
                    test = new ArrayList[50];
                    path.clear();
                }
            }
        }
        if (e instanceof EventPacket) {
            EventPacket ep = (EventPacket) e;
            Packet packet = ep.getPacket();
        }
    }

    private ArrayList<Vec3> computePath(Vec3 topFrom, Vec3 to) {
        if (!canPassThrow(new BlockPos(topFrom.mc()))) {
            topFrom = topFrom.addVector(0, 1, 0);
        }
        AStarCustomPathFinder pathfinder = new AStarCustomPathFinder(topFrom, to);
        pathfinder.compute();

        int i = 0;
        Vec3 lastLoc = null;
        Vec3 lastDashLoc = null;
        ArrayList<Vec3> path = new ArrayList<Vec3>();
        ArrayList<Vec3> pathFinderPath = pathfinder.getPath();
        for (Vec3 pathElm : pathFinderPath) {
            if (i == 0 || i == pathFinderPath.size() - 1) {
                if (lastLoc != null) {
                    path.add(lastLoc.addVector(0.5, 0, 0.5));
                }
                path.add(pathElm.addVector(0.5, 0, 0.5));
                lastDashLoc = pathElm;
            } else {
                boolean canContinue = true;
                if (pathElm.squareDistanceTo(lastDashLoc) > dashDistance * dashDistance) {
                    canContinue = false;
                } else {
                    double smallX = Math.min(lastDashLoc.getX(), pathElm.getX());
                    double smallY = Math.min(lastDashLoc.getY(), pathElm.getY());
                    double smallZ = Math.min(lastDashLoc.getZ(), pathElm.getZ());
                    double bigX = Math.max(lastDashLoc.getX(), pathElm.getX());
                    double bigY = Math.max(lastDashLoc.getY(), pathElm.getY());
                    double bigZ = Math.max(lastDashLoc.getZ(), pathElm.getZ());
                    cordsLoop:
                    for (int x = (int) smallX; x <= bigX; x++) {
                        for (int y = (int) smallY; y <= bigY; y++) {
                            for (int z = (int) smallZ; z <= bigZ; z++) {
                                if (!AStarCustomPathFinder.checkPositionValidity(x, y, z, false)) {
                                    canContinue = false;
                                    break cordsLoop;
                                }
                            }
                        }
                    }
                }
                if (!canContinue) {
                    path.add(lastLoc.addVector(0.5, 0, 0.5));
                    lastDashLoc = lastLoc;
                }
            }
            lastLoc = pathElm;
            i++;
        }
        return path;
    }

    private boolean canPassThrow(BlockPos pos) {
        Block block = Minecraft.getMinecraft().theWorld.getBlockState(new net.minecraft.util.BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock();
        return block.getMaterial() == Material.air || block.getMaterial() == Material.plants || block.getMaterial() == Material.vine || block == Blocks.ladder || block == Blocks.water || block == Blocks.flowing_water || block == Blocks.wall_sign || block == Blocks.standing_sign;
    }


    boolean validEntity(EntityLivingBase entity) {
        float range = ((Number) mrange.getValue()).floatValue();
        boolean players = true;

        if ((mc.thePlayer.isEntityAlive())
                && !(entity instanceof EntityPlayerSP)) {
            if (mc.thePlayer.getDistanceToEntity(entity) <= range) {

                if (entity.isPlayerSleeping()) {
                    return false;
                }
                if (entity instanceof EntityPlayer) {
                    if (players) {

                        EntityPlayer player = (EntityPlayer) entity;
                        if (!player.isEntityAlive() && player.getHealth() == 0.0) {
                            return false;
                        } else if (player.isInvisible()) {
                            return false;
                        } else
                            return true;
                    }
                } else {
                    if (!entity.isEntityAlive()) {

                        return false;
                    }
                }

                if (entity instanceof EntityMob) {

                    return false;
                }
                if ((entity instanceof EntityAnimal || entity instanceof EntityVillager)) {
                    if (entity.getName().equals("Villager")) {
                        return false;
                    }
                    return false;
                }
            }
        }

        return false;
    }

    private List<EntityLivingBase> getTargets() {
        List<EntityLivingBase> targets = new ArrayList<>();

        try{
	        for (Object o : mc.theWorld.getLoadedEntityList()) {
	            if (o instanceof EntityLivingBase) {
	                EntityLivingBase entity = (EntityLivingBase) o;
	                if (validEntity(entity)) {
	                    targets.add(entity);
	                }
	            }
	        }
        }catch(ConcurrentModificationException e) {
        	
        }
        targets.sort((o1, o2) -> (int) (o1.getDistanceToEntity(mc.thePlayer) * 1000 - o2.getDistanceToEntity(mc.thePlayer) * 1000));
        return targets;
    }

    public void drawESP(Entity entity, int color) {
        double x = entity.lastTickPosX
                + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks;

        double y = entity.lastTickPosY
                + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks;

        double z = entity.lastTickPosZ
                + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks;
        double width = Math.abs(entity.boundingBox.maxX - entity.boundingBox.minX);
        double height = Math.abs(entity.boundingBox.maxY - entity.boundingBox.minY);
        Vec3 vec = new Vec3(x - width / 2, y, z - width / 2);
        Vec3 vec2 = new Vec3(x + width / 2, y + height, z + width / 2);
        RenderUtil.pre3D();
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
        RenderUtil.glColor(color);
        RenderUtil.drawSelectionBoundingBox(new AxisAlignedBB(
                vec.getX() - RenderManager.renderPosX, vec.getY() - RenderManager.renderPosY, vec.getZ() - RenderManager.renderPosZ,
                vec2.getX() - RenderManager.renderPosX, vec2.getY() - RenderManager.renderPosY, vec2.getZ() - RenderManager.renderPosZ));
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        RenderUtil.post3D();
    }

    public void drawPath(Vec3 vec) {
    	if(mode.is("Box")) {
	        double x = vec.getX() - RenderManager.renderPosX;
	        double y = vec.getY() - RenderManager.renderPosY;
	        double z = vec.getZ() - RenderManager.renderPosZ;
	        double width = 0.3;
	        double height = mc.thePlayer.getEyeHeight();
	        RenderUtil.pre3D();
	        GL11.glLoadIdentity();
	        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
	        int colors[] = {Color.BLACK.getRGB(), Color.WHITE.getRGB()};
	        for (int i = 0; i < 2; i++) {
	        	RenderUtil.glColor(colors[i]);
	            GL11.glLineWidth(3 - i * 2);
	            GL11.glBegin(GL11.GL_LINE_STRIP);
	            GL11.glVertex3d(x - width, y, z - width);
	            GL11.glVertex3d(x - width, y, z - width);
	            GL11.glVertex3d(x - width, y + height, z - width);
	            GL11.glVertex3d(x + width, y + height, z - width);
	            GL11.glVertex3d(x + width, y, z - width);
	            GL11.glVertex3d(x - width, y, z - width);
	            GL11.glVertex3d(x - width, y, z + width);
	            GL11.glEnd();
	            GL11.glBegin(GL11.GL_LINE_STRIP);
	            GL11.glVertex3d(x + width, y, z + width);
	            GL11.glVertex3d(x + width, y + height, z + width);
	            GL11.glVertex3d(x - width, y + height, z + width);
	            GL11.glVertex3d(x - width, y, z + width);
	            GL11.glVertex3d(x + width, y, z + width);
	            GL11.glVertex3d(x + width, y, z - width);
	            GL11.glEnd();
	            GL11.glBegin(GL11.GL_LINE_STRIP);
	            GL11.glVertex3d(x + width, y + height, z + width);
	            GL11.glVertex3d(x + width, y + height, z - width);
	            GL11.glEnd();
	            GL11.glBegin(GL11.GL_LINE_STRIP);
	            GL11.glVertex3d(x - width, y + height, z + width);
	            GL11.glVertex3d(x - width, y + height, z - width);
	            GL11.glEnd();
	        }
	
	        RenderUtil.post3D();
    	}else if(mode.is("Line")) {
    		ArrayList<Vec3> trailList = new ArrayList<Vec3>();
    		for (Vec3 pos : path) {
    			trailList.add(new Vec3(pos.x, pos.y, pos.z));
    		}
    		
    		Vec3 lastLoc = null;
    		
    		for (Vec3 loc: trailList) {
    			
    			if (lastLoc == null) {
    				lastLoc = loc;
    			}else {
    				
    				if (mc.thePlayer.getDistance(loc.x, loc.y, loc.z) > 100) {
    					
    				}else {
    					
    					RenderUtils.glColor(Client.color);
    					ScaffoldRenderUtils.drawLine(lastLoc.x, lastLoc.y, lastLoc.z, loc.x, loc.y, loc.z);
    					
    				}
    				
    				lastLoc = loc;
    			}
    			
    		}
    	}
    }


    public boolean canReach(Timer timer) {
        long value = 10 * 1000L;
        return !timer.hasTimeElapsed(value, true);
    }
}
