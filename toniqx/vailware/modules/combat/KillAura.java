package toniqx.vailware.modules.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings.GameType;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventRotationMotion;
import toniqx.vailware.main.event.process.EventPacket;
import toniqx.vailware.main.event.process.EventRenderWorld;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.ColorUtil;
import toniqx.vailware.main.util.impl.MathUtils;
import toniqx.vailware.main.util.impl.RenderUtils;
import toniqx.vailware.main.util.impl.ScaffoldMovementUtils;
import toniqx.vailware.main.util.impl.ScaffoldRenderUtils;
import toniqx.vailware.main.util.impl.ScaffoldRotationUtils;
import toniqx.vailware.main.util.impl.Timer;
import toniqx.vailware.main.util.impl.render.TargetStrafeUtils;
import toniqx.vailware.main.util.impl.server.RotationUtils;
import toniqx.vailware.main.util.impl.server.angle.Angle;
import toniqx.vailware.main.util.impl.server.angle.AngleUtility;
import toniqx.vailware.main.util.impl.server.vector.impl.Vector3;
import toniqx.vailware.modules.Module;
import toniqx.vailware.modules.misc.Targets;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class KillAura extends Module {

    public static ModeSetting mode = new ModeSetting("Mode", "Switch","Switch", "Priority");
    public static ModeSetting sorting = new ModeSetting("Sorting", "Health","Health", "Angle", "Distance");
    public static ModeSetting autoblock = new ModeSetting("AutoBlock", "Fake","Fake", "Vanilla", "Hypixel", "None");
    public static ModeSetting type = new ModeSetting("Attack", "Pre","Pre", "Post");
    public static NumberSetting aps = new NumberSetting("APS", 11, 1, 20, 0.05);
    public static NumberSetting range = new NumberSetting("Range", 4.15,  3, 6,  0.05);
    public static NumberSetting blockRange = new NumberSetting("Block Range",  4.15, 3, 8, 0.05);
    public static NumberSetting switchDelay = new NumberSetting("Switch Delay", 250, 1, 750, 1);
    public static BooleanSetting critical = new BooleanSetting("Critical", false);
    public static BooleanSetting hvhMode = new BooleanSetting("HvH", false);
    public static BooleanSetting silent = new BooleanSetting("Silent", true);
    public static BooleanSetting render = new BooleanSetting("Render", true);
    public BooleanSetting rainbow = new BooleanSetting("Rainbow", false);
    public NumberSetting red = new NumberSetting("Red", 255, 1, 255, 1);
    public NumberSetting green = new NumberSetting("Green", 110, 1, 255, 1);
    public NumberSetting blue = new NumberSetting("Blue", 240, 1, 255, 1);
    public static BooleanSetting rotations = new BooleanSetting("Rotations", true);
    public static BooleanSetting teams = new BooleanSetting("Teams", false);
    public static BooleanSetting walls = new BooleanSetting("Walls", false);
    public static BooleanSetting disableOnDeath = new BooleanSetting("AutoDisable", true);

    public static boolean blocking;
    public float yaw1, pitch1;
    private int targetIndex;

    public EntityLivingBase target;

    private Timer attackTimer = new Timer();
    private Timer switchTimer = new Timer();
    
    private List<EntityLivingBase> targetList = new ArrayList<>();

    private final String[] strings = new String[]{"1st Killer - ", "1st Place - ", "You died! Want to play again? Click here!", " - Damage Dealt - ", "1st - ", "Winning Team - ", "Winners: ", "Winner: ", "Winning Team: ", " win the game!", "1st Place: ", "Last team standing!", "Winner #1 (", "Top Survivors", "Winners - "};
    public KillAura() {
        super("Killaura", "", Keyboard.KEY_NONE, Category.COMBAT);
        addSettings(mode, sorting, autoblock, type, aps, range, blockRange, switchDelay, red, green, blue, rainbow, render, hvhMode, silent, rotations, teams, walls, disableOnDeath);
    }

    @Override
    public void onEnable() {
        targetList.clear();
        target = null;
        blocking = false;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        targetList.clear();
        target = null;
        blocking = false;
    }

    public void onEvent(Event e) {
    	if(e instanceof EventRenderWorld && render.enabled) {
    		drawCircle(mc.thePlayer, mc.timer.elapsedPartialTicks, range.getValue());
    	}
    	
    	if(mc.thePlayer.isDead && disableOnDeath.enabled) {
			if (this.isEnabled())
				this.toggle();
    	}
    	super.mname = ChatFormatting.GRAY + "R" + " " + range.getValue() + " " + "|" + " " + "APS" + " " + aps.getValue() + "   ";
    	
		if (e instanceof EventPacket && critical.enabled) {
			if(Criticals.smode.is("HvH") && Client.getModule("Criticals").isEnabled()) {
				return;
			}else {
				Packet packet = ((EventPacket)e).packet;
					
				if (packet instanceof C02PacketUseEntity) {
						
					C02PacketUseEntity attack = (C02PacketUseEntity) packet;
						
					if (attack.getAction() == Action.ATTACK) {
							
						double[] crits = new double[] {0.11, 0.1100013579, 0.1090013579};
							
						for(short i = 0; i < crits.length; i++) {
							double offset = crits[i];
							mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
						}
				            
						try {
							mc.thePlayer.onCriticalHit(mc.theWorld.getEntityByID(attack.getEntityId()));
						} catch (Exception e2) {
							
						}
				            
						}
						
					}
				}
			}
    	
    	if (e instanceof EventPacket) {
    		if (e.isIncoming()) {
    			if (((EventPacket) e).getPacket() instanceof S02PacketChat && disableOnDeath.isEnabled()) {
    				for (String string : strings) {
    					if (((S02PacketChat) ((EventPacket) e).getPacket()).getChatComponent().getUnformattedText().contains(string))
    						if (this.isEnabled())
    							this.toggle();
    				}
    			}
    		}
    	}
    	if (e instanceof EventRotationMotion) {
    		if(Client.getModule("Scaffold").isEnabled())
    			return;
    		if(AutoGapple.isEating())
    			return;
    		
    		collectTargets();
	
    		sortTargets();
		
    		if (switchTimer.hasTimeElapsed((long) switchDelay.getValue(), true) && mode.getMode().equals("Switch")) {
    			targetIndex++;
    		}
		
    		if (targetIndex >= targetList.size())
    			targetIndex = 0;
    		
    		target = !targetList.isEmpty() && targetIndex < targetList.size() ? targetList.get(targetIndex) : null;
		
    		if (!isHoldingSword())
    			blocking = false;
	
    		if (target == null) {
    			if (blocking)
    				unblock();
    			return;
    		}
	
    		switch (e.getType()) {
    		case PRE:
    			if(rotations.enabled) {
    	    		if(Client.getModule("Scaffold").isEnabled())
    	    			return;
    				EventRotationMotion event = (EventRotationMotion) e;
	    			float yaw = ScaffoldRotationUtils.getRotations(target)[0];
	    			float pitch = ScaffoldRotationUtils.getRotations(target)[1];
	    			if (!silent.isEnabled()) {
	    				mc.thePlayer.rotationYaw = yaw;
	    				mc.thePlayer.rotationPitch = pitch;
	    			}else {
	    				final AngleUtility angleUtil = new AngleUtility(10, 190, 10, 10);

	                    Vector3<Double> targetPos= new Vector3<>(target.posX, target.posY, target.posZ);
	                    Vector3<Double> playerPos= new Vector3<>(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);

	                    Angle distance = angleUtil.calculateAngle(targetPos, playerPos);

	                    yaw1 = distance.getYaw();
	                    pitch1 = distance.getPitch();

	                    event.setYaw(yaw1);
	                    event.setPitch(pitch1);
	                    
	    				ScaffoldRenderUtils.setCustomPitch(event.pitch);
	    				ScaffoldRenderUtils.setCustomYaw(event.yaw);
	            		ScaffoldRenderUtils.SetCustomPitch = true;
	            		ScaffoldRenderUtils.SetCustomYaw = true;
	    			}
    			}else {
    	    		if(Client.getModule("Scaffold").isEnabled())
    	    			return;
	    			float yaw = ScaffoldRotationUtils.getRotations(target)[0];
	    			float pitch = ScaffoldRotationUtils.getRotations(target)[1];
    				ScaffoldRenderUtils.setCustomPitch(pitch);
    				ScaffoldRenderUtils.setCustomYaw(yaw);
            		ScaffoldRenderUtils.SetCustomPitch = true;
            		ScaffoldRenderUtils.SetCustomYaw = true;
    			}
		
    			if(type.is("Pre")) {
    	    		if(Client.getModule("Scaffold").isEnabled())
    	    			return;
	    			if (attackTimer.hasTimeElapsed((long) (1000 / aps.getValue() - MathUtils.randomNumber(1F, 0F)), true)) {
	    				if (isValid(target, false)) {
		
	    					MovingObjectPosition ray = RotationUtils.rayCast(mc.thePlayer,target.posX,target.posY + target.getEyeHeight(),target.posZ);
		
	    					if (ray != null) {
	    						Entity entityHit = ray.entityHit;
	    						if (entityHit instanceof EntityLivingBase)
	    							if (isValid((EntityLivingBase) entityHit, false))
	    								target = (EntityLivingBase) entityHit;
	    					}
	
	    					if (isHoldingSword())
	    						unblock();
	    					
	    					if(!Client.getModule("KeepSprint").isEnabled()) {
	    						mc.thePlayer.setSprinting(false);
	    					}
	    					mc.thePlayer.swingItem();
	    					if(Client.getModule("KeepSprint").isEnabled()) {
                                attack(target);
                            } else {
                                mc.playerController.attackEntity(mc.thePlayer, target);
                            }
	    				}
	    			}
    			}
    			break;
    		case POST:
        		if(Client.getModule("Scaffold").isEnabled())
        			return;
    			if(type.is("Post")) {
	    			if (attackTimer.hasTimeElapsed((long) (1000 / aps.getValue() - MathUtils.randomNumber(1F, 0F)), true)) {
	    				if (isValid(target, false)) {
		
	    					MovingObjectPosition ray = RotationUtils.rayCast(mc.thePlayer,target.posX,target.posY + target.getEyeHeight(),target.posZ);
		
	    					if (ray != null) {
	    						Entity entityHit = ray.entityHit;
	    						if (entityHit instanceof EntityLivingBase)
	    							if (isValid((EntityLivingBase) entityHit, false))
	    								target = (EntityLivingBase) entityHit;
	    					}
	
	    					if (isHoldingSword())
	    						unblock();
	    					
	    					if(!Client.getModule("KeepSprint").isEnabled()) {
	    						mc.thePlayer.setSprinting(false);
	    					}
	    					mc.thePlayer.swingItem();
	    					if(Client.getModule("KeepSprint").isEnabled()) {
                                attack(target);
                            } else {
                                mc.playerController.attackEntity(mc.thePlayer, target);
                            }
	    				}
	    			}
    			}
    			if (isHoldingSword() && !autoblock.is("None"))
    				block();
    		case BEFOREPRE:
    			break;
    		}
    	}
    }

    private void attack(EntityLivingBase entityLivingBase) {
        final float sharpLevel = EnchantmentHelper.func_152377_a(mc.thePlayer.getHeldItem(), EnumCreatureAttribute.UNDEFINED);

        if (sharpLevel > 0.0F)
            mc.thePlayer.onEnchantmentCritical(entityLivingBase);

        mc.getNetHandler().getNetworkManager().sendPacket(new C02PacketUseEntity(entityLivingBase, C02PacketUseEntity.Action.ATTACK));
    }

    public boolean sendUseItem(EntityPlayer playerIn, World worldIn, ItemStack itemStackIn)
    {
    	
        if (mc.playerController.currentGameType == GameType.SPECTATOR)
        {
            return false;
        }
        else
        {
        	
        	if (itemStackIn == null) {
        		return false;
        	}
        	
            mc.playerController.syncCurrentPlayItem();
            int i = itemStackIn.stackSize;
            ItemStack itemstack = itemStackIn.useItemRightClick(worldIn, playerIn);

            if (itemstack != itemStackIn || itemstack != null && itemstack.stackSize != i)
            {
                playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = itemstack;

                if (itemstack.stackSize == 0)
                {
                    playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = null;
                }

                return true;
            }
            else
            {
                return false;
            }
        }
    }
    
    private void block() {
    	if(autoblock.is("Fake")) {
    		blocking = true;
    	}else if(autoblock.is("Vanilla")) {
    		mc.thePlayer.setItemInUse(mc.thePlayer.getCurrentEquippedItem(), (int) Client.killAura.aps.getValue());
    	}else if(autoblock.is("Hypixel")) {
    		
    		if (Client.killAura.target == null) {
    			return;
    		}
    		
    		sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
    			
    		float[] rotations = ScaffoldRotationUtils.getRotations(Client.killAura.target);
    		mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
    		
    	}
    }

    public void unblock() {
    	try {
			if (autoblock.is("Hypixel") && mc.thePlayer.inventory.getCurrentItem().getItem() != null && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
				mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-0.8, -0.8, -0.8), EnumFacing.DOWN));
	            mc.gameSettings.keyBindUseItem.pressed = false;
	            
	        }
		} catch (NullPointerException e) {
			blocking = false;
		}
        blocking = false;
    }

	private void drawCircle(Entity entity, float partialTicks, double rad) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		TargetStrafeUtils.startSmooth();
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glLineWidth(1.0f);
		GL11.glBegin(GL11.GL_LINE_STRIP);

		final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX;
		final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY;
		final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ;
	 	      
		final float r = ((float) 1 / 255) * Color.WHITE.getRed();
		final float g = ((float) 1 / 255) * Color.WHITE.getGreen();
		final float b = ((float) 1 / 255) * Color.WHITE.getBlue();

		final double pix2 = Math.PI * 2.0D;
	
		for (int i = 0; i <= 90; ++i) {
			if(rainbow.enabled) {
				RenderUtils.glColor(ColorUtil.getRainbow(12, 0.75f, 1f, 5));
			}else {
				GlStateManager.color((float) red.getValue() / 255, (float) green.getValue() / 255, (float) blue.getValue() / 255);
			}
			GL11.glVertex3d(x + rad * Math.cos(i * pix2 / 45.0), y, z + rad * Math.sin(i * pix2 / 45.0));
		}

		GL11.glEnd();
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		TargetStrafeUtils.endSmooth();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}
    
    private void sortTargets() {
        switch (sorting.getMode()) {
            case "Angle":
                targetList.sort(
                        Comparator.comparingDouble(
                                RotationUtils::getAngleChange));
                break;
            case "Distance":
                targetList.sort(
                        Comparator.comparingDouble(
                                RotationUtils::getDistanceToEntity));
                break;
            case "Health":
                targetList.sort(
                        Comparator.comparingDouble(
                                EntityLivingBase::getHealth));
                break;
        }
    }

    private void collectTargets() {
        targetList.clear();

        for (Entity entity : mc.thePlayer.getEntityWorld().loadedEntityList) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                if (isValid(entityLivingBase, true))
                    targetList.add(entityLivingBase);
            }
        }
    }

    private boolean isHoldingSword() {
        return mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword;
    }

    private boolean isValid(EntityLivingBase entityLivingBase, boolean blocking) {
        if (entityLivingBase == mc.thePlayer || entityLivingBase.isDead || entityLivingBase.getDistanceToEntity(mc.thePlayer) > (blocking ? blockRange.getValue() : range.getValue()) || entityLivingBase.getHealth() == 0 || entityLivingBase.isInvisible() || entityLivingBase.ticksExisted < 10 || entityLivingBase instanceof EntityArmorStand)
            return false;

        if (!entityLivingBase.canEntityBeSeen(mc.thePlayer) && !walls.isEnabled())
            return false;

        if (entityLivingBase instanceof EntityPlayer && !Targets.target.is("Player"))
            return false;
        
        if(entityLivingBase.getName().contains("NPC"))
        	return false;

        if (RotationUtils.isOnSameTeam(entityLivingBase) && teams.isEnabled())
            return false;
        
        if ((entityLivingBase instanceof EntityMob || entityLivingBase instanceof EntityAmbientCreature || entityLivingBase instanceof EntityWaterMob) && !Targets.target.is("Mob"))
            return false;

        if (entityLivingBase instanceof EntityAnimal && !Targets.target.is("Animal"))
            return false;

        if (entityLivingBase instanceof EntityGolem)
            return true;
     
        return !(entityLivingBase instanceof EntityVillager);
    }
    
}
