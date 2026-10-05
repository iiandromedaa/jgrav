package jgrav;

import static jgrav.Settings.*;

import java.util.ArrayList;
import java.util.List;

public class Simulation {

    private static Simulation instance;
    private volatile List<Body> bodies; 
    private volatile List<Body> cullList;
    private volatile List<Body> createList;
    private boolean pause;
    private double simTime;
    private double realTime;
    private double realDt;

    private Simulation() {
        bodies = new ArrayList<>();
        cullList = new ArrayList<>();
        createList = new ArrayList<>();
        pause = false;
        realTime = 0;
        simTime = 0;
    }

    public static Simulation getInstance() {
        if (instance == null)
            instance = new Simulation();
        return instance;
    }

    public void createBody(double x, double y, double mass, Vec2d v) {
        Body body = new Body(x, y, mass, v);
        log(String.format("Body created at %f, %f", x, y), LogSeverity.INFO);
        createList.add(body);
    }

    public void clearBodies() {
        cullList.addAll(bodies);
        log("Cleared bodies!", LogSeverity.INFO);
    }

    public void destroyBody(Body body) {
        cullList.add(body);
    }

    public List<Body> getBodies() {
        return bodies;
    }

    public void updateBodies() {
        if (!cullList.isEmpty())
            bodies.removeAll(cullList);
        cullList.clear();
        if (!createList.isEmpty())
            bodies.addAll(createList);
        createList.clear();
        if (pause || bodies.isEmpty())
            return;
        for (Body body : bodies) {
            body.updatePosition(bodies);
            if (body.getVelocity().getLength() > 1e7) {
                log("Body marked for deletion, absurd velocity", LogSeverity.WARN);
                cullList.add(body);
            }
            log(body.toString(), LogSeverity.INFO);
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
            log(String.format(
                    "Stepped! Simtime: %.3fs Realtime: %.3fs Accumulator: %.3fs Dt: %.3fs%n",
                    simTime, realTime, realDt, dt), LogSeverity.INFO);
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

    public void log(String msg, LogSeverity severity) {
        if (severity.getSeverity() >= LOGGING)
            System.out.printf("[%s] %s%n", severity.name(), msg);
    }

    public enum LogSeverity {
        INFO(1),
        WARN(2),
        ERROR(3);

        private final int severity;

        LogSeverity(int severity) {
            this.severity = severity;
        }

        public int getSeverity() {
            return severity;
        }
    }

}
