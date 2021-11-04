package toniqx.vailware.main.event.readers;

import java.util.ArrayList;
import java.util.EventListener;

import toniqx.vailware.main.event.Event;

public abstract class CancellableEvent<T extends EventListener> extends Event<T>
{
	private boolean cancelled = false;
	
	public void cancel()
	{
		cancelled = true;
	}
	
	public boolean isCancelled()
	{
		return cancelled;
	}

}