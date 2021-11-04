package toniqx.vailware.modules.player;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import toniqx.vailware.Client;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.Timer;
import toniqx.vailware.modules.Module;

import org.lwjgl.input.Keyboard;

public class ChestSteal extends Module {

    public BooleanSetting priority = new BooleanSetting("Priority", true),
            autoClose = new BooleanSetting("AutoClose", true);
    public NumberSetting speed = new NumberSetting("Speed", 8, 1, 20, 1);
    public Timer startDelay = new Timer();
    public Timer closeDelay = new Timer();
    public boolean wasNotChest;

    public ChestSteal() {
        super("ChestSteal", "", Keyboard.KEY_NONE, Category.PLAYER);
        this.addSettings(speed, priority, autoClose);
    }

    public void onEvent(Event e) {
        if (e instanceof EventUpdate && e.isPre()) {
            if (wasNotChest && mc.currentScreen instanceof GuiChest) {
                startDelay.reset();
            }
            wasNotChest = !(mc.currentScreen instanceof GuiChest);
            if (mc.currentScreen instanceof GuiChest && startDelay.getTime() > 25 && Client.invCooldownElapsed((long) (1000 / Math.max(1, (speed.getValue()))))) {
                GuiChest chestGui = (GuiChest) mc.currentScreen;
                Container container = chestGui.inventorySlots;

                String name = chestGui.lowerChestInventory.getDisplayName().getUnformattedText();
                if (!(name.equals("Large Chest") || name.equals("Chest") || name.equals("LOW")))
                    return;

                boolean shouldClose = true;
                for (Slot slot : container.inventorySlots) {
                    int id = slot.slotNumber;
                    ItemStack item = slot.getStack();

                    if (id <= 26 && item != null && shouldSteal(item)) {
                        shouldClose = false;
                        for (Slot playerSlot : mc.thePlayer.inventoryContainer.inventorySlots) {
                            int playerSlotId = playerSlot.slotNumber;
                            boolean empty = !playerSlot.getHasStack();

                            if (empty) {
                                //mc.thePlayer.addChatMessage(new ChatComponentText(id + " " + item.toString()));
                                mc.playerController.windowClick(container.windowId, id, 0, 1, mc.thePlayer);
                                closeDelay.reset();
                                //startDelay.reset();
                                return;
                            }
                        }
                    }
                }
                if (closeDelay.getTime() > 25 && shouldClose && autoClose.isEnabled()) {
                    mc.thePlayer.closeScreen();
                    startDelay.reset();
                }


            }
        }

    }

    public boolean isBestArmor(ItemStack compareStack) {
        for (int i = 0; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack = slot.getStack();
            boolean hotbar = i >= 36;

            if (stack != null && compareStack != stack && stack.getItem() instanceof ItemArmor) {
                ItemArmor item = (ItemArmor) stack.getItem();
                ItemArmor compare = (ItemArmor) compareStack.getItem();
                if (item.armorType == compare.armorType) {
                    if (AutoArmor.getProtection(compareStack) <= AutoArmor.getProtection(stack))
                        return false;
                }
            }
        }

        return true;
    }

    public static float getSwordStrength(ItemStack stack) {
        return (!(stack.getItem() instanceof ItemSword) ? 0.0F : (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25F) + (!(stack.getItem() instanceof ItemSword) ? 0.0F : (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 1F);
    }

    public boolean isBestSword(ItemStack compareStack) {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack = slot.getStack();
            boolean hotbar = i >= 36;

            if (stack != null && compareStack != stack && stack.getItem() instanceof ItemSword) {
                ItemSword item = (ItemSword) stack.getItem();
                ItemSword compare = (ItemSword) compareStack.getItem();
                if (item.getClass() == compare.getClass()) {
                    if (compare.attackDamage + getSwordStrength(compareStack) <= item.attackDamage + getSwordStrength(stack))
                        return false;
                }
            }
        }

        return true;
    }

    public boolean isBestTool(ItemStack compareStack) {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack = slot.getStack();
            boolean hotbar = i >= 36;

            if (stack != null && compareStack != stack && stack.getItem() instanceof ItemTool) {
                ItemTool item = (ItemTool) stack.getItem();
                ItemTool compare = (ItemTool) compareStack.getItem();
                if (item.getClass() == compare.getClass()) {
                    if (compare.getStrVsBlock(stack, preferredBlock(item.getClass())) <= item.getStrVsBlock(compareStack, preferredBlock(compare.getClass())))
                        return false;
                }
            }
        }

        return true;
    }

    public Block preferredBlock(Class clazz) {
        return clazz == ItemPickaxe.class ? Blocks.cobblestone : clazz == ItemAxe.class ? Blocks.log : Blocks.dirt;
    }

    public boolean shouldSteal(ItemStack itemStack) {
        Item item = itemStack.getItem();

        if (!priority.isEnabled())
            return true;

        if (item instanceof ItemSword)
            return isBestSword(itemStack);

        if (item instanceof ItemTool)
            return isBestTool(itemStack);

        if (item instanceof ItemFood)
            return true;//!Fan.inventoryManager.has(item, 64);

        if (item instanceof ItemBlock)
            return true;

        if (item instanceof ItemEnderPearl)
            return true;

        if (item instanceof ItemArmor)
            return isBestArmor(itemStack);

        if (item instanceof ItemSnowball)
            return true;

        if (item instanceof ItemEgg)
            return true;

        if (item instanceof ItemMonsterPlacer)
            return true;

        if (item instanceof ItemBow)
            return true;

        if (item instanceof ItemPotion)
            return true;

        return item == Items.arrow;
    }
}
	