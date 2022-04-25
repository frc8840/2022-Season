package frc.robot;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Old code. I wouldn't use this in any of our stuff.
 *
 * @author JaidenAGrimminck
 * */
public class Controller {
    final String unit = "m/s";

    double x = 0.0;
    double velocityX = 0.0;
    double thrustX = 0.0;
    double goal = 100.0;
    double error = 0.0;

    double errorSum = 0;
    double errorChange = 0;
    double previousError = 0;

    double p = .004;
    double i = 0.0001;
    double d = 0.001;

    private final Timer timer;

    boolean firstErr = true;
    public void updateError() {
        error = goal - x;
        if (firstErr) {firstErr=false; return;}
        errorSum += error;

        errorChange = error - previousError;
        previousError = error;
    }

    private void updateLocation() {
        x += velocityX / (error < 10 ? (10 - error + 0.001) : 0.5);
    }

    private void updateVelocity() {
        velocityX += thrustX;

        velocityX *= 0.9;

        if (Math.abs(velocityX) < 0.01) velocityX = 0;
    }

    private void updateThrust() {
        thrustX = p * error + i * errorSum + d * errorChange;
    }

    public void setThrust(double thrust) {
        this.thrustX = thrust;
    }

    public double getSpeed() {
        return this.velocityX;
    }

    public String getUnit() {
        return this.unit;
    }

    public void stop() {
        this.timer.cancel();
    }

    public Controller() {
        updateError();

        System.out.println("Using " + unit);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateError();
                updateVelocity();
                updateLocation();
                updateThrust();

//                TopLevelWindow.updateWindow("X: " + x + "m, travelling at " + velocityX + " " + unit);
//
//                if (Math.abs(error) < 0.1) {
//                    timer.cancel();
//                    TopLevelWindow.updateWindow("Reached goal, X: " + x + "m");
//                }
            }
        }, 100, 100);
    }
}
