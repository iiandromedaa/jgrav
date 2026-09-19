package jgrav;

import static jgrav.Settings.G;
import static jgrav.Settings.MASS_MULTIPLIER;
import static jgrav.Settings.SPACE_SCALE;
import static jgrav.Settings.TIME_MULTIPLIER;
import static jgrav.Settings.TIME_SCALE;

import java.util.List;

/**
 * Body
 */
public class Body {

    private double x, y;
    private double mass;
    private Vec2d velocity;
    private Simulation sim = Simulation.getInstance();

    public Body(double x, double y, double mass, Vec2d velocity) {
        this.x = x;
        this.y = y;
        this.mass = mass;
        this.velocity = velocity;
    }

    public void updatePosition(List<Body> bodies) {
        updateVelocity(bodies);
        // divided by timescale and time multiplier so that velocity is in specifically pixels/real sec
        x += sim.mSecToPxFr(velocity.getX());
        y += sim.mSecToPxFr(velocity.getY());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getMass() {
        return mass;
    }

    public Vec2d getVelocity() {
        return velocity;
    }

    public Vec2d distanceToBody(Body body) {
        return Vec2d.fromComponent((body.getX() - x)*SPACE_SCALE,
            (body.getY() - y)*SPACE_SCALE);
    }

    public Vec2d getFg(Body body) {
        Vec2d r = distanceToBody(body);
        return (Vec2d.fromPolar(
            (G*mass*body.getMass())/Math.pow(r.getLength(), 2), r.getAngle()
        ));
    }

    private void updateVelocity(List<Body> bodies) {
        double dax = 0;
        double day = 0;
        for (Body body : bodies) {
            if (body != this) {
                Vec2d fg = getFg(body);
                System.out.println(fg.getLength());
                dax += (fg.getX()/(mass));
                day += (fg.getY()/(mass));
            }
        }
        velocity.add(Vec2d.fromComponent(dax, day));
    }

}
