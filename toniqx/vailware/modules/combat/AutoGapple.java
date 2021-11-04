package toniqx.vailware.modules.combat;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.TimerUtils;
import toniqx.vailware.modules.Module;
import toniqx.vailware.modules.Module.Category;

public class AutoGapple extends Module {

	public static NumberSetting health = new NumberSetting("Health", 7, 1, 20, 1);
	public static NumberSetting delay = new NumberSetting("Delay", 200, 100, 600, 1);
	
    public AutoGapple() {
        super("AutoGapple", "", Keyboard.KEY_NONE, Category.COMBAT);
        addSettings(health, delay);
    }

	private TimerUtils timer = new TimerUtils();
    private TimerUtils eatTimer = new TimerUtils();
    public static String DELAY = "DELAY";
    public static String HEALTH = "HEALTH";
    private static int prevSlot = -1;

    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            EventUpdate em = (EventUpdate) event;
            if (em.isPre()) {
                int appleSlot = getGappleFromInventory();
                if (appleSlot != -1 && prevSlot < 0
                        && mc.thePlayer.getHealth() < ((Number) health.getValue()).floatValue()
                        && !mc.thePlayer.isPotionActive(Potion.regeneration.id)
                        && timer.delay(((Number) delay.getValue()).floatValue())) {
                    swap(getGappleFromInventory(), 6);

                    prevSlot = mc.thePlayer.inventory.currentItem;
                    mc.thePlayer.inventory.currentItem = 6;

                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                    eatTimer.reset();
                    timer.reset();
                } else if (prevSlot >= 0 && eatTimer.getDifference() > 35 * 50) {
                    mc.playerController.onStoppedUsingItem(mc.thePlayer);

                    mc.thePlayer.inventory.currentItem = prevSlot;
                    prevSlot = -1;
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    timer.reset();
                }
            }
        }
    }

    protected void swap(int slot, int hotbarNum) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hotbarNum, 2, mc.thePlayer);
    }

    public static int getGappleFromInventory() {
        Minecraft mc = Minecraft.getMinecraft();
        int apple = -1;
        int counter = 0;
        for (int i = 1; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                Item item = is.getItem();
                if (Item.getIdFromItem(item) == 322) {
                    counter++;
                    apple = i;
                }
            }
        }
        return apple;
    }

    public static boolean isEating() {
        return prevSlot >= 0;
    }
}
