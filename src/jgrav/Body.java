package jgrav;

import static jgrav.Settings.TIME_SCALE;

import java.util.List;

/**
 * Body
 */
public class Body {

    private double x, y;
    private double mass;
    private Vec2d velocity;

    public Body(double x, double y, Vec2d velocity) {
        this.x = x;
        this.y = y;
        this.velocity = velocity;
    }

    public void updatePosition(List<Body> bodies) {
        // divided by timescale so that velocity is in specifically distance/sec
        x += velocity.getX()/TIME_SCALE;
        y += velocity.getY()/TIME_SCALE;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

}
