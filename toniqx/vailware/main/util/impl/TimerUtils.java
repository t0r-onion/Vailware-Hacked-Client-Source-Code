package toniqx.vailware.main.util.impl;

public class TimerUtils {

	private long lastMS;

	public TimerUtils() {
		this.reset();
	}

	private long getCurrentMS() {
		return System.nanoTime() / 1000000L;
	}

	public boolean hasReached(final double milliseconds) {
		return this.getCurrentMS() - this.lastMS >= milliseconds;
	}

	public void reset() {
		this.lastMS = this.getCurrentMS();
	}

	public boolean delay(final float milliSec) {
		return this.getTime() - this.lastMS >= milliSec;
	}

	public long getTime() {
		return System.nanoTime() / 1000000L;
	}
	
    public long getDifference() {
        return getTime() - this.lastMS;
    }

}
