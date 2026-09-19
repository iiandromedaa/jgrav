package jgrav;

import static jgrav.Settings.SPACE_SCALE;
import static jgrav.Settings.TIME_MULTIPLIER;
import static jgrav.Settings.TIME_SCALE;

import java.util.ArrayList;
import java.util.List;

public class Simulation {

    private static Simulation instance;
    private List<Body> bodies; 
    private boolean pause;
    private int timesteps;

    private Simulation() {
        bodies = new ArrayList<>();
        pause = false;
        timesteps = 0;
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

    public void step() {
        if (pause)
            return;
        timesteps++;
    }

    public int getSteps() {
        return timesteps;
    }

    public double pxFrToMSec(double v) {
        return v*(((1/TIME_SCALE)/SPACE_SCALE)*TIME_MULTIPLIER);
    }

    public Vec2d pxFrToMSec(Vec2d v) {
        return Vec2d.fromComponent(pxFrToMSec(v.getX()), pxFrToMSec(v.getY()));
    }

    public double mSecToPxFr(double v) {
        return v*((SPACE_SCALE/(1/TIME_SCALE))*TIME_MULTIPLIER);
    }
}
