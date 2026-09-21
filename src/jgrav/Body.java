package jgrav;

import static jgrav.Settings.*;
import java.util.List;

/**
 * Body
 */
public class Body {

    private double x, y;
    private double mass;
    private Vec2d velocity;

    public Body(double x, double y, double mass, Vec2d velocity) {
        this.x = x;
        this.y = y;
        this.mass = mass;
        this.velocity = velocity;
    }

    public void updatePosition(List<Body> bodies) {
        updateVelocity(bodies);
        // this is called euler integration apparently
        x += (velocity.getX()*PHYSICS_DT);
        y += (velocity.getY()*PHYSICS_DT);
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
        return Vec2d.fromComponent((body.getX() - x),
            (body.getY() - y));
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
                // System.out.println(fg);
                dax += (fg.getX()/(mass));
                day += (fg.getY()/(mass));
            }
        }
        velocity.add(Vec2d.fromComponent(dax*PHYSICS_DT, day*PHYSICS_DT));
    }

}
