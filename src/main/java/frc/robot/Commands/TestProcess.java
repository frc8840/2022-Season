package frc.robot.Commands;

import frc.robot.Robot;

/**
 * A test process that will run all motors at 10% speed.
 * @author JaidenAGrimminck
 * */
public class TestProcess extends Process{

    public TestProcess(String name) {
        super(name, true);
    }

    /**
     * Stops all the motors.
     * */
    @Override
    public void run() {
        System.out.println("Running: " + this.getName());
    }

    /**
     * Runs the motors at 10% speed when run.
     * */
    @Override
    public void runFrame() {
        System.out.println("Running motors at 10% speed.");
    }
}
