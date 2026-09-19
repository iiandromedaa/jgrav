package jgrav;
public class Settings {
    
    private Settings() {}

    /**
     * target delay between frames in ms
     * (suggested 16ms)
     */
    public static final int FRAME_TIME = 16;

    /**
     * multiplier for speed of time, 1 real second = 1*TIME_MULTIPLIER seconds
     * (suggested 1)
     */
    public static final double TIME_MULTIPLIER = 1;

    /**
     * equation to get number of frames in 1 second
     * (ideally, don't touch this)
     */
    public static final double TIME_SCALE = 1/(FRAME_TIME*0.001*TIME_MULTIPLIER);

    /**
     * how many meters in 1 pixel
     * (suggested 1)
     */
    public static final double SPACE_SCALE = 100;

    /**
     * scales the masses of all objects by this
     * (suggested 1e12)
     */
    public static final double MASS_MULTIPLIER = 1e11;

    /**
     * draw F_G as lines?
     */
    public static final boolean DRAW_FORCES = false;

    /**
     * should labels be drawn regardless of whether or not widget is active
     */
    public static final boolean ALWAYS_LABEL = false;

    /**
     * gravitation constant, you can change it if you'd really like
     */
    public static final double G = 6.674e-11;

    /**
     * power of the proportionality p between mass and radius of bodies onscreen, in that r = km^RADIUS_SCALING
     * (suggested 0.4)
     */
    public static final double RADIUS_POWER = 0.3;

    /**
     * constant multiplier k in the proportionality between radius and mass, in that r = RADIUS_SCALING*m^p
     */
    public static final double RADIUS_MULT = 0.001;

}
