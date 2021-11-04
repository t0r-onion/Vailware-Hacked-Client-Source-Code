package toniqx.vailware.main.event.listeners;

import net.minecraft.client.gui.ScaledResolution;
import toniqx.vailware.main.event.Event;

public class EventRenderGUI extends Event<EventRenderGUI> {
    public ScaledResolution sr;

    public EventRenderGUI(ScaledResolution sr) {
        this.sr = sr;
    }
}
