package toniqx.vailware.modules.render;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.process.EventRenderEntity;
import toniqx.vailware.main.event.process.EventRenderWorld;
import toniqx.vailware.main.settings.impl.ModeSetting;
import toniqx.vailware.modules.Module;

public class Chams extends Module {

    public Chams() {
        super("Chams", "", Keyboard.KEY_NONE, Category.RENDER);
    }

    public void onEvent(Event e) {
        if(e instanceof EventRenderEntity) {
        	EventRenderEntity er = (EventRenderEntity) e;
            if(er.isPre()) {
                GL11.glEnable(32823);
                GL11.glPolygonOffset(1.0f, -1000000.0F);
            }

            if(er.isPost()) {
                GL11.glDisable(32823);
                GL11.glPolygonOffset(1.0f, 1000000.0F);
            }
        }
    }
}