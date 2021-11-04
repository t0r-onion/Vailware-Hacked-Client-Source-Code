package toniqx.vailware.modules.ghost;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.settings.impl.BooleanSetting;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.Timer;
import toniqx.vailware.main.util.impl.TimerUtils;
import toniqx.vailware.modules.Module;
import toniqx.vailware.modules.Module.Category;

public class AutoClicker extends Module {
	
	public NumberSetting CPS = new NumberSetting("CPS", 8, 1, 20, 1);
	public NumberSetting MIN = new NumberSetting("Minimum", 7, 1, 20, 1);
	public NumberSetting MAX = new NumberSetting("Maximum", 10, 1, 20, 1);
	public BooleanSetting RANDOM = new BooleanSetting("Random", true);
	public BooleanSetting MOUSE = new BooleanSetting("Hold", true);
	
    public AutoClicker() {
        super("AutoClicker", "", Keyboard.KEY_NONE, Category.GHOST);
        this.addSettings(CPS, MIN, MAX, RANDOM);
    }

    TimerUtils timer = new TimerUtils();

    public static float randomNumber(float max, float min) {
    	float ii = -min + (float) (Math.random() * ((max - (-min)) + 1));
        return ii;
    }

    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            EventUpdate em = (EventUpdate) e;
            if (em.isPre() && mc.currentScreen == null && mc.thePlayer.isEntityAlive()) {
                if (MOUSE.enabled && !Mouse.isButtonDown(0)) {
                    return;
                }
                float cps = (int) CPS.getValue();
                float minran = (float) MIN.getValue();
                float maxran = (float) MAX.getValue();
                boolean random = RANDOM.enabled;
                float rand = random ? randomNumber(minran, maxran) : 0;
                float cpsdel = cps+rand <= 0? 1:cps+rand;
                float del = 1000/(cpsdel) ;
                if (timer.delay(del)) {
                    mc.playerController.onStoppedUsingItem(mc.thePlayer);
                    mc.thePlayer.swingItem();
                    mc.clickMouse();
                    timer.reset();
                }
            }
        }
    }
}
