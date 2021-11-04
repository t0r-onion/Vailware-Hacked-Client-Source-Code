package toniqx.vailware.main.music.radio;

import java.io.InputStream;
import java.util.Objects;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class RadioManager {

	private Player player;
	private static Thread thread;
	
	public void stop() {
		if(isRunning()) {
			thread.interrupt();
			thread = null;
			
			if(player != null) {
				player.close();
			}
		}
	}
	
	public void setVolume(double vol) {
		try {
			Mixer.Info[] infos = AudioSystem.getMixerInfo();
			for (Mixer.Info info : infos) {
				Mixer mixer = AudioSystem.getMixer(info);
				if (mixer.isLineSupported(Port.Info.SPEAKER)) {
					Port port = (Port) mixer.getLine(Port.Info.SPEAKER);
					port.open();
					if (port.isControlSupported(FloatControl.Type.VOLUME)) {
						FloatControl volume = (FloatControl) port.getControl(FloatControl.Type.VOLUME);
						volume.setValue((float) (vol / 100));
					}
					port.close();
				}
			}
		} catch (Exception e) {
			
		}
	}
	
	public static boolean isRunning() {
		return thread != null;
	}
	
	public void start() {
		try {
			Objects.requireNonNull(player);
			
			thread = new Thread(() -> {
				try {
					player.play();
				}catch (JavaLayerException e) {
					try {
						start();
					}catch(Exception e1) {
						
					}
				}
			});
			thread.start();
		}catch(Exception ignore) {
			
		}
	}
	
	public void setStream(InputStream inputStream) {
		try {
			player = new Player(inputStream);
		}catch (JavaLayerException e) {
			e.printStackTrace();
		}
	}
	
}
