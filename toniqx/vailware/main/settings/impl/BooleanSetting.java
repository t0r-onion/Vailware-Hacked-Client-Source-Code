package toniqx.vailware.main.settings.impl;

import toniqx.vailware.Client;
import toniqx.vailware.main.settings.Setting;

public class BooleanSetting extends Setting {

	public BooleanSetting(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean enabled;

	public BooleanSetting(String name, boolean enabled) {
		this.name = name;
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void toggle() {;
		enabled = !enabled;
	}
}
