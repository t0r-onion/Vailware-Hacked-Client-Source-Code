package toniqx.vailware.main.authentication;

import toniqx.vailware.main.util.impl.RenderUtils;

public class Translate {
    private double x, y;

    public Translate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void animate(double newX, double newY) {
        x = RenderUtils.transition(x, newX, 0.5D);
        y = RenderUtils.transition(y, newY, 0.5D);
    }

    public double getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}