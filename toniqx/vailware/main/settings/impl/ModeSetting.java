package toniqx.vailware.main.settings.impl;

import java.util.List;

import toniqx.vailware.main.settings.Setting;

import java.util.Arrays;

public class ModeSetting extends Setting {

	public int index;
	public List<String> modes;
	public boolean Expanded;
	
	public ModeSetting(String name, String defaultMode, String... modes) {
		this.name = name;
		this.modes = Arrays.asList(modes);
		index = this.modes.indexOf(defaultMode);
	}	
	
	public String getMode() {
		return modes.get(index);
	}
	
	public void setMode(String mode) {
		
		for (String string : modes) {
			if (string.equals(mode)) {
				index = modes.indexOf(string);
			}
		}
		
	}
	
	public boolean is(String mode) {
		return index == modes.indexOf(mode);
	}
	
	public void cycle() {
		if(index < modes.size() - 1) {
			index++;
		}else {
			index = 0;
		}
	}
	
	public void cycleback() {
		if (index > 0) {
			index--;
		}else {
			index = modes.size() - 1;
		}
	}
	
	
}