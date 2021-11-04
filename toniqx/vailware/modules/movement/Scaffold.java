package toniqx.vailware.modules.movement;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.world.World;
import toniqx.vailware.Client;
import toniqx.vailware.main.bypass.Rotation;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventRender3D;
import toniqx.vailware.main.event.listeners.EventRenderGUI;
import toniqx.vailware.main.event.listeners.EventRotationMotion;
import toniqx.vailware.main.event.process.EventRenderWorld;
import toniqx.vailware.main.event.process.EventSafeWalk;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.MathUtils;
import toniqx.vailware.main.util.impl.RenderUtils;
import toniqx.vailware.main.util.impl.ScaffoldRenderUtils;
import toniqx.vailware.main.util.impl.SetBlockAndFacingUtils;
import toniqx.vailware.main.util.impl.Timer;
import toniqx.vailware.main.util.impl.TimerUtils;
import toniqx.vailware.main.util.impl.movement.MovementUtil;
import toniqx.vailware.main.util.impl.render.EspUtil;
import toniqx.vailware.main.util.impl.render.RenderUtil;
import toniqx.vailware.main.util.impl.server.RotationUtils;
import toniqx.vailware.main.util.impl.server.Vec3d;
import toniqx.vailware.modules.Module;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

public class Scaffold extends Module {
	private static final BlockPos[] BLOCK_POSITIONS = new BlockPos[]{new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1)};

    private static final EnumFacing[] FACINGS = new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST, EnumFacing.SOUTH, EnumFacing.NORTH};


    private ModeSetting scaffoldmode = new ModeSetting("Mode", "Normal", "Normal", "AAC");
    private ModeSetting towerMode = new ModeSetting("Tower Mode", "NCP", "None", "Packet", "NCP");

    private BooleanSetting keeprots = new BooleanSetting("Keep Rots", true);
    private BooleanSetting downwards = new BooleanSetting("Downwards", false);

    private NumberSetting delay = new NumberSetting("Delay", 0, 0, 1000, 10);

    private NumberSetting timerSpeed = new NumberSetting("Timer Speed", 4, 1, 10, 1);
    private NumberSetting expandDistance = new NumberSetting("Expand Value", 0.5, 0.1, 5, 0.1);

    private BooleanSetting timerboost = new BooleanSetting("TimerBoost", false);
    private BooleanSetting safewalk = new BooleanSetting("Safewalk", true);
    private BooleanSetting tower = new BooleanSetting("Tower", false);
    private BooleanSetting towermove = new BooleanSetting("Tower Move", false);
    private BooleanSetting swing = new BooleanSetting("Swing", true);
    private BooleanSetting sprint = new BooleanSetting("Sprint", true);
    private BooleanSetting keepY = new BooleanSetting("KeepY", false);
    private BooleanSetting rayCast = new BooleanSetting("RayCast", true);
    private BooleanSetting esp = new BooleanSetting("Indicator", true);
    private BooleanSetting draw = new BooleanSetting("Render", true);

    private float[] rotations = new float[2];
    private static final Map<Integer, Boolean> glCapMap = new HashMap<>();
    private List<Block> badBlocks = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.trapped_chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.tallgrass, Blocks.tripwire, Blocks.tripwire_hook, Blocks.rail, Blocks.waterlily, Blocks.red_flower, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.vine, Blocks.trapdoor, Blocks.yellow_flower, Blocks.ladder, Blocks.furnace, Blocks.sand, Blocks.cactus, Blocks.dispenser, Blocks.noteblock, Blocks.dropper, Blocks.crafting_table, Blocks.web, Blocks.pumpkin, Blocks.sapling, Blocks.cobblestone_wall, Blocks.oak_fence);
    private BlockData blockData;

    public static boolean isPlaceTick = false;
    public static boolean stopWalk = false;
    private double startY;
    public Timer towerTimer = new Timer();
    private BlockPos currentPos;
    private EnumFacing currentFacing;
    private boolean rotated = false;
    private Timer timer = new Timer();
    private TimerUtils slotTimer = new TimerUtils();
    private Timer boostTimer = new Timer();
    private Timer reduceTimer = new Timer();
    private boolean placing;
    private BlockData lastBlockData;
    private RotationUtils rotationUtils;
    float yaw = 0;
    float pitch = 0;
    int ticks = 0;

    public Scaffold() {
        super("Scaffold", "", Keyboard.KEY_NONE, Category.MOVEMENT);
        this.addSettings(scaffoldmode, towerMode, keeprots, downwards, delay, timerboost, timerSpeed, safewalk, draw, tower, sprint, towermove, swing, keepY, rayCast, esp);
    }

    public void onEnable() {
        timer.reset();
        boostTimer.reset();
        slotTimer.reset();
        reduceTimer.reset();
        ticks = 0;
        lastBlockData = null;
        startY = mc.thePlayer.posY;
    }

    public void onEvent(Event e) {

        if(e instanceof EventRenderGUI) {
            if(esp.isEnabled()) {
            	
            	int blocks = getBlockCount();
            	String BlocksLeft = blocks + "";
            	
                mc.FontRendererClean.drawCenteredString(BlocksLeft, GuiScreen.width / 2, GuiScreen.height / 2 + 10, -1);
            }
        }
        
        if(e instanceof EventRenderWorld) {
        	if(draw.enabled) {
        		
        	}
        }

        if(e instanceof EventSafeWalk) {
            if(safewalk.isEnabled()) {
                e.setCancelled(true);
            }
        }

        if(e instanceof EventRotationMotion) {
            if(e.isPre()) {

            }

            if(scaffoldmode.is("Expand")) {
            	EventRotationMotion event = (EventRotationMotion) e;
                double addition = expandDistance.getValue();
                final double x2 = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0f));
                final double z2 = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0f));
                final double xOffset = MovementInput.moveForward * addition * x2 + MovementInput.moveStrafe * addition * z2;
                final double zOffset = MovementInput.moveForward * addition * z2 - MovementInput.moveStrafe * addition * x2;

                BlockPos blockBelow = new BlockPos(mc.thePlayer.posX + xOffset, mc.thePlayer.posY - 1, mc.thePlayer.posZ + zOffset);
                BlockData blockEntry = mc.theWorld.getBlockState(blockBelow).getBlock() == Blocks.air ? blockEntry = getBlockData2(blockBelow) : null;

                placing = mc.thePlayer.ticksExisted % 3 == 0;
                if (blockEntry == null) {
                    if (lastBlockData != null && event.isPre()) {
                        float[] rotations = getRotationsNeeded(lastBlockData);
                        event.setPitch(rotations[1]);
                        event.setYaw(rotations[0]);
                    }
                }
                if (blockEntry == null)
                    return;
                if (event.isPre()) {
                    float[] rotations = getRotationsNeeded(blockEntry);
                    event.setPitch(rotations[1]);
                    event.setYaw(rotations[0]);
                } else {
                    if (getBlockCount() <= 0) {
                        return;
                    }
                    final int heldItem = mc.thePlayer.inventory.currentItem;
                    boolean hasBlock = false;
                    for (int i = 0; i < 9; ++i) {
                        ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
                        if (itemStack != null && itemStack.stackSize != 0 && itemStack.getItem() instanceof ItemBlock && !badBlocks.contains(((ItemBlock) mc.thePlayer.inventory.getStackInSlot(i).getItem()).getBlock())) {
                            mc.thePlayer.sendQueue.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem = i));
                            hasBlock = true;
                            break;
                        }
                    }
                    if (!hasBlock) {
                        for (int i = 0; i < 45; ++i) {
                            if (mc.thePlayer.inventory.getStackInSlot(i) != null && mc.thePlayer.inventory.getStackInSlot(i).stackSize != 0
                                    && mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock
                                    && !badBlocks.contains(
                                    ((ItemBlock) mc.thePlayer.inventory.getStackInSlot(i).getItem()).getBlock())) {
                                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 8, 2,
                                        mc.thePlayer);
                                break;
                            }
                        }
                    }

                    if (tower.isEnabled()) {
                        if (mc.gameSettings.keyBindJump.isKeyDown() && !mc.thePlayer.isPotionActive(Potion.jump)) {
                            if (!MovementUtil.isMoving()) {
                                mc.thePlayer.motionY = 0.42f;
                                mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
                            } else {
                                if (mc.thePlayer.onGround && towermove.isEnabled()) {
                                    mc.thePlayer.motionY = 0.42f;
                                } else if (mc.thePlayer.motionY < 0.17D && mc.thePlayer.motionY > 0.16D && towermove.isEnabled()) {
                                    mc.thePlayer.motionY = -0.01f;
                                }
                            }
                        }
                    }

                    mc.playerController.onPlayerRightClick3d(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), blockEntry.position.add(0, 0, 0), blockEntry.face, new Vec3d(blockEntry.position.getX(), blockEntry.position.getY(), blockEntry.position.getZ()));
                    lastBlockData = blockEntry;


                    if (!swing.isEnabled()) {
                        mc.thePlayer.sendQueue.sendPacketNoEvent(new C0APacketAnimation());
                    } else {
                        mc.thePlayer.swingItem();
                    }

                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem = heldItem));
                }

            } else {
                if(e.isPre()) {
                    EventRotationMotion event = (EventRotationMotion) e;


                    if(scaffoldmode.is("Normal")) {
                    	try {
                    		int slot = this.getSlot();
	                        this.stopWalk = (getBlockCount() == 0 || slot == -1);
	                        this.isPlaceTick = keeprots.isEnabled() ? blockData != null && slot != -1 : blockData != null && slot != -1 && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).add(0, -1, 0)).getBlock() == Blocks.air;
	                        if (slot == -1) {
	                            moveBlocksToHotbar();
	
	                            return;
	                        }
	
	                        this.blockData = getBlockData();
	                        if (this.blockData == null) {
	                            return;
	                        }
	
	                        if(timerboost.isEnabled()) {
	                            if(boostTimer.hasTimeElapsed(2000L, true)) {
	                                mc.timer.timerSpeed = (float) timerSpeed.getValue();
	                                reduceTimer.reset();
	                            } else if(reduceTimer.hasTimeElapsed(100L, true)) {
	                                mc.timer.timerSpeed = 1.0f;
	                            }
	                        } else {
	                           // mc.timer.timerSpeed = 1.0f;
	                        }
                		}catch(Exception e1){
                		}



                        if (mc.gameSettings.keyBindJump.isKeyDown() && tower.isEnabled() && (this.towermove.isEnabled() || !MovementUtil.isMoving()) && !mc.thePlayer.isPotionActive(Potion.jump) && !keepY.isEnabled()) {
                            if (towerMode.is("Packet")) {
                                if (mc.thePlayer.onGround) {
                                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.99, mc.thePlayer.posZ);
                                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.41999998688698, mc.thePlayer.posZ, false));
                                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.7531999805212, mc.thePlayer.posZ, false));
                                }
                            }

                            if(towerMode.is("NCP")) {
                                if (!MovementUtil.isOnGround(0.79) || mc.thePlayer.onGround) {
                                    mc.thePlayer.motionY = 0.41985;
                                }
                                if(towerTimer.hasTimeElapsed(1500, true)){
                                    mc.thePlayer.motionY = -1;
                                }
                            }
                        } else {
                            towerTimer.reset();
                        }

                        if (this.isPlaceTick) {
                            Rotation targetRotation = new Rotation(SetBlockAndFacingUtils.BlockUtil.getDirectionToBlock(blockData.getPosition().getX(), blockData.getPosition().getY(), blockData.getPosition().getZ(), blockData.getFacing())[0], 79.44f);
                            Rotation limitedRotation = SetBlockAndFacingUtils.BlockUtil.limitAngleChange(new Rotation(yaw, event.getPitch()), targetRotation, (float) ThreadLocalRandom.current().nextDouble(20, 30));
                            yaw = limitedRotation.getYaw();
                            pitch = limitedRotation.getPitch();
                            event.setYaw(yaw);
                            event.setPitch(79.44f);
                            ScaffoldRenderUtils.CustomYaw = yaw;
                            ScaffoldRenderUtils.CustomPitch = pitch;
                            ScaffoldRenderUtils.SetCustomYaw = true;
                            ScaffoldRenderUtils.SetCustomPitch = true;
                            if(sprint.enabled) {
                            	if(mc.thePlayer.moveForward > 0 && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally) {
                            		mc.thePlayer.setSprinting(true);
                            	}
                            }else {
                            	mc.thePlayer.setSprinting(false);
                            }
                        }
                    } else if(scaffoldmode.is("Dev")) {

                        rotated = false;
                        currentPos = null;
                        currentFacing = null;

                        BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ);
                        if (mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) {
                            setBlockAndFacing(pos);

                            if (currentPos != null) {
                                float facing[] = SetBlockAndFacingUtils.BlockUtil.getDirectionToBlock(currentPos.getX(), currentPos.getY(), currentPos.getZ(), currentFacing);

                                float yaw = facing[0] + randomNumber(3, -3);
                                float pitch = Math.min(90, facing[1] + 9 + randomNumber(3, -3));

                                rotations[0] = yaw;
                                rotations[1] = pitch;

                                rotated = !rayCast.isEnabled() || rayTrace(yaw, pitch);

                                event.setYaw(yaw);
                                event.setPitch(79.44f);
                            }
                        } else {
                            if (keeprots.isEnabled()) {

                                event.setYaw(rotations[0]);
                                event.setPitch(rotations[1]);
                                
                                ScaffoldRenderUtils.CustomYaw = rotations[0];
                                ScaffoldRenderUtils.CustomPitch = rotations[1];
                            }
                        }
                        mc.thePlayer.rotationYawHead = event.getYaw();
                        mc.thePlayer.renderYawOffset = event.getYaw();
                    }else if(scaffoldmode.is("AAC")) {
                    	try {
                    		int slot = this.getSlot();
	                        this.stopWalk = (getBlockCount() == 0 || slot == -1);
	                        this.isPlaceTick = keeprots.isEnabled() ? blockData != null && slot != -1 : blockData != null && slot != -1 && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).add(0, -1, 0)).getBlock() == Blocks.air;
	                        if (slot == -1) {
	                            moveBlocksToHotbar();
	
	                            return;
	                        }
	
	                        this.blockData = getBlockData();
	                        if (this.blockData == null) {
	                            return;
	                        }
	
	                        if(timerboost.isEnabled()) {
	                            if(boostTimer.hasTimeElapsed(2000L, true)) {
	                                mc.timer.timerSpeed = (float) timerSpeed.getValue();
	                                reduceTimer.reset();
	                            } else if(reduceTimer.hasTimeElapsed(100L, true)) {
	                                mc.timer.timerSpeed = 1.0f;
	                            }
	                        } else {
	                           // mc.timer.timerSpeed = 1.0f;
	                        }
                		}catch(Exception e1){
                		}



                        if (mc.gameSettings.keyBindJump.isKeyDown() && tower.isEnabled() && (this.towermove.isEnabled() || !MovementUtil.isMoving()) && !mc.thePlayer.isPotionActive(Potion.jump) && !keepY.isEnabled()) {
                            if (towerMode.is("Packet")) {
                                if (mc.thePlayer.onGround) {
                                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.99, mc.thePlayer.posZ);
                                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.41999998688698, mc.thePlayer.posZ, false));
                                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.7531999805212, mc.thePlayer.posZ, false));
                                }
                            }

                            if(towerMode.is("NCP")) {
                                if (!MovementUtil.isOnGround(0.79) || mc.thePlayer.onGround) {
                                    mc.thePlayer.motionY = 0.41985;
                                }
                                if(towerTimer.hasTimeElapsed(1500, true)){
                                    mc.thePlayer.motionY = -1;
                                }
                            }
                        } else {
                            towerTimer.reset();
                        }

                        if (this.isPlaceTick) {
                            Rotation targetRotation = new Rotation(SetBlockAndFacingUtils.BlockUtil.getDirectionToBlock(blockData.getPosition().getX(), blockData.getPosition().getY(), blockData.getPosition().getZ(), blockData.getFacing())[0], 79.44f);
                            Rotation limitedRotation = SetBlockAndFacingUtils.BlockUtil.limitAngleChange(new Rotation(yaw, event.getPitch()), targetRotation, (float) ThreadLocalRandom.current().nextDouble(20, 30));
                            yaw = limitedRotation.getYaw();
                            pitch = limitedRotation.getPitch();
                            event.setYaw((float) (yaw + MathUtils.randomNumber(0.2f, -0.2f)));
                            event.setPitch(pitch);
                            ScaffoldRenderUtils.CustomYaw = (float) (yaw + MathUtils.randomNumber(0.2f, -0.2f));
                            ScaffoldRenderUtils.CustomPitch = pitch;
                            ScaffoldRenderUtils.SetCustomYaw = true;
                            ScaffoldRenderUtils.SetCustomPitch = true;
                            if(sprint.enabled) {
                            	if(mc.thePlayer.moveForward > 0 && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally) {
                            		mc.thePlayer.setSprinting(true);
                            	}
                            }else {
                            	mc.thePlayer.setSprinting(false);
                            }
                        }
                    }
                } else {
                    if(scaffoldmode.is("Normal")) {
                        int slot = this.getSlot();
                        BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
                        if (slot != -1 && this.blockData != null) {
                            final int currentSlot = mc.thePlayer.inventory.currentItem;
                            if (pos.getBlock() instanceof BlockAir) {
                                mc.thePlayer.inventory.currentItem = slot;
                                if (this.getPlaceBlock(this.blockData.getPosition(), this.blockData.getFacing())) {
                                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(currentSlot));
                                }
                            }else{
                                MovementUtil.setMotion(MovementUtil.getSpeed() - MovementUtil.getSpeed() / 50);
                            }

                            mc.thePlayer.inventory.currentItem = currentSlot;
                        }
                       
                    } else if(scaffoldmode.is("Dev")) {
                        for (int i = 0; i < 9; i++) {
                            if (mc.thePlayer.inventory.getStackInSlot(i) == null)
                                continue;
                            if (mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock) {
                                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem = i));
                            }
                        }
                        if (currentPos != null) {
                            if (timer.hasTimeElapsed((long) this.delay.getValue(), true) && rotated) {
                                if (mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock) {
                                    if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), currentPos, currentFacing, new Vec3(currentPos.getX() * 0.5, currentPos.getY() * 0.5, currentPos.getZ() * 0.5))) {
                                        timer.reset();
                                        if (swing.isEnabled()) {
                                            mc.thePlayer.swingItem();
                                        } else {
                                            mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                                        }
                                    }
                                }
                            }
                        }
                    } else if(scaffoldmode.is("AAC")) {
                        int slot = this.getSlot();
                        BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
                        if (slot != -1 && this.blockData != null) {
                            final int currentSlot = mc.thePlayer.inventory.currentItem;
                            if (pos.getBlock() instanceof BlockAir) {
                                mc.thePlayer.inventory.currentItem = slot;
                                if (this.getPlaceBlock(this.blockData.getPosition(), this.blockData.getFacing())) {
                                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(currentSlot));
                                }
                            }else{
                                MovementUtil.setMotion(MovementUtil.getSpeed() - MovementUtil.getSpeed() / 50);
                            }

                            mc.thePlayer.inventory.currentItem = currentSlot;
                        }
                       
                    }
                }
            }
        }
    }

    public static float[] getRotationsNeeded(final BlockData data) {
        final EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        final Vec3 hitVec = data.hitVec;

        final double xDist = hitVec.xCoord - player.posX;
        final double zDist = hitVec.zCoord - player.posZ;

        final double yDist = hitVec.yCoord - (player.posY + player.getEyeHeight());
        final double fDist = MathHelper.sqrt_double(xDist * xDist + zDist * zDist);
        final float rotationYaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
        final float var1 = MovementUtil.getMovementDirection() - 180.0F;

        final float yaw = rotationYaw + MathHelper.wrapAngleTo180_float(var1 - rotationYaw);
        final float rotationPitch = Minecraft.getMinecraft().thePlayer.rotationPitch;

        if (data.face != EnumFacing.DOWN && data.face != EnumFacing.UP) {
            final double yDistFeet = hitVec.yCoord - player.posY;
            final double totalAbsDist = Math.abs(xDist * xDist + yDistFeet * yDistFeet + zDist * zDist);

            if (totalAbsDist < 1.0)
                return new float[]{yaw, (float) MathUtils.getRandomInRange(80, 90)};
        }

        final float var2 = (float) (-(StrictMath.atan2(yDist, fDist) * 180.0D / Math.PI));
        final float pitch = rotationPitch + MathHelper.wrapAngleTo180_float(var2 - rotationPitch);

        return new float[]{yaw, MathHelper.clamp_float(pitch, -90.0F, 90.0F)};
    }

    private static BlockData getBlockData2(final BlockPos pos) {
        final BlockPos[] blockPositions = BLOCK_POSITIONS;
        final EnumFacing[] facings = FACINGS;
        final WorldClient world = Minecraft.getMinecraft().theWorld;

        for (int i = 0; i < blockPositions.length; i++) {
            final BlockPos blockPos = pos.add(blockPositions[i]);
            if (isValidBlock(world.getBlockState(blockPos).getBlock(), false)) {
                final BlockData data = new BlockData(blockPos, facings[i]);
                if (validateBlockRange(data))
                    return data;
            }
        }

        final BlockPos posBelow = pos.add(0, -1, 0);
        if (isValidBlock(world.getBlockState(posBelow).getBlock(), false)) {
            final BlockData data = new BlockData(posBelow, EnumFacing.UP);
            if (validateBlockRange(data))
                return data;
        }

        for (BlockPos blockPosition : blockPositions) {
            final BlockPos blockPos = pos.add(blockPosition);
            for (int i = 0; i < blockPositions.length; i++) {
                final BlockPos blockPos1 = blockPos.add(blockPositions[i]);
                if (isValidBlock(world.getBlockState(blockPos1).getBlock(), false)) {
                    final BlockData data = new BlockData(blockPos1, facings[i]);
                    if (validateBlockRange(data))
                        return data;
                }
            }
        }

        for (final BlockPos blockPosition : blockPositions) {
            final BlockPos blockPos = pos.add(blockPosition);
            for (final BlockPos position : blockPositions) {
                final BlockPos blockPos1 = blockPos.add(position);
                for (int i = 0; i < blockPositions.length; i++) {
                    final BlockPos blockPos2 = blockPos1.add(blockPositions[i]);
                    if (isValidBlock(world.getBlockState(blockPos2).getBlock(), false)) {
                        final BlockData data = new BlockData(blockPos2, facings[i]);
                        if (validateBlockRange(data))
                            return data;
                    }
                }
            }
        }

        for (final BlockPos blackPosition : blockPositions) {
            final BlockPos blockPos = pos.add(blackPosition);
            for (final BlockPos blockPosition : blockPositions) {
                final BlockPos blockPos1 = blockPos.add(blockPosition);
                for (final BlockPos position : blockPositions) {
                    final BlockPos blockPos2 = blockPos1.add(position);
                    for (int i = 0; i < blockPositions.length; i++) {
                        final BlockPos blockPos3 = blockPos2.add(blockPositions[i]);
                        if (isValidBlock(world.getBlockState(blockPos3).getBlock(), false)) {
                            final BlockData data = new BlockData(blockPos3, facings[i]);
                            if (validateBlockRange(data))
                                return data;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static boolean validateBlockRange(final BlockData data) {
        final Vec3 pos = data.hitVec;
        if (pos == null)
            return false;
        final EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        final double x = (pos.xCoord - player.posX);
        final double y = (pos.yCoord - (player.posY + player.getEyeHeight()));
        final double z = (pos.zCoord - player.posZ);
        return StrictMath.sqrt(x * x + y * y + z * z) <= 5.0D;
    }


    public static boolean isValidBlock(Block block, boolean toPlace) {
        if (block instanceof BlockContainer)
            return false;
        if (toPlace) {
            return !(block instanceof BlockFalling) && block.isFullBlock() && block.isFullCube();
        } else {
            final Material material = block.getMaterial();
            return !material.isReplaceable() && !material.isLiquid();
        }
    }


    private boolean getPlaceBlock(final BlockPos pos, final EnumFacing facing) {
        if (timer.hasTimeElapsed((long) delay.getValue(), true)) {
            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, facing, getVec3(new BlockData(pos, facing)))) {
                if (this.swing.isEnabled()) {
                    mc.thePlayer.swingItem();
                } else {
                    mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                }

                timer.reset();
                return true;
            }


        }
        return false;
    }

    private Vec3 getVec3(BlockData data) {
        BlockPos pos = data.getPosition();
        EnumFacing face = data.getFacing();
        double x = (double) pos.getX() + 0.5D;
        double y = (double) pos.getY() + 0.5D;
        double z = (double) pos.getZ() + 0.5D;
        x += (double) face.getFrontOffsetX() / 2.0D;
        z += (double) face.getFrontOffsetZ() / 2.0D;
        y += (double) face.getFrontOffsetY() / 2.0D;
        if (face != EnumFacing.UP && face != EnumFacing.DOWN) {
            y += this.randomNumber(0.49D, 0.5D);
        } else {
            x += this.randomNumber(0.3D, -0.3D);
            z += this.randomNumber(0.3D, -0.3D);
        }

        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += this.randomNumber(0.3D, -0.3D);
        }

        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x += this.randomNumber(0.3D, -0.3D);
        }

        return new Vec3(x, y, z);
    }

    private double randomNumber(double max, double min) {
        return Math.random() * (max - min) + min;
    }

    private boolean rayTrace(float yaw, float pitch) {
        Vec3 vec3 = mc.thePlayer.getPositionEyes(1.0f);
        Vec3 vec31 = rotationUtils.getVectorForRotation(new float[]{yaw, pitch});
        Vec3 vec32 = vec3.addVector(vec31.xCoord * 5, vec31.yCoord * 5, vec31.zCoord * 5);

        MovingObjectPosition result = mc.theWorld.rayTraceBlocks(vec3, vec32, false);

        return result != null && result.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && currentPos.equals(result.getBlockPos());
    }

    static Random rng = new Random();

    public static int getRandom(final int floor, final int cap) {
        return floor + rng.nextInt(cap - floor + 1);
    }

    public void setBlockAndFacing(BlockPos var1) {
        if (this.mc.theWorld.getBlockState(var1.add(0, -1, 0)).getBlock() != Blocks.air) {
            currentPos = var1.add(0, -1, 0);
            currentFacing = EnumFacing.UP;
        } else if (this.mc.theWorld.getBlockState(var1.add(-1, 0, 0)).getBlock() != Blocks.air) {
            currentPos = var1.add(-1, 0, 0);
            currentFacing = EnumFacing.EAST;
        } else if (this.mc.theWorld.getBlockState(var1.add(1, 0, 0)).getBlock() != Blocks.air) {
            currentPos = var1.add(1, 0, 0);
            currentFacing = EnumFacing.WEST;
        } else if (this.mc.theWorld.getBlockState(var1.add(0, 0, -1)).getBlock() != Blocks.air) {

            currentPos = var1.add(0, 0, -1);
            currentFacing = EnumFacing.SOUTH;

        } else if (this.mc.theWorld.getBlockState(var1.add(0, 0, 1)).getBlock() != Blocks.air) {

            currentPos = var1.add(0, 0, 1);
            currentFacing = EnumFacing.NORTH;

        } else if (this.mc.theWorld.getBlockState(var1.add(-1, 0, -1)).getBlock() != Blocks.air) {
            currentPos = var1.add(-1, 0, -1);
            currentFacing = EnumFacing.EAST;
        } else if (this.mc.theWorld.getBlockState(var1.add(-1, 0, 1)).getBlock() != Blocks.air) {
            currentPos = var1.add(-1, 0, 1);
            currentFacing = EnumFacing.EAST;
        } else if (this.mc.theWorld.getBlockState(var1.add(1, 0, -1)).getBlock() != Blocks.air) {
            currentPos = var1.add(1, 0, -1);
            currentFacing = EnumFacing.WEST;
        } else if (this.mc.theWorld.getBlockState(var1.add(1, 0, 1)).getBlock() != Blocks.air) {
            currentPos = var1.add(1, 0, 1);
            currentFacing = EnumFacing.WEST;
        } else if (this.mc.theWorld.getBlockState(var1.add(-1, -1, 0)).getBlock() != Blocks.air) {
            currentPos = var1.add(-1, -1, 0);
            currentFacing = EnumFacing.EAST;
        } else if (this.mc.theWorld.getBlockState(var1.add(1, -1, 0)).getBlock() != Blocks.air) {
            currentPos = var1.add(1, -1, 0);
            currentFacing = EnumFacing.WEST;
        } else if (this.mc.theWorld.getBlockState(var1.add(0, -1, -1)).getBlock() != Blocks.air) {
            currentPos = var1.add(0, -1, -1);
            currentFacing = EnumFacing.SOUTH;
        } else if (this.mc.theWorld.getBlockState(var1.add(0, -1, 1)).getBlock() != Blocks.air) {
            currentPos = var1.add(0, -1, 1);
            currentFacing = EnumFacing.NORTH;
        } else if (this.mc.theWorld.getBlockState(var1.add(-1, -1, -1)).getBlock() != Blocks.air) {
            currentPos = var1.add(-1, -1, -1);
            currentFacing = EnumFacing.EAST;
        } else if (this.mc.theWorld.getBlockState(var1.add(-1, -1, 1)).getBlock() != Blocks.air) {
            currentPos = var1.add(-1, -1, 1);
            currentFacing = EnumFacing.EAST;
        } else if (this.mc.theWorld.getBlockState(var1.add(1, -1, -1)).getBlock() != Blocks.air) {
            currentPos = var1.add(1, -1, -1);
            currentFacing = EnumFacing.WEST;
        } else if (this.mc.theWorld.getBlockState(var1.add(1, -1, 1)).getBlock() != Blocks.air) {
            currentPos = var1.add(1, -1, 1);
            currentFacing = EnumFacing.WEST;
        } else if (this.mc.theWorld.getBlockState(var1.add(-2, 0, 0)).getBlock() != Blocks.air) {
            currentPos = var1.add(-2, 0, 0);
            currentFacing = EnumFacing.EAST;
        } else if (this.mc.theWorld.getBlockState(var1.add(2, 0, 0)).getBlock() != Blocks.air) {
            currentPos = var1.add(2, 0, 0);
            currentFacing = EnumFacing.WEST;
        } else if (this.mc.theWorld.getBlockState(var1.add(0, 0, -2)).getBlock() != Blocks.air) {
            currentPos = var1.add(0, 0, -2);
            currentFacing = EnumFacing.SOUTH;
        } else if (this.mc.theWorld.getBlockState(var1.add(0, 0, 2)).getBlock() != Blocks.air) {
            currentPos = var1.add(0, 0, 2);
            currentFacing = EnumFacing.NORTH;
        } else if (this.mc.theWorld.getBlockState(var1.add(-2, 0, -2)).getBlock() != Blocks.air) {
            currentPos = var1.add(-2, 0, -2);
            currentFacing = EnumFacing.EAST;
        } else if (this.mc.theWorld.getBlockState(var1.add(-2, 0, 2)).getBlock() != Blocks.air) {
            currentPos = var1.add(-2, 0, 2);
            currentFacing = EnumFacing.EAST;
        } else if (this.mc.theWorld.getBlockState(var1.add(2, 0, -2)).getBlock() != Blocks.air) {
            currentPos = var1.add(2, 0, -2);
            currentFacing = EnumFacing.WEST;
        } else if (this.mc.theWorld.getBlockState(var1.add(2, 0, 2)).getBlock() != Blocks.air) {
            currentPos = var1.add(2, 0, 2);
            currentFacing = EnumFacing.WEST;
        } else if (this.mc.theWorld.getBlockState(var1.add(0, 1, 0)).getBlock() != Blocks.air) {
            currentPos = var1.add(0, 1, 0);
            currentFacing = EnumFacing.DOWN;
        } else if (this.mc.theWorld.getBlockState(var1.add(-1, 1, 0)).getBlock() != Blocks.air) {
            currentPos = var1.add(-1, 1, 0);
            currentFacing = EnumFacing.EAST;
        } else if (this.mc.theWorld.getBlockState(var1.add(1, 1, 0)).getBlock() != Blocks.air) {
            currentPos = var1.add(1, 1, 0);
            currentFacing = EnumFacing.WEST;
        } else if (this.mc.theWorld.getBlockState(var1.add(0, 1, -1)).getBlock() != Blocks.air) {
            currentPos = var1.add(0, 1, -1);
            currentFacing = EnumFacing.SOUTH;
        } else if (this.mc.theWorld.getBlockState(var1.add(0, 1, 1)).getBlock() != Blocks.air) {
            currentPos = var1.add(0, 1, 1);
            currentFacing = EnumFacing.NORTH;
        } else if (this.mc.theWorld.getBlockState(var1.add(-1, 1, -1)).getBlock() != Blocks.air) {
            currentPos = var1.add(-1, 1, -1);
            currentFacing = EnumFacing.EAST;
        } else if (this.mc.theWorld.getBlockState(var1.add(-1, 1, 1)).getBlock() != Blocks.air) {
            currentPos = var1.add(-1, 1, 1);
            currentFacing = EnumFacing.EAST;
        } else if (this.mc.theWorld.getBlockState(var1.add(1, 1, -1)).getBlock() != Blocks.air) {
            currentPos = var1.add(1, 1, -1);
            currentFacing = EnumFacing.WEST;
        } else if (this.mc.theWorld.getBlockState(var1.add(1, 1, 1)).getBlock() != Blocks.air) {
            currentPos = var1.add(1, 1, 1);
            currentFacing = EnumFacing.WEST;
        }
    }

    public void onDisable() {
        super.onDisable();
        mc.timer.timerSpeed = 1.0f;
        this.setSneaking(false);
    }

    private void setSneaking(boolean b) {
        mc.gameSettings.keyBindSneak.pressed = b;
    }

    public BlockData getBlockData() {
        final EnumFacing[] invert = {EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST};
        double yValue = 0;
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && !mc.gameSettings.keyBindJump.isKeyDown() && downwards.isEnabled()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
            yValue -= 0.6;
        }
        BlockPos aa = new BlockPos(mc.thePlayer.getPositionVector()).offset(EnumFacing.DOWN).add(0, yValue, 0);
        BlockPos playerpos = aa;

        boolean tower = !this.towermove.isEnabled() && this.tower.isEnabled() && !MovementUtil.isMoving();
        if (!this.downwards.isEnabled() && this.keepY.isEnabled() && !tower) {
            playerpos = new BlockPos(new Vec3(mc.thePlayer.getPositionVector().xCoord, this.startY, mc.thePlayer.getPositionVector().zCoord)).offset(EnumFacing.DOWN);
        } else {
            this.startY = mc.thePlayer.posY;
        }

        for (EnumFacing facing : EnumFacing.values()) {
            if (playerpos.offset(facing).getBlock().getMaterial() != Material.air) {
                return new BlockData(playerpos.offset(facing), invert[facing.ordinal()]);
            }
        }
        final BlockPos[] addons = {
                new BlockPos(-1, 0, 0),
                new BlockPos(1, 0, 0),
                new BlockPos(0, 0, -1),
                new BlockPos(0, 0, 1)};

        for (int length2 = addons.length, j = 0; j < length2; ++j) {
            final BlockPos offsetPos = playerpos.add(addons[j].getX(), 0, addons[j].getZ());
            if (mc.theWorld.getBlockState(offsetPos).getBlock() instanceof BlockAir) {
                for (int k = 0; k < EnumFacing.values().length; ++k) {
                    if (mc.theWorld.getBlockState(offsetPos.offset(EnumFacing.values()[k])).getBlock().getMaterial() != Material.air) {

                        return new BlockData(offsetPos.offset(EnumFacing.values()[k]), invert[EnumFacing.values()[k].ordinal()]);
                    }
                }
            }
        }

        return null;
    }

    int slotIndex = 0;

    public float[] getRotationsAAC(BlockPos block, EnumFacing face) {
    	double x = block.getX() + 0.5D - this.mc.thePlayer.posX + face.getFrontOffsetX() / 2.0D;
    	double z = block.getZ() + 0.5D - this.mc.thePlayer.posZ + face.getFrontOffsetZ() / 2.0D;
    	double y = block.getY() + 0.5D + (face.getFrontOffsetZ() / 2);
    	y += 0.5D;
    	double d1 = this.mc.thePlayer.posY + this.mc.thePlayer.getEyeHeight() - y;
    	double d3 = MathHelper.sqrt_double(x * x + z * z);
    	float yaw = (float)(Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
    	float pitch = (float)(Math.atan2(d1, d3) * 180.0D / Math.PI);
    	if (yaw < 0.0F)
    	yaw += 360.0F;
    	
    	return new float[] { yaw, pitch };
    }
    
    private int getSlot() {
        ArrayList<Integer> slots = new ArrayList<>();
        for (int k = 0; k < 9; ++k) {
            final ItemStack itemStack = mc.thePlayer.inventory.mainInventory[k];
            if (itemStack != null && this.isValid(itemStack) && itemStack.stackSize >= 1) {
                slots.add(k);
            }
        }
        if (slots.isEmpty()) {
            return -1;
        }
        if (slotTimer.hasReached(150)) {
            if (slotIndex >= slots.size() || slotIndex == slots.size() - 1) {
                slotIndex = 0;
            } else {
                slotIndex++;
            }
            slotTimer.reset();
        }
        return slots.get(slotIndex);
    }

    private boolean isValid(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ItemBlock) {
            boolean isBad = false;

            ItemBlock block = (ItemBlock) itemStack.getItem();
            for (int i = 0; i < this.badBlocks.size(); i++) {
                if (block.getBlock().equals(this.badBlocks.get(i))) {
                    isBad = true;
                }
            }

            return !isBad;
        }
        return false;
    }

    private int getBlockCount() {
        int count = 0;
        for (int k = 0; k < mc.thePlayer.inventory.mainInventory.length; ++k) {
            final ItemStack itemStack = mc.thePlayer.inventory.mainInventory[k];
            if (itemStack != null && this.isValid(itemStack) && itemStack.stackSize >= 1) {
                count += itemStack.stackSize;
            }
        }
        return count;
    }

    public static class BlockData {
        public BlockPos position;
        public EnumFacing face;
        public Vec3 hitVec;

        public BlockData(BlockPos position, EnumFacing face) {
            this.position = position;
            this.face = face;
            this.hitVec = getHitVec();
        }

        public EnumFacing getFacing() {
            return this.face;
        }

        public BlockPos getPosition() {
            return this.position;
        }

        private Vec3 getHitVec() {
            final Vec3i directionVec = face.getDirectionVec();
            double x = directionVec.getX() * 0.5D;
            double z = directionVec.getZ() * 0.5D;

            if (face.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE) {
                x = -x;
                z = -z;
            }

            final Vec3 hitVec = new Vec3(position).addVector(x + z, directionVec.getY() * 0.5D, x + z);

            final Vec3 src = Minecraft.getMinecraft().thePlayer.getPositionEyes(1.0F);
            final MovingObjectPosition obj = Minecraft.getMinecraft().theWorld.rayTraceBlocks(src,
                    hitVec,
                    false,
                    false,
                    true);

            if (obj == null || obj.hitVec == null || obj.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
                return null;

            switch (face.getAxis()) {
                case Z:
                    obj.hitVec = new Vec3(obj.hitVec.xCoord, obj.hitVec.yCoord, Math.round(obj.hitVec.zCoord));
                    break;
                case X:
                    obj.hitVec = new Vec3(Math.round(obj.hitVec.xCoord), obj.hitVec.yCoord, obj.hitVec.zCoord);
                    break;
			case Y:
				break;
			default:
				break;
            }

            if (face != EnumFacing.DOWN && face != EnumFacing.UP) {
                final IBlockState blockState = Minecraft.getMinecraft().theWorld.getBlockState(obj.getBlockPos());
                final Block blockAtPos = blockState.getBlock();

                double blockFaceOffset;

                if (blockAtPos instanceof BlockSlab && !((BlockSlab) blockAtPos).isDouble()) {
                    final BlockSlab.EnumBlockHalf half = blockState.getValue(BlockSlab.HALF);

                    blockFaceOffset = org.apache.commons.lang3.RandomUtils.nextDouble(0.1, 0.4);

                    if (half == BlockSlab.EnumBlockHalf.TOP) {
                        blockFaceOffset += 0.5;
                    }
                } else {
                    blockFaceOffset = org.apache.commons.lang3.RandomUtils.nextDouble(0.1, 0.9);
                }

                obj.hitVec = obj.hitVec.addVector(0.0D, -blockFaceOffset, 0.0D);
            }

            return obj.hitVec;
        }
    }

    private void moveBlocksToHotbar() {
        boolean added = false;
        if (!isHotbarFull()) {
            for (int k = 0; k < mc.thePlayer.inventory.mainInventory.length; ++k) {
                if (k > 8 && !added) {
                    final ItemStack itemStack = mc.thePlayer.inventory.mainInventory[k];
                    if (itemStack != null && this.isValid(itemStack)) {
                        shiftClick(k);
                        added = true;
                    }
                }
            }
        }
    }

    public boolean isHotbarFull() {
        int count = 0;
        for (int k = 0; k < 9; ++k) {
            final ItemStack itemStack = mc.thePlayer.inventory.mainInventory[k];
            if (itemStack != null) {
                count++;
            }
        }
        return count == 8;
    }


    public static void shiftClick(int slot) {
        Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.inventoryContainer.windowId, slot, 0, 1, Minecraft.getMinecraft().thePlayer);
    }

    public static int randomNumber(int max, int min) {
        return Math.round(min + (float) Math.random() * ((max - min)));
    }
}