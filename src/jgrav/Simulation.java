package jgrav;

import static jgrav.Settings.*;

import java.awt.SplashScreen;
import java.util.ArrayList;
import java.util.List;

public class Simulation {

    private static Simulation instance;
    private List<Body> bodies; 
    private boolean pause;
    private double simTime;
    private double realTime;
    private double realDt;

    private Simulation() {
        bodies = new ArrayList<>();
        pause = false;
        realTime = 0;
        simTime = 0;
    }

    public static Simulation getInstance() {
        if (instance == null)
            instance = new Simulation();
        return instance;
    }

    public void createBody(int x, int y, double mass, Vec2d v) {
        Body body = new Body(x, y, mass, v);
        bodies.add(body);
    }

    public List<Body> getBodies() {
        return bodies;
    }

    public void updateBodies() {
        if (pause)
            return;
        for (Body body : bodies) {
            body.updatePosition(bodies);
        }
    }

    public void togglePause() {
        pause = !pause;
    }

    public void step(double dt) {
        if (pause)
            return;
        realTime += dt;
        simTime += (dt*TIME_MULTIPLIER);
        realDt += dt * TIME_MULTIPLIER;
        while (realDt > PHYSICS_DT) {
            if (LOGGING)
                System.out.printf(
                    "Stepped! Simtime: %.3fs Realtime: %.3fs Accumulator: %.3fs Dt: %.3fs%n",
                    simTime, realTime, realDt, dt);
            updateBodies();
            realDt -= PHYSICS_DT;
        }
    }

    public double getRealTime() {
        return realTime;
    }

    public double getSimTime() {
        return simTime;
    }

    public double pxFrToMSec(double v) {
        return v*SPACE_SCALE/PHYSICS_DT;
    }

    public Vec2d pxFrToMSec(Vec2d v) {
        return Vec2d.fromComponent(pxFrToMSec(v.getX()), pxFrToMSec(v.getY()));
    }

    public double mSecToPxFr(double v) {
        return v*PHYSICS_DT/SPACE_SCALE;
    }

    public void accelerate() {
        accelerate(5);
    }

    public void accelerate(int t) {
        TIME_MULTIPLIER *= t;
    }

    public void decelerate() {
        decelerate(5);
    }

    public void decelerate(int t) {
        TIME_MULTIPLIER /= t;
        if (TIME_MULTIPLIER < 1)
            TIME_MULTIPLIER = 1;
    }
}
