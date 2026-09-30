package jgrav;

import static jgrav.Settings.*;
import java.util.List;

import jgrav.Simulation.LogSeverity;

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

    /**
     * combines two bodies into 1, combining their mass and conserving momentum
     * @param body body to combine into this (will be destroyed)
     */
    public void combine(Body body) {
        double newmass = mass + body.getMass();
        Vec2d p = Vec2d.multScalar(velocity, mass);
        Vec2d otherP = Vec2d.multScalar(body.getVelocity(), body.getMass());
        Vec2d newvelocity = Vec2d.divScalar(Vec2d.add(p, otherP), mass);

        Simulation.getInstance().destroyBody(body);
        Simulation.getInstance().destroyBody(this);
        Simulation.getInstance().createBody(x, y, newmass, newvelocity);
    }

    private void updateVelocity(List<Body> bodies) {
        double dax = 0;
        double day = 0;
        for (Body body : bodies) {
            if (body != this) {
                if (BODY_COLLISION) {
                    if (distanceToBody(body).getLength() < calculateScreenRadius()*SPACE_SCALE) {
                        Simulation.getInstance().log(String.format("Body collision at %f, %f", x, y), LogSeverity.WARN);
                        if (body.getMass() <= mass) {
                            combine(body);
                        }
                    }
                }
                Vec2d fg = getFg(body);
                dax += (fg.getX()/(mass));
                day += (fg.getY()/(mass));
            }
        }
        velocity = Vec2d.add(Vec2d.fromComponent(dax*PHYSICS_DT, day*PHYSICS_DT), velocity);
    }

    public int calculateScreenRadius() {
        return (int)(RADIUS_MULT * Math.pow(Math.abs(mass), RADIUS_POWER)/2 + 5);
    }

    @Override
    public String toString() {
        return String.format("Body: x: %.3fm y: %.3fm Mass: %.3gkg vx: %.3f vy: %.3f", 
            x, y, mass, velocity.getX(), velocity.getY());
    }

}
