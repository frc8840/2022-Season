package frc.robot.utils;

import org.opencv.core.Scalar;

public class Colors {
    //BGR Colors. Not RGB.

    //https://stackoverflow.com/questions/36817133/identifying-the-range-of-a-color-in-hsv-using-opencv/51686953
    public static final Scalar lower_blue = new Scalar(90, 50, 70);
    public static final Scalar upper_blue = new Scalar(128, 255, 255);
    public static final Scalar blue = new Scalar(255, 0, 0);

    public static final Scalar lower_red = new Scalar(161, 155, 84);
    public static final Scalar upper_red = new Scalar(179, 255, 255);
    public static final Scalar red = new Scalar(0, 0, 255);

    public static final Scalar better_lower_red = new Scalar(150, 100, 150);
    public static final Scalar better_upper_red = new Scalar(173, 255, 255);
}

