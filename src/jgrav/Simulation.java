package jgrav;

import java.util.ArrayList;
import java.util.List;

public class Simulation {

    private static Simulation instance;
    private List<Body> bodies; 

    private Simulation() {
        bodies = new ArrayList<>();
    }

    public static Simulation getInstance() {
        if (instance == null)
            instance = new Simulation();
        return instance;
    }

    public void createBody(int x, int y, double vx, double vy) {
        Body body = new Body(x, y, Vec2d.fromComponent(vx, vy));
        bodies.add(body);
    }

    public List<Body> getBodies() {
        return bodies;
    }
}
