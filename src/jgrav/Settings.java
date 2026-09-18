package jgrav;
public class Settings {
    
    private Settings() {}

    /**
     * target delay between frames in ms
     * (default 16ms)
     */
    public static final int FRAME_TIME = 16;

    /**
     * how many timesteps (frames) should equal 1 second
     * (default 1sec = 62.5frames)
     */
    public static final double TIME_SCALE = (1/(FRAME_TIME*0.001));

    /**
     * how many pixels should equal 1 meter
     */
    public static final double SPACE_SCALE = 1;

    /**
     * draw lines between bodies?
     */
    public static final boolean DRAW_LINES = false;

    public static final double G = 6.67e-11;

}
