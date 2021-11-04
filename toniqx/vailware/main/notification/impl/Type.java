package toniqx.vailware.main.notification.impl;

public enum Type {
	
	INFO("info"),
	WARNING("warning"),
	ERROR("error");
	
	public String filePrefix;
	
	Type(String filePrefix) {
		
		this.filePrefix = filePrefix;
		
	}
	
}