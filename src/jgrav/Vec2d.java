package jgrav;

/**
 * Vec2d wow look at that factory pattern
 */
public class Vec2d {

    private double x, y;

    private Vec2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * create vector using component scalars
     * @param x x length
     * @param y y length
     */
    public static Vec2d fromComponent(double x, double y) {
        return new Vec2d(x, y);
    }

    /**
     * create vector using polar form
     * @param length magnitude
     * @param angleRad direction, 0deg is x+
     */
    public static Vec2d fromPolar(double length, double angleRad) {
        return new Vec2d(Math.cos(angleRad)*length, Math.sin(angleRad)*length);
    }

    /**
     * @return x component of vector
     */
    public double getX() {
        return x;
    }

    /**
     * @return y component of vector
     */
    public double getY() {
        return y;
    }

    /**
     * @return length of vector (hypotenuse)
     */
    public double getLength() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    /**
     * @return angle of vector in radians, from 0deg = x+
     */
    public double getAngle() {
        return Math.atan2(y, x);
    }

    @Override
    public String toString() {
        return String.format("x: %+.3f, y: %+.3f, angle: %+.3f rad, %.3f deg, length: %+.3f",
            getX(), getY(), getAngle(), Math.toDegrees(getAngle()), getLength());
    }

}
