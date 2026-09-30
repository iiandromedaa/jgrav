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

    public static Vec2d fromZero() {
        return new Vec2d(0, 0);
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

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setLength(double length) {
        setX(Math.cos(getAngle())*length);
        setY(Math.sin(getAngle())*length);
    }

    public static Vec2d add(Vec2d a, Vec2d b) {
        return Vec2d.fromComponent(a.getX() + b.getX(), a.getY() + b.getY());
    }

    public static Vec2d subtract(Vec2d a, Vec2d b) {
        return Vec2d.fromComponent(a.getX() - b.getX(), a.getY() - b.getY());
    }

    public static Vec2d multScalar(Vec2d a, double m) {
        return Vec2d.fromComponent(a.getX() * m, a.getY() * m);
    }

    public static Vec2d divScalar(Vec2d a, double d) {
        return Vec2d.fromComponent(a.getX() / d, a.getY() / d);
    }

    public void zero() {
        x = 0;
        y = 0;
    }

    @Override
    public String toString() {
        return String.format("x: %+.3f, y: %+.3f, angle: %+.3f rad, %.3f deg, length: %+.3f",
            getX(), getY(), getAngle(), Math.toDegrees(getAngle()), getLength());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vec2d) {
            if (((Vec2d)obj).getX() == x && ((Vec2d)obj).getY() == y)
                return true;
            else
                return false;
        } else
            return super.equals(obj);
    }

}
